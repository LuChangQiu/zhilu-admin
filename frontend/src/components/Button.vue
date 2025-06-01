<template>
  <button :disabled="disabled" @click="handleClick" type="button" :class="{
      'text-white': true,
      'focus:ring-4': true,
      'focus:outline-none': true,
      'focus:ring-blue-300': true,
      'font-medium': true,
      'rounded-lg': true,
      'text-sm': true,
      'px-5': true,
      'py-2.5': true,
      'text-center': true,
      'inline-flex': true,
      'items-center': true,
      'bg-blue-700 hover:bg-blue-800': !isLoading,
      [loadingStyle]: isLoading
    }">
    <LoadingIcon v-if="isLoading && !abortable" />
    <StopIcon v-else-if="isLoading && abortable" />
    {{isLoading ? loadingContent : submitContent}}
  </button>
</template>

<script setup lang="ts">
import { computed } from "vue";
import LoadingIcon from "./icons/LoadingIcon.vue";
import StopIcon from "./icons/StopIcon.vue";

const {
	loadingContent,
	submitContent,
	isLoading = false,
	abortable = false,
} = defineProps<{
	loadingContent?: string;
	submitContent: string;
	isLoading: boolean;
	abortable: boolean;
	handleClick: (event: Event) => void;
}>();

const loadingStyle = computed<string>(() => {
	switch (true) {
		case isLoading && !abortable:
			return "bg-blue-400 cursor-not-allowed";
		case isLoading && abortable:
			return "bg-blue-700 hover:bg-blue-800";
		default:
			return "";
	}
});

const disabled = computed(() => {
	return !abortable && isLoading;
});
</script>
