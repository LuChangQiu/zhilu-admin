import { fetchEventSource } from "@microsoft/fetch-event-source";
import { ref } from "vue";
import client from "../../api/client";
import useAuthStore from "../store/useAuthStore";

const authStore = useAuthStore();

export const useAiChat = () => {
	const messages = ref<
		{
			content: string;
			type: "chat" | "action";
			isUser: boolean;
			username: string;
			command?: string;
		}[]
	>([]);
	const isLoading = ref(false);

	let currentController: AbortController | null = null;

	const chat = async (message: string) => {
		isLoading.value = true;
		const ctrl = new AbortController();
		currentController = ctrl;
		messages.value.push({
			content: "",
			type: "chat",
			isUser: false,
			username: "知路智能体",
		});
		try {
			const baseUrl = `${import.meta.env.VITE_BASE_URL}`;
			await fetchEventSource(`${baseUrl}/ai/chat`, {
				method: "POST",
				headers: {
					Authorization: authStore.get(),
					"Content-Type": "application/json",
				},
				body: message,
				signal: ctrl.signal,
				onmessage(ev) {
					messages.value[messages.value.length - 1].content += ev.data;
				},
				onclose() {
					console.log("onclose");
				},
				onerror(err) {
					throw err;
				},
			});
		} catch (error) {
			messages.value.pop();
			throw error;
		} finally {
			isLoading.value = false;
		}
	};

	const actionChat = async (message: string) => {
		isLoading.value = true;
		try {
			const { data } = await client.POST("/ai/action/chat", {
				body: message,
			});
			messages.value.push({
				content: data?.action
					? "接收到指令，请您执行。"
					: "未找到有效指令，请告诉我更加准确的信息。",
				type: "action",
				isUser: false,
				username: "知路智能体",
				command: data?.action,
			});
			return data;
		} finally {
			isLoading.value = false;
		}
	};

	const cancel = () => {
		if (currentController) {
			currentController.abort();
			currentController = null;
		}
	};

	return { messages, chat, isLoading, cancel, actionChat };
};
