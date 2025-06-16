<template>
	<div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
		<div class="mb-4 sm:mb-6 col-span-full">
			<Breadcrumbs :names="['用户管理', '角色分配']" :routes="[Routes.USERVIEW.fullPath()]" />
			<h1 class="text-xl sm:text-2xl mb-4 sm:mb-6 font-semibold text-gray-900">角色分配</h1>
		</div>

		<TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
			@update:values="updateFilterValues">
			<template #actions>
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
			</template>
		</TableFilterForm>

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
import type { components } from "@/api/types/schema";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import BindModal from "@/components/modals/ConfirmationDialog.vue";
import UnModal from "@/components/modals/ConfirmationDialog.vue";
import MobileCardListWithCheckbox from "@/components/tables/MobileCardListWithCheckbox.vue";
import TableButton from "@/components/tables/TableButton.vue";
import TableFilterForm from "@/components/tables/TableFilterForm.vue";
import type { FilterItem } from "@/components/tables/TableFilterForm.vue";
import TableFormLayout from "@/components/tables/TableFormLayout.vue";
import TablePagination from "@/components/tables/TablePagination.vue";
import { useRoleBind } from "@/composables/role/useRoleBind";
import { useRolesQuery } from "@/composables/role/useRolesQuery";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";

const filterConfig: FilterItem[] = [
	{
		type: "input",
		name: "roleName",
		placeholder: "角色名",
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

const filterValues = reactive<{
	roleName: string;
	bindState: "BIND" | "ALL" | "UNBIND";
}>({
	roleName: "",
	bindState: "ALL",
});

const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.roleName !== undefined) {
		filterValues.roleName = values.roleName as string;
	}
	if (values.bindState !== undefined) {
		filterValues.bindState = values.bindState as "BIND" | "ALL" | "UNBIND";
	}
};

const checkedRoleIds = ref<number[]>([]);
const roleBindModal = ref<ModalInterface>();
const roleUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();

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
		name: filterValues.roleName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
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
		name: filterValues.roleName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
};

onMounted(async () => {
	await fetchRolesWith({
		name: filterValues.roleName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
	initFlowbite();
	const $bindModalElement: HTMLElement | null =
		document.querySelector("#role-bind-modal");
	if ($bindModalElement) {
		roleBindModal.value = new Modal($bindModalElement, {});
	}
	const $unbindModalElement: HTMLElement | null =
		document.querySelector("#role-unbind-modal");
	if ($unbindModalElement) {
		roleUnbindModal.value = new Modal($unbindModalElement, {});
	}
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const clearCheckedRoleIds = () => {
	checkedRoleIds.value = [];
};

const handleSearch = async () => {
	await fetchRolesWith({
		name: filterValues.roleName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchRolesWith(
		{
			name: filterValues.roleName,
			userId: Number($route.params.userId),
			bindState: filterValues.bindState,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
