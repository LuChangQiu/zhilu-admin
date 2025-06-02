<template>
  <Headbar :changeAssistantVisible="changeAssistantVisible"></Headbar>
  <Sidebar />
  <div class="flex flex-row h-[calc(100vh-3.5rem)] mt-14">
    <article class="flex-1 sm:ml-44 overflow-y-auto">
      <RouterView></RouterView>
    </article>
    <!-- Assistant is now fixed positioned and controlled by isAssistantVisible -->
  </div>
  <Assistant :isVisible="isAssistantVisible" @close="changeAssistantVisible"></Assistant>
</template>

<script setup lang="ts">
import { RouterView } from "vue-router";
import Headbar from "./Headbar.vue";
import Sidebar from "./Sidebar.vue";
import Assistant from "./Assistant.vue";
import { ref } from "vue";

const isAssistantVisible = ref(false);

const changeAssistantVisible = () => {
	isAssistantVisible.value = !isAssistantVisible.value;
};
</script>
