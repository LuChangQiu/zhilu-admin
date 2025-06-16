import type { RouteRecordRaw } from "vue-router";
import { Routes } from "../constants";

const authRoutes: RouteRecordRaw[] = [
	{
		path: Routes.HOME.path,
		name: Routes.HOME.name,
		redirect: {
			name: Routes.LOGIN.name,
		},
	},
	{
		path: Routes.LOGIN.path,
		name: Routes.LOGIN.name,
		component: () => import("../../views/LoginView.vue"),
	},
];

export default authRoutes;
