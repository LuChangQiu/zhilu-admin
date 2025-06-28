<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <Breadcrumbs :names="['知识库管理']" />
    <div class="mb-4">
      <h1 class="text-2xl font-semibold text-gray-900">知识库管理</h1>
    </div>

    <!-- 知识库列表 -->
    <div v-if="libraries.length > 0" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <KnowledgeLibraryCard v-for="library in libraries" :key="library.id" :library="library">
        <template #actions-top>
          <button @click="handleEditLibrary(library)" class="text-gray-500 hover:text-blue-700 focus:outline-none">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z">
              </path>
            </svg>
          </button>
          <button @click="handleDeleteLibrary(library)" class="text-gray-500 hover:text-red-700 focus:outline-none">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
              </path>
            </svg>
          </button>
        </template>
        <template #actions-bottom>
          <button @click="navigateToLibraryDocs(library)"
            class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-xs px-3 py-1.5">
            查看知识库
          </button>
        </template>
      </KnowledgeLibraryCard>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-10">
      <div class="text-gray-500 text-lg mb-4">暂无知识库</div>
      <div>
        <button @click="handleCreateLibraryClick"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 focus:outline-none">
          创建知识库
        </button>
      </div>
    </div>
  </div>

  <!-- 知识库表单对话框 -->
  <LibraryFormDialog :id="'library-form-modal'" :library="selectedLibrary" :closeModal="() => {
    libraryFormModal?.hide();
  }" :onSubmit="handleLibraryFormSubmit" />

  <!-- 删除确认对话框 -->
  <ConfirmationDialog :id="'library-delete-modal'" :title="`确定删除知识库 '${selectedLibrary?.name || ''}' 吗？`"
    content="删除后将无法恢复，且其中的所有文档也将被删除。" :closeModal="() => {
      libraryDeleteModal?.hide();
    }" :onSubmit="handleLibraryDeleteSubmit" />
</template>

<script setup lang="ts">
import dayjs from "dayjs";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import { KnowledgeLibraryCard } from "@/components/common/knowledge";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import ConfirmationDialog from "@/components/modals/ConfirmationDialog.vue";
import LibraryFormDialog from "@/components/modals/LibraryFormDialog.vue";

import { useKnowledgeQuery } from "@/composables/knowledge/useKnowledgeQuery";
import { useKnowledgeUpsert } from "@/composables/knowledge/useKnowledgeUpsert";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";

import type { Library, LibraryUpsertModel } from "@/types/KnowledgeTypes";

// 获取知识库列表
const { libraries, fetchLibraries } = useKnowledgeQuery();
// 知识库操作
const { upsertLibrary, deleteLibrary } = useKnowledgeUpsert();

// 模态框引用
const libraryFormModal = ref<ModalInterface>();
const libraryDeleteModal = ref<ModalInterface>();

// 选中的知识库
const selectedLibrary = ref<Library | undefined>();

// 路由
const router = useRouter();
const alertStore = useAlertStore();

// 格式化日期
const formatDate = (dateString?: string) => {
	if (!dateString) return "未知";
	return dayjs(dateString).format("YYYY-MM-DD HH:mm");
};

// 处理创建知识库点击
const handleCreateLibraryClick = () => {
	selectedLibrary.value = undefined;
	libraryFormModal.value?.show();
};

// 处理编辑知识库
const handleEditLibrary = (library: Library) => {
	selectedLibrary.value = library;
	libraryFormModal.value?.show();
};

// 处理删除知识库
const handleDeleteLibrary = (library: Library) => {
	selectedLibrary.value = library;
	libraryDeleteModal.value?.show();
};

// 处理知识库表单提交
const handleLibraryFormSubmit = async (data: LibraryUpsertModel) => {
	await upsertLibrary(data);
	alertStore.showAlert({
		level: "success",
		content: data.id ? "知识库更新成功" : "知识库创建成功",
	});
	libraryFormModal.value?.hide();
	await fetchLibraries();
};

// 处理知识库删除确认
const handleLibraryDeleteSubmit = async () => {
	if (!selectedLibrary.value?.id) return;

	await deleteLibrary(selectedLibrary.value.id);
	alertStore.showAlert({
		level: "success",
		content: "知识库删除成功",
	});
	libraryDeleteModal.value?.hide();
	await fetchLibraries();
};

// 导航到知识库文档页面
const navigateToLibraryDocs = (library: Library) => {
	if (!library.id) return;
	router.push(Routes.KNOWLEDGEDOCVIEW.withParams({ libraryId: library.id }));
};

// 初始化
onMounted(async () => {
	initFlowbite();

	// 初始化模态框
	const libraryFormElement = document.getElementById("library-form-modal");
	if (libraryFormElement) {
		libraryFormModal.value = new Modal(libraryFormElement);
	}

	const libraryDeleteElement = document.getElementById("library-delete-modal");
	if (libraryDeleteElement) {
		libraryDeleteModal.value = new Modal(libraryDeleteElement);
	}

	// 获取知识库列表
	await fetchLibraries();
});
</script>
