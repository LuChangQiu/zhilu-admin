<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['大模型管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">大模型管理</h1>
    </div>
    <div class="mb-4">
      <form class="w-full sm:max-w-xs">
        <label for="default-search" class="mb-2 text-sm font-medium text-gray-900 sr-only">Search</label>
        <div class="relative">
          <div class="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
            <svg class="w-4 h-4 text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
              viewBox="0 0 20 20">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
            </svg>
          </div>
          <input type="search" id="default-search" v-model="name"
            class="block w-full p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
            placeholder="模型名称" required />
          <button type="submit"
            class="text-white absolute end-1.5 bottom-1.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-3 py-1.5 sm:px-4 sm:py-2"
            @click.prevent="handleSearch">搜索</button>
        </div>
      </form>
    </div>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
      <MobileCardList :items="llms as Array<components['schemas']['LlmVm']>">
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
          <div class="grid grid-cols-2 gap-2">
            <div>
              <p class="text-xs text-gray-500">模型名称</p>
              <p>{{ item.modelName }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-500">类型</p>
              <p>{{ item.type === 'CHAT' ? '聊天' : '嵌入' }}</p>
            </div>
            <div>
              <p class="text-xs text-gray-500">优先级</p>
              <p>{{ item.priority }}</p>
            </div>
            <div class="col-span-2">
              <p class="text-xs text-gray-500">API Key</p>
              <p class="truncate">{{ item.apiKey }}</p>
            </div>
            <div class="col-span-2">
              <p class="text-xs text-gray-500">URL</p>
              <p class="truncate">{{ item.url }}</p>
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
    <div class="relative overflow-x-auto shadow-md sm:rounded-lg hidden md:block">
      <table class="w-full whitespace-nowrap text-sm text-left rtl:text-right text-gray-500">
        <thead class="text-xs text-gray-700 uppercase bg-gray-50">
          <tr>
            <th scope="col" class="p-4">
              <div class="flex items-center">
                <input id="checkbox-all-search" disabled type="checkbox"
                  class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
                <label for="checkbox-all-search" class="sr-only">checkbox</label>
              </div>
            </th>
            <th scope="col" class="px-6 py-3">名称</th>
            <th scope="col" class="px-6 py-3">模型名称</th>
            <th scope="col" class="px-6 py-3">类型</th>
            <th scope="col" class="px-6 py-3 hidden lg:table-cell">apiKey</th>
            <th scope="col" class="px-6 py-3 hidden lg:table-cell">url</th>
            <th scope="col" class="px-6 py-3">状态</th>
            <th scope="col" class="px-6 py-3">优先级</th>
            <th scope="col" class="px-6 py-3">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="llm in llms" :key="llm.id" class="bg-white border-b border-gray-200 hover:bg-gray-50">
            <td class="w-4 p-4">
              <div class="flex items-center">
                <input :id="'checkbox-table-search-' + llm.id" type="checkbox" disabled
                  class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
                <label :for="'checkbox-table-search-' + llm.id" class="sr-only">checkbox</label>
              </div>
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis font-medium text-gray-900">
              {{ llm.name }}
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis">
              {{ llm.modelName }}
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis">
              {{ llm.type === 'CHAT' ? '聊天' : '嵌入' }}
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis hidden lg:table-cell">
              {{ llm.apiKey }}
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis hidden lg:table-cell">
              {{ llm.url }}
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis">
              <div class="flex items-center">
                <div class="h-2.5 w-2.5 rounded-full me-2" :class="llm.enable ? 'bg-blue-500' : 'bg-red-500'"></div> {{
                llm.enable === true ? "启用" : "禁用" }}
              </div>
            </td>
            <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis">
              {{ llm.priority }}
            </td>
            <td class="px-6 py-4">
              <div class="flex items-center gap-x-2">
                <button @click="handleLlmUpdateClick(llm)"
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
            </td>
          </tr>
        </tbody>
      </table>
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
import TablePagination from "@/components/TablePagination.vue";
import { useLlmQuery } from "@/composables/ai/useLlmQuery";
import { useLlmUpdate } from "@/composables/ai/useLlmUpdate";
import useAlertStore from "@/composables/store/useAlertStore";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, ref } from "vue";
import type { components } from "../api/types/schema";

const llmUpdateModal = ref<ModalInterface>();
const selectedLlm = ref<components["schemas"]["LlmVm"]>();
const name = ref<string>("");

const { llms, fetchLlmConfigs, total } = useLlmQuery();
const { updateLlmConfig } = useLlmUpdate();

const alertStore = useAlertStore();

const handleUpdateModalSubmit = async (llm: components["schemas"]["LlmVm"]) => {
	await updateLlmConfig(llm);
	llmUpdateModal.value?.hide();
	alertStore.showAlert({
		level: "success",
		content: "操作成功",
	});
	await fetchLlmConfigs();
};

const handleSearch = async () => {
	await fetchLlmConfigs();
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchLlmConfigs(page, pageSize);
};

const handleLlmUpdateClick = async (llm: components["schemas"]["LlmVm"]) => {
	selectedLlm.value = llm;
	await nextTick(() => {
		llmUpdateModal.value?.show();
	});
};

onMounted(async () => {
	await fetchLlmConfigs();
	initFlowbite();
	const $llmUpdateModalElement: HTMLElement | null =
		document.querySelector("#llm-update-modal");
	if ($llmUpdateModalElement) {
		llmUpdateModal.value = new Modal($llmUpdateModalElement, {});
	}
});
</script>

<style scoped></style>
