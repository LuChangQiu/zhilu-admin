import type { RouteRecordRaw } from "vue-router";
import { EPermission, ERole, RouteName, RoutePath } from "../constants";

const userManagementRoutes: RouteRecordRaw[] = [
	{
		path: RoutePath.USERVIEW,
		name: RouteName.USERVIEW,
		component: () => import("@/views/UserView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.ROLEVIEW,
		name: RouteName.ROLEVIEW,
		component: () => import("@/views/RoleView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.BINDROLEVIEW,
		name: RouteName.BINDROLEVIEW,
		component: () => import("@/views/BindRoleView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.BINDDEPARTMENTVIEW,
		name: RouteName.BINDDEPARTMENTVIEW,
		component: () => import("@/views/BindDepartmentView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.BINDPERMISSIONVIEW,
		name: RouteName.BINDPERMISSIONVIEW,
		component: () => import("@/views/BindPermissionView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.PERMISSIONVIEW,
		name: RouteName.PERMISSIONVIEW,
		component: () => import("@/views/PermissionView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
	{
		path: RoutePath.BINDPOSITIONVIEW,
		name: RouteName.BINDPOSITIONVIEW,
		component: () => import("@/views/BindPositionView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.WRITE_USER_ROLE_PERMISSION,
		},
	},
];

export default userManagementRoutes;
