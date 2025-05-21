<script setup lang="ts">
import { RouterView } from "vue-router";
import Headbar from "./Headbar.vue";
import Sidebar from "./Sidebar.vue";
</script>

<template>
  <Headbar></Headbar>
  <Sidebar>
  </Sidebar>
  <article class="ml-64">
    <RouterView></RouterView>
  </article>
</template>
