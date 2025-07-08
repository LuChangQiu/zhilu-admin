import type { RouteRecordRaw } from "vue-router";
import Dashboard from "../../components/layout/Dashboard.vue";
import { EPermission, Routes } from "../constants";
import aiRoutes from "./ai";
import systemRoutes from "./system";
import userManagementRoutes from "./user";

const dashboardRoutes: RouteRecordRaw = {
	path: Routes.DASHBOARD.path,
	name: Routes.DASHBOARD.name,
	component: Dashboard,
	meta: {
		requiresAuth: true,
	},
	children: [
		...userManagementRoutes,
		...aiRoutes,
		...systemRoutes,
		{
			path: Routes.OVERVIEW.path,
			name: Routes.OVERVIEW.name,
			component: () => import("@/views/DashboardPage.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: Routes.SETTINGS.path,
			name: Routes.SETTINGS.name,
			component: () => import("@/views/SystemSettingsPage.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: Routes.NOTFOUND.path,
			name: Routes.NOTFOUND.name,
			component: () => import("@/views/NotFoundPage.vue"),
		},
		{
			path: Routes.SCHEDULERVIEW.path,
			name: Routes.SCHEDULERVIEW.name,
			component: () => import("@/views/SchedulerManagementPage.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_SCHEDULER_PERMISSION,
			},
		},
		{
			path: Routes.DEPARTMENTVIEW.path,
			name: Routes.DEPARTMENTVIEW.name,
			component: () => import("@/views/DepartmentManagementPage.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_DEPARTMENT_PERMISSION,
			},
		},
		{
			path: Routes.POSITIONVIEW.path,
			name: Routes.POSITIONVIEW.name,
			component: () => import("@/views/PositionManagementPage.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_POSITION_PERMISSION,
			},
		},
	],
};

export default dashboardRoutes;
