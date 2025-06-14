<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['大模型管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">大模型管理</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
    </TableFilterForm>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
      <MobileCardList :items="llms">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #status="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.enable ? 'bg-blue-500' : 'bg-red-500'"></div>
            <span class="text-sm">{{ item.enable === true ? "启用" : "禁用" }}</span>
          </div>
        </template>
        <template #content="{ item }">
          <div class="grid grid-cols-2 gap-3">
            <div>
              <p class="text-xs font-medium text-gray-600">模型名称</p>
              <p class="text-sm text-gray-900 mt-0.5">{{ item.modelName }}</p>
            </div>
            <div>
              <p class="text-xs font-medium text-gray-600">类型</p>
              <p class="text-sm text-gray-900 mt-0.5">{{ item.type === 'CHAT' ? '聊天' : '嵌入' }}</p>
            </div>
            <div>
              <p class="text-xs font-medium text-gray-600">优先级</p>
              <p class="text-sm text-gray-900 mt-0.5">{{ item.priority }}</p>
            </div>
            <div class="col-span-2">
              <p class="text-xs font-medium text-gray-600">API Key</p>
              <p class="text-sm text-gray-900 mt-0.5 truncate">{{ item.apiKey }}</p>
            </div>
            <div class="col-span-2">
              <p class="text-xs font-medium text-gray-600">URL</p>
              <p class="text-sm text-gray-900 mt-0.5 truncate">{{ item.url }}</p>
            </div>
          </div>
        </template>
        <template #actions="{ item }">
          <button @click="handleLlmUpdateClick(item)"
            class="flex items-center justify-center gap-x-1 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-xs px-3 py-1.5"
            type="button">
            <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
              <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
              <path fill-rule="evenodd"
                d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                clip-rule="evenodd"></path>
            </svg>
            <span>编辑</span>
          </button>
        </template>
      </MobileCardList>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="llms || []" :columns="columns">
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #modelName="{ item }">
          {{ item.modelName }}
        </template>
        <template #type="{ item }">
          {{ item.type === 'CHAT' ? '聊天' : '嵌入' }}
        </template>
        <template #apiKey="{ item }">
          {{ item.apiKey }}
        </template>
        <template #url="{ item }">
          {{ item.url }}
        </template>
        <template #status="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.enable ? 'bg-blue-500' : 'bg-red-500'"></div>
            {{ item.enable === true ? "启用" : "禁用" }}
          </div>
        </template>
        <template #priority="{ item }">
          {{ item.priority }}
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <button @click="handleLlmUpdateClick(item)"
              class="flex items-center justify-center whitespace-nowrap gap-x-1 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2.5"
              type="button">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                <path fill-rule="evenodd"
                  d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                  clip-rule="evenodd"></path>
              </svg>
              <span>编辑</span>
            </button>
          </div>
        </template>
      </TableFormLayout>
    </div>

    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <LlmUpdateModal :llm="selectedLlm" :id="'llm-update-modal'" :closeModal="() => {
    llmUpdateModal!.hide();
  }" :onSubmit="handleUpdateModalSubmit"></LlmUpdateModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import LlmUpdateModal from "@/components/LlmUpdateModal.vue";
import MobileCardList from "@/components/MobileCardList.vue";
import TableFilterForm from "@/components/TableFilterForm.vue";
import type { FilterItem } from "@/components/TableFilterForm.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import { useLlmQuery } from "@/composables/ai/useLlmQuery";
import { useLlmUpdate } from "@/composables/ai/useLlmUpdate";
import useAlertStore from "@/composables/store/useAlertStore";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, reactive, ref } from "vue";
import type { components } from "../api/types/schema";

// 定义筛选配置
const filterConfig: FilterItem[] = [
	{
		type: "input",
		name: "modelName",
		placeholder: "模型名称",
	},
];

// 筛选值
const filterValues = reactive<{
	modelName: string;
}>({
	modelName: "",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.modelName !== undefined) {
		filterValues.modelName = values.modelName as string;
	}
};

const llmUpdateModal = ref<ModalInterface>();
const selectedLlm = ref<components["schemas"]["LlmVm"]>();

const { llms, fetchLlmConfigs, total } = useLlmQuery();
const { updateLlmConfig } = useLlmUpdate();

const alertStore = useAlertStore();

// 定义表格列配置
const columns = [
	{ title: "名称", field: "name" },
	{ title: "模型名称", field: "modelName" },
	{ title: "类型", field: "type" },
	{ title: "apiKey", field: "apiKey", class: "hidden lg:table-cell" },
	{ title: "url", field: "url", class: "hidden lg:table-cell" },
	{ title: "状态", field: "status" },
	{ title: "优先级", field: "priority" },
	{ title: "操作", field: "actions" },
];

const handleUpdateModalSubmit = async (llm: components["schemas"]["LlmVm"]) => {
	await updateLlmConfig(llm);
	llmUpdateModal.value?.hide();
	alertStore.showAlert({
		level: "success",
		content: "操作成功",
	});
	await fetchLlmConfigs(1, 10, filterValues.modelName);
};

const handleSearch = async () => {
	await fetchLlmConfigs(1, 10, filterValues.modelName);
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchLlmConfigs(page, pageSize, filterValues.modelName);
};

const handleLlmUpdateClick = async (llm: components["schemas"]["LlmVm"]) => {
	selectedLlm.value = llm;
	await nextTick(() => {
		llmUpdateModal.value?.show();
	});
};

onMounted(async () => {
	await fetchLlmConfigs(1, 10, filterValues.modelName);
	initFlowbite();
	const $llmUpdateModalElement: HTMLElement | null =
		document.querySelector("#llm-update-modal");
	if ($llmUpdateModalElement) {
		llmUpdateModal.value = new Modal($llmUpdateModalElement, {});
	}
});
</script>

<style scoped></style>
