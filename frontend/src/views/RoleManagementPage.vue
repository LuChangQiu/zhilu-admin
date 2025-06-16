<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['角色管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">角色管理</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <TableButton variant="primary" @click="handleUpsertRoleClick(undefined)" class="w-full sm:w-auto">
          <template #icon>
            <PlusIcon class="w-4 h-4" />
          </template>
          新增角色
        </TableButton>
      </template>
    </TableFilterForm>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
      <MobileCardList :items="roles">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #content="{ item }">
          <div>
            <p class="text-xs font-medium text-gray-600">角色编码</p>
            <p class="text-sm text-gray-900 mt-0.5">{{ item.code }}</p>
          </div>
        </template>
        <template #tags="{ item }">
          <button
            class="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs px-3 py-1.5"
            @click="handleBindPermissionClick(item)" type="button">
            分配权限
          </button>
        </template>
        <template #actions="{ item }">
          <div class="flex gap-x-2">
            <button @click="handleUpsertRoleClick(item)"
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
            <button
              class="flex items-center justify-center gap-x-1 bg-red-700 hover:bg-red-800 focus:outline-none focus:ring-red-300 text-white focus:ring-4 font-medium rounded-lg text-xs px-3 py-1.5"
              @click="handleDeleteRoleClick(item)" type="button">
              <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd"
                  d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                  clip-rule="evenodd"></path>
              </svg>
              <span>删除</span>
            </button>
          </div>
        </template>
      </MobileCardList>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="roles || []" :columns="columns">
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #code="{ item }">
          {{ item.code }}
        </template>
        <template #assign="{ item }">
          <div>
            <button
              class="flex items-center justify-center min-w-min text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-sm px-4 py-2.5 whitespace-nowrap"
              @click="handleBindPermissionClick(item)" type="button">
              <span>分配权限</span>
            </button>
          </div>
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <button @click="handleUpsertRoleClick(item)"
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
            <button
              class="flex items-center justify-center whitespace-nowrap gap-x-1 bg-red-700 hover:bg-red-800 focus:outline-none focus:ring-red-300 text-white focus:ring-4 font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleDeleteRoleClick(item)" type="button">
              <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                <path fill-rule="evenodd"
                  d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                  clip-rule="evenodd"></path>
              </svg>
              <span>删除</span>
            </button>
          </div>
        </template>
      </TableFormLayout>
    </div>
    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <RoleDeleteModal :id="'role-delete-modal'" :closeModal="() => {
    roleDeleteModal!.hide();
  }" :onSubmit="handleDeletedModalSubmit" title="确定删除该角色吗" content="删除角色"></RoleDeleteModal>
  <RoleFormDialog :id="'role-upsert-modal'" :onSubmit="handleUpsertModalSubmit" :closeModal="() => {
    roleUpsertModal!.hide();
  }" :role="selectedRole">
  </RoleFormDialog>
</template>

<script setup lang="ts">
import type { components } from "@/api/types/schema";
import PlusIcon from "@/components/icons/PlusIcon.vue";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import RoleDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import RoleFormDialog from "@/components/modals/RoleFormDialog.vue";
import MobileCardList from "@/components/tables/MobileCardList.vue";
import TableButton from "@/components/tables/TableButton.vue";
import TableFilterForm from "@/components/tables/TableFilterForm.vue";
import type { FilterItem } from "@/components/tables/TableFilterForm.vue";
import TableFormLayout from "@/components/tables/TableFormLayout.vue";
import TablePagination from "@/components/tables/TablePagination.vue";
import useRoleDelete from "@/composables/role/useRoleDelete";
import { useRoleUpsert } from "@/composables/role/useRoleUpsert";
import { useRolesQuery } from "@/composables/role/useRolesQuery";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";
import type { RoleUpsertModel } from "@/types/RoleTypes";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";

// 定义筛选配置
const filterConfig = [
	{
		type: "input",
		name: "roleName",
		placeholder: "角色名",
	},
] as FilterItem[];

// 筛选值
const filterValues = reactive<{
	roleName: string;
}>({
	roleName: "",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.roleName !== undefined) {
		filterValues.roleName = values.roleName as string;
	}
};

const selectedRole = ref<components["schemas"]["RoleRespDto"]>();
const roleUpsertModal = ref<ModalInterface>();
const roleDeleteModal = ref<ModalInterface>();
const actionExcStore = useActionExcStore();
const { total, roles, fetchRolesWith } = useRolesQuery();

const { deleteRole } = useRoleDelete();
const alertStore = useAlertStore();
const router = useRouter();
const upsertRole = useRoleUpsert();

// 定义表格列配置
const columns = [
	{ title: "角色名称", field: "name" },
	{ title: "角色编码", field: "code" },
	{ title: "分配", field: "assign" },
	{ title: "操作", field: "actions" },
];

onMounted(async () => {
	await fetchRolesWith({
		name: filterValues.roleName,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null =
		document.querySelector("#role-upsert-modal");
	const $deleteModalElement: HTMLElement | null =
		document.querySelector("#role-delete-modal");
	if ($upsertModalElement) {
		roleUpsertModal.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		roleDeleteModal.value = new Modal($deleteModalElement, {});
	}
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleUpsertModalSubmit = async (data: RoleUpsertModel) => {
	await upsertRole.upsertRole(data);
	roleUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchRolesWith({
		name: filterValues.roleName,
	});
};

const handleUpsertRoleClick = async (
	role?: components["schemas"]["RoleRespDto"],
) => {
	selectedRole.value = role;
	await nextTick(() => {
		roleUpsertModal.value?.show();
	});
};

const handleBindPermissionClick = async (
	role: components["schemas"]["RoleRespDto"],
) => {
	router.push(
		Routes.BINDPERMISSIONVIEW.withParams({
			roleId: role.id!,
		}),
	);
};

const handleDeletedModalSubmit = async () => {
	if (!selectedRole.value?.id) return;
	await deleteRole(selectedRole.value.id);
	roleDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
	await fetchRolesWith({
		name: filterValues.roleName,
	});
};

const handleDeleteRoleClick = async (
	role: components["schemas"]["RoleRespDto"],
) => {
	selectedRole.value = role;
	await nextTick(() => {
		roleDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchRolesWith({
		name: filterValues.roleName,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchRolesWith(
		{
			name: filterValues.roleName,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
