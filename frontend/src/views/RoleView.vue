<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['角色管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">角色管理</h1>
    </div>
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-y-3 sm:gap-y-0">
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
          <input type="search" id="default-search" v-model="roleName"
            class="block w-full p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
            placeholder="角色名" required />
          <button type="submit"
            class="text-white absolute end-1.5 bottom-1.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-3 py-1.5 sm:px-4 sm:py-2"
            @click.prevent="handleSearch">搜索</button>
        </div>
      </form>
      <!-- Create Modal toggle -->
      <Button :handleClick="() => handleUpsertRoleClick(undefined)" :isLoading="false" :abortable="false"
        submitContent="新增角色" class="w-full sm:w-auto">
        <template #icon>
          <svg class="w-4 h-4 me-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
        </template>
      </Button>
    </div>
    <div class="relative overflow-x-auto shadow-md sm:rounded-lg">
      <table class="w-full text-sm text-left rtl:text-right text-gray-500">
        <thead class="text-xs text-gray-700 uppercase bg-gray-50">
          <tr>
            <th scope="col" class="p-2 sm:p-4 hidden sm:table-cell">
              <div class="flex items-center">
                <input id="checkbox-all-search" disabled type="checkbox"
                  class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
                <label for="checkbox-all-search" class="sr-only">checkbox</label>
              </div>
            </th>
            <th scope="col" class="px-3 py-2 sm:px-6 sm:py-3">角色名称</th>
            <th scope="col" class="px-3 py-2 sm:px-6 sm:py-3 hidden md:table-cell">角色编码</th>
            <th scope="col" class="px-3 py-2 sm:px-6 sm:py-3">分配</th>
            <th scope="col" class="px-3 py-2 sm:px-6 sm:py-3">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="role in roles" :key="role.id" class="bg-white border-b border-gray-200 hover:bg-gray-50">
            <td class="w-4 p-2 sm:p-4 hidden sm:table-cell">
              <div class="flex items-center">
                <input :id="'checkbox-table-search-' + role.id" type="checkbox" disabled
                  class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
                <label :for="'checkbox-table-search-' + role.id" class="sr-only">checkbox</label>
              </div>
            </td>
            <td scope="row" class="px-3 py-2 sm:px-6 sm:py-4 font-medium text-gray-900 whitespace-nowrap">
              {{ role.name }}
            </td>
            <td
              class="px-3 py-2 sm:px-6 sm:py-4 max-w-xs sm:max-w-sm overflow-hidden text-ellipsis hidden md:table-cell">
              {{ role.code }}</td>
            <td class="px-3 py-2 sm:px-6 sm:py-4 max-w-xs sm:max-w-sm overflow-hidden text-ellipsis">
              <div>
                <button
                  class="flex items-center justify-center min-w-min text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs sm:text-sm px-3 py-1.5 sm:px-4 sm:py-2.5 whitespace-nowrap"
                  @click="handleBindPermissionClick(role)" type="button">
                  <span>分配权限</span>
                </button>
              </div>
            </td>
            <td class="px-3 py-2 sm:px-6 sm:py-4 max-w-xs sm:max-w-sm overflow-hidden text-ellipsis">
              <div class="flex flex-col sm:flex-row items-start sm:items-center gap-y-2 sm:gap-y-0 sm:gap-x-2">
                <button @click="handleUpsertRoleClick(role)"
                  class="flex items-center justify-center whitespace-nowrap gap-x-1 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-xs sm:text-sm px-3 py-1.5 sm:px-4 sm:py-2.5"
                  type="button">
                  <svg class="w-3 h-3 sm:w-4 sm:h-4" fill="currentColor" viewBox="0 0 20 20"
                    xmlns="http://www.w3.org/2000/svg">
                    <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                    <path fill-rule="evenodd"
                      d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                      clip-rule="evenodd"></path>
                  </svg>
                  <span>编辑</span>
                </button>
                <button
                  class="flex items-center justify-center whitespace-nowrap gap-x-1 bg-red-700 hover:bg-red-800 focus:outline-none focus:ring-red-300 text-white focus:ring-4 font-medium rounded-lg text-xs sm:text-sm px-3 py-1.5 sm:px-4 sm:py-2.5"
                  @click="handleDeleteRoleClick(role)" type="button">
                  <svg class="w-3 h-3 sm:w-4 sm:h-4" fill="currentColor" viewBox="0 0 20 20"
                    xmlns="http://www.w3.org/2000/svg">
                    <path fill-rule="evenodd"
                      d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                      clip-rule="evenodd"></path>
                  </svg>
                  <span>删除</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <RoleDeleteModal :id="'role-delete-modal'" :closeModal="() => {
    roleDeleteModal!.hide();
  }" :onSubmit="handleDeletedModalSubmit" title="确定删除该角色吗" content="删除角色"></RoleDeleteModal>
  <RoleUpsertModal :onSubmit="handleUpsertModalSubmit" :closeModal="() => {
    roleUpsertModal!.hide();
  }" :role="selectedRole">
  </RoleUpsertModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import Button from "@/components/Button.vue";
import RoleDeleteModal from "@/components/PopupModal.vue";
import RoleUpsertModal from "@/components/RoleUpsertModal.vue";
import TablePagination from "@/components/TablePagination.vue";
import useRoleDelete from "@/composables/role/useRoleDelete";
import { useRolesQuery } from "@/composables/role/useRolesQuery";
import { RouteName } from "@/router/constants";
import type { RoleUpsertModel } from "@/types/role";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import type { components } from "../api/types/schema";
import { useRoleUpsert } from "../composables/role/useRoleUpsert";
import useAlertStore from "../composables/store/useAlertStore";

const roleName = ref<string>("");
const selectedRole = ref<components["schemas"]["RoleDto"]>();
const roleUpsertModal = ref<ModalInterface>();
const roleDeleteModal = ref<ModalInterface>();

const { total, roles, fetchRolesWith } = useRolesQuery();

const { deleteRole } = useRoleDelete();
const alertStore = useAlertStore();
const router = useRouter();
const upsertRole = useRoleUpsert();
onMounted(async () => {
	await fetchRolesWith({
		name: roleName.value,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null =
		document.querySelector("#role-upsert-modal");
	const $deleteModalElement: HTMLElement | null =
		document.querySelector("#role-delete-modal");
	roleUpsertModal.value = new Modal($upsertModalElement, {});
	roleDeleteModal.value = new Modal($deleteModalElement, {});
});

const handleUpsertModalSubmit = async (data: RoleUpsertModel) => {
	await upsertRole.upsertRole(data);
	await fetchRolesWith({
		name: roleName.value,
	});
	roleUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};

const handleUpsertRoleClick = async (
	role?: components["schemas"]["RoleDto"],
) => {
	selectedRole.value = role;
	await nextTick(() => {
		roleUpsertModal.value?.show();
	});
};

const handleDeletedModalSubmit = async () => {
	if (!selectedRole?.value?.id) return;
	await deleteRole(selectedRole.value.id);
	await fetchRolesWith({
		name: roleName.value,
	});
	roleDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
};

const handleDeleteRoleClick = async (
	role: components["schemas"]["RoleDto"],
) => {
	selectedRole.value = role;
	await nextTick(() => {
		roleDeleteModal.value?.show();
	});
};

const handleBindPermissionClick = async (
	role: components["schemas"]["RoleDto"],
) => {
	router.push({
		name: RouteName.BINDPERMISSIONVIEW,
		params: { roleId: role.id },
	});
};
const handleSearch = async () => {
	await fetchRolesWith({
		name: roleName.value,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchRolesWith(
		{
			name: roleName.value,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
