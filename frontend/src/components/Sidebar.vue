<template>
	<aside id="logo-sidebar"
		class="fixed top-0 left-0 z-30 w-64 min-h-screen overflow-y-auto pt-20 transition-transform -translate-x-full bg-white border-r border-gray-200 sm:translate-x-0  "
		aria-label="Sidebar">
		<div class="h-full px-3 pb-4 overflow-y-auto bg-white ">
			<ul class="space-y-2 font-medium">
				<li v-for="item in menuItems" :key="item.path">
					<RouterLink :to="item.path"
						class="flex items-center p-2 gap-x-2 text-gray-900 rounded-lg  hover:bg-gray-100  group"
						:class="{ 'bg-gray-100 ': isActive(item.path) }">
						<component :is="item.icon"
							class="shrink-0 text-gray-700 transition duration-75  group-hover:text-gray-900 " />
						<span>{{ item.title }}</span>
					</RouterLink>
				</li>
			</ul>
		</div>
	</aside>
</template>

<script setup lang="ts">
import { RoutePath } from "@/router/constants";
import { initFlowbite } from "flowbite";
import { onMounted } from "vue";
import { RouterLink, useRoute } from "vue-router";

import DepartmentIcon from "./icons/DepartmentIcon.vue";
import PermissionIcon from "./icons/PermissionIcon.vue";
import PositionIcon from "./icons/PositionIcon.vue";
import RoleIcon from "./icons/RoleIcon.vue";
import SchedulerIcon from "./icons/SchedulerIcon.vue";
import SettingsIcon from "./icons/SettingsIcon.vue";
import UsersIcon from "./icons/UsersIcon.vue";
import AiChatIcon from "./icons/AiChatIcon.vue";
import LlmConfigIcon from "./icons/LlmConfigIcon.vue";

// 菜单配置
const menuItems = [
	{
		title: "用户管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.USERVIEW}`,
		icon: UsersIcon,
	},
	{
		title: "角色管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.ROLEVIEW}`,
		icon: RoleIcon,
	},
	{
		title: "权限管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.PERMISSIONVIEW}`,
		icon: PermissionIcon,
	},
	{
		title: "部门管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.DEPARTMENTVIEW}`,
		icon: DepartmentIcon,
	},
	{
		title: "岗位管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.POSITIONVIEW}`,
		icon: PositionIcon,
	},
	{
		title: "个人中心",
		path: `${RoutePath.DASHBOARD}/${RoutePath.SETTINGS}`,
		icon: SettingsIcon,
	},
	{
		title: "定时任务",
		path: `${RoutePath.DASHBOARD}/${RoutePath.SCHEDULERVIEW}`,
		icon: SchedulerIcon,
	},
	{
		title: "AI 对话",
		path: `${RoutePath.DASHBOARD}/${RoutePath.AICHATVIEW}`,
		icon: AiChatIcon,
	},
	{
		title: "大模型管理",
		path: `${RoutePath.DASHBOARD}/${RoutePath.LLMCONFIGVIEW}`,
		icon: LlmConfigIcon,
	},
];

const route = useRoute();

const isActive = (path: string) => {
	return route.path === path;
};

onMounted(() => {
	initFlowbite();
});
</script>
