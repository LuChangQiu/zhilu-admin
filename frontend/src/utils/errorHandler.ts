import type { ComponentPublicInstance } from "vue";
import type { Router } from "vue-router";
import {
	ForbiddenError,
	InternalServerError,
	RequestError,
	ValidationError,
	UnAuthError,
} from "../types/error";
import { z } from "zod";
import { Routes } from "../router/constants";

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
		if (err instanceof ValidationError) {
			showAlert({
				level: "error",
				content: err.message,
			});
		} else if (err instanceof UnAuthError) {
			signOut();
			router.push(Routes.LOGIN.path);
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
		} else if (err instanceof z.ZodError) {
			showAlert({
				level: "error",
				content: err.errors[0].message,
			});
		} else {
			showAlert({
				level: "error",
				content: "发生异常，请稍候再试",
			});
		}
	};

export default makeErrorHandler;
