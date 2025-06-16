<template>
	<div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
		<div class="mb-4 sm:mb-6 col-span-full">
			<Breadcrumbs :names="['用户管理', '绑定岗位']" :routes="[Routes.USERVIEW.fullPath()]" />
			<h1 class="text-xl sm:text-2xl mb-4 sm:mb-6 font-semibold text-gray-900">绑定岗位</h1>
		</div>

		<TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
			@update:values="updateFilterValues">
			<template #actions>
				<div class="flex gap-x-2">
					<TableButton variant="primary" @click="() => {
              if (checkedPositionIds.length === 0) {
                alertStore.showAlert({
                  content: '没有选择岗位',
                  level: 'error',
                });
              } else {
                positionBindModal?.show();
              }
            }">
						绑定
					</TableButton>
					<TableButton variant="danger" @click="() => {
              if (checkedPositionIds.length === 0) {
                alertStore.showAlert({
                  content: '没有选择岗位',
                  level: 'error',
                });
              } else {
                positionUnbindModal?.show();
              }
            }">
						解绑
					</TableButton>
				</div>
			</template>
		</TableFilterForm>

		<!-- 移动端卡片布局 -->
		<div class="md:hidden space-y-4">
			<MobileCardListWithCheckbox :items="positions || []" v-model="checkedPositionIds">
				<template #title="{ item }">
					{{ item.name }}
				</template>
				<template #status="{ item }">
					<div class="flex items-center">
						<div class="h-2.5 w-2.5 rounded-full me-2" :class="item.isBound ? 'bg-green-500' : 'bg-red-500'"></div>
						<span class="text-sm">{{ item.isBound === true ? "已绑定" : "未绑定" }}</span>
					</div>
				</template>
			</MobileCardListWithCheckbox>
		</div>

		<!-- PC端表格布局 -->
		<div class="hidden md:block">
			<TableFormLayout :items="positions || []" :columns="columns" :hasCheckbox="true" v-model="checkedPositionIds"
				@all-checked-change="allChecked = $event">
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

	<BindModal :id="'position-bind-modal'" :closeModal="() => {
    positionBindModal!.hide();
  }" :onSubmit="handleBindPositionSubmit" title="绑定选中的岗位吗"></BindModal>
	<UnModal :id="'position-unbind-modal'" :closeModal="() => {
    positionUnbindModal!.hide();
  }" :onSubmit="handleUnbindPositionSubmit" title="解绑选中的岗位吗"></UnModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import MobileCardListWithCheckbox from "@/components/tables/MobileCardListWithCheckbox.vue";
import TableButton from "@/components/tables/TableButton.vue";
import TableFilterForm from "@/components/tables/TableFilterForm.vue";
import type { FilterItem } from "@/components/tables/TableFilterForm.vue";
import TableFormLayout from "@/components/tables/TableFormLayout.vue";
import TablePagination from "@/components/tables/TablePagination.vue";
import BindModal from "@/components/modals/PopupModal.vue";
import UnModal from "@/components/modals/PopupModal.vue";
import { usePositionBind } from "@/composables/position/usePositionBind";
import { usePositionQuery } from "@/composables/position/usePositionQuery";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, reactive, ref, watch } from "vue";
import { useRoute } from "vue-router";

// 定义筛选配置
const filterConfig: FilterItem[] = [
	{
		type: "input",
		name: "positionName",
		placeholder: "岗位名",
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
	positionName: string;
	bindState: "BIND" | "ALL" | "UNBIND";
}>({
	positionName: "",
	bindState: "ALL",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.positionName !== undefined) {
		filterValues.positionName = values.positionName as string;
	}
	if (values.bindState !== undefined) {
		filterValues.bindState = values.bindState as "BIND" | "ALL" | "UNBIND";
	}
};

const checkedPositionIds = ref<number[]>([]);
const positionBindModal = ref<ModalInterface>();
const positionUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();

const alertStore = useAlertStore();
const actionExcStore = useActionExcStore();
const { total, positions, fetchPositionWith } = usePositionQuery();

const { bindPosition, unbindPosition } = usePositionBind();

// 定义表格列配置
const columns = [
	{ title: "岗位名称", field: "name" },
	{ title: "绑定状态", field: "bindState" },
];

const handleBindPositionSubmit = async () => {
	await bindPosition(Number($route.params.userId), checkedPositionIds.value);
	positionBindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchPositionWith({
		name: filterValues.positionName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
	clearCheckedPositionIds();
	allChecked.value = false;
};

const handleUnbindPositionSubmit = async () => {
	await unbindPosition(Number($route.params.userId), checkedPositionIds.value);
	positionUnbindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchPositionWith({
		name: filterValues.positionName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
	clearCheckedPositionIds();
	allChecked.value = false;
};

onMounted(async () => {
	await fetchPositionWith({
		name: filterValues.positionName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
	initFlowbite();
	const $bindModalElement: HTMLElement | null = document.querySelector(
		"#position-bind-modal",
	);
	if ($bindModalElement) {
		positionBindModal.value = new Modal($bindModalElement, {});
	}
	const $unbindModalElement: HTMLElement | null = document.querySelector(
		"#position-unbind-modal",
	);
	positionUnbindModal.value = new Modal(
		$unbindModalElement,
		{},
		{ id: "position-unbind-modal" },
	);
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleSearch = async () => {
	await fetchPositionWith({
		name: filterValues.positionName,
		userId: Number($route.params.userId),
		bindState: filterValues.bindState,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPositionWith(
		{
			name: filterValues.positionName,
			userId: Number($route.params.userId),
			bindState: filterValues.bindState,
		},
		page,
		pageSize,
	);
};

watch(allChecked, async () => {
	if (allChecked.value) {
		checkedPositionIds.value = positions.value?.map((d) => d.id!) ?? [];
	} else {
		checkedPositionIds.value = [];
	}
});

const clearCheckedPositionIds = () => {
	checkedPositionIds.value = [];
};
</script>

<style scoped></style>
