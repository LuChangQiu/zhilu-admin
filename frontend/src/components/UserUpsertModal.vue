<template>
	<!-- Main modal -->
	<div tabindex="-1" aria-hidden="true"
		class="bg-gray-900/50 hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
		<div class="relative p-4 w-full max-w-xs sm:max-w-sm md:max-w-md max-h-full">
			<!-- Modal content -->
			<div class="relative bg-white rounded-lg shadow">
				<!-- Modal header -->
				<div class="flex items-center justify-between p-4 md:p-5 border-b rounded-t border-gray-200">
					<h3 class="text-base sm:text-lg font-semibold text-gray-900">
						用户管理
					</h3>
					<button type="button" @click="closeModal"
						class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center">
						<svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
							<path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
								d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
						</svg>
						<span class="sr-only">Close modal</span>
					</button>
				</div>
				<!-- Modal body -->
				<form class="p-4 md:p-5">
					<div class="space-y-4">
						<div class="w-full">
							<label for="name" class="block mb-2 text-sm font-medium text-gray-900">用户名</label>
							<input type="text" name="用户名" id="name" v-model="formData.username"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
								required="true">
						</div>
						<div class="w-full">
							<label for="password" class="block mb-2 text-sm font-medium text-gray-900">密码</label>
							<input type="password" id="password" autocomplete="new-password" v-model="formData.password"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								placeholder="编辑时非必填" required />
						</div>
						<div class="w-full">
							<label for="confirm_password" class="block mb-2 text-sm font-medium text-gray-900">确认密码</label>
							<input type="password" id="confirm_password" autocomplete="new-password"
								v-model="formData.confirmPassword"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								required placeholder="编辑时非必填" />
						</div>
						<label class="block mb-2 text-sm font-medium text-gray-900" for="file_input">上传头像</label>
						<input
							class="block w-full text-sm text-gray-900 border border-gray-300 rounded-lg cursor-pointer bg-gray-50 focus:outline-none"
							id="file_input" type="file" accept="image/*" @change="handleFileChange">
						<div class="w-full">
							<label for="status" class="block mb-2 text-sm font-medium text-gray-900">状态</label>
							<select id="status" v-model="formData.enable"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5">
								<option :value=true>启用</option>
								<option :value=false>禁用</option>
							</select>
						</div>
					</div>
					<button type="submit" @click.prevent="handleSubmit" :disabled="uploadLoading"
						class="mt-5 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center disabled:opacity-50 disabled:cursor-not-allowed">
						保存
					</button>
				</form>
			</div>
		</div>
	</div>

</template>
<script setup lang="ts">
import { useUserUpsert } from "@/composables/user/useUserUpsert";
import type { UserUpsertSubmitModel } from "@/types/user";
import Compressor from "compressorjs";
import { initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { z } from "zod";
import type { components } from "../api/types/schema";
import { ValidationError } from "@/types/error";
import useAlertStore from "@/composables/store/useAlertStore";

const { user, onSubmit } = defineProps<{
	user?: components["schemas"]["UserRolePermissionDto"];
	closeModal: () => void;
	onSubmit: (data: UserUpsertSubmitModel) => Promise<void>;
}>();

const formData = ref();
const { uploadUserAvatar } = useUserUpsert();
const { showAlert } = useAlertStore();
const uploadLoading = ref(false);


const updateFormData = (newUser: typeof user) => {
	formData.value = {
		id: newUser?.id,
		username: newUser?.username,
		password: undefined,
		avatar: newUser?.avatar ?? undefined,
		enable: newUser?.enable ?? true,
		confirmPassword: undefined,
	};
};

watch(() => user, updateFormData, {
	immediate: true,
});

const validateFile = (file?: File) => {
	if (!file) {
		throw new ValidationError("您未选择文件");
	}
	const allowedTypes = ["image/jpeg", "image/png"];
	if (!allowedTypes.includes(file.type)) {
		throw new ValidationError("不支持的文件类型");
	}
	const maxSize = 200 * 1024; // 200KB
	if (file.size > maxSize) {
		throw new ValidationError("文件大小超过限制(200KB)");
	}
};

const handleFileChange = (event: Event) => {
	const file = (event.target as HTMLInputElement).files?.[0];
	uploadLoading.value = true;
	try {
		validateFile(file);
		new Compressor(file!, {
			quality: 0.8, // 压缩质量，0-1之间
			maxWidth: 800, // 最大宽度
			maxHeight: 800, // 最大高度
			mimeType: "auto", // 自动选择最佳格式
			success: async (compressedFile: File) => {
				formData.value.avatar = await uploadUserAvatar(compressedFile);
				uploadLoading.value = false;
				showAlert({
					content: "上传成功",
					level: "success",
				});
			},
			error: (err: Error) => {
				throw err;
			},
		});
	} catch (error) {
		(event.target as HTMLInputElement).value = "";
		uploadLoading.value = false;
		throw error;
	}
};

const handleSubmit = async () => {
	const userSchema = z
		.object({
			id: z.number().optional(),
			avatar: z.string().optional(),
			username: z
				.string({
					message: "用户名不能为空",
				})
				.min(1, "用户名至少1个字符")
				.max(15, "用户名最多15个字符"),
			enable: z.boolean({
				message: "状态不能为空",
			}),
			password: z
				.string({
					message: "密码不能为空",
				})
				.min(5, "密码至少5个字符")
				.max(20, "密码最多20个字符")
				.optional(),
			confirmPassword: z
				.string({
					message: "密码不能为空",
				})
				.min(5, "密码至少5个字符")
				.max(20, "密码最多20个字符")
				.optional(),
		})
		.refine(
			(data) => {
				if (!data.password) return true;
				return data.password === data.confirmPassword;
			},
			{
				message: "密码输入不一致。",
			},
		);

	const validatedData = userSchema.parse(formData.value);
	await onSubmit(validatedData);
	updateFormData(undefined);
};

onMounted(() => {
	initFlowbite();
});
</script>
