<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['岗位管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">岗位管理</h1>
    </div>

    <TableFilterForm :filters="filterConfig" :initialValues="filterValues" @search="handleSearch"
      @update:values="updateFilterValues">
      <template #actions>
        <Button :handleClick="() => handleUpsertPositionClick()" :isLoading="false" :abortable="false"
          submitContent="新增岗位" class="w-full sm:w-auto">
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
      <MobileCardList :items="positions">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #actions="{ item }">
          <div class="flex gap-x-2">
            <button @click="handleUpsertPositionClick(item)"
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
              @click="handleDeletePositionClick(item)" type="button">
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
      <TableFormLayout :items="positions" :columns="columns">
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <button @click="handleUpsertPositionClick(item)"
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
              class="flex items-center justify-center whitespace-nowrap gap-x-1 bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 font-medium rounded-lg text-sm px-4 py-2.5"
              @click="handleDeletePositionClick(item)" type="button">
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
    <TablePagination :total="total" :pageChange="handlePageChange" />
  </div>

  <PositionDeleteModal :id="'position-delete-modal'" :closeModal="() => {
    positionDeleteModal!.hide();
  }" :onSubmit="handleDeletePositionSubmit" title="确定删除该岗位吗" content="删除岗位"></PositionDeleteModal>

  <PositionUpsertModal :id="'position-upsert-modal'" :onSubmit="handleUpsertPositionSubmit" :closeModal="() => {
    positionUpsertModal!.hide();
  }" :position="selectedPosition" :allPositions="allPositions">
  </PositionUpsertModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import Button from "@/components/Button.vue";
import MobileCardList from "@/components/MobileCardList.vue";
import PositionDeleteModal from "@/components/PopupModal.vue";
import PositionUpsertModal from "@/components/PositionUpsertModal.vue";
import TableFilterForm from "@/components/TableFilterForm.vue";
import type { FilterItem } from "@/components/TableFilterForm.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import usePositionDelete from "@/composables/position/usePositionDelete";
import { usePositionQuery } from "@/composables/position/usePositionQuery";
import { usePositionUpsert } from "@/composables/position/usePositionUpsert";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, reactive, ref } from "vue";
import type { components } from "../api/types/schema";
import useAlertStore from "../composables/store/useAlertStore";

// 定义筛选配置
const filterConfig = [
	{
		type: "input",
		name: "positionName",
		placeholder: "岗位名称",
	},
] as FilterItem[];

// 筛选值
const filterValues = reactive<{
	positionName: string;
}>({
	positionName: "",
});

// 更新筛选值
const updateFilterValues = (
	values: Record<string, string | number | boolean | Date[] | undefined>,
) => {
	if (values.positionName !== undefined) {
		filterValues.positionName = values.positionName as string;
	}
};

const selectedPosition = ref<components["schemas"]["Position"]>();
const positionUpsertModal = ref<ModalInterface>();
const positionDeleteModal = ref<ModalInterface>();

const { positions, allPositions, fetchPositionWith, fetchAllPositions, total } =
	usePositionQuery();

const { deletePosition } = usePositionDelete();
const { upsertPosition } = usePositionUpsert();

const alertStore = useAlertStore();
const actionExcStore = useActionExcStore();
// 定义表格列配置
const columns = [
	{ title: "岗位名称", field: "name" },
	{ title: "操作", field: "actions" },
];

onMounted(async () => {
	await fetchAllPositions();
	await fetchPositionWith({
		name: filterValues.positionName,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null = document.querySelector(
		"#position-upsert-modal",
	);
	const $deleteModalElement: HTMLElement | null = document.querySelector(
		"#position-delete-modal",
	);
	if ($upsertModalElement) {
		positionUpsertModal.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		positionDeleteModal.value = new Modal($deleteModalElement, {});
	}
	actionExcStore.setCallback((result) => {
		if (result) {
			handleSearch();
		}
	});
});

const handleUpsertPositionSubmit = async (
	position: components["schemas"]["Position"],
) => {
	await upsertPosition(position);
	positionUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	fetchAllPositions();
	await fetchPositionWith({
		name: filterValues.positionName,
	});
};

const handleUpsertPositionClick = async (
	position?: components["schemas"]["Position"],
) => {
	selectedPosition.value = position;
	await nextTick(() => {
		positionUpsertModal.value?.show();
	});
};

const handleDeletePositionSubmit = async () => {
	if (!selectedPosition.value?.id) return;
	await deletePosition(selectedPosition.value.id);
	positionDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
	await fetchPositionWith({
		name: filterValues.positionName,
	});
};

const handleDeletePositionClick = async (
	position: components["schemas"]["Position"],
) => {
	selectedPosition.value = position;
	await nextTick(() => {
		positionDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchPositionWith({
		name: filterValues.positionName,
	});
};

const handlePageChange = async (page: number, pageSize: number) => {
	await fetchPositionWith(
		{
			name: filterValues.positionName,
		},
		page,
		pageSize,
	);
};
</script>

<style scoped></style>
