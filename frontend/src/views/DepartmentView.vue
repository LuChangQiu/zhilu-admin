<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <div class="mb-4 col-span-full">
      <Breadcrumbs :names="['部门管理']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">部门管理</h1>
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
          <input type="search" id="default-search" v-model="name"
            class="block w-full p-3 ps-10 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
            placeholder="部门名称" required />
          <button type="submit"
            class="text-white absolute end-1.5 bottom-1.5 bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-3 py-1.5 sm:px-4 sm:py-2"
            @click.prevent="handleSearch">搜索</button>
        </div>
      </form>
      <Button :handleClick="() => handleUpsertDepartmentClick()" :isLoading="false" :abortable="false"
        submitContent="新增部门" class="w-full sm:w-auto">
        <template #icon>
          <svg class="w-4 h-4 me-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
            viewBox="0 0 24 24" stroke-width="2" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
          </svg>
        </template>
      </Button>
    </div>

    <!-- 移动端卡片布局 -->
    <div class="md:hidden">
      <MobileCardList :items="departments">
        <template #title="{ item }">
          {{ item.name }}
        </template>
        <template #content="{ item }">
          <div>
            <p class="text-xs font-medium text-gray-600">上级部门</p>
            <p class="text-sm text-gray-900 mt-0.5">{{ !item.parentName ? '无' : item.parentName }}</p>
          </div>
        </template>
        <template #actions="{ item }">
          <div class="flex gap-x-2">
            <TableButton variant="primary" size="xs" isMobile @click="handleUpsertDepartmentClick(item)">
              <template #icon>
                <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                  <path fill-rule="evenodd"
                    d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                    clip-rule="evenodd"></path>
                </svg>
              </template>
              编辑
            </TableButton>
            <TableButton variant="danger" size="xs" isMobile @click="handleDeleteDepartmentClick(item)">
              <template #icon>
                <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                    d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                    clip-rule="evenodd"></path>
                </svg>
              </template>
              删除
            </TableButton>
          </div>
        </template>
      </MobileCardList>
    </div>

    <!-- PC端表格布局 -->
    <div class="hidden md:block">
      <TableFormLayout :items="departments || []" :columns="columns">
        <template #parentName="{ item }">
          {{ !item.parentName ? '无' : item.parentName }}
        </template>
        <template #name="{ item }">
          {{ item.name }}
        </template>
        <template #actions="{ item }">
          <div class="flex items-center gap-x-2">
            <TableButton variant="primary" @click="handleUpsertDepartmentClick(item)">
              <template #icon>
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
                  <path fill-rule="evenodd"
                    d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z"
                    clip-rule="evenodd"></path>
                </svg>
              </template>
              编辑
            </TableButton>
            <TableButton variant="danger" @click="handleDeleteDepartmentClick(item)">
              <template #icon>
                <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                  <path fill-rule="evenodd"
                    d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                    clip-rule="evenodd"></path>
                </svg>
              </template>
              删除
            </TableButton>
          </div>
        </template>
      </TableFormLayout>
    </div>

    <TablePagination :total="total" :pageChange="handlePageChange" />
  </div>

  <DepartmentDeleteModal :id="'department-delete-modal'" :closeModal="() => {
    departmentDeleteModal!.hide();
  }" :onSubmit="handleDeleteDepartmentSubmit" title="确定删除该部门吗" content="删除部门"></DepartmentDeleteModal>
  <DepartmentUpsertModal :id="'department-upsert-modal'" :onSubmit="handleUpsertDepartmentSubmit" :closeModal="() => {
    availableDepartments = undefined
    departmentUpsertModal!.hide();
  }" :department="selectedDepartment" :availableDepartments="availableDepartments">
  </DepartmentUpsertModal>
</template>

<script setup lang="ts">
import Breadcrumbs from "@/components/Breadcrumbs.vue";
import Button from "@/components/Button.vue";
import DepartmentUpsertModal from "@/components/DepartmentUpsertModal.vue";
import MobileCardList from "@/components/MobileCardList.vue";
import DepartmentDeleteModal from "@/components/PopupModal.vue";
import TableButton from "@/components/TableButton.vue";
import TableFormLayout from "@/components/TableFormLayout.vue";
import TablePagination from "@/components/TablePagination.vue";
import type { DepartmentUpsertModel } from "@/types/department";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { nextTick, onMounted, ref } from "vue";
import type { components } from "../api/types/schema";
import useDepartmentDelete from "../composables/department/useDepartmentDelete";
import { useDepartmentQuery } from "../composables/department/useDepartmentQuery";
import { useDepartmentUpsert } from "../composables/department/useDepartmentUpsert";
import useAlertStore from "../composables/store/useAlertStore";

const name = ref<string>("");
const selectedDepartment = ref<components["schemas"]["Department"]>();
const departmentUpsertModal = ref<ModalInterface>();
const departmentDeleteModal = ref<ModalInterface>();

const {
	departments,
	availableDepartments,
	fetchDepartmentWith,
	total,
	fetchAvailableDepartments,
} = useDepartmentQuery();

const { deleteDepartment } = useDepartmentDelete();
const { upsertDepartment } = useDepartmentUpsert();

const alertStore = useAlertStore();

// 定义表格列配置
const columns = [
  { title: '上级部门', field: 'parentName' },
  { title: '部门名称', field: 'name' },
  { title: '操作', field: 'actions' }
];

onMounted(async () => {
	await fetchDepartmentWith({
		name: name.value,
	});
	initFlowbite();
	const $upsertModalElement: HTMLElement | null = document.querySelector(
		"#department-upsert-modal",
	);
	const $deleteModalElement: HTMLElement | null = document.querySelector(
		"#department-delete-modal",
	);
	if ($upsertModalElement) {
		departmentUpsertModal.value = new Modal($upsertModalElement, {});
	}
	if ($deleteModalElement) {
		departmentDeleteModal.value = new Modal($deleteModalElement, {});
	}
});

const handleUpsertDepartmentSubmit = async (
	department: DepartmentUpsertModel,
) => {
	await upsertDepartment(department);
	departmentUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	await fetchDepartmentWith({
		name: name.value,
	});
};

const handleUpsertDepartmentClick = async (
	department?: components["schemas"]["Department"],
) => {
	selectedDepartment.value = department;
	await fetchAvailableDepartments(selectedDepartment.value?.id);
	await nextTick(() => {
		departmentUpsertModal.value?.show();
	});
};

const handleDeleteDepartmentSubmit = async () => {
	if (!selectedDepartment?.value?.id) return;
	await deleteDepartment(selectedDepartment.value.id);
	departmentDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "删除成功",
		level: "success",
	});
	await fetchDepartmentWith({
		name: name.value,
	});
};

const handleDeleteDepartmentClick = async (
	department: components["schemas"]["Department"],
) => {
	selectedDepartment.value = department;
	await nextTick(() => {
		departmentDeleteModal.value?.show();
	});
};

const handleSearch = async () => {
	await fetchDepartmentWith({
		name: name.value,
	});
};

const handlePageChange = async (page: number, size: number) => {
	await fetchDepartmentWith(
		{
			name: name.value,
		},
		page,
		size,
	);
};
</script>

<style scoped></style>
