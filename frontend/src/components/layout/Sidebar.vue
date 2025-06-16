<template>
	<aside id="logo-sidebar"
		class="fixed top-0 left-0 px-1 w-44 min-h-screen overflow-y-auto pt-20 transform transition-transform duration-300 ease-in-out bg-white border-r border-gray-200"
		:class="[
			isDrawerVisible ? 'translate-x-0' : '-translate-x-full sm:translate-x-0',
			isDrawerVisible ? 'z-30' : ''
		]" aria-label="Sidebar">
		<div class="h-full px-3 pb-4 overflow-y-auto bg-white">
			<ul class="space-y-2 font-medium">
				<li v-for="item in menuItems" :key="item.path">
					<RouterLink :to="item.path"
						class="flex items-center p-2 gap-x-2 text-gray-900 rounded-lg hover:bg-gray-100 group"
						:class="{ 'bg-gray-100': isActive(item.path) }" @click="handleMenuClick">
						<component :is="item.icon"
							class="shrink-0 text-gray-500 transition duration-75 group-hover:text-gray-900" />
						<span>{{ item.title }}</span>
					</RouterLink>
				</li>
			</ul>
		</div>
	</aside>

	<!-- 遮罩层 -->
	<div class="fixed inset-0 bg-gray-900/50 transition-all duration-300 sm:hidden z-20" :class="[
			isDrawerVisible ? 'opacity-100' : 'opacity-0 pointer-events-none'
		]" @click="closeSidebar">
	</div>
</template>

<script setup lang="ts">
import { Routes } from "@/router/constants";
import { initFlowbite } from "flowbite";
import { onMounted, ref } from "vue";
import { RouterLink, useRoute } from "vue-router";

import {
	DepartmentIcon,
	LlmConfigIcon,
	PermissionIcon,
	PositionIcon,
	RoleIcon,
	SchedulerIcon,
	SettingsIcon,
	UsersIcon,
} from "@/components/icons";

const isDrawerVisible = ref(false);
const emit = defineEmits(["menu-click"]);

// 菜单点击处理
const handleMenuClick = () => {
	emit("menu-click");
};

const toggleSidebar = () => {
	isDrawerVisible.value = !isDrawerVisible.value;
};

const openSidebar = () => {
	isDrawerVisible.value = true;
};

const closeSidebar = () => {
	isDrawerVisible.value = false;
};

defineExpose({
	toggleSidebar,
	openSidebar,
	closeSidebar,
	isDrawerVisible,
});

// 菜单配置
const menuItems = [
	{
		title: "用户管理",
		path: Routes.USERVIEW.fullPath(),
		icon: UsersIcon,
	},
	{
		title: "角色管理",
		path: Routes.ROLEVIEW.fullPath(),
		icon: RoleIcon,
	},
	{
		title: "权限管理",
		path: Routes.PERMISSIONVIEW.fullPath(),
		icon: PermissionIcon,
	},
	{
		title: "部门管理",
		path: Routes.DEPARTMENTVIEW.fullPath(),
		icon: DepartmentIcon,
	},
	{
		title: "岗位管理",
		path: Routes.POSITIONVIEW.fullPath(),
		icon: PositionIcon,
	},
	{
		title: "个人中心",
		path: Routes.SETTINGS.fullPath(),
		icon: SettingsIcon,
	},
	{
		title: "定时任务",
		path: Routes.SCHEDULERVIEW.fullPath(),
		icon: SchedulerIcon,
	},
	{
		title: "大模型管理",
		path: Routes.LLMCONFIGVIEW.fullPath(),
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

<style scoped>
.invisible {
	visibility: hidden;
}

.visible {
	visibility: visible;
}

/* 添加移动端样式 */
@media (max-width: 640px) {
	.translate-x-0 {
		transform: translateX(0);
	}

	.-translate-x-full {
		transform: translateX(-100%);
	}
}
</style>
