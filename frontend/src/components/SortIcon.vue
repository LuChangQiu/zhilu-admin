<template>
  <span class="ml-1">
            <svg class="w-3.5 h-3.5" :class="{'text-blue-700': sortField?.order}" fill="currentColor" viewBox="0 0 24 24">
              <path v-if="sortField?.order === 'asc'" d="M8 12l6-6 6 6"/>
              <path v-else-if="sortField?.order === 'desc'" d="M8 12l6 6 6-6"/>
              <path v-else d="M8 9l6-6 6 6M8 15l6 6 6-6"/>
            </svg>
          </span>
</template>

<script setup lang="ts">
defineProps<{
	sortField?: {
		field: string;
		order: "asc" | "desc" | undefined;
	};
}>();
</script>
