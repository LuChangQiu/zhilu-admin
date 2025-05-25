import client from "../../api/client";

const useAiAction = () => {
	const actionChat = (message: string) => {
		return client.POST("/ai/action/chat", {
			body: message,
		});
	};

	return { actionChat };
};
