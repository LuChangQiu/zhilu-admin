import type { RouteRecordRaw } from "vue-router";
import Dashboard from "../../components/Dashboard.vue";
import OverView from "../../views/OverView.vue";
import { EPermission, ERole, RouteName, RoutePath } from "../constants";
import userManagementRoutes from "./user";
import aiRoutes from "./ai";

const dashboardRoutes: RouteRecordRaw = {
	path: RoutePath.DASHBOARD,
	name: RouteName.DASHBOARD,
	component: Dashboard,
	meta: {
		requiresAuth: true,
	},
	children: [
		{
			path: RoutePath.OVERVIEW,
			name: RouteName.OVERVIEW,
			component: () => import("@/views/OverView.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		{
			path: RoutePath.SETTINGS,
			name: RouteName.SETTINGS,
			component: () => import("@/views/SettingsView.vue"),
			meta: {
				requiresAuth: true,
			},
		},
		...userManagementRoutes,
		...aiRoutes,
		{
			path: RoutePath.NOTFOUND,
			name: RouteName.NOTFOUND,
			component: () => import("@/views/NotFound.vue"),
		},
		{
			path: RoutePath.SCHEDULERVIEW,
			name: RouteName.SCHEDULERVIEW,
			component: () => import("@/views/SchedulerView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_SCHEDULER_PERMISSION,
			},
		},
		{
			path: RoutePath.DEPARTMENTVIEW,
			name: RouteName.DEPARTMENTVIEW,
			component: () => import("@/views/DepartmentView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_DEPARTMENT_PERMISSION,
			},
		},
		{
			path: RoutePath.POSITIONVIEW,
			name: RouteName.POSITIONVIEW,
			component: () => import("@/views/PositionView.vue"),
			meta: {
				requiresAuth: true,
				hasPermission: EPermission.READ_POSITION_PERMISSION,
			},
		},
	],
};

export default dashboardRoutes;
