import type { ComponentPublicInstance } from "vue";
import type { Router } from "vue-router";
import { RoutePath } from "../router/constants";
import {
	ForbiddenError,
	InternalServerError,
	RequestError,
	UnAuthError,
} from "../types/error";

const makeErrorHandler =
	(
		router: Router,
		signOut: () => void,
		showAlert: ({
			content,
			level,
		}: {
			content: string;
			level: "info" | "success" | "warning" | "error";
		}) => void,
	) =>
	(err: unknown, instance: ComponentPublicInstance | null, info: string) => {
		console.error(err);
		if (err instanceof UnAuthError) {
			signOut();
			router.push(RoutePath.LOGIN);
			showAlert({
				level: "error",
				content: err.message,
			});
		} else if (err instanceof ForbiddenError) {
			showAlert({
				level: "error",
				content: err.message,
			});
		} else if (err instanceof RequestError) {
			showAlert({
				level: "error",
				content: err.message,
			});
		} else if (err instanceof InternalServerError) {
			showAlert({
				level: "error",
				content: err.detail ?? err.message,
			});
		} else {
			showAlert({
				level: "error",
				content: "发生异常，请稍候再试",
			});
		}
	};

export default makeErrorHandler;
