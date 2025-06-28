import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { setupGuards } from "./guards";

import { Routes } from "./constants";
import authRoutes from "./modules/auth";
import dashboardRoutes from "./modules/dashboard";
import errorRoutes from "./modules/error";

const routes: RouteRecordRaw[] = [
	dashboardRoutes,
	...authRoutes,
	...errorRoutes,
	{
		path: Routes.HOME.path,
		name: Routes.HOME.name,
		redirect: {
			path: Routes.USERVIEW.fullPath(),
		},
	},
];

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes,
});

router.onError((err) => {
	console.error("router err:", err);
	router.push(Routes.USERVIEW.fullPath());
	return false;
});

const isMockEnabled = import.meta.env.VITE_ENABLE_MOCK === "true";
if (!isMockEnabled) {
	setupGuards(router);
}

export default router;
