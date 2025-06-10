<template>
  <nav class="flex items-center flex-col md:flex-row flex-wrap justify-between py-4 px-3 sm:px-5"
    aria-label="Table navigation">
    <span class="text-xs sm:text-sm font-normal text-gray-500 mb-4 md:mb-0 block w-full md:inline md:w-auto">
      显示
      <span class="font-semibold text-gray-900">
        {{ displayRange.start }}-{{ displayRange.end }}
      </span>
      共
      <span class="font-semibold text-gray-900">{{ total }}</span> 条
    </span>
    <ul class="inline-flex -space-x-px rtl:space-x-reverse text-sm h-8">
      <li>
        <a href="#" @click.prevent="handlePageChangeClick(currentPage - 1)" :class="[
              'flex items-center justify-center px-3 h-8 ms-0 leading-tight text-gray-500 bg-white border border-gray-300 rounded-s-lg hover:bg-gray-100 hover:text-gray-700',
              { 'opacity-50 cursor-not-allowed': isFirstPage }
            ]">上一页</a>
      </li>
      <li v-for="page in pageNumbers" :key="page">
        <button @click.prevent="handlePageChangeClick(page)" :class="[
              'flex items-center justify-center px-3 h-8 leading-tight border border-gray-300 hover:bg-gray-100 hover:text-gray-700',
              currentPage === page 
                ? 'text-blue-600 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 font-medium' 
                : 'text-gray-500 bg-white'
            ]">{{ page }}</button>
      </li>

      <li>
        <button @click.prevent="handlePageChangeClick(currentPage + 1)" :class="[
              'flex items-center justify-center px-3 h-8 leading-tight text-gray-500 bg-white border border-gray-300 rounded-e-lg hover:bg-gray-100 hover:text-gray-700',
              { 'opacity-50 cursor-not-allowed': isLastPage }
            ]">下一页</button>
      </li>
    </ul>
  </nav>
</template>

<script setup lang="ts">
import { usePagination } from "@/composables/page";
import { watch } from "vue";

const { pageChange, total } = defineProps<{
	pageChange: (page: number, size: number) => Promise<void>;
	total: number;
}>();

const {
	currentPage,
	pageNumbers,
	pageSize,
	displayRange,
	isFirstPage,
	isLastPage,
	totalPages,
	updatePaginationState,
} = usePagination();

const handlePageChangeClick = async (page: number) => {
	if (page < 1 || page > totalPages.value) return;
	await pageChange(page, pageSize.value);
	updatePaginationState({
		currentPage: page,
		pageSize: pageSize.value,
		total,
	});
};

watch(
	() => total,
	() => {
		updatePaginationState({
			currentPage: currentPage.value,
			pageSize: pageSize.value,
			total,
		});
	},
);
</script>
