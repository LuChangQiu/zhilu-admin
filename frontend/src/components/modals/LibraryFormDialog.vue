<template>
	<BaseDialog :id="id" title="知识库管理" size="md" :closeModal="closeModal">
		<!-- Modal body -->
		<div class="p-4 md:p-5">
			<div class="grid gap-4 mb-4 grid-cols-1">
				<div class="col-span-full">
					<label for="name" class="block mb-2 text-sm font-medium text-gray-900">知识库名称</label>
					<input type="text" id="name" v-model="formData.name"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
				<div class="col-span-full">
					<label for="description" class="block mb-2 text-sm font-medium text-gray-900">知识库描述</label>
					<textarea id="description" v-model="formData.description" rows="3"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"></textarea>
				</div>
			</div>
			<button type="submit" @click="handleSubmit"
				class="w-auto text-sm px-4 py-2 text-white flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-center self-start mt-5">
				保存
			</button>
		</div>
	</BaseDialog>
</template>

<script setup lang="ts">
import useAlertStore from "@/composables/store/useAlertStore";
import type { Library } from "@/types/KnowledgeTypes";
import type { LibraryUpsertModel } from "@/types/KnowledgeTypes";
import { ref, watch } from "vue";
import { z } from "zod";
import BaseDialog from "./BaseDialog.vue";

const alertStore = useAlertStore();
const { library, closeModal, onSubmit, id } = defineProps<{
	library?: Library;
	closeModal: () => void;
	onSubmit: (data: LibraryUpsertModel) => Promise<void>;
	id: string;
}>();

const formData = ref<LibraryUpsertModel>({
	name: "",
	description: "",
});

const updateFormData = (newLibrary: typeof library) => {
	if (!newLibrary) {
		formData.value = {
			name: "",
			description: "",
		};
		return;
	}

	formData.value = {
		id: newLibrary.id,
		name: newLibrary.name ?? "",
		description: newLibrary.description ?? "",
	};
};

watch(() => library, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		name: z.string().min(1, "知识库名称不能为空"),
		description: z.string().optional(),
	});

	try {
		const validatedData = schema.parse(formData.value);
		await onSubmit({
			...formData.value,
			name: validatedData.name,
			description: validatedData.description,
		});
	} catch (error) {
		if (error instanceof z.ZodError) {
			alertStore.showAlert({
				level: "error",
				content: error.errors[0].message,
			});
		} else {
			console.error("表单提交错误:", error);
			alertStore.showAlert({
				level: "error",
				content: "表单提交失败，请重试",
			});
		}
	}
};
</script> 
