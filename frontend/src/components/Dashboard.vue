<template>
  <Headbar :changeAssistantVisible="changeAssistantVisible" :onSidebarToggle="toggleSidebar"></Headbar>
  <Sidebar ref="sidebarRef" @menu-click="handleMenuClick"></Sidebar>
  <div class="flex flex-row h-[calc(100vh-3.5rem)] mt-14">
    <article class="flex-1 sm:ml-44 overflow-y-auto">
      <RouterView></RouterView>
    </article>
    <Assistant v-if="isAssistantVisible"></Assistant>
  </div>

</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterView } from "vue-router";
import Assistant from "./Assistant.vue";
import Headbar from "./Headbar.vue";
import Sidebar from "./Sidebar.vue";

const isAssistantVisible = ref(false);
const sidebarRef = ref();

const changeAssistantVisible = () => {
	isAssistantVisible.value = !isAssistantVisible.value;
};

const toggleSidebar = () => {
	if (sidebarRef.value) {
		sidebarRef.value.toggleSidebar();
	}
};

const handleMenuClick = () => {
	if (sidebarRef.value) {
		sidebarRef.value.closeSidebar();
	}
};
</script>
