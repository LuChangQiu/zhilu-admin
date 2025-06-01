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
  <!-- 主内容区域 -->
  <div class="flex flex-row h-[calc(100vh-3.5rem)] mt-14"> <!-- 减去Headbar高度 -->
    <!-- 文章内容区域 -->
    <article class="flex-1 ml-44 overflow-y-auto">
      <RouterView></RouterView>
    </article>
    <Assistant></Assistant>
  </div>

</template>
