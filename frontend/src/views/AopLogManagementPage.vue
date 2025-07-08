<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['日志管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">日志管理</h1>
    </div>

    <!-- 筛选表单 -->
    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <div class="flex flex-wrap gap-2">
          <TableButton variant="danger" @click="handleBatchDeleteClick()" :disabled="selectedLogs.length === 0"
            class="w-full sm:w-auto">
            <template #icon>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
                </path>
              </svg>
            </template>
            批量删除
          </TableButton>
          <TableButton variant="info" @click="handleClearBeforeClick()" class="w-full sm:w-auto">
            <template #icon>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                xmlns="http://www.w3.org/2000/svg">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path>
              </svg>
            </template>
            清理历史日志
          </TableButton>
        </div>
      </template>
    </TableFilterForm>

    <!-- PC端表格 -->
    <div class="hidden md:block mt-4">
      <TableFormLayout :items="logs" :columns="columns" :loading="loading" :hasCheckbox="true" v-model="selectedLogs"
        @all-checked-change="handleSelectAll">
        <template #className="{ item }">
          <div class="truncate max-w-xs" :title="item.className">{{ item.className }}</div>
        </template>
        <template #methodName="{ item }">
          {{ item.methodName }}
        </template>
        <template #executionTime="{ item }">
          {{ formatExecutionTime(item.executionTime) }}
        </template>
        <template #success="{ item }">
          <LogStatusBadge :success="item.success || false" />
        </template>
        <template #username="{ item }">
          {{ item.username || '-' }}
        </template>
        <template #createTime="{ item }">
          {{ formatDateTime(item.createTime) }}
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <TableButton variant="primary" @click="handleViewDetail(item)">
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z">
                  </path>
                </svg>
              </template>
              查看
            </TableButton>
            <TableButton variant="danger" @click="handleDeleteClick(item)">
              <template #icon>
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                  xmlns="http://www.w3.org/2000/svg">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
                  </path>
                </svg>
              </template>
              删除
            </TableButton>
          </div>
        </template>
      </TableFormLayout>
    </div>

    <!-- 移动端卡片列表 -->
    <div class="md:hidden mt-4">
      <MobileCardListWithCheckbox :items="logs" v-model="selectedLogs" idField="id">
        <template #item="{ item }">
          <div class="flex flex-col gap-2">
            <div class="flex justify-between items-start">
              <div class="font-medium text-gray-900 truncate max-w-[200px]" :title="item.className">
                {{ item.className }}
              </div>
              <LogStatusBadge :success="item.success || false" />
            </div>
            <div class="grid grid-cols-2 gap-2 text-sm">
              <div>
                <div class="text-gray-500">方法</div>
                <div>{{ item.methodName }}</div>
              </div>
              <div>
                <div class="text-gray-500">执行时间</div>
                <div>{{ formatExecutionTime(item.executionTime) }}</div>
              </div>
              <div>
                <div class="text-gray-500">用户</div>
                <div>{{ item.username || '-' }}</div>
              </div>
              <div>
                <div class="text-gray-500">创建时间</div>
                <div>{{ formatDateTime(item.createTime) }}</div>
              </div>
            </div>
            <div class="flex justify-end gap-2 mt-2">
              <TableButton variant="primary" size="xs" @click="handleViewDetail(item)">
                <template #icon>
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z">
                    </path>
                  </svg>
                </template>
                查看
              </TableButton>
              <TableButton variant="danger" size="xs" @click="handleDeleteClick(item)">
                <template #icon>
                  <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    xmlns="http://www.w3.org/2000/svg">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16">
                    </path>
                  </svg>
                </template>
                删除
              </TableButton>
            </div>
          </div>
        </template>
      </MobileCardListWithCheckbox>
    </div>

    <!-- 分页 -->
    <div class="mt-4">
      <TablePagination :current-page="currentPage" :total-pages="Math.ceil(total / pageSize)" :total="total"
        @change-page="handlePageChange" />
    </div>

    <!-- 删除确认对话框 -->
    <ConfirmationDialog id="delete-log-modal" title="删除日志" content="确定要删除选中的日志吗？此操作不可撤销。"
      :closeModal="() => deleteLogModal?.hide()" :onSubmit="confirmDelete" />

    <!-- 批量删除确认对话框 -->
    <ConfirmationDialog id="batch-delete-logs-modal" title="批量删除日志"
      :content="`确定要删除选中的 ${selectedLogs.length} 条日志吗？此操作不可撤销。`" :closeModal="() => batchDeleteLogsModal?.hide()"
      :onSubmit="confirmBatchDelete" />

    <!-- 清理历史日志对话框 -->
    <BaseDialog id="clear-before-modal" title="清理历史日志" :closeModal="closeClearBeforeModal">
      <div class="p-4">
        <div class="mb-4">
          <label for="clearBeforeDate" class="block mb-2 text-sm font-medium text-gray-900">
            删除此日期之前的所有日志
          </label>
          <div class="datepicker-container">
            <VueDatePicker v-model="clearBeforeDate" locale="zh-CN" format="yyyy-MM-dd" :enable-time-picker="false"
              :auto-apply="true" class="filter-datepicker" teleport="body" />
          </div>
        </div>
        <div class="flex justify-end gap-2">
          <button type="button" @click="closeClearBeforeModal"
            class="text-gray-500 bg-white hover:bg-gray-100 focus:ring-4 focus:outline-none focus:ring-gray-200 rounded-lg border border-gray-200 text-sm font-medium px-5 py-2.5 hover:text-gray-900">
            取消
          </button>
          <button type="button" @click="confirmClearBefore"
            class="text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5">
            确认清理
          </button>
        </div>
      </div>
    </BaseDialog>
  </div>
</template>

<script setup lang="ts">
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";

import LogStatusBadge from "@/components/common/LogStatusBadge.vue";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import BaseDialog from "@/components/modals/BaseDialog.vue";
import ConfirmationDialog from "@/components/modals/ConfirmationDialog.vue";
import MobileCardListWithCheckbox from "@/components/tables/MobileCardListWithCheckbox.vue";
import TableButton from "@/components/tables/TableButton.vue";
import TableFilterForm from "@/components/tables/TableFilterForm.vue";
import type { FilterItem } from "@/components/tables/TableFilterForm.vue";
import TableFormLayout from "@/components/tables/TableFormLayout.vue";
import TablePagination from "@/components/tables/TablePagination.vue";

import type { components } from "@/api/types/schema";
import { useAopLogDelete } from "@/composables/aop/useAopLogDelete";
import { useAopLogQuery } from "@/composables/aop/useAopLogQuery";
import { useErrorHandling } from "@/composables/common/useErrorHandling";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import { Routes } from "@/router/constants";
import type { AopLogQueryParams } from "@/types/AlertTypes";

// 路由
const router = useRouter();

// 获取错误处理
const { handleError } = useErrorHandling();

// 获取日志查询和删除的composables
const {
  logs,
  currentPage,
  pageSize,
  total,
  loading,
  fetchLogs,
  formatDateTime,
  formatExecutionTime,
  handleSort,
  getSortField,
} = useAopLogQuery();

const { deleteLog, batchDeleteLogs, deleteLogsBefore } = useAopLogDelete();

// 选中的日志
const selectedLogs = ref<number[]>([]);

// 清理历史日志的日期
const clearBeforeDate = ref(new Date());

// 模态框引用
const deleteLogModal = ref<ModalInterface>();
const batchDeleteLogsModal = ref<ModalInterface>();
const clearBeforeModal = ref<ModalInterface>();

// 当前要删除的日志
const currentLogToDelete = ref<components["schemas"]["AopLogRespDto"]>();

// 筛选配置
const filterConfig = [
  {
    type: "input",
    name: "className",
    placeholder: "类名",
  },
  {
    type: "input",
    name: "methodName",
    placeholder: "方法名",
  },
  {
    type: "select",
    name: "success",
    placeholder: "状态",
    options: [
      { value: "", label: "全部" },
      { value: "true", label: "成功" },
      { value: "false", label: "失败" },
    ],
  },
  {
    type: "input",
    name: "username",
    placeholder: "用户名",
  },
  {
    type: "date-range",
    name: "dateRange",
  },
] as FilterItem[];

// 筛选值
const filterValues = reactive<{
  className: string;
  methodName: string;
  success: string;
  username: string;
  dateRange: Date[];
}>({
  className: "",
  methodName: "",
  success: "",
  username: "",
  dateRange: [],
});

// 表格列配置
const columns = [
  { title: "类名", field: "className", sortable: true },
  { title: "方法名", field: "methodName", sortable: true },
  { title: "执行时间", field: "executionTime", sortable: true },
  { title: "状态", field: "success" },
  { title: "用户", field: "username" },
  { title: "创建时间", field: "createTime", sortable: true },
  { title: "操作", field: "actions" },
];

// 更新筛选值
const updateFilterValues = (
  values: Record<string, string | number | boolean | Date[] | undefined>
) => {
  if (values.className !== undefined) {
    filterValues.className = values.className as string;
  }
  if (values.methodName !== undefined) {
    filterValues.methodName = values.methodName as string;
  }
  if (values.success !== undefined) {
    filterValues.success = values.success as string;
  }
  if (values.username !== undefined) {
    filterValues.username = values.username as string;
  }
  if (values.dateRange !== undefined) {
    filterValues.dateRange = values.dateRange as Date[];
  }
};

// 处理搜索
const handleSearch = async () => {
  try {
    const params: AopLogQueryParams = {
      className: filterValues.className || undefined,
      methodName: filterValues.methodName || undefined,
      success: filterValues.success ? filterValues.success === "true" : undefined,
      username: filterValues.username || undefined,
    };

    // 处理日期范围
    if (filterValues.dateRange && filterValues.dateRange.length === 2) {
      params.startTime = filterValues.dateRange[0].toISOString();
      params.endTime = filterValues.dateRange[1].toISOString();
    }

    await fetchLogs(params);
    selectedLogs.value = [];
  } catch (error) {
    handleError(error);
  }
};

// 处理页码变化
const handlePageChange = async (page: number) => {
  try {
    currentPage.value = page;
    await handleSearch();
  } catch (error) {
    handleError(error);
  }
};

// 处理查看详情
const handleViewDetail = (log: components["schemas"]["AopLogRespDto"]) => {
  if (log.id) {
    router.push(Routes.AOPLOGDETAILVIEW.withParams({ id: log.id }));
  }
};

// 处理删除点击
const handleDeleteClick = (log: components["schemas"]["AopLogRespDto"]) => {
  currentLogToDelete.value = log;
  deleteLogModal.value?.show();
};

// 确认删除
const confirmDelete = async () => {
  try {
    if (currentLogToDelete.value?.id) {
      await deleteLog(currentLogToDelete.value.id);
      await handleSearch();
      deleteLogModal.value?.hide();
    }
  } catch (error) {
    handleError(error);
  }
};

// 处理批量删除点击
const handleBatchDeleteClick = () => {
  if (selectedLogs.value.length > 0) {
    batchDeleteLogsModal.value?.show();
  }
};

// 确认批量删除
const confirmBatchDelete = async () => {
  try {
    if (selectedLogs.value.length > 0) {
      await batchDeleteLogs(selectedLogs.value);
      await handleSearch();
      batchDeleteLogsModal.value?.hide();
    }
  } catch (error) {
    handleError(error);
  }
};

// 处理清理历史日志点击
const handleClearBeforeClick = () => {
  clearBeforeModal.value?.show();
};

// 关闭清理历史日志对话框
const closeClearBeforeModal = () => {
  clearBeforeModal.value?.hide();
};

// 确认清理历史日志
const confirmClearBefore = async () => {
  try {
    if (clearBeforeDate.value) {
      const dateString = clearBeforeDate.value.toISOString().split("T")[0];
      await deleteLogsBefore(dateString);
      await handleSearch();
      clearBeforeModal.value?.hide();
    }
  } catch (error) {
    handleError(error);
  }
};

// 处理全选
const handleSelectAll = (selected: boolean) => {
  if (selected) {
    selectedLogs.value = logs.value.map(log => log.id).filter((id): id is number => id !== undefined);
  } else {
    selectedLogs.value = [];
  }
};

// 初始化
const actionExcStore = useActionExcStore();

onMounted(async () => {
  try {
    // 初始化Flowbite
    initFlowbite();

    // 初始化模态框
    const $deleteModalElement = document.querySelector<HTMLElement>("#delete-log-modal");
    const $batchDeleteModalElement = document.querySelector<HTMLElement>("#batch-delete-logs-modal");
    const $clearBeforeModalElement = document.querySelector<HTMLElement>("#clear-before-modal");

    if ($deleteModalElement) {
      deleteLogModal.value = new Modal($deleteModalElement);
    }
    if ($batchDeleteModalElement) {
      batchDeleteLogsModal.value = new Modal($batchDeleteModalElement);
    }
    if ($clearBeforeModalElement) {
      clearBeforeModal.value = new Modal($clearBeforeModalElement);
    }

    // 加载日志数据
    await fetchLogs();

    // 设置刷新回调
    actionExcStore.setCallback((result) => {
      if (result) {
        handleSearch();
      }
    });
  } catch (error) {
    handleError(error);
  }
});
</script>
