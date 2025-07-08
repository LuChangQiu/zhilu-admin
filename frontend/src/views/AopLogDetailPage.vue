<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <Breadcrumbs :names="['日志管理', '日志详情']" :routes="[Routes.AOPLOGVIEW.fullPath()]" />
    <div class="mb-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">日志详情</h1>
      <div class="mt-2 sm:mt-0">
        <Button variant="secondary" size="sm" @click="navigateBack">
          <template #icon>
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18">
              </path>
            </svg>
          </template>
          返回日志列表
        </Button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="flex justify-center items-center py-10">
      <div class="animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-500"></div>
    </div>

    <!-- 日志详情内容 -->
    <div v-else-if="currentLog" class="bg-white shadow rounded-lg">
      <!-- curl命令 -->
      <div v-if="currentLog.curl" class="p-4 border-b border-gray-200">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-medium text-gray-900">CURL</h2>
          <button @click="copyCurl" class="text-sm font-medium text-blue-600 hover:text-blue-800 flex items-center">
            <svg class="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z">
              </path>
            </svg>
            {{ isCopied ? '已复制' : '复制' }}
          </button>
        </div>
        <div class="bg-gray-50 p-4 rounded-lg">
          <pre class="text-sm text-gray-800 overflow-x-auto whitespace-pre-wrap">{{ currentLog.curl }}</pre>
        </div>
      </div>

      <!-- 基本信息 -->
      <div class="p-4 border-b border-gray-200">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-lg font-medium text-gray-900">基本信息</h2>
          <LogStatusBadge :success="currentLog.success || false" />
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          <div>
            <div class="text-sm font-medium text-gray-500">类名</div>
            <div class="mt-1 text-sm text-gray-900">{{ currentLog.className }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">方法名</div>
            <div class="mt-1 text-sm text-gray-900">{{ currentLog.methodName }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">执行时间</div>
            <div class="mt-1 text-sm text-gray-900">{{ formatExecutionTime(currentLog.executionTime) }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">用户名</div>
            <div class="mt-1 text-sm text-gray-900">{{ currentLog.username || '-' }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">用户ID</div>
            <div class="mt-1 text-sm text-gray-900">{{ currentLog.userId || '-' }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">IP地址</div>
            <div class="mt-1 text-sm text-gray-900">{{ currentLog.ipAddress || '-' }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">创建时间</div>
            <div class="mt-1 text-sm text-gray-900">{{ formatDateTime(currentLog.createTime) }}</div>
          </div>
          <div>
            <div class="text-sm font-medium text-gray-500">User Agent</div>
            <div class="mt-1 text-sm text-gray-900 truncate" :title="currentLog.userAgent">
              {{ currentLog.userAgent || '-' }}
            </div>
          </div>
        </div>
      </div>

      <!-- 方法参数 -->
      <div class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900 mb-4">方法参数</h2>
        <div class="bg-gray-50 p-4 rounded-lg">
          <pre class="text-sm text-gray-800 overflow-x-auto whitespace-pre-wrap">{{ isMethodArgsExpanded ?
        formatJson(currentLog.methodArgs) : methodArgsPreview }}</pre>
          <div v-if="shouldCollapseMethodArgs" class="mt-2 flex justify-end">
            <button @click="isMethodArgsExpanded = !isMethodArgsExpanded"
              class="text-blue-600 hover:text-blue-800 text-sm font-medium">
              {{ isMethodArgsExpanded ? '折叠' : '展开' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 返回值 -->
      <div class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900 mb-4">返回值</h2>
        <div class="bg-gray-50 p-4 rounded-lg">
          <pre class="text-sm text-gray-800 overflow-x-auto whitespace-pre-wrap">{{ isReturnValueExpanded ?
        formatJson(currentLog.returnValue) : returnValuePreview }}</pre>
          <div v-if="shouldCollapseReturnValue" class="mt-2 flex justify-end">
            <button @click="isReturnValueExpanded = !isReturnValueExpanded"
              class="text-blue-600 hover:text-blue-800 text-sm font-medium">
              {{ isReturnValueExpanded ? '折叠' : '展开' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 错误信息 -->
      <div v-if="currentLog.errorMessage" class="p-4 border-b border-gray-200">
        <h2 class="text-lg font-medium text-gray-900 mb-4">错误信息</h2>
        <div class="bg-red-50 p-4 rounded-lg">
          <pre class="text-sm text-red-800 overflow-x-auto whitespace-pre-wrap">{{ isErrorMessageExpanded ?
        currentLog.errorMessage : errorMessagePreview }}</pre>
          <div v-if="shouldCollapseErrorMessage" class="mt-2 flex justify-end">
            <button @click="isErrorMessageExpanded = !isErrorMessageExpanded"
              class="text-blue-600 hover:text-blue-800 text-sm font-medium">
              {{ isErrorMessageExpanded ? '折叠' : '展开' }}
            </button>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="p-4 bg-gray-50 rounded-b-lg flex justify-end">
        <Button variant="danger" @click="handleDeleteClick">
          <template #icon>
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
              </path>
            </svg>
          </template>
          删除日志
        </Button>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-10">
      <div class="text-gray-500 text-lg mb-4">未找到日志详情</div>
      <Button variant="secondary" @click="navigateBack">返回日志列表</Button>
    </div>

    <!-- 删除确认对话框 -->
    <ConfirmationDialog id="delete-log-modal" title="删除日志" content="确定要删除此日志吗？此操作不可撤销。"
      :closeModal="() => deleteLogModal?.hide()" :onSubmit="confirmDelete" />
  </div>
</template>

<script setup lang="ts">
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

import LogStatusBadge from "@/components/common/LogStatusBadge.vue";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import ConfirmationDialog from "@/components/modals/ConfirmationDialog.vue";
import { Button } from "@/components/ui";

import { useAopLogDelete } from "@/composables/aop/useAopLogDelete";
import { useAopLogQuery } from "@/composables/aop/useAopLogQuery";
import { Routes } from "@/router/constants";

// 路由
const route = useRoute();
const router = useRouter();
const logId = Number(route.params.id);

// 获取日志查询和删除的composables
const {
	currentLog,
	loading,
	fetchLogDetail,
	formatDateTime,
	formatExecutionTime,
	formatJson,
} = useAopLogQuery();

const { deleteLog } = useAopLogDelete();

// 模态框引用
const deleteLogModal = ref<ModalInterface>();

// 内容折叠状态
const isMethodArgsExpanded = ref(false);
const isReturnValueExpanded = ref(false);
const isErrorMessageExpanded = ref(false);

// 复制状态
const isCopied = ref(false);

// 计算预览内容
const previewLength = 300;

const methodArgsPreview = computed(() => {
	const content = formatJson(currentLog.value?.methodArgs);
	return content && content.length > previewLength
		? `${content.substring(0, previewLength)}...`
		: content;
});

const returnValuePreview = computed(() => {
	const content = formatJson(currentLog.value?.returnValue);
	return content && content.length > previewLength
		? `${content.substring(0, previewLength)}...`
		: content;
});

const errorMessagePreview = computed(() => {
	const content = currentLog.value?.errorMessage;
	return content && content.length > previewLength
		? `${content.substring(0, previewLength)}...`
		: content;
});

// 判断是否需要折叠
const shouldCollapseMethodArgs = computed(() => {
	const content = formatJson(currentLog.value?.methodArgs);
	return content && content.length > previewLength;
});

const shouldCollapseReturnValue = computed(() => {
	const content = formatJson(currentLog.value?.returnValue);
	return content && content.length > previewLength;
});

const shouldCollapseErrorMessage = computed(() => {
	const content = currentLog.value?.errorMessage;
	return content && content.length > previewLength;
});

// 复制CURL命令
const copyCurl = () => {
	if (currentLog.value?.curl) {
		navigator.clipboard.writeText(currentLog.value.curl);
		isCopied.value = true;
		setTimeout(() => {
			isCopied.value = false;
		}, 2000);
	}
};

// 返回日志列表
const navigateBack = () => {
	router.push(Routes.AOPLOGVIEW.fullPath());
};

// 处理删除点击
const handleDeleteClick = () => {
	deleteLogModal.value?.show();
};

// 确认删除
const confirmDelete = async () => {
	if (currentLog.value?.id) {
		await deleteLog(currentLog.value.id);
		navigateBack();
	}
};

onMounted(async () => {
	// 初始化Flowbite
	initFlowbite();

	// 初始化模态框
	const $deleteModalElement =
		document.querySelector<HTMLElement>("#delete-log-modal");
	if ($deleteModalElement) {
		deleteLogModal.value = new Modal($deleteModalElement);
	}

	// 加载日志详情
	await fetchLogDetail(logId);
});
</script>
