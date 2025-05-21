import type { RouteRecordRaw } from "vue-router";
import { EPermission, RouteName, RoutePath } from "../constants";

const aiRoutes: RouteRecordRaw[] = [
	{
		path: RoutePath.AICHATVIEW,
		name: RouteName.AICHATVIEW,
		component: () => import("@/views/AiChatView.vue"),
		meta: {
			requiresAuth: true,
			// hasPermission: EPermission.READ_USER_ROLE_PERMISSION,
		},
	},
];

export default aiRoutes;
