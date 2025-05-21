import { ref } from "vue";
import client from "../../api/client";

export const useAiChat = () => {
	const messages = ref<string[]>([]);
	const isLoading = ref(false);

	const chat = async (message: string) => {
		isLoading.value = true;
		try {
			const { response } = await client.POST("/ai/chat", {
				body: message,
				parseAs: "stream",
			});
			const reader = response.body?.getReader();
			if (reader) {
				const decoder = new TextDecoder();
				while (true) {
					const { done, value } = await reader.read();
					if (done) break;
					messages.value.push(decoder.decode(value, { stream: true }));
					console.log(decoder.decode(value));
				}
				return;
			}
		} finally {
			isLoading.value = false;
		}
	};
	return { messages, chat, isLoading };
};
