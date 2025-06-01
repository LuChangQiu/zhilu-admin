import useUserStore from "@/composables/store/useUserStore";
import type { NavigationGuard, Router } from "vue-router";
import type { RouteMeta } from "../types/router";
import { RoutePath } from "./constants";
import useAlertStore from "@/composables/store/useAlertStore";

export const authGuard: NavigationGuard = (to) => {
	const userStore = useUserStore();
	if (to.meta.requiresAuth && !userStore.user) {
		return {
			path: RoutePath.LOGIN,
			query: { redirect: to.fullPath },
		};
	}
	if (to.path === RoutePath.LOGIN && userStore.user) {
		return { path: `${RoutePath.DASHBOARD}/${RoutePath.USERVIEW}` };
	}
};

export const permissionGuard: NavigationGuard = (to) => {
	const userStore = useUserStore();
	const { showAlert } = useAlertStore();
	const routeMeta: RouteMeta = to.meta;
	if (routeMeta.hasPermission) {
		const hasPermission = userStore.permissionCodes?.includes(
			routeMeta.hasPermission,
		);
		if (!hasPermission) {
			showAlert({
				content: `没有访问页面所需的 ${routeMeta.hasPermission} 权限`,
				level: "error",
			});
			return false;
		}
	}
};

export const roleGuard: NavigationGuard = (to) => {
	const userStore = useUserStore();
	const { showAlert } = useAlertStore();
	const routeMeta: RouteMeta = to.meta;
	if (routeMeta.hasRole) {
		const hasRole = userStore.roleCodes?.includes(routeMeta.hasRole);
		if (!hasRole) {
			showAlert({
				content: `没有访问页面所需的 ${routeMeta.hasRole} 角色`,
				level: "error",
			});
			return false;
		}
	}
};

export const setupGuards = (router: Router) => {
	router.beforeEach(authGuard);
	router.beforeEach(permissionGuard);
	router.beforeEach(roleGuard);
};
