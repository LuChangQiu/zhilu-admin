<template>
	<BaseModal :id="id" title="权限管理" size="md" :closeModal="closeModal">
		<!-- Modal body -->
		<div class="p-4 md:p-5">
			<div class="grid gap-4 mb-4 grid-cols-1">
				<div class="col-span-full">
					<label for="name" class="block mb-2 text-sm font-medium text-gray-900">权限名称</label>
					<input type="text" id="name" v-model="formData.name"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
				<div class="col-span-full">
					<label for="code" class="block mb-2 text-sm font-medium text-gray-900">权限代码</label>
					<input type="text" id="code" v-model="formData.code"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
			</div>
			<button type="submit" @click="handleSubmit"
				class="w-auto text-sm px-4 py-2 text-white flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-center self-start mt-5">
				保存
			</button>
		</div>
	</BaseModal>
</template>

<script setup lang="ts">
import type { components } from "@/api/types/schema";
import useAlertStore from "@/composables/store/useAlertStore";
import type { PermissionUpsertModel } from "@/types/permission";
import { ref, watch } from "vue";
import { z } from "zod";
import BaseModal from "./BaseModal.vue";

const alertStore = useAlertStore();
const { permission, onSubmit, closeModal, id } = defineProps<{
	permission?: components["schemas"]["PermissionRespDto"];
	onSubmit: (data: PermissionUpsertModel) => Promise<void>;
	closeModal: () => void;
	id: string;
}>();

const formData = ref<PermissionUpsertModel>({
	name: "",
	code: "",
});

const updateFormData = (newPermission: typeof permission) => {
	if (!newPermission) {
		formData.value = {
			name: "",
			code: "",
		};
		return;
	}

	formData.value = {
		id: newPermission.id,
		name: newPermission.name ?? "",
		code: newPermission.code ?? "",
	};
};

watch(() => permission, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		id: z.number().optional(),
		name: z
			.string({
				message: "权限名称不能为空",
			})
			.min(2, "权限名称至少2个字符")
			.max(15, "权限名称最多15个字符"),
		code: z
			.string({
				message: "权限代码不能为空",
			})
			.min(2, "权限代码至少2个字符")
			.max(50, "权限代码最多50个字符"),
	});

	try {
		const validatedData = schema.parse(formData.value);
		await onSubmit(validatedData);
	} catch (error) {
		if (error instanceof z.ZodError) {
			alertStore.showAlert({
				content: error.errors[0].message,
				level: "error",
			});
		}
	}
};
</script>
