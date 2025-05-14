import client from "@/api/client";
import { ref } from "vue";
import type { components } from "../../api/types/schema";
export const useJobsPaginationQuery = () => {
	const total = ref<number>(0);
	const jobs = ref<components["schemas"]["JobTriggerDto"][]>();
	const fetchJobsWith = async (
		queryParam?: {
			name?: string;
		},
		page = 1,
		size = 10,
	) => {
		const { data } = await client.GET("/scheduler/page-query", {
			params: {
				query: {
					pageRequestDto: {
						page: page,
						size: size,
					},
					queryDto: {
						name: queryParam?.name,
					},
				},
			},
		});
		total.value = !data || !data.total ? 0 : data.total;
		jobs.value = data?.data ?? [];
	};

	return {
		total,
		jobs,
		fetchJobsWith,
	};
};
