import type { RouteRecordRaw } from "vue-router";
import { Routes } from "../constants";

const errorRoutes: RouteRecordRaw[] = [
	{
		path: Routes.GLOBAL_NOTFOUND.path,
		name: Routes.GLOBAL_NOTFOUND.name,
		component: () => import("../../views/NotFoundPage.vue"),
	},
];

export default errorRoutes;
