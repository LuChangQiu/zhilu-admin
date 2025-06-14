<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['权限管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">权限管理</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <Button :handleClick="() => handleUpsertPermissionClick(undefined)" :isLoading="false" :abortable="false"
          submitContent="新增权限" class="w-full sm:w-auto">
          <template #icon>
            <svg class="w-4 h-4 me-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
              viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
            </svg>
          </template>
        </Button>
      </template>
    </TableFilterForm>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
      <MobileCardList :items="permissions">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #content="{ item }">
          <div>
            <p class="text-xs font-medium text-gray-600">权限编码</p>
            <p class="text-sm text-gray-900 mt-0.5">{{ item.code }}</p>
          </div>
        </template>
        <template #actions="{ item }">
          <div class="flex gap-x-2">
            <TableButton variant="primary" size="xs" isMobile @click="handleUpsertPermissionClick(item)">
              <template #icon>
                <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                  <path fill-rule="evenodd"
                    d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                    clip-rule="evenodd"></path>
                </svg>
              </template>
              编辑
            </TableButton>
            <TableButton variant="danger" size="xs" isMobile @click="handleDeletePermissionClick(item)">
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
    <div class="hidden md:block">
      <TableFormLayout :items="permissions || []" :columns="columns">
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #code="{ item }">
          {{ item.code }}
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <TableButton variant="primary" @click="handleUpsertPermissionClick(item)">
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
            <TableButton variant="danger" @click="handleDeletePermissionClick(item)">
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

    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <PermissionDeleteModal :id="'permission-delete-modal'" :closeModal="() => {
    permissionDeleteModal!.hide();
  }" :onSubmit="handleDeleteModalSubmit" title="确定删除该权限吗" content="删除权限"></PermissionDeleteModal>
  <PermissionUpsertModal :id="'permission-upsert-modal'" :onSubmit="handleUpsertModalSubmit" :closeModal="() => {
    permissionUpsertModal!.hide();
  }" :permission="selectedPermission">
  </PermissionUpsertModal>
</template>

<script setup lang="ts">
import type { components } from "@/api/types/schema";
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import Button from "@/components/Button.vue";
import MobileCardList from "@/components/MobileCardList.vue";
import PermissionUpsertModal from "@/components/PermissionUpsertModal.vue";
import PermissionDeleteModal from "@/components/PopupModal.vue";
import TableButton from "@/components/TableButton.vue";
import TableFilterForm from "@/components/TableFilterForm.vue";
import type { FilterItem } from "@/components/TableFilterForm.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import usePermissionDelete from "@/composables/permission/usePermissionDelete";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, reactive, ref } from "vue";
import usePermissionsQuery from "../composables/permission/usePermissionQuery";
import usePermissionUpsert from "../composables/permission/usePermissionUpsert";
import useAlertStore from "../composables/store/useAlertStore";
import type { PermissionUpsertModel } from "../types/permission";

// 定义筛选配置
const filterConfig = [
	{
		type: "input",
		name: "permissionName",
		placeholder: "权限名",
	},
] as FilterItem[];

// 筛选值
const filterValues = reactive<{
	permissionName: string;
}>({
	permissionName: "",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.permissionName !== undefined) {
		filterValues.permissionName = values.permissionName as string;
	}
};

const selectedPermission = ref<components["schemas"]["PermissionRespDto"]>();
const permissionUpsertModal = ref<ModalInterface>();
const permissionDeleteModal = ref<ModalInterface>();

const { total, permissions, fetchPermissionsWith } = usePermissionsQuery();

const { deletePermission } = usePermissionDelete();
const permissionUpsert = usePermissionUpsert();
const alertStore = useAlertStore();
const actionExcStore = useActionExcStore();
// 定义表格列配置
const columns = [
	{ title: "权限名称", field: "name" },
	{ title: "权限编码", field: "code" },
	{ title: "操作", field: "actions" },
];

onMounted(async () => {
	await fetchPermissionsWith({
		name: filterValues.permissionName,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null = document.querySelector(
		"#permission-upsert-modal",
	);
	const $deleteModalElement: HTMLElement | null = document.querySelector(
		"#permission-delete-modal",
	);
	if ($upsertModalElement) {
		permissionUpsertModal.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		permissionDeleteModal.value = new Modal($deleteModalElement, {});
	}
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleUpsertModalSubmit = async (data: PermissionUpsertModel) => {
	await permissionUpsert.upsertPermission(data);
	await fetchPermissionsWith({
		name: filterValues.permissionName,
	});
	permissionUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};

const handleUpsertPermissionClick = async (
	permission?: components["schemas"]["PermissionRespDto"],
) => {
	selectedPermission.value = permission;
	await nextTick(() => {
		permissionUpsertModal.value?.show();
	});
};

const handleDeleteModalSubmit = async () => {
	if (!selectedPermission.value?.id) return;
	await deletePermission(selectedPermission.value.id);
	permissionDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
	await fetchPermissionsWith({
		name: filterValues.permissionName,
	});
};

const handleDeletePermissionClick = async (
	permission: components["schemas"]["PermissionRespDto"],
) => {
	selectedPermission.value = permission;
	await nextTick(() => {
		permissionDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchPermissionsWith({
		name: filterValues.permissionName,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPermissionsWith(
		{
			name: filterValues.permissionName,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
