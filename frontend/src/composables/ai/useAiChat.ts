import { fetchEventSource } from "@microsoft/fetch-event-source";
import { ref } from "vue";
import client from "../../api/client";
import useAuthStore from "../store/useAuthStore";

export const useAiChat = () => {
	const messages = ref<
		{
			content: string;
			type: "chat" | "action";
			isUser: boolean;
			username: string;
			command?: string;
			withLibrary?: boolean;
			libraryName?: string;
		}[]
	>([]);
	const isLoading = ref(false);

	let currentController: AbortController | null = null;

	const chat = async (
		message: string,
		libraryId?: number | null,
		libraryName?: string,
	) => {
		isLoading.value = true;
		const authStore = useAuthStore();
		const ctrl = new AbortController();
		currentController = ctrl;
		messages.value.push({
			content: "",
			type: "chat",
			isUser: false,
			username: "知路智能体",
			withLibrary: libraryId !== undefined,
			libraryName: libraryName,
		});
		try {
			const baseUrl = `${import.meta.env.VITE_BASE_URL}`;
			await fetchEventSource(`${baseUrl}/ai/chat`, {
				method: "POST",
				headers: {
					Authorization: authStore.get(),
					"Content-Type": "application/json",
				},
				body: JSON.stringify({
					mode: libraryId !== undefined ? "WITH_LIBRARY" : "NORMAL",
					libraryId: libraryId,
					message: message,
				}),
				signal: ctrl.signal,
				onmessage(ev) {
					messages.value[messages.value.length - 1].content += ev.data;
				},
				onclose() {
					console.log("onclose");
				},
				onerror(err) {
					console.error(err);
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

	const executeAction = async (message: string) => {
		isLoading.value = true;
		const authStore = useAuthStore();
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
			await fetchEventSource(`${baseUrl}/ai/action/execute`, {
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

	const searchAction = async (message: string) => {
		isLoading.value = true;
		try {
			messages.value.push({
				content: "",
				type: "action",
				isUser: false,
				username: "知路智能体",
				command: undefined,
			});
			const { data } = await client.POST("/ai/action/search", {
				body: message,
			});
			messages.value[messages.value.length - 1].content = data?.action
				? "搜索到功能，请您执行。"
				: "未搜索到指定功能，请告诉我更加准确的信息。";
			messages.value[messages.value.length - 1].command = data?.action;
		} catch (error) {
			messages.value.pop();
			throw error;
		} finally {
			isLoading.value = false;
		}
	};

	const clearConversation = async () => {
		await client.POST("/ai/chat/refresh");
		messages.value = [];
	};

	const cancel = () => {
		if (currentController) {
			currentController.abort();
			currentController = null;
		}
	};

	return {
		messages,
		chat,
		isLoading,
		cancel,
		searchAction,
		executeAction,
		clearConversation,
	};
};
