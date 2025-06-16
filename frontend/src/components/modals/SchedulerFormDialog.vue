<template>
	<BaseDialog :id="id" title="定时任务配置" size="md" :closeModal="closeModal">
		<!-- Modal body -->
		<div class="p-4 md:p-5">
			<div class="grid gap-4 mb-4 grid-cols-1">
				<div class="col-span-full">
					<label for="cronExpression" class="block mb-2 text-sm font-medium text-gray-900">CRON表达式</label>
					<input type="text" id="cronExpression" v-model="formData.cronExpression"
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
import { ref, watch } from "vue";
import { z } from "zod";
import BaseDialog from "./BaseDialog.vue";

const { job, closeModal, onSubmit, id } = defineProps<{
	job?: components["schemas"]["JobTriggerDto"];
	closeModal: () => void;
	onSubmit: (cronExpression: string) => Promise<void>;
	id: string;
}>();

const formData = ref({
	cronExpression: "",
});

const updateFormData = (newJob: typeof job) => {
	if (!newJob) {
		formData.value = {
			cronExpression: "",
		};
		return;
	}

	formData.value = {
		cronExpression: newJob.cronExpression || "",
	};
};

watch(() => job, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		cronExpression: z
			.string({
				message: "CRON表达式不能为空",
			})
			.min(1, "CRON表达式不能为空"),
	});

	const validatedData = schema.parse(formData.value);
	await onSubmit(validatedData.cronExpression);
};
</script>
