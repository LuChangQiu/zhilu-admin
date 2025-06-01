import type { RouteRecordRaw } from "vue-router";
import { EPermission, RouteName, RoutePath } from "../constants";

const aiRoutes: RouteRecordRaw[] = [
	{
		path: RoutePath.LLMCONFIGVIEW,
		name: RouteName.LLMCONFIGVIEW,
		component: () => import("@/views/LlmConfigView.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_LLM_CONFIG_PERMISSION,
		},
	},
];

export default aiRoutes;
