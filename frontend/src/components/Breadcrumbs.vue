<template>
  <nav class="flex mb-4" aria-label="Breadcrumb">
    <ol class="inline-flex items-center space-x-1 sm:space-x-2 text-sm">
      <li class="inline-flex items-center">
        <RouterLink :to="{ name: RouteName.HOME }"
          class="inline-flex items-center font-medium text-gray-500 hover:text-blue-600">
          <svg class="w-3.5 h-3.5 mr-1.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
            viewBox="0 0 20 20">
            <path
              d="m19.707 9.293-2-2-7-7a1 1 0 0 0-1.414 0l-7 7-2 2a1 1 0 0 0 1.414 1.414L2 10.414V18a2 2 0 0 0 2 2h3a1 1 0 0 0 1-1v-4a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v4a1 1 0 0 0 1 1h3a2 2 0 0 0 2-2v-7.586l.293.293a1 1 0 0 0 1.414-1.414Z" />
          </svg>
          首页
        </RouterLink>
      </li>
      <li v-for="(item, index) in breadcrumbs" :key="index">
        <div class="flex items-center">
          <svg class="w-3 h-3 text-gray-400 mx-1.5 sm:mx-2" aria-hidden="true" xmlns="http://www.w3.org/2000/svg"
            fill="none" viewBox="0 0 6 10">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="m1 9 4-4-4-4" />
          </svg>
          <RouterLink v-if="item.route" :to="item.route" class="font-medium text-gray-500 hover:text-blue-600 truncate">
            {{ item.name }}
          </RouterLink>
          <span v-else class="font-medium text-gray-500 truncate">{{ item.name }}</span>
        </div>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
import { RouteName } from "@/router/constants";
import { computed } from "vue";
import type { RouteLocationRaw } from "vue-router";

interface BreadcrumbItem {
  name: string;
  route?: RouteLocationRaw;
}

const props = defineProps<{
  names: string[];
  routes?: RouteLocationRaw[];
}>();

const breadcrumbs = computed<BreadcrumbItem[]>(() => {
  return props.names.map((name, index) => {
    return {
      name,
      route: props.routes?.[index]
    };
  });
});
</script>
