<template>
  <button :disabled="disabled" @click="handleClick" type="button" :class="[
      'text-white focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center me-2 dark:bg-blue-600 inline-flex items-center',
      disabled ? `${disabledStyle}` : 'bg-blue-700 hover:bg-blue-800 '
    ]">
    <LoadingIcon v-if="isLoading && !abortable" />
    <StopIcon v-if="isLoading && abortable" />
    {{isLoading ? loadingContent : submitContent}}
  </button>
</template>

<script setup lang="ts">
import LoadingIcon from "./icons/LoadingIcon.vue";
import StopIcon from "./icons/StopIcon.vue";

const { loadingContent, submitContent, isLoading = false, disabledStyle, disabled = false, abortable = false } =
	defineProps<{
		loadingContent?: string;
		submitContent: string;
		isLoading: boolean;
		disabledStyle?: string;
		disabled: boolean;
    abortable: boolean;
		handleClick: (event: Event) => void;
	}>();
</script>
