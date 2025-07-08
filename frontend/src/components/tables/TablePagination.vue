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
			<li v-for="(item, index) in visiblePageNumbers" :key="index">
				<template v-if="item.type === 'page'">
					<button @click.prevent="item.page && handlePageChangeClick(item.page)" :class="[
						'flex items-center justify-center px-3 h-8 leading-tight border border-gray-300 hover:bg-gray-100 hover:text-gray-700',
						currentPage === item.page 
							? 'text-blue-600 bg-blue-50 hover:bg-blue-100 hover:text-blue-700 font-medium' 
							: 'text-gray-500 bg-white'
					]">{{ item.page }}</button>
				</template>
				<template v-else-if="item.type === 'ellipsis'">
					<span
						class="flex items-center justify-center px-3 h-8 leading-tight border border-gray-300 bg-white text-gray-500">
						&hellip;
					</span>
				</template>
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
import { usePagination } from "@/composables/common/usePagination";
import { ref, watch } from "vue";

const props = defineProps<{
	pageChange?: (page: number, size: number) => Promise<void>;
	total: number;
	currentPage?: number;
	totalPages?: number;
	maxVisiblePages?: number;
}>();

const emit = defineEmits<{
	'change-page': [page: number];
}>();

// 创建一个本地的totalPages引用
const localTotalPages = ref<number | undefined>(props.totalPages);

const {
	currentPage,
	pageNumbers,
	visiblePageNumbers,
	pageSize,
	displayRange,
	isFirstPage,
	isLastPage,
	totalPages,
	updatePaginationState,
} = usePagination({
	initialPage: props.currentPage,
	initialTotal: props.total,
	maxVisiblePages: props.maxVisiblePages || 7 // 默认显示7个页码
});

const handlePageChangeClick = async (page: number) => {
	if (page < 1 || page > totalPages.value) return;
	
	if (props.pageChange) {
		// 如果传入了pageChange函数，则调用它
		await props.pageChange(page, pageSize.value);
	} else {
		// 否则触发change-page事件
		emit('change-page', page);
	}
	
	updatePaginationState({
		currentPage: page,
		pageSize: pageSize.value,
		total: props.total,
	});
};

watch(
	() => props.total,
	(newTotal) => {
		updatePaginationState({
			currentPage: currentPage.value,
			pageSize: pageSize.value,
			total: newTotal,
		});
	},
);

watch(
	() => props.currentPage,
	(newVal) => {
		if (newVal !== undefined && newVal !== currentPage.value) {
			updatePaginationState({
				currentPage: newVal,
				pageSize: pageSize.value,
				total: props.total,
			});
		}
	}
);

watch(
	() => props.totalPages,
	(newVal) => {
		if (newVal !== undefined) {
			localTotalPages.value = newVal;
		}
	}
);
</script>
