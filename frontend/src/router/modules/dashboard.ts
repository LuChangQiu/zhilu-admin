import type { RouteRecordRaw } from "vue-router";
import Dashboard from "../../components/Dashboard.vue";
import { EPermission, ERole, Routes } from "../constants";
import aiRoutes from "./ai";
import userManagementRoutes from "./user";

const dashboardRoutes: RouteRecordRaw = {
	path: Routes.DASHBOARD.path,
	name: Routes.DASHBOARD.name,
	component: Dashboard,
	meta: {
		requiresAuth: true,
	},
	children: [
		{
			path: Routes.OVERVIEW.path,
			name: Routes.OVERVIEW.name,
			component: () => import("@/views/OverView.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: Routes.SETTINGS.path,
			name: Routes.SETTINGS.name,
			component: () => import("@/views/SettingsView.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		...userManagementRoutes,
		...aiRoutes,
		{
			path: Routes.NOTFOUND.path,
			name: Routes.NOTFOUND.name,
			component: () => import("@/views/NotFound.vue"),
		},
		{
			path: Routes.SCHEDULERVIEW.path,
			name: Routes.SCHEDULERVIEW.name,
			component: () => import("@/views/SchedulerView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_SCHEDULER_PERMISSION,
			},
		},
		{
			path: Routes.DEPARTMENTVIEW.path,
			name: Routes.DEPARTMENTVIEW.name,
			component: () => import("@/views/DepartmentView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_DEPARTMENT_PERMISSION,
			},
		},
		{
			path: Routes.POSITIONVIEW.path,
			name: Routes.POSITIONVIEW.name,
			component: () => import("@/views/PositionView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_POSITION_PERMISSION,
			},
		},
	],
};

export default dashboardRoutes;
