<template>
  <nav class="flex mb-3 sm:mb-4 md:mb-5" aria-label="Breadcrumb">
    <ol class="inline-flex items-center space-x-1 text-xs sm:text-sm font-medium md:space-x-2">
      <li class="inline-flex items-center">
        <RouterLink :to="{name: RouteName.USERVIEW}"
          class="inline-flex items-center text-gray-700 hover:text-primary-600">
          <svg class="w-4 h-4 sm:w-5 sm:h-5 me-1.5 sm:me-2 md:me-2.5" fill="currentColor" viewBox="0 0 20 20"
            xmlns="http://www.w3.org/2000/svg">
            <path
              d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293.293a1 1 0 001.414-1.414l-7-7z">
            </path>
          </svg>
          首页
        </RouterLink>
      </li>
      <li v-for="name in names" :key="name">
        <div class="flex items-center">
          <svg class="w-3 h-3 sm:w-4 sm:h-4 text-gray-400" fill="currentColor" viewBox="0 0 20 20"
            xmlns="http://www.w3.org/2000/svg">
            <path fill-rule="evenodd"
              d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z"
              clip-rule="evenodd"></path>
          </svg>
          <span class="ms-1 text-gray-400 hover:text-primary-600 md:ms-2">{{
            name }}</span>
        </div>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
import { RouteName } from "@/router/constants";

const { names } = defineProps<{
	names: string[];
}>();
</script>
