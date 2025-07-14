<template>
  <nav class="fixed top-0 w-full bg-white border-b border-gray-200 z-40">
    <div class="px-3 py-3 lg:px-5 lg:pl-3">
      <div class="flex items-center justify-between">
        <div class="flex items-center justify-start rtl:justify-end">
          <button type="button" @click="handleSidebarToggle"
            class="inline-flex items-center p-2 text-sm text-gray-500 rounded-lg sm:hidden hover:bg-gray-100">
            <span class="sr-only">Open sidebar</span>
            <svg class="w-6 h-6" aria-hidden="true" fill="currentColor" viewBox="0 0 20 20"
              xmlns="http://www.w3.org/2000/svg">
              <path clip-rule="evenodd" fill-rule="evenodd"
                d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 4.75zm0 10.5a.75.75 0 01.75-.75h7.5a.75.75 0 010 1.5h-7.5a.75.75 0 01-.75-.75zM2 10a.75.75 0 01.75-.75h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 10z">
              </path>
            </svg>
          </button>
          <a href="https://github.com/ccmjga/zhilu-admin" target="_blank" class="flex items-center ms-2 md:me-24 ">
            <img class="me-3" src="/logo.svg" alt="logo">
            <span class="self-center text-lg sm:text-xl md:text-2xl font-semibold whitespace-nowrap">知路 AI 后台管理</span>
          </a>
        </div>
        <div class="flex items-center space-x-2 sm:space-x-3">
          <a href="https://github.com/ccmjga/zhilu-admin" target="_blank"
            class="hidden sm:flex items-center border rounded-sm border-gray-300">
            <span class="bg-gray-200 rounded-r-none border-r border-r-gray-300">
              <svg class="me-0.5 inline pl-1.5 pb-1 w-6 h-6 text-gray-800 bg-gray-200" aria-hidden="true"
                xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="none" viewBox="0 0 24 24">
                <path stroke="currentColor" stroke-width="2"
                  d="M11.083 5.104c.35-.8 1.485-.8 1.834 0l1.752 4.022a1 1 0 0 0 .84.597l4.463.342c.9.069 1.255 1.2.556 1.771l-3.33 2.723a1 1 0 0 0-.337 1.016l1.03 4.119c.214.858-.71 1.552-1.474 1.106l-3.913-2.281a1 1 0 0 0-1.008 0L7.583 20.8c-.764.446-1.688-.248-1.474-1.106l1.03-4.119A1 1 0 0 0 6.8 14.56l-3.33-2.723c-.698-.571-.342-1.702.557-1.771l4.462-.342a1 1 0 0 0 .84-.597l1.753-4.022Z" />
              </svg>
              <span class="text-sm pl-0.5 pr-2 font-medium">Star</span>
            </span>
            <span class="text-sm py-0.5 px-2 font-medium">0.3k</span>
          </a>
          <button class="cursor-pointer pt-1" @click="changeAssistantVisible">
            <AiChatIcon />
          </button>
          <div class="flex items-center ms-2 sm:ms-3">
            <div>
              <button type="button" id="dropdown-button" class="flex text-sm rounded-full cursor-pointer"
                aria-expanded="false" data-dropdown-toggle="dropdown-user">
                <span class="sr-only">打开用户菜单</span>
                <Avatar :src="user.avatar" size="sm" />
              </button>
            </div>
            <div class="z-50 hidden my-4 text-base list-none bg-white divide-y divide-gray-100 rounded-sm shadow-sm"
              id="dropdown-user">
              <div class="px-4 py-3" role="none">
                <p class="text-sm font-medium text-gray-900 truncate " role="none">
                  {{ user.username }}
                </p>
              </div>
              <ul class="py-1" role="none">
                <li>
                  <button @click="() => {
                    userDropDownMenu?.toggle()
                    router.push(Routes.SETTINGS.fullPath())
                  }" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100   "
                    role="menuitem">Settings</button>
                </li>
                <li>
                  <button @click="() => {
                    userDropDownMenu?.toggle()
                    router.push(Routes.USERVIEW.fullPath())
                  }" class="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100   "
                    role="menuitem">Dashboard</button>
                </li>
                <li>
                  <button @click="handleLogoutClick"
                    class="flex items-center space-x-1 px-4 py-2 text-sm text-gray-700 hover:bg-gray-100   "
                    role="menuitem">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                      stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
                      class="lucide lucide-log-out-icon w-4 h-4 lucide-log-out">
                      <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                      <polyline points="16 17 21 12 16 7" />
                      <line x1="21" x2="9" y1="12" y2="12" />
                    </svg><span>
                      Sign out
                    </span>
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </nav>
</template>
<script setup lang="ts">
import { AiChatIcon } from "@/components/icons";
import Avatar from "@/components/ui/Avatar.vue";
import useUserAuth from "@/composables/auth/useUserAuth";
import useUserStore from "@/composables/store/useUserStore";
import { Routes } from "@/router/constants";
import { Dropdown, type DropdownInterface, initFlowbite } from "flowbite";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const props = defineProps<{
	changeAssistantVisible: () => void;
	onSidebarToggle: () => void;
}>();

const handleSidebarToggle = () => {
	props.onSidebarToggle();
};

const userDropDownMenu = ref<DropdownInterface>();

const { user } = useUserStore();
const { signOut } = useUserAuth();
const router = useRouter();
const handleLogoutClick = () => {
	signOut();
	router.push(Routes.LOGIN.path);
};

onMounted(() => {
	initFlowbite();
	const $dropdownUser = document.getElementById("dropdown-user");
	const $dropdownButton = document.getElementById("dropdown-button");
	userDropDownMenu.value = new Dropdown(
		$dropdownUser,
		$dropdownButton,
		{},
		{ id: "dropdownMenu", override: true },
	);
});
</script>
