import { computed, ref } from "vue";

export const useSort = () => {
	const sortFields = ref<
		{
			field: string;
			order: "asc" | "desc" | undefined;
		}[]
	>([]);

	const getSortField = (field: string) => {
		return sortFields.value.find((item) => item.field === field);
	};

	const sortBy = computed(() => {
		return sortFields.value
			.map((item) => `${item.field}:${item.order}`)
			.join(",");
	});

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
};
