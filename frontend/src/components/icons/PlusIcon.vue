<template>
  <svg :class="props.class" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
    stroke-width="3" stroke="currentColor">
    <path stroke-linecap="round" stroke-linejoin="round" d="M12 4.5v15m7.5-7.5h-15" />
  </svg>
</template>

<script setup lang="ts">
const props = defineProps<{
	class?: string;
}>();
</script>
