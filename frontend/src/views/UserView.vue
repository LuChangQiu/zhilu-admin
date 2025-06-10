<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4">
      <Breadcrumbs :names="['用户管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">用户管理</h1>
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
          <input type="search" id="default-search" v-model="username"
            class="block w-full p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
            placeholder="用户名" required />
          <button type="submit"
            class="text-white absolute end-1.5 bottom-1.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-3 py-1.5 sm:px-4 sm:py-2"
            @click.prevent="handleSearch">搜索</button>
        </div>
      </form>
      <!-- Create Modal toggle -->
      <Button :handleClick="() => handleUpsertUserClick(undefined)" :isLoading="false" :abortable="false"
        submitContent="新增用户" class="w-full sm:w-auto">
        <template #icon>
          <svg class="w-4 h-4 me-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
        </template>
      </Button>
    </div>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden space-y-4">
      <div v-for="user in users" :key="user.id" class="p-4 bg-white rounded-lg shadow">
        <div class="flex justify-between items-start mb-3">
          <div class="font-medium text-gray-900">{{ user.username }}</div>
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="user.enable ? 'bg-blue-500' : 'bg-red-500'"></div>
            <span class="text-sm">{{ user.enable === true ? "启用" : "禁用" }}</span>
          </div>
        </div>

        <div class="text-xs text-gray-500 mb-3">
          创建时间: {{ dayjs(user.createTime).format("llll") }}
        </div>

        <div class="space-y-2">
          <div class="flex flex-wrap gap-2">
            <button
              class="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs px-3 py-1.5"
              @click="handleBindRoleClick(user)" type="button">
              分配角色
            </button>
            <button
              class="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs px-3 py-1.5"
              @click="handleBindPositionClick(user)" type="button">
              分配岗位
            </button>
            <button
              class="text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs px-3 py-1.5"
              @click="handleBindDepartmentClick(user)" type="button">
              分配部门
            </button>
          </div>
        </div>

        <div class="flex justify-between mt-4">
          <button @click="handleUpsertUserClick(user)"
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
            class="flex items-center justify-center gap-x-1 bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none font-medium rounded-lg text-xs px-3 py-1.5"
            @click="handleDeleteUserClick(user)" type="button">
            <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
              <path fill-rule="evenodd"
                d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                clip-rule="evenodd"></path>
            </svg>
            <span>删除</span>
          </button>
        </div>
      </div>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="users" :columns="columns" @sort="handleSortClick">
        <template #sort-icon="{ field }">
          <SortIcon :sortField="getSortField(field)" />
        </template>
        <template #status="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.enable ? 'bg-blue-500' : 'bg-red-500'"></div>
            {{ item.enable === true ? "启用" : "禁用" }}
          </div>
        </template>
        <template #assign="{ item }">
          <div class="flex items-center gap-x-2">
            <button
              class="text-gray-900 bg-white border whitespace-nowrap border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleBindRoleClick(item)" type="button">
              分配角色
            </button>
            <button
              class="text-gray-900 bg-white border whitespace-nowrap border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleBindPositionClick(item)" type="button">
              分配岗位
            </button>
            <button
              class="text-gray-900 bg-white border whitespace-nowrap border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleBindDepartmentClick(item)" type="button">
              分配部门
            </button>
          </div>
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <button @click="handleUpsertUserClick(item)"
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
              class="flex items-center justify-center whitespace-nowrap gap-x-1 bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleDeleteUserClick(item)" type="button">
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

  <UserDeleteModal :id="'user-delete-modal'" :closeModal="() => {
    userDeleteModal!.hide();
  }" :onSubmit="handleDeleteUserSubmit" title="确定删除该用户吗" content="删除用户"></UserDeleteModal>
  <UserUpsertModal :id="'user-upsert-modal'" :onSubmit="handleUpsertUserSubmit" :closeModal="() => {
    userUpsertModal!.hide();
  }" :user="selectedUser">
  </UserUpsertModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import Button from "@/components/Button.vue";
import UserDeleteModal from "@/components/PopupModal.vue";
import SortIcon from "@/components/SortIcon.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import UserUpsertModal from "@/components/UserUpsertModal.vue";
import { useSort } from "@/composables/sort";
import useUserDelete from "@/composables/user/useUserDelete";
import { useUserQuery } from "@/composables/user/useUserQuery";
import { RouteName } from "@/router/constants";
import type { UserUpsertSubmitModel } from "@/types/user";
import dayjs from "@/utils/dateUtil";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import type { components } from "../api/types/schema";
import useAlertStore from "../composables/store/useAlertStore";
import { useUserUpsert } from "../composables/user/useUserUpsert";

const username = ref<string>("");
const selectedUser = ref<components["schemas"]["UserRolePermissionDto"]>();
const userUpsertModal = ref<ModalInterface>();
const userDeleteModal = ref<ModalInterface>();
const router = useRouter();
const { total, users, fetchUsersWith } = useUserQuery();
const { deleteUser } = useUserDelete();
const userUpsert = useUserUpsert();
const { sortBy, handleSort, getSortField } = useSort();
const alertStore = useAlertStore();

// 定义表格列配置
const columns = [
  { title: '用户名', field: 'username', sortable: true },
  { title: '创建时间', field: 'createTime', sortable: true },
  { title: '状态', field: 'status' },
  { title: '分配', field: 'assign' },
  { title: '操作', field: 'actions' }
];

onMounted(async () => {
	await fetchUsersWith({
		username: username.value,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null =
		document.querySelector("#user-upsert-modal");
	const $deleteModalElement: HTMLElement | null =
		document.querySelector("#user-delete-modal");
	if ($upsertModalElement) {
		userUpsertModal.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		userDeleteModal.value = new Modal($deleteModalElement, {});
	}
});

const handleUpsertUserSubmit = async (data: UserUpsertSubmitModel) => {
	await userUpsert.upsertUser(data);
	userUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchUsersWith(
		{
			username: username.value,
		},
		1,
		10,
		sortBy.value,
	);
};

const handleUpsertUserClick = async (
	user?: components["schemas"]["UserRolePermissionDto"],
) => {
	selectedUser.value = user;
	await nextTick(() => {
		userUpsertModal.value?.show();
	});
};

const handleBindRoleClick = async (
	user: components["schemas"]["UserRolePermissionDto"],
) => {
	router.push({
		name: RouteName.BINDROLEVIEW,
		params: {
			userId: user.id,
		},
	});
};

const handleBindDepartmentClick = async (
	user: components["schemas"]["UserRolePermissionDto"],
) => {
	router.push({
		name: RouteName.BINDDEPARTMENTVIEW,
		params: {
			userId: user.id,
		},
	});
};

const handleBindPositionClick = async (
	user: components["schemas"]["UserRolePermissionDto"],
) => {
	router.push({
		name: RouteName.BINDPOSITIONVIEW,
		params: {
			userId: user.id,
		},
	});
};

const handleSortClick = async (field: string) => {
	handleSort(field);
	await fetchUsersWith(
		{
			username: username.value,
		},
		1,
		10,
		sortBy.value,
	);
};

const handleDeleteUserSubmit = async () => {
	if (!selectedUser?.value?.id) return;
	await deleteUser(selectedUser.value.id);
	userDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
	await fetchUsersWith(
		{
			username: username.value,
		},
		1,
		10,
		sortBy.value,
	);
};

const handleDeleteUserClick = async (
	user: components["schemas"]["UserRolePermissionDto"],
) => {
	selectedUser.value = user;
	await nextTick(() => {
		userDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchUsersWith(
		{
			username: username.value,
		},
		1,
		10,
		sortBy.value,
	);
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchUsersWith(
		{
			username: username.value,
		},
		page,
		pageSize,
		sortBy.value,
	);
};
</script>

<style scoped></style>
