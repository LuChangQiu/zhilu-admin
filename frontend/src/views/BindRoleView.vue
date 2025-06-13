<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 sm:mb-6 col-span-full">
      <Breadcrumbs :names="['用户管理', '角色分配']" :routes="[{ name: RouteName.USERVIEW }]" />
      <h1 class="text-xl sm:text-2xl mb-4 sm:mb-6 font-semibold text-gray-900">角色分配</h1>
    </div>
    <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-y-3 sm:gap-y-0">
      <form class="w-full sm:w-auto flex flex-col xs:flex-row gap-2 xs:gap-3 items-stretch xs:items-center">
        <div class="flex-grow">
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
              class="block w-full p-2.5 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
              placeholder="角色名" required />
          </div>
        </div>
        <select id="countries" v-model="bindState"
          class="w-full xs:w-auto bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-2.5">
          <option value="BIND">已绑定</option>
          <option value="UNBIND">未绑定</option>
          <option value="ALL">全部</option>
        </select>
        <button type="submit"
          class="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-4 py-2.5"
          @click.prevent="handleSearch">搜索</button>
      </form>
      <div class="flex gap-x-2">
        <TableButton variant="primary" @click="() => {
            if (checkedRoleIds.length === 0) {
              alertStore.showAlert({
                content: '没有选择角色',
                level: 'error',
              });
            } else {
              roleBindModal?.show();
            }
          }">
          绑定
        </TableButton>
        <TableButton variant="danger" @click="() => {
            if (checkedRoleIds.length === 0) {
              alertStore.showAlert({
                content: '没有选择角色',
                level: 'error',
              });
            } else {
              roleUnbindModal?.show();
            }
          }">
          解绑
        </TableButton>
      </div>
    </div>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden space-y-4">
      <MobileCardListWithCheckbox :items="roles" v-model="checkedRoleIds">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #status="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.isBound ? 'bg-green-500' : 'bg-red-500'"></div>
            <span class="text-sm">{{ item.isBound === true ? "已绑定" : "未绑定" }}</span>
          </div>
        </template>
        <template #content="{ item }">
          <div>
            <p class="text-xs font-medium text-gray-600">角色编码</p>
            <p class="text-sm text-gray-900 mt-0.5">{{ item.code }}</p>
          </div>
        </template>
      </MobileCardListWithCheckbox>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="roles || []" :columns="columns" :hasCheckbox="true" v-model="checkedRoleIds"
        @all-checked-change="allChecked = $event">
        <template #code="{ item }">
          {{ item.code }}
        </template>
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #bindState="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.isBound ? 'bg-green-500' : 'bg-red-500'">
            </div>
            {{ item.isBound === true ? "已绑定" : "未绑定" }}
          </div>
        </template>
      </TableFormLayout>
    </div>

    <TablePagination :pageChange="handlePageChange" :total="total" />
  </div>

  <BindModal :id="'role-bind-modal'" :closeModal="() => {
    roleBindModal!.hide();
  }" :onSubmit="handleBindRoleSubmit" title="确定绑定选中的角色吗"></BindModal>
  <UnModal :id="'role-unbind-modal'" :closeModal="() => {
    roleUnbindModal!.hide();
  }" :onSubmit="handleUnbindRoleSubmit" title="确定解绑选中的角色吗"></UnModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import MobileCardListWithCheckbox from "@/components/MobileCardListWithCheckbox.vue";
import BindModal from "@/components/PopupModal.vue";
import UnModal from "@/components/PopupModal.vue";
import TableButton from "@/components/TableButton.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import { useRolesQuery } from "@/composables/role/useRolesQuery";
import { RouteName } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useRoleBind } from "../composables/role/useRoleBind";
import useAlertStore from "../composables/store/useAlertStore";
import { useActionExcStore } from "@/composables/store/useActionExcStore";

const roleName = ref<string>("");
const checkedRoleIds = ref<number[]>([]);
const roleBindModal = ref<ModalInterface>();
const roleUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();
const bindState = ref<"BIND" | "ALL" | "UNBIND">("ALL");

const alertStore = useAlertStore();
const { total, roles, fetchRolesWith } = useRolesQuery();
const { bindRole, unbindRole } = useRoleBind();
const actionExcStore = useActionExcStore();
// 定义表格列配置
const columns = [
	{ title: "角色编码", field: "code" },
	{ title: "角色名称", field: "name" },
	{ title: "绑定状态", field: "bindState" },
];

const handleBindRoleSubmit = async () => {
	await bindRole({
		userId: Number($route.params.userId),
		roleIds: checkedRoleIds.value,
	});
	roleBindModal.value?.hide();
	clearCheckedRoleIds();
	allChecked.value = false;
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchRolesWith({
		name: roleName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
};

const handleUnbindRoleSubmit = async () => {
	await unbindRole(Number($route.params.userId), checkedRoleIds.value);
	clearCheckedRoleIds();
	allChecked.value = false;
	roleUnbindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchRolesWith({
		name: roleName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
};

onMounted(async () => {
	await fetchRolesWith({
		name: roleName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
	initFlowbite();
	const $bindModalElement: HTMLElement | null =
		document.querySelector("#role-bind-modal");
	if ($bindModalElement) {
		roleBindModal.value = new Modal($bindModalElement, {});
	}
	const $unbindModalElement: HTMLElement | null =
		document.querySelector("#role-unbind-modal");
	roleUnbindModal.value = new Modal(
		$unbindModalElement,
		{},
		{ id: "role-unbind-modal" },
	);
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleSearch = async () => {
	await fetchRolesWith({
		name: roleName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchRolesWith(
		{
			name: roleName.value,
			userId: Number($route.params.userId),
			bindState: bindState.value,
		},
		page,
		pageSize,
	);
};

watch(allChecked, () => {
	if (allChecked.value) {
		checkedRoleIds.value = roles.value?.map((r) => r.id!) ?? [];
	} else {
		checkedRoleIds.value = [];
	}
});

const clearCheckedRoleIds = () => {
	checkedRoleIds.value = [];
};
</script>

<style scoped></style>
