import { computed, ref } from "vue";

export interface PaginationState {
	currentPage: number;
	pageSize: number;
	total: number;
}

export interface UsePaginationOptions {
	initialPage?: number;
	initialPageSize?: number;
	initialTotal?: number;
	maxVisiblePages?: number;
}

/**
 * 分页逻辑Composable - 提供分页相关的状态和操作
 * @param options 分页选项
 * @returns 分页状态和方法
 */
export function usePagination(options: UsePaginationOptions = {}) {
	const {
		initialPage = 1,
		initialPageSize = 10,
		initialTotal = 0,
		maxVisiblePages = 5, // 最多显示的页码数量
	} = options;

	const currentPage = ref(initialPage);
	const pageSize = ref(initialPageSize);
	const total = ref(initialTotal);

	const totalPages = computed(() => Math.ceil(total.value / pageSize.value));

	// 所有页码
	const pageNumbers = computed(() => {
		const pages = [];
		for (let i = 1; i <= totalPages.value; i++) {
			pages.push(i);
		}
		return pages;
	});

	// 智能分页显示，处理页码过多的情况
	const visiblePageNumbers = computed(() => {
		const totalPageCount = totalPages.value;
		const current = currentPage.value;

		// 如果总页数小于等于最大显示页数，则全部显示
		if (totalPageCount <= maxVisiblePages) {
			return pageNumbers.value.map((page) => ({ page, type: "page" }));
		}

		const result = [];
		const sidePages = Math.floor((maxVisiblePages - 3) / 2); // 当前页两侧显示的页数

		// 添加第一页
		result.push({ page: 1, type: "page" });

		// 添加省略号或第二页
		if (current - sidePages > 2) {
			result.push({ page: null, type: "ellipsis" });
		} else if (current > 1) {
			result.push({ page: 2, type: "page" });
		}

		// 添加当前页附近的页码
		const startPage = Math.max(current - sidePages, 2);
		const endPage = Math.min(current + sidePages, totalPageCount - 1);

		for (let i = startPage; i <= endPage; i++) {
			if (i > 2 && i < totalPageCount - 1) {
				result.push({ page: i, type: "page" });
			}
		}

		// 添加省略号或倒数第二页
		if (current + sidePages < totalPageCount - 1) {
			result.push({ page: null, type: "ellipsis" });
		} else if (current < totalPageCount) {
			result.push({ page: totalPageCount - 1, type: "page" });
		}

		// 添加最后一页
		if (totalPageCount > 1) {
			result.push({ page: totalPageCount, type: "page" });
		}

		return result;
	});

	const displayRange = computed(() => {
		const start =
			total.value === 0 ? 0 : (currentPage.value - 1) * pageSize.value + 1;
		const end =
			total.value === 0
				? 0
				: Math.min(currentPage.value * pageSize.value, total.value);
		return { start, end };
	});

	const isFirstPage = computed(
		() => total.value === 0 || currentPage.value === 1,
	);

	const isLastPage = computed(
		() => total.value === 0 || currentPage.value === totalPages.value,
	);

	const updatePaginationState = (state: Partial<PaginationState>) => {
		if (state.currentPage !== undefined) currentPage.value = state.currentPage;
		if (state.pageSize !== undefined) pageSize.value = state.pageSize;
		if (state.total !== undefined) total.value = state.total;
	};

	return {
		currentPage,
		pageSize,
		total,
		totalPages,
		pageNumbers,
		visiblePageNumbers,
		displayRange,
		isFirstPage,
		isLastPage,
		updatePaginationState,
	};
}
