import { fetchEventSource } from "@microsoft/fetch-event-source";
import { ref } from "vue";
import useAuthStore from "../store/useAuthStore";
import client from "../../api/client";

const authStore = useAuthStore();

export const useAiChat = () => {
	const messages = ref<string[]>([]);
	const isLoading = ref(false);

	let currentController: AbortController | null = null;

	const chat = async (message: string) => {
		isLoading.value = true;
		messages.value.push(message);
		messages.value.push("");
		const ctrl = new AbortController();
		currentController = ctrl;

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
					messages.value[messages.value.length - 1] += ev.data;
				},
				onclose() {
					console.log("onclose");
				},
				onerror(err) {
					throw err;
				},
			});
		} finally {
			isLoading.value = false;
		}
	};

	const actionChat = async (message: string) => {
		messages.value.push(message);
		messages.value.push("");
		isLoading.value = true;
		try {
			const { data } = await client.POST("/ai/action/chat", {
				body: message,
			});
			messages.value[messages.value.length - 1] += "接收到指令，请您执行。";
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
