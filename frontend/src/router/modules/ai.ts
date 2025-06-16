import type { RouteRecordRaw } from "vue-router";
import { EPermission, Routes } from "../constants";

const aiRoutes: RouteRecordRaw[] = [
	{
		path: Routes.LLMCONFIGVIEW.path,
		name: Routes.LLMCONFIGVIEW.name,
		component: () => import("@/views/LlmConfigurationPage.vue"),
		meta: {
			requiresAuth: true,
			hasPermission: EPermission.READ_LLM_CONFIG_PERMISSION,
		},
	},
];

export default aiRoutes;
