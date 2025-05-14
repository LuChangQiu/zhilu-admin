import client from "@/api/client";
import { ref } from "vue";
import useAuthStore from "../store/useAuthStore";
import useUserStore from "../store/useUserStore";

const useUserAuth = () => {
	const isAuthenticated = ref(false);
	const authStore = useAuthStore();
	const userStore = useUserStore();

	const queryCurrentUser = async () => {
		const { data } = await client.GET("/iam/me");
		return data;
	};

	const refreshCurrentUser = async () => {
		const currentUser = await queryCurrentUser();
		if (currentUser) {
			userStore.set(currentUser);
			isAuthenticated.value = true;
		}
	};

	const upsertCurrentUser = async ({
		username,
		password,
		enable,
	}: {
		username: string;
		password?: string | null;
		enable: boolean;
	}) => {
		await client.POST("/iam/me", {
			body: {
				username,
				password: password ?? undefined,
				enable,
			},
		});
		await refreshCurrentUser();
	};

	const signIn = async (username: string, password: string) => {
		const signInResponse = await client.POST("/auth/sign-in", {
			body: {
				username,
				password,
			},
		});
		authStore.set(
			signInResponse.response.headers.get("authorization") ?? undefined,
		);
		await refreshCurrentUser();
	};

	const signOut = () => {
		authStore.remove();
		isAuthenticated.value = false;
		userStore.remove();
	};

	return {
		isAuthenticated,
		signIn,
		signOut,
		queryCurrentUser,
		upsertCurrentUser,
	};
};

export default useUserAuth;
