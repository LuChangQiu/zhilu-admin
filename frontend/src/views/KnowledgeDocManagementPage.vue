<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <Breadcrumbs :names="['知识库管理', '文档管理']" :routes="[Routes.KNOWLEDGEVIEW.fullPath()]" />
    <div class="mb-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">{{ currentLibrary?.name || '知识库' }} - 文档管理</h1>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="doc in docs" :key="doc.id"
        class="bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-shadow">
        <div class="p-4">
          <div class="flex justify-between items-start">
            <div>
              <h5 class="text-xl font-semibold tracking-tight text-gray-900 mb-1 truncate">{{ doc.name }}</h5>
              <div class="flex items-center mb-2">
                <span :class="`inline-flex items-center px-2 py-1 text-xs font-medium rounded-full ${
                    doc.status === DocStatus.SUCCESS ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
                  } mr-2`">
                  {{ doc.status === DocStatus.SUCCESS ? '解析完成' : '解析中' }}
                </span>
                <span :class="`inline-flex items-center px-2 py-1 text-xs font-medium rounded-full ${
                    doc.enable ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800'
                  }`">
                  {{ doc.enable ? '已启用' : '已禁用' }}
                </span>
              </div>
            </div>
            <div class="flex space-x-2">
              <label class="inline-flex items-center mb-5"
                :class="!doc.enable ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'">
                <input type="checkbox" class="sr-only peer" :checked="doc.enable" @change="handleToggleDocStatus(doc)">
                <div
                  class="relative w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer dark:bg-gray-700 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:w-5 after:h-5 after:transition-all dark:border-gray-600 peer-checked:bg-blue-600">
                </div>
              </label>

            </div>
          </div>
          <!-- <div class="text-sm text-gray-600 mb-3">
            <div class="truncate">{{ doc.path || '无' }}</div>
          </div> -->
          <div class="flex justify-between items-center">
            <span class="text-xs text-gray-500">
              上传时间: {{ formatDate(doc.createTime) }}
            </span>
            <div class="flex space-x-2">
              <button @click="navigateToDocSegments(doc)" :class="
                ['text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:ring-blue-300 font-medium rounded-lg text-xs px-3 py-content',
                doc.status !== DocStatus.SUCCESS ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer']"
                :disabled="doc.status !== DocStatus.SUCCESS">
                查看内容
              </button>
              <button @click="handleDeleteDoc(doc)"
                class="text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:ring-red-300 font-medium rounded-lg text-xs px-3 py-1.5">
                删除
              </button>
            </div>

          </div>
        </div>
      </div>
    </div>
    <div class="flex flex-col items-center justify-center py-10">
      <div v-if="docs.length === 0" class="text-gray-500 text-lg mb-4">暂无文档</div>
      <div>
        <input ref="fileInputRef" class="hidden" id="doc_file_input" type="file" @change="handleFileChange">
        <TableButton variant="primary" size="md" @click="triggerFileInput">
          <template #icon>
            <PlusIcon class="w-4 h-4" />
          </template>
          上传文档
        </TableButton>
      </div>
    </div>

  </div>

  <!-- 删除确认对话框 -->
  <ConfirmationDialog :id="'doc-delete-modal'" :title="`确定删除文档 '${selectedDoc?.name || ''}' 吗？`"
    content="删除后将无法恢复，且其中的所有分段内容也将被删除。" :closeModal="() => {
      docDeleteModal?.hide();
    }" :onSubmit="handleDocDeleteSubmit" />

</template>

<script setup lang="ts">
import dayjs from "dayjs";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, ref, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";

import { PlusIcon } from "@/components/icons";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import ConfirmationDialog from "@/components/modals/ConfirmationDialog.vue";
import TableButton from "@/components/tables/TableButton.vue";

import { useKnowledgeQuery } from "@/composables/knowledge/useKnowledgeQuery";
import { useKnowledgeUpsert } from "@/composables/knowledge/useKnowledgeUpsert";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";

import type { Library, LibraryDoc } from "@/types/KnowledgeTypes";
import { DocStatus } from "@/types/KnowledgeTypes";

// 路由参数
const route = useRoute();
const router = useRouter();
const libraryId = ref<number>(
	Number.parseInt(route.params.libraryId as string, 10),
);

// 文件输入引用
const fileInputRef = ref<HTMLInputElement | null>(null);

// 获取知识库信息
const { libraries, fetchLibraries } = useKnowledgeQuery();
const currentLibrary = ref<Library | undefined>();

// 获取文档列表
const { docs, fetchLibraryDocs } = useKnowledgeQuery();
const { uploadDoc, deleteDoc, updateDoc } = useKnowledgeUpsert();

// 模态框引用
const docDeleteModal = ref<ModalInterface>();

// 选中的文档
const selectedDoc = ref<LibraryDoc | undefined>();

// 提示store
const alertStore = useAlertStore();

// 格式化日期
const formatDate = (dateString?: string) => {
	if (!dateString) return "未知";
	return dayjs(dateString).format("YYYY-MM-DD HH:mm");
};

// 触发文件选择
const triggerFileInput = () => {
	fileInputRef.value?.click();
};

// 处理文件选择
const handleFileChange = async (event: Event) => {
	const file = (event.target as HTMLInputElement).files?.[0];

	if (!file) return;

	try {
		await uploadDoc(libraryId.value, file);
		alertStore.showAlert({
			level: "success",
			content: "文档上传成功",
		});

		// 清空文件选择框
		(event.target as HTMLInputElement).value = "";

		// 刷新文档列表
		await fetchLibraryDocs({ libraryId: libraryId.value });
	} finally {
		// 清空文件选择框
		(event.target as HTMLInputElement).value = "";
	}
};

// 处理删除文档
const handleDeleteDoc = (doc: LibraryDoc) => {
	selectedDoc.value = doc;
	docDeleteModal.value?.show();
};

// 处理文档删除确认
const handleDocDeleteSubmit = async () => {
	if (!selectedDoc.value?.id) return;

	await deleteDoc(selectedDoc.value.id);
	alertStore.showAlert({
		level: "success",
		content: "文档删除成功",
	});
	docDeleteModal.value?.hide();
	await fetchLibraryDocs({ libraryId: libraryId.value });
};

// 处理切换文档状态
const handleToggleDocStatus = async (doc: LibraryDoc) => {
	try {
		doc.enable = !doc.enable;
		await updateDoc({
			id: doc.id!,
			libId: doc.libId!,
			enable: doc.enable,
		});

		alertStore.showAlert({
			level: "success",
			content: "操作成功",
		});
	} finally {
		await fetchLibraryDocs({ libraryId: libraryId.value });
	}
};

// 导航到文档分段页面
const navigateToDocSegments = (doc: LibraryDoc) => {
	router.push(
		Routes.KNOWLEDGESEGMENTSVIEW.withParams({
			libraryId: libraryId.value,
			docId: doc.id!,
		}),
	);
};

// 初始化
onMounted(async () => {
	initFlowbite();

	// 初始化模态框
	const docDeleteElement = document.getElementById("doc-delete-modal");
	if (docDeleteElement) {
		docDeleteModal.value = new Modal(docDeleteElement);
	}

	// 获取知识库列表和文档列表
	await fetchLibraries();
	await fetchLibraryDocs({ libraryId: libraryId.value });
});

// 监听知识库列表变化，找到当前知识库
watchEffect(() => {
	if (libraries.value && libraries.value.length > 0) {
		currentLibrary.value = libraries.value.find(
			(lib) => lib.id === libraryId.value,
		);
	}
});
</script>
