<template>
  <div class="relative overflow-x-auto px-4 pt-6 xl:grid-cols-3 xl:gap-4 rounded-lg ">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['绑定权限']" />
      <h1 class="text-xl mb-2 font-semibold text-gray-900 sm:text-2xl ">
        绑定权限</h1>
    </div>
    <div class="relative">
      <form class="max-w-sm mb-4 grid grid-cols-5 gap-y-4">
        <div class="col-span-3">
          <label for="default-search" class="mb-2 text-sm font-medium text-gray-900 sr-only ">Search</label>
          <div class="relative">
            <div class="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
              <svg class="w-4 h-4 text-gray-500 " aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                viewBox="0 0 20 20">
                <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
              </svg>
            </div>
            <input type="search" id="default-search" v-model="permissionName"
              class="block p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500      "
              placeholder="权限名" required />
          </div>
        </div>
        <select id="countries" v-model="bindState"
          class="col-span-2 block bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500      ">
          <option value="BIND">已绑定</option>
          <option value="UNBIND">未绑定</option>
          <option value="ALL">全部</option>
        </select>
        <button type="submit"
          class="text-white  bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2   "
          @click.prevent="handleSearch">搜索</button>
      </form>
      <div class="flex items-center justify-end gap-2 absolute right-5 bottom-2">
        <button @click="() => {
          if (checkedPermissionIds.length === 0) {
            alertStore.showAlert({
              content: '没有选择权限',
              level: 'error',
            });
          } else {
            permissionBindModal?.show();
          }
        }"
          class="flex items-center block text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
          type="button">
          绑定
        </button>
        <button @click="() => {
          if (checkedPermissionIds.length === 0) {
            alertStore.showAlert({
              content: '没有选择权限',
              level: 'error',
            });
          } else {
            permissionUnbindModal?.show();
          }
        }"
          class="flex items-center block text-white bg-red-700 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center"
          type="button">
          解绑
        </button>
      </div>
    </div>

    <table class="w-full text-sm text-left rtl:text-right text-gray-500 ">
      <thead class="text-xs text-gray-700 uppercase bg-gray-50  ">
        <tr>
          <th scope="col" class="p-4">
            <div class="flex items-center">
              <input id="checkbox-all-search" type="checkbox" v-model="allChecked"
                class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500    focus:ring-2  ">
              <label for="checkbox-all-search" class="sr-only">checkbox</label>
            </div>
          </th>
          <th scope="col" class="px-6 py-3">权限编码</th>
          <th scope="col" class="px-6 py-3">权限名称</th>
          <th scope="col" class="px-6 py-3">绑定状态</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="permission in permissions" :key="permission.id"
          class="bg-white border-b   border-gray-200 hover:bg-gray-50 ">
          <td class="w-4 p-4">
            <div class="flex items-center">
              <input :id="'checkbox-table-search-' + permission.id" :value="permission.id" type="checkbox"
                v-model="checkedPermissionIds"
                class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500    focus:ring-2  ">
              <label :for="'checkbox-table-search-' + permission.id" class="sr-only">checkbox</label>
            </div>
          </td>
          <td scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap ">
            {{ permission.code }}
          </td>
          <td scope="row" class="px-6 py-4 font-medium text-gray-900 whitespace-nowrap ">
            {{ permission.name }}
          </td>
          <td class="px-6 py-4 max-w-sm overflow-hidden text-ellipsis">
            <div class="flex items-center">
              <div class="h-2.5 w-2.5 rounded-full me-2" :class="permission.isBound ? 'bg-green-500' : 'bg-red-500'">
              </div> {{
              permission.isBound === true ? "已绑定" : "未绑定" }}
            </div>
          </td>
        </tr>
      </tbody>
    </table>

    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <BindModal :id="'permission-bind-modal'" :closeModal="() => {
    permissionBindModal!.hide();
  }" :onSubmit="handleBindPermissionSubmit" title="确定绑定选中的权限吗"></BindModal>
  <UnModal :id="'permission-unbind-modal'" :closeModal="() => {
    permissionUnbindModal!.hide();
  }" :onSubmit="handleUnbindPermissionSubmit" title="确定解绑选中的权限吗"></UnModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import BindModal from "@/components/PopupModal.vue";
import UnModal from "@/components/PopupModal.vue";
import TablePagination from "@/components/TablePagination.vue";
import { RouteName } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { usePermissionBind } from "../composables/permission/usePermissionBind";
import usePermissionsQuery from "../composables/permission/usePermissionQuery";
import useAlertStore from "../composables/store/useAlertStore";

const permissionName = ref<string>("");
const checkedPermissionIds = ref<number[]>([]);
const permissionBindModal = ref<ModalInterface>();
const permissionUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();
const bindState = ref<"BIND" | "ALL" | "UNBIND">("ALL");

const alertStore = useAlertStore();
const { total, permissions, fetchPermissionsWith } = usePermissionsQuery();
const { bindPermission, unbindPermission } = usePermissionBind();

const handleBindPermissionSubmit = async () => {
	await bindPermission({
		roleId: Number($route.params.roleId),
		permissionIds: checkedPermissionIds.value,
	});
	permissionBindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	clearCheckedRoleIds();
	allChecked.value = false;
	await fetchPermissionsWith({
		name: permissionName.value,
		roleId: Number($route.params.roleId),
		bindState: bindState.value,
	});
};

const handleUnbindPermissionSubmit = async () => {
	await unbindPermission({
		roleId: Number($route.params.roleId),
		permissionIds: checkedPermissionIds.value,
	});
	permissionUnbindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	clearCheckedRoleIds();
	allChecked.value = false;
	await fetchPermissionsWith({
		name: permissionName.value,
		roleId: Number($route.params.roleId),
		bindState: bindState.value,
	});
};

onMounted(async () => {
	await fetchPermissionsWith({
		name: permissionName.value,
		roleId: Number($route.params.roleId),
		bindState: bindState.value,
	});
	initFlowbite();
	const $bindModalElement: HTMLElement | null = document.querySelector(
		"#permission-bind-modal",
	);
	permissionBindModal.value = new Modal(
		$bindModalElement,
		{},
		{ id: "permission-bind-modal" },
	);
	const $unbindModalElement: HTMLElement | null = document.querySelector(
		"#permission-unbind-modal",
	);
	permissionUnbindModal.value = new Modal(
		$unbindModalElement,
		{},
		{ id: "permission-unbind-modal" },
	);
});

const handleSearch = async () => {
	await fetchPermissionsWith({
		name: permissionName.value,
		roleId: Number($route.params.roleId),
		bindState: bindState.value,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPermissionsWith(
		{
			name: permissionName.value,
			roleId: Number($route.params.roleId),
			bindState: bindState.value,
		},
		page,
		pageSize,
	);
};

watch(allChecked, async () => {
	if (allChecked.value) {
		checkedPermissionIds.value = permissions.value?.map((p) => p.id!) ?? [];
	} else {
		checkedPermissionIds.value = [];
	}
});

const clearCheckedRoleIds = () => {
	checkedPermissionIds.value = [];
};
</script>

<style scoped></style>
