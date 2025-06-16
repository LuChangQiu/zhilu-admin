import { computed, ref } from "vue";

export interface SortField {
	field: string;
	order: "asc" | "desc" | undefined;
}

/**
 * 排序逻辑Composable - 提供排序相关的状态和操作
 * @returns 排序状态和方法
 */
export function useSorting() {
	const sortFields = ref<SortField[]>([]);

	/**
	 * 获取指定字段的排序信息
	 * @param field 字段名
	 * @returns 排序字段对象
	 */
	const getSortField = (field: string) => {
		return sortFields.value.find((item) => item.field === field);
	};

	/**
	 * 排序表达式，用于API请求
	 */
	const sortBy = computed(() => {
		return sortFields.value.length
			? sortFields.value.map((item) => `${item.field}:${item.order}`).join(",")
			: undefined;
	});

	/**
	 * 处理字段排序
	 * @param field 字段名
	 */
	const handleSort = async (field: string) => {
		if (sortFields.value?.find((item) => item.field === field)) {
			sortFields.value = sortFields.value?.map((item) =>
				item.field === field
					? { ...item, order: item.order === "asc" ? "desc" : undefined }
					: item,
			);
		} else {
			sortFields.value.push({ field, order: "asc" });
		}
		sortFields.value = sortFields.value?.filter(
			(item) => item.order !== undefined,
		);
	};

	return {
		sortFields,
		sortBy,
		handleSort,
		getSortField,
	};
}
