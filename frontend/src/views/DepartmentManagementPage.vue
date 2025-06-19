<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['部门管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">部门管理</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <TableButton variant="primary" @click="handleUpsertDepartmentClick()" class="w-full sm:w-auto">
          <template #icon>
            <PlusIcon class="w-4 h-4" />
          </template>
          新增部门
        </TableButton>
      </template>
    </TableFilterForm>

    <!-- 内容布局：两栏布局，响应式处理 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-6">
      <!-- 左侧：部门树状图（仅在桌面端显示） -->
      <div class="hidden lg:block lg:col-span-1">
        <div class="bg-white rounded-lg shadow">
          <div class="p-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">部门结构</h2>
          </div>
          <div class="p-4">
            <DepartmentTree :departmentTree="departmentTree" @add-child="handleAddChildClick"
              @edit="handleEditDepartment" />
          </div>
        </div>
      </div>

      <!-- 右侧：部门表格（在移动端占满宽度） -->
      <div class="col-span-1 lg:col-span-2">
        <!-- 移动端卡片布局 -->
        <div class="md:hidden">
          <MobileCardList :items="departments">
            <template #title="{ item }">
              {{ item.name }}
            </template>
            <template #content="{ item }">
              <div>
                <p class="text-xs font-medium text-gray-600">上级部门</p>
                <p class="text-sm text-gray-900 mt-0.5">{{ !item.parentName ? '无' : item.parentName }}</p>
              </div>
            </template>
            <template #actions="{ item }">
              <div class="flex gap-x-2">
                <TableButton variant="primary" size="xs" isMobile @click="handleUpsertDepartmentClick(item)">
                  <template #icon>
                    <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                      <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z">
                      </path>
                      <path fill-rule="evenodd"
                        d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                        clip-rule="evenodd"></path>
                    </svg>
                  </template>
                  编辑
                </TableButton>
                <TableButton variant="danger" size="xs" isMobile @click="handleDeleteDepartmentClick(item)">
                  <template #icon>
                    <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                      <path fill-rule="evenodd"
                        d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                        clip-rule="evenodd"></path>
                    </svg>
                  </template>
                  删除
                </TableButton>
              </div>
            </template>
          </MobileCardList>
        </div>

        <!-- PC端表格布局 -->
        <div class="hidden md:block bg-white rounded-lg shadow">
          <div class="p-4 border-b border-gray-200">
            <h2 class="text-lg font-semibold text-gray-900">部门列表</h2>
          </div>
          <div class="p-4">
            <TableFormLayout :items="departments || []" :columns="columns">
              <template #parentName="{ item }">
                {{ !item.parentName ? '无' : item.parentName }}
              </template>
              <template #name="{ item }">
                {{ item.name }}
              </template>
              <template #actions="{ item }">
                <div class="flex items-center gap-x-2">
                  <TableButton variant="primary" @click="handleUpsertDepartmentClick(item)">
                    <template #icon>
                      <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                        <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                        <path fill-rule="evenodd"
                          d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                          clip-rule="evenodd"></path>
                      </svg>
                    </template>
                    编辑
                  </TableButton>
                  <TableButton variant="danger" @click="handleDeleteDepartmentClick(item)">
                    <template #icon>
                      <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                        <path fill-rule="evenodd"
                          d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                          clip-rule="evenodd"></path>
                      </svg>
                    </template>
                    删除
                  </TableButton>
                </div>
              </template>
            </TableFormLayout>
          </div>
        </div>
        <div class="mt-4">
          <TablePagination :total="total" :pageChange="handlePageChange" />
        </div>
      </div>
    </div>
  </div>

  <DepartmentDeleteModal :id="'department-delete-modal'" :closeModal="() => {
    departmentDeleteModal!.hide();
  }" :onSubmit="handleDeleteDepartmentSubmit" title="确定删除该部门吗" content="删除部门"></DepartmentDeleteModal>

  <DepartmentFormDialog :id="'department-upsert-modal'" :onSubmit="handleUpsertDepartmentSubmit" :closeModal="() => {
    availableDepartments = undefined
    departmentFormDialog!.hide();
  }" :department="selectedDepartment" :availableDepartments="availableDepartments">
  </DepartmentFormDialog>
</template>

<script setup lang="ts">
import type { components } from "@/api/types/schema";
import { DepartmentTree } from "@/components/common/department";
import PlusIcon from "@/components/icons/PlusIcon.vue";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import DepartmentDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import DepartmentFormDialog from "@/components/modals/DepartmentFormDialog.vue";
import MobileCardList from "@/components/tables/MobileCardList.vue";
import TableButton from "@/components/tables/TableButton.vue";
import TableFilterForm from "@/components/tables/TableFilterForm.vue";
import type { FilterItem } from "@/components/tables/TableFilterForm.vue";
import TableFormLayout from "@/components/tables/TableFormLayout.vue";
import TablePagination from "@/components/tables/TablePagination.vue";
import useDepartmentDelete from "@/composables/department/useDepartmentDelete";
import {
	type DepartmentTreeNode,
	useDepartmentQuery,
} from "@/composables/department/useDepartmentQuery";
import { useDepartmentUpsert } from "@/composables/department/useDepartmentUpsert";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import type { DepartmentUpsertModel } from "@/types/DepartmentTypes";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, onUnmounted, reactive, ref } from "vue";

// 定义筛选配置
const filterConfig = [
	{
		type: "input",
		name: "departmentName",
		placeholder: "部门名称",
	},
] as FilterItem[];

// 筛选值
const filterValues = reactive<{
	departmentName: string;
}>({
	departmentName: "",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.departmentName !== undefined) {
		filterValues.departmentName = values.departmentName as string;
	}
};

const selectedDepartment = ref<components["schemas"]["Department"]>();
const departmentFormDialog = ref<ModalInterface>();
const departmentDeleteModal = ref<ModalInterface>();

const {
	departments,
	availableDepartments,
	departmentTree,
	fetchDepartmentWith,
	fetchAvailableDepartments,
	fetchDepartmentTree,
	total,
} = useDepartmentQuery();

const { deleteDepartment } = useDepartmentDelete();
const { upsertDepartment } = useDepartmentUpsert();

const alertStore = useAlertStore();
const actionExcStore = useActionExcStore();
// 定义表格列配置
const columns = [
	{ title: "上级部门", field: "parentName" },
	{ title: "部门名称", field: "name" },
	{ title: "操作", field: "actions" },
];

// 同步更新部门数据和树形结构
const refreshDepartmentData = async () => {
	// 更新部门列表数据和树形结构
	await Promise.all([
		fetchDepartmentWith({
			name: filterValues.departmentName,
		}),
		fetchDepartmentTree(),
	]);
};

// 处理添加子部门点击
const handleAddChildClick = async (parentNode: DepartmentTreeNode) => {
	// 创建默认的空部门，但设置父部门ID
	selectedDepartment.value = {
		parentId: parentNode.id,
	} as components["schemas"]["Department"];
	await fetchAvailableDepartments();
	await nextTick(() => {
		departmentFormDialog.value?.show();
	});
};

// 处理编辑部门
const handleEditDepartment = async (node: DepartmentTreeNode) => {
	// 将节点转换为部门对象
	const department: components["schemas"]["Department"] = {
		id: node.id,
		name: node.name,
		parentId: node.parentId !== null ? node.parentId : undefined,
	};

	await handleUpsertDepartmentClick(department);
};

onMounted(async () => {
	// 初始化加载部门数据和树形结构
	await refreshDepartmentData();

	initFlowbite();
	const $upsertModalElement = document.querySelector<HTMLElement>(
		"#department-upsert-modal",
	);
	const $deleteModalElement = document.querySelector<HTMLElement>(
		"#department-delete-modal",
	);

	if ($upsertModalElement) {
		departmentFormDialog.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		departmentDeleteModal.value = new Modal($deleteModalElement, {});
	}

	actionExcStore.setCallback((result) => {
		if (result) {
			refreshDepartmentData();
		}
	});
});

// 组件卸载时清理资源
onUnmounted(() => {
	// 重置回调，避免内存泄漏
	actionExcStore.setCallback(() => {});

	// 清理模态框
	departmentFormDialog.value?.hide();
	departmentDeleteModal.value?.hide();
});

const handleUpsertDepartmentSubmit = async (
	department: DepartmentUpsertModel,
) => {
	const success = await upsertDepartment(department);
	departmentFormDialog.value?.hide();

	if (success) {
		alertStore.showAlert({
			content: department.id ? "部门更新成功" : "部门创建成功",
			level: "success",
		});
		// 同时刷新部门列表和树形结构
		await refreshDepartmentData();

		// 如果是新增或修改部门，重置筛选条件
		if (!department.id || selectedDepartment.value?.id !== department.id) {
			filterValues.departmentName = "";
		}
	} else {
		alertStore.showAlert({
			content: "操作失败，请稍后重试",
			level: "error",
		});
	}

	// 操作完成后清空选中部门
	selectedDepartment.value = undefined;
	availableDepartments.value = undefined;
};

const handleUpsertDepartmentClick = async (
	department?: components["schemas"]["Department"],
) => {
	selectedDepartment.value = department;
	await fetchAvailableDepartments(department?.id);
	await nextTick(() => {
		departmentFormDialog.value?.show();
	});
};

const handleDeleteDepartmentSubmit = async () => {
	if (!selectedDepartment.value?.id) return;

	const success = await deleteDepartment(selectedDepartment.value.id);
	departmentDeleteModal.value?.hide();

	if (success) {
		alertStore.showAlert({
			content: "部门删除成功",
			level: "success",
		});
		// 删除后清空筛选条件，并刷新数据
		filterValues.departmentName = "";
		await refreshDepartmentData();
	} else {
		alertStore.showAlert({
			content: "删除失败，请稍后重试",
			level: "error",
		});
	}

	// 操作完成后清空选中部门
	selectedDepartment.value = undefined;
};

const handleDeleteDepartmentClick = async (
	department: components["schemas"]["Department"],
) => {
	selectedDepartment.value = department;
	await nextTick(() => {
		departmentDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchDepartmentWith({
		name: filterValues.departmentName,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchDepartmentWith(
		{
			name: filterValues.departmentName,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
