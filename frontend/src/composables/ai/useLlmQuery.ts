import { ref } from "vue";
import type { components } from "../../api/types/schema";
import client from "../../api/client";

export const useLlmQuery = () => {
	const total = ref<number>(0);
	const llms = ref<components["schemas"]["LlmVm"][]>([]);

	const fetchLlmConfigs = async (page = 1, size = 10, name?: string) => {
		const { data } = await client.GET("/ai/llm/page-query", {
			params: {
				query: {
					pageRequestDto: {
						page,
						size,
					},
					llmQueryDto: {
						name,
					},
				},
			},
		});
		llms.value = data?.data ?? [];
		total.value = !data || !data.total ? 0 : data.total;
	};

	return {
		llms,
		total,
		fetchLlmConfigs,
	};
};
