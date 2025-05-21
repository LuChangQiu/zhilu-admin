<template>
  <button 
    :disabled="disabled" 
    @click="handleClick"
    type="button" 
    :class="[
      'text-white focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 dark:bg-blue-600 inline-flex items-center',
      disabled ? `${disabledStyle} cursor-not-allowed` : 'bg-blue-700 hover:bg-blue-800 '
    ]"
  >
    <LoadingIcon v-if="isLoading" />
    {{isLoading ? loadingContent : submitContent}}
  </button>
</template>

<script setup lang="ts">
import LoadingIcon from "./icons/LoadingIcon.vue";

const { loadingContent, submitContent, isLoading, disabledStyle, disabled } =
	defineProps<{
		loadingContent: string;
		submitContent: string;
		isLoading: boolean;
		disabledStyle: string;
		disabled: boolean;
		handleClick: (event: Event) => void;
	}>();
</script>
