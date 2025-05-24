import type { components } from "../../api/types/schema";
import client from "../../api/client";

export const useLlmUpdate = () => {
	const updateLlmConfig = async (llm: components["schemas"]["LlmVm"]) => {
		await client.PUT("/ai/llm", {
			body: llm,
		});
	};
	return {
		updateLlmConfig,
	};
};
