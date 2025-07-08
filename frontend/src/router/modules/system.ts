import type { RouteRecordRaw } from "vue-router";
import { Routes } from "../constants";

const systemRoutes: RouteRecordRaw[] = [
	{
		path: Routes.AOPLOGVIEW.path,
		name: Routes.AOPLOGVIEW.name,
		component: () => import("@/views/AopLogManagementPage.vue"),
		meta: {
			requiresAuth: true,
		},
	},
	{
		path: Routes.AOPLOGDETAILVIEW.path,
		name: Routes.AOPLOGDETAILVIEW.name,
		component: () => import("@/views/AopLogDetailPage.vue"),
		meta: {
			requiresAuth: true,
		},
	},
];

export default systemRoutes;
