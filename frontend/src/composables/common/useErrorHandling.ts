import useUserAuth from "@/composables/auth/useUserAuth";
import useAlertStore from "@/composables/store/useAlertStore";
import { Routes } from "@/router/constants";
import {
	ForbiddenError,
	InternalServerError,
	RequestError,
	UnAuthError,
	ValidationError,
} from "@/types/ErrorTypes";
import { useRouter } from "vue-router";
import { z } from "zod";

/**
 * 错误处理 Composable - 提供统一的错误处理机制
 * @returns 包含错误处理函数的对象
 */
export function useErrorHandling() {
	const router = useRouter();
	const { signOut } = useUserAuth();
	const alertStore = useAlertStore();

	/**
	 * 处理各类错误，显示对应的提示信息
	 * @param err 错误对象
	 */
	const handleError = (err: unknown) => {
		console.error(err);

		try {
			if (err instanceof ValidationError) {
				alertStore.showAlert({
					level: "error",
					content: err.message,
				});
			} else if (err instanceof UnAuthError) {
				signOut();
				router.push(Routes.LOGIN.path);
				alertStore.showAlert({
					level: "error",
					content: err.message,
				});
			} else if (err instanceof ForbiddenError || err instanceof RequestError) {
				alertStore.showAlert({
					level: "error",
					content: err.message,
				});
			} else if (err instanceof InternalServerError) {
				alertStore.showAlert({
					level: "error",
					content: err.detail ?? err.message,
				});
			} else if (err instanceof z.ZodError) {
				alertStore.showAlert({
					level: "error",
					content: err.errors[0].message,
				});
			} else {
				alertStore.showAlert({
					level: "error",
					content: "发生异常，请稍候再试",
				});
			}
		} catch (e) {
			console.error("Error handler failed:", e);
		}
	};

	return {
		handleError,
	};
}

export default useErrorHandling;
