<template>
	<!-- Main modal -->
	<div :id="llm?.id ? 'llm-update-modal' : 'llm-create-modal'" tabindex="-1" aria-hidden="true"
		class="bg-gray-900/50 hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
		<div class="relative p-4 w-full max-w-sm sm:max-w-md max-h-full">
			<!-- Modal content -->
			<div class="relative bg-white rounded-lg shadow-sm">
				<!-- Modal header -->
				<div class="flex items-center justify-between p-4 md:p-5 border-b rounded-t border-gray-200">
					<h3 class="text-base sm:text-lg font-semibold text-gray-900">
						大模型管理
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
					<div class="grid gap-4 mb-4 grid-cols-1 sm:grid-cols-2">
						<div class="col-span-full sm:col-span-2">
							<label for="name" class="block mb-2 text-sm font-medium text-gray-900">名称</label>
							<input type="text" name="名称" id="name" v-model="formData.name"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5"
								required="true">
						</div>
						<div class="col-span-full sm:col-span-2">
							<label for="modelName" class="block mb-2 text-sm font-medium autocomplete text-gray-900">模型名称</label>
							<input type="text" id="modelName" autocomplete="new-password" v-model="formData.modelName"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								required />
						</div>
						<div class="col-span-full sm:col-span-1">
							<label for="type" class="block mb-2 text-sm font-medium text-gray-900">类型</label>
							<select id="type" v-model="formData.type"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5">
								<option :value="'CHAT'">聊天</option>
								<option :value="'EMBEDDING'">嵌入</option>
							</select>
						</div>
						<div class="col-span-full sm:col-span-1">
							<label for="status" class="block mb-2 text-sm font-medium text-gray-900">状态</label>
							<select id="status" v-model="formData.enable"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5">
								<option :value=true>启用</option>
								<option :value=false>禁用</option>
							</select>
						</div>
						<div class="col-span-full sm:col-span-2">
							<label for="apiKey" class="block mb-2 text-sm font-medium autocomplete text-gray-900">apiKey</label>
							<input type="text" id="apiKey" autocomplete="new-password" v-model="formData.apiKey"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								required />
						</div>
						<div class="col-span-full sm:col-span-2">
							<label for="url" class="block mb-2 text-sm font-medium text-gray-900">url</label>
							<input type="text" id="url" autocomplete="new-password" v-model="formData.url"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								required />
						</div>
						<div class="col-span-full sm:col-span-2">
							<label for="priority" class="block mb-2 text-sm font-medium autocomplete text-gray-900">优先级</label>
							<input type="number" id="priority" autocomplete="new-password" v-model="formData.priority"
								class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
								required />
						</div>
					</div>
					<button type="submit" @click.prevent="handleSubmit"
						class="w-full sm:w-auto text-white flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-xs sm:text-sm px-4 py-2 sm:px-5 sm:py-2.5 text-center self-start mt-5">
						保存
					</button>
				</form>
			</div>
		</div>
	</div>

</template>
<script setup lang="ts">
import { initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { z } from "zod";
import type { components } from "../api/types/schema";

const { llm, onSubmit } = defineProps<{
	llm?: components["schemas"]["LlmVm"];
	closeModal: () => void;
	onSubmit: (data: components["schemas"]["LlmVm"]) => Promise<void>;
}>();

const formData = ref();

const updateFormData = (newLlm: typeof llm) => {
	formData.value = {
		...newLlm,
	};
};

watch(() => llm, updateFormData, {
	immediate: true,
});

const handleSubmit = async () => {
	const llmSchema = z.object({
		id: z.number({
			message: "id不能为空",
		}),
		name: z.string({
			message: "名称不能为空",
		}),
		modelName: z.string({
			message: "模型名称不能为空",
		}),
		apiKey: z.string({
			message: "apiKey不能为空",
		}),
		url: z.string({
			message: "url不能为空",
		}),
		enable: z.boolean({
			message: "状态不能为空",
		}),
		priority: z.number({
			message: "优先级必须为数字",
		}),
		type: z.string({
			message: "类型不能为空",
		}),
	});
	const validatedData = llmSchema.parse(formData.value);
	await onSubmit(validatedData);
	updateFormData(undefined);
};

onMounted(() => {
	initFlowbite();
});
</script>
