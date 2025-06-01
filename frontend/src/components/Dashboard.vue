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
  <article class="flex flex-row ml-44 mr-64 scroll-auto overflow-auto mt-14">
    <RouterView></RouterView>
  </article>
  <Assistant></Assistant>
</template>
