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
	{
		path: Routes.KNOWLEDGEVIEW.path,
		name: Routes.KNOWLEDGEVIEW.name,
		component: () => import("@/views/KnowledgeManagementPage.vue"),
		meta: {
			requiresAuth: true,
		},
	},
	{
		path: Routes.KNOWLEDGEDOCVIEW.path,
		name: Routes.KNOWLEDGEDOCVIEW.name,
		component: () => import("@/views/KnowledgeDocManagementPage.vue"),
		meta: {
			requiresAuth: true,
		},
	},
	{
		path: Routes.KNOWLEDGESEGMENTSVIEW.path,
		name: Routes.KNOWLEDGESEGMENTSVIEW.name,
		component: () => import("@/views/KnowledgeSegmentsPage.vue"),
		meta: {
			requiresAuth: true,
		},
	},
];

export default aiRoutes;
