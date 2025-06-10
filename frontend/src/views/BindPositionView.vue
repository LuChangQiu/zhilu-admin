<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['用户管理', '绑定岗位']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">绑定岗位</h1>
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
          <input type="search" id="default-search" v-model="positionName"
            class="block w-full p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
            placeholder="岗位名" required />
          <button type="submit"
            class="text-white absolute end-1.5 bottom-1.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-3 py-1.5 sm:px-4 sm:py-2"
            @click.prevent="handleSearch">搜索</button>
        </div>
      </form>
      <div class="flex items-center justify-end gap-2">
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
    </div>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
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
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import MobileCardListWithCheckbox from "@/components/MobileCardListWithCheckbox.vue";
import BindModal from "@/components/PopupModal.vue";
import UnModal from "@/components/PopupModal.vue";
import TableButton from "@/components/TableButton.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import { usePositionBind } from "@/composables/position/usePositionBind";
import { usePositionQuery } from "@/composables/position/usePositionQuery";
import { useMobileStyles } from "@/composables/useMobileStyles";
import { RouteName } from "@/router/constants";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import useAlertStore from "../composables/store/useAlertStore";

const positionName = ref<string>("");
const checkedPositionIds = ref<number[]>([]);
const positionBindModal = ref<ModalInterface>();
const positionUnbindModal = ref<ModalInterface>();
const allChecked = ref<boolean>(false);
const $route = useRoute();
const bindState = ref<"BIND" | "ALL" | "UNBIND">("ALL");

const alertStore = useAlertStore();

const { total, positions, fetchPositionWith } = usePositionQuery();

const { bindPosition, unbindPosition } = usePositionBind();

// 定义表格列配置
const columns = [
  { title: '岗位名称', field: 'name' },
  { title: '绑定状态', field: 'bindState' }
];

const handleBindPositionSubmit = async () => {
	await bindPosition(Number($route.params.userId), checkedPositionIds.value);
	positionBindModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchPositionWith({
		name: positionName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
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
		name: positionName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
	clearCheckedPositionIds();
	allChecked.value = false;
};

onMounted(async () => {
	await fetchPositionWith({
		name: positionName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
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
});

const handleSearch = async () => {
	await fetchPositionWith({
		name: positionName.value,
		userId: Number($route.params.userId),
		bindState: bindState.value,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPositionWith(
		{
			name: positionName.value,
			userId: Number($route.params.userId),
			bindState: bindState.value,
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
