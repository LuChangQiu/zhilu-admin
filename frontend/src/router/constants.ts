import type { RouteLocationRaw } from "vue-router";

export type RouteConfig = {
	path: string;
	name: string;
	fullPath: () => string;
	withParams: <T extends Record<string, string | number>>(
		params: T,
	) => RouteLocationRaw;
};

// 基础路由
export const BaseRoutes = {
	HOME: {
		path: "/",
		name: "home",
		fullPath: () => "/",
		withParams: () => ({ name: "home" }),
	},
	LOGIN: {
		path: "/login",
		name: "login",
		fullPath: () => "/login",
		withParams: () => ({ name: "login" }),
	},
	DASHBOARD: {
		path: "/dashboard",
		name: "dashboard",
		fullPath: () => "/dashboard",
		withParams: () => ({ name: "dashboard" }),
	},
	NOTFOUND: {
		path: ":pathMatch(.*)*",
		name: "notfound",
		fullPath: () => "/dashboard/:pathMatch(.*)*",
		withParams: () => ({ name: "notfound" }),
	},
	GLOBAL_NOTFOUND: {
		path: "/:pathMatch(.*)*",
		name: "global-notfound",
		fullPath: () => "/:pathMatch(.*)*",
		withParams: () => ({ name: "global-notfound" }),
	},
} as const;

// 仪表盘子路由
export const DashboardRoutes = {
	OVERVIEW: {
		path: "overview",
		name: "overview",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/overview`,
		withParams: () => ({ name: "overview" }),
	},
	SETTINGS: {
		path: "settings",
		name: "settings",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/settings`,
		withParams: () => ({ name: "settings" }),
	},
} as const;

// 用户管理相关路由
export const UserRoutes = {
	USERVIEW: {
		path: "users",
		name: "users",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/users`,
		withParams: () => ({ name: "users" }),
	},
	ROLEVIEW: {
		path: "roles",
		name: "roles",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/roles`,
		withParams: () => ({ name: "roles" }),
	},
	BINDROLEVIEW: {
		path: "bind-roles/:userId",
		name: "bind-roles",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/bind-roles/:userId`,
		withParams: <T extends { userId: string | number }>(params: T) => ({
			name: "bind-roles",
			params: { userId: params.userId.toString() },
		}),
	},
	BINDPERMISSIONVIEW: {
		path: "bind-permissions/:roleId",
		name: "bind-permissions",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/bind-permissions/:roleId`,
		withParams: <T extends { roleId: string | number }>(params: T) => ({
			name: "bind-permissions",
			params: { roleId: params.roleId.toString() },
		}),
	},
	BINDDEPARTMENTVIEW: {
		path: "bind-departments/:userId",
		name: "bind-departments",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/bind-departments/:userId`,
		withParams: <T extends { userId: string | number }>(params: T) => ({
			name: "bind-departments",
			params: { userId: params.userId.toString() },
		}),
	},
	BINDPOSITIONVIEW: {
		path: "bind-positions/:userId",
		name: "bind-positions",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/bind-positions/:userId`,
		withParams: <T extends { userId: string | number }>(params: T) => ({
			name: "bind-positions",
			params: { userId: params.userId.toString() },
		}),
	},
	PERMISSIONVIEW: {
		path: "permissions",
		name: "permissions",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/permissions`,
		withParams: () => ({ name: "permissions" }),
	},
	DEPARTMENTVIEW: {
		path: "departments",
		name: "departments",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/departments`,
		withParams: () => ({ name: "departments" }),
	},
	POSITIONVIEW: {
		path: "positions",
		name: "positions",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/positions`,
		withParams: () => ({ name: "positions" }),
	},
} as const;

// AI相关路由
export const AiRoutes = {
	LLMCONFIGVIEW: {
		path: "llm/config",
		name: "llm-config",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/llm/config`,
		withParams: () => ({ name: "llm-config" }),
	},
	SCHEDULERVIEW: {
		path: "scheduler",
		name: "scheduler",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/scheduler`,
		withParams: () => ({ name: "scheduler" }),
	},
	KNOWLEDGEVIEW: {
		path: "knowledge",
		name: "knowledge",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/knowledge`,
		withParams: () => ({ name: "knowledge" }),
	},
	KNOWLEDGEDOCVIEW: {
		path: "knowledge/:libraryId",
		name: "knowledge-docs",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/knowledge/:libraryId`,
		withParams: <T extends { libraryId: string | number }>(params: T) => ({
			name: "knowledge-docs",
			params: { libraryId: params.libraryId.toString() },
		}),
	},
	KNOWLEDGESEGMENTSVIEW: {
		path: "knowledge/:libraryId/:docId",
		name: "knowledge-segments",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/knowledge/:libraryId/:docId`,
		withParams: <
			T extends { libraryId: string | number; docId: string | number },
		>(
			params: T,
		) => ({
			name: "knowledge-segments",
			params: {
				libraryId: params.libraryId.toString(),
				docId: params.docId.toString(),
			},
		}),
	},
} as const;

// 系统管理相关路由
export const SystemRoutes = {
	AOPLOGVIEW: {
		path: "aop-logs",
		name: "aop-logs",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/aop-logs`,
		withParams: () => ({ name: "aop-logs" }),
	},
	AOPLOGDETAILVIEW: {
		path: "aop-logs/:id",
		name: "aop-log-detail",
		fullPath: () => `${BaseRoutes.DASHBOARD.path}/aop-logs/:id`,
		withParams: <T extends { id: string | number }>(params: T) => ({
			name: "aop-log-detail",
			params: { id: params.id.toString() },
		}),
	},
} as const;

export const Routes = {
	...BaseRoutes,
	...DashboardRoutes,
	...UserRoutes,
	...AiRoutes,
	...SystemRoutes,
} as const;

export enum ERole {
	ADMIN = "ADMIN",
	USER = "GENERAL",
}

export enum EPermission {
	READ_POSITION_PERMISSION = "READ_POSITION_PERMISSION",
	WRITE_POSITION_PERMISSION = "WRITE_POSITION_PERMISSION",
	READ_DEPARTMENT_PERMISSION = "READ_DEPARTMENT_PERMISSION",
	WRITE_DEPARTMENT_PERMISSION = "WRITE_DEPARTMENT_PERMISSION",
	READ_SCHEDULER_PERMISSION = "READ_SCHEDULER_PERMISSION",
	WRITE_SCHEDULER_PERMISSION = "WRITE_SCHEDULER_PERMISSION",
	WRITE_USER_ROLE_PERMISSION = "WRITE_USER_ROLE_PERMISSION",
	DELETE_USER_ROLE_PERMISSION = "DELETE_USER_ROLE_PERMISSION",
	READ_USER_ROLE_PERMISSION = "READ_USER_ROLE_PERMISSION",
	READ_LLM_CONFIG_PERMISSION = "READ_LLM_CONFIG_PERMISSION",
	WRITE_LLM_CONFIG_PERMISSION = "WRITE_LLM_CONFIG_PERMISSION",
}
