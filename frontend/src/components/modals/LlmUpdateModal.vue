<template>
	<BaseModal :id="id" title="大模型配置" size="md" :closeModal="closeModal">
		<!-- Modal body -->
		<div class="p-4 md:p-5">
			<div class="grid gap-4 mb-4 grid-cols-1">
				<div class="col-span-full">
					<label for="name" class="block mb-2 text-sm font-medium text-gray-900">名称</label>
					<input type="text" id="name" v-model="formData.name"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
				<div class="col-span-full">
					<label for="modelName" class="block mb-2 text-sm font-medium text-gray-900">模型名称</label>
					<input type="text" id="modelName" v-model="formData.modelName"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
				<div class="col-span-full">
					<label for="url" class="block mb-2 text-sm font-medium text-gray-900">API地址</label>
					<input type="text" id="url" v-model="formData.url"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
						required />
				</div>
				<div class="col-span-full">
					<label for="apiKey" class="block mb-2 text-sm font-medium text-gray-900">秘钥</label>
					<input type="text" id="apiKey" v-model="formData.apiKey"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5" />
				</div>
				<div class="col-span-full">
					<label for="type" class="block mb-2 text-sm font-medium text-gray-900">类型</label>
					<select id="type" v-model="formData.type"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5">
						<option value="CHAT">聊天</option>
						<option value="EMBEDDING">嵌入</option>
					</select>
				</div>
				<div class="col-span-full">
					<label for="priority" class="block mb-2 text-sm font-medium text-gray-900">优先级</label>
					<input type="number" id="priority" v-model="formData.priority"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5" />
				</div>
				<div class="col-span-full">
					<label for="enable" class="block mb-2 text-sm font-medium text-gray-900">状态</label>
					<select id="enable" v-model="formData.enable"
						class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5">
						<option :value="true">启用</option>
						<option :value="false">禁用</option>
					</select>
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
import { initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { z } from "zod";
import BaseModal from "./BaseModal.vue";

const { llm, onSubmit, id } = defineProps<{
	llm?: components["schemas"]["LlmVm"];
	closeModal: () => void;
	onSubmit: (data: components["schemas"]["LlmVm"]) => Promise<void>;
	id: string;
}>();

// 初始化默认值，避免undefined错误
const formData = ref<components["schemas"]["LlmVm"]>({
	id: 0,
	name: "",
	modelName: "",
	apiKey: "",
	url: "",
	enable: true,
	priority: 0,
	type: "CHAT",
});

const updateFormData = (newLlm: typeof llm) => {
	if (!newLlm) {
		formData.value = {
			id: 0,
			name: "",
			modelName: "",
			apiKey: "",
			url: "",
			enable: true,
			priority: 0,
			type: "CHAT",
		};
		return;
	}

	formData.value = {
		id: newLlm.id ?? 0,
		name: newLlm.name ?? "",
		modelName: newLlm.modelName ?? "",
		apiKey: newLlm.apiKey ?? "",
		url: newLlm.url ?? "",
		enable: newLlm.enable ?? true,
		priority: newLlm.priority ?? 0,
		type: newLlm.type ?? "CHAT",
	};
};

watch(() => llm, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const configSchema = z.object({
		id: z.number(),
		name: z
			.string({
				message: "名称不能为空",
			})
			.min(1, "名称至少1个字符")
			.max(15, "名称最多15个字符"),
		modelName: z
			.string({
				message: "模型名称不能为空",
			})
			.min(1, "模型名称至少1个字符"),
		apiKey: z.string(),
		url: z
			.string({
				message: "API地址不能为空",
			})
			.min(1, "API地址至少1个字符"),
		enable: z.boolean(),
		priority: z.number(),
		type: z.string(),
	});

	const validatedData = configSchema.parse(formData.value);
	await onSubmit(validatedData);
	updateFormData(undefined);
};
</script>
