<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 sm:mb-6 col-span-full">
      <Breadcrumbs :names="['角色管理', '绑定权限']" :routes="[{ name: RouteName.ROLEVIEW }]" />
      <h1 class="text-xl sm:text-2xl mb-4 sm:mb-6 font-semibold text-gray-900">绑定权限</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <div class="flex gap-x-2">
          <TableButton variant="primary" @click="() => {
              if (checkedPermissionIds.length === 0) {
                alertStore.showAlert({
                  content: '没有选择权限',
                  level: 'error',
                });
              } else {
                permissionBindModal?.show();
              }
            }">
            绑定
          </TableButton>
          <TableButton variant="danger" @click="() => {
              if (checkedPermissionIds.length === 0) {
                alertStore.showAlert({
                  content: '没有选择权限',
                  level: 'error',
                });
              } else {
                permissionUnbindModal?.show();
              }
            }">
            解绑
          </TableButton>
        </div>
      </template>
    </TableFilterForm>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden space-y-4">
      <MobileCardListWithCheckbox :items="permissions || []" v-model="checkedPermissionIds">
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
            <p class="text-xs font-medium text-gray-600">权限编码</p>
            <p class="text-sm text-gray-900 mt-0.5">{{ item.code }}</p>
          </div>
        </template>
      </MobileCardListWithCheckbox>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="permissions || []" :columns="columns" :hasCheckbox="true" v-model="checkedPermissionIds"
        @all-checked-change="allChecked = $event">
        <template #code="{ item }">
          {{ item.code }}
        </template>
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #bindState="{ item }">
          <div class="flex items-center">
            <div class="h-2.5 w-2.5 rounded-full me-2" :class="item.isBound ? 'bg-green-500' : 'bg-red-500'"></div>
            {{ item.isBound === true ? "已绑定" : "未绑定" }}
          </div>
        </template>
      </TableFormLayout>
    </div>

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
import MobileCardListWithCheckbox from "@/components/MobileCardListWithCheckbox.vue";
import BindModal from "@/components/PopupModal.vue";
import UnModal from "@/components/PopupModal.vue";
import TableButton from "@/components/TableButton.vue";
import TableFilterForm from "@/components/TableFilterForm.vue";
import type { FilterItem } from "@/components/TableFilterForm.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import { RouteName } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { usePermissionBind } from "../composables/permission/usePermissionBind";
import usePermissionsQuery from "../composables/permission/usePermissionQuery";
import useAlertStore from "../composables/store/useAlertStore";

// 定义筛选配置
const filterConfig: FilterItem[] = [
	{
		type: "input",
		name: "permissionName",
		placeholder: "权限名",
	},
	{
		type: "select",
		name: "bindState",
		options: [
			{ value: "BIND", label: "已绑定" },
			{ value: "UNBIND", label: "未绑定" },
			{ value: "ALL", label: "全部" },
		],
	},
];

// 筛选值
const filterValues = reactive<{
	permissionName: string;
	bindState: "BIND" | "ALL" | "UNBIND";
}>({
	permissionName: "",
	bindState: "ALL",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.permissionName !== undefined) {
		filterValues.permissionName = values.permissionName as string;
	}
	if (values.bindState !== undefined) {
		filterValues.bindState = values.bindState as "BIND" | "ALL" | "UNBIND";
	}
};

const checkedPermissionIds = ref<number[]>([]);
const permissionBindModal = ref<ModalInterface>();
const permissionUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();

const alertStore = useAlertStore();
const actionExcStore = useActionExcStore();
const { total, permissions, fetchPermissionsWith } = usePermissionsQuery();
const { bindPermission, unbindPermission } = usePermissionBind();

// 定义表格列配置
const columns = [
	{ title: "权限编码", field: "code" },
	{ title: "权限名称", field: "name" },
	{ title: "绑定状态", field: "bindState" },
];

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
		name: filterValues.permissionName,
		roleId: Number($route.params.roleId),
		bindState: filterValues.bindState,
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
		name: filterValues.permissionName,
		roleId: Number($route.params.roleId),
		bindState: filterValues.bindState,
	});
};

onMounted(async () => {
	await fetchPermissionsWith({
		name: filterValues.permissionName,
		roleId: Number($route.params.roleId),
		bindState: filterValues.bindState,
	});
	initFlowbite();
	const $bindModalElement: HTMLElement | null = document.querySelector(
		"#permission-bind-modal",
	);
	if ($bindModalElement) {
		permissionBindModal.value = new Modal($bindModalElement, {});
	}
	const $unbindModalElement: HTMLElement | null = document.querySelector(
		"#permission-unbind-modal",
	);
	permissionUnbindModal.value = new Modal(
		$unbindModalElement,
		{},
		{ id: "permission-unbind-modal" },
	);
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleSearch = async () => {
	await fetchPermissionsWith({
		name: filterValues.permissionName,
		roleId: Number($route.params.roleId),
		bindState: filterValues.bindState,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPermissionsWith(
		{
			name: filterValues.permissionName,
			roleId: Number($route.params.roleId),
			bindState: filterValues.bindState,
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
