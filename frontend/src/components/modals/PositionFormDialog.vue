<template>
	<BaseDialog :id="id" title="岗位管理" size="md" :closeModal="closeModal">
		<!-- Modal body -->
		<div class="p-4 md:p-5">
			<div class="grid gap-4 mb-4 grid-cols-1">
				<div class="col-span-full">
					<label for="name" class="block mb-2 text-sm font-medium text-gray-900">岗位名称</label>
					<input type="text" id="name" v-model="formData.name"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
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
import type { components } from "@/api/types/schema";
import useAlertStore from "@/composables/store/useAlertStore";
import type { PositionUpsertModel } from "@/types/PositionTypes";
import { ref, watch } from "vue";
import { z } from "zod";
import BaseDialog from "./BaseDialog.vue";

const alertStore = useAlertStore();
const { position, closeModal, onSubmit, id } = defineProps<{
	position?: components["schemas"]["Position"];
	closeModal: () => void;
	onSubmit: (data: PositionUpsertModel) => Promise<void>;
	id: string;
}>();

const formData = ref<PositionUpsertModel>({
	name: "",
});

const updateFormData = (newPosition: typeof position) => {
	if (!newPosition) {
		formData.value = {
			name: "",
		};
		return;
	}

	formData.value = {
		id: newPosition.id,
		name: newPosition.name ?? "",
	};
};

watch(() => position, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		id: z.number().optional(),
		name: z
			.string({
				message: "岗位名称不能为空",
			})
			.min(2, "岗位名称至少2个字符")
			.max(15, "岗位名称最多15个字符"),
	});

	try {
		const validatedData = schema.parse(formData.value);
		await onSubmit(validatedData);
		updateFormData(undefined);
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
