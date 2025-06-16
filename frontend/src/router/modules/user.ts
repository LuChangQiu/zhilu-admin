import type { RouteRecordRaw } from "vue-router";
import { EPermission, Routes } from "../constants";

const userManagementRoutes: RouteRecordRaw[] = [
	{
		path: Routes.USERVIEW.path,
		name: Routes.USERVIEW.name,
		component: () => import("@/views/UserManagementPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.ROLEVIEW.path,
		name: Routes.ROLEVIEW.name,
		component: () => import("@/views/RoleManagementPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.BINDROLEVIEW.path,
		name: Routes.BINDROLEVIEW.name,
		component: () => import("@/views/UserRoleAssignmentPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.BINDDEPARTMENTVIEW.path,
		name: Routes.BINDDEPARTMENTVIEW.name,
		component: () => import("@/views/UserDepartmentAssignmentPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.BINDPERMISSIONVIEW.path,
		name: Routes.BINDPERMISSIONVIEW.name,
		component: () => import("@/views/RolePermissionAssignmentPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.PERMISSIONVIEW.path,
		name: Routes.PERMISSIONVIEW.name,
		component: () => import("@/views/PermissionManagementPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: Routes.BINDPOSITIONVIEW.path,
		name: Routes.BINDPOSITIONVIEW.name,
		component: () => import("@/views/UserPositionAssignmentPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
];

export default userManagementRoutes;
