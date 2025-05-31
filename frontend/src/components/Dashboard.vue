<script setup lang="ts">
import { RouterView } from "vue-router";
import Headbar from "./Headbar.vue";
import Sidebar from "./Sidebar.vue";
import Assistant from "./Assistant.vue";
</script>

<template>
  <Headbar></Headbar>
  <Sidebar>
  </Sidebar>
  <article class="ml-64 mr-96 scroll-auto overflow-auto">
    <RouterView></RouterView>
  </article>
  <div class="fixed top-0 right-0 w-1/5 border-gray-200 border-l">
    <Assistant></Assistant>
  </div>
</template>
