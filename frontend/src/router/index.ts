import { createRouter, createWebHistory } from "vue-router";
import type { RouteRecordRaw } from "vue-router";
import { setupGuards } from "./guards";

import authRoutes from "./modules/auth";
import dashboardRoutes from "./modules/dashboard";
import errorRoutes from "./modules/error";
import { RouteName } from "./constants";

const routes: RouteRecordRaw[] = [
	dashboardRoutes,
	...authRoutes,
	...errorRoutes,
];

const router = createRouter({
	history: createWebHistory(import.meta.env.BASE_URL),
	routes,
});

router.onError((err) => {
	console.error("router err:", err);
	router.push(RouteName.USERVIEW);
	return false;
});

const isMockEnabled = import.meta.env.VITE_ENABLE_MOCK === "true";
if (!isMockEnabled) {
	setupGuards(router);
}

export default router;
