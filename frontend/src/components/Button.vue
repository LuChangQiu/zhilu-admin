<template>
  <button :disabled="disabled" @click="handleClick" type="button" :class="[
    'text-white',
    'focus:ring-4',
    'focus:outline-none',
    'focus:ring-blue-300',
    'font-medium',
    'rounded-lg',
    'text-center',
    'inline-flex',
    'items-center',
    isLoading && !abortable ? 'bg-blue-400 cursor-not-allowed' : 'bg-blue-700 hover:bg-blue-800',
    sizeClasses
  ]">
    <LoadingIcon v-if="isLoading && !abortable" :class="iconSizeClasses" />
    <StopIcon v-else-if="isLoading && abortable" :class="iconSizeClasses" />
    <span v-if="iconOnly && isLoading" class="sr-only">{{ loadingContent }}</span>
    <span v-else-if="iconOnly && !isLoading" class="sr-only">{{ submitContent }}</span>
    <template v-else>
      {{ isLoading ? loadingContent : submitContent }}
    </template>
  </button>
</template>

<script setup lang="ts">
import { computed } from "vue";
import LoadingIcon from "./icons/LoadingIcon.vue";
import StopIcon from "./icons/StopIcon.vue";

const props = defineProps<{
	loadingContent?: string;
	submitContent: string;
	isLoading: boolean;
	abortable: boolean;
	handleClick: (event: Event) => void;
	size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
	iconOnly?: boolean;
}>();

const sizeClasses = computed(() => {
	if (props.iconOnly) {
		switch (props.size) {
			case 'xs': return 'p-1.5';
			case 'sm': return 'p-2';
			case 'lg': return 'p-3';
			case 'xl': return 'p-3.5';
			default: return 'p-2.5';
		}
	}
	switch (props.size) {
		case 'xs': return 'text-xs px-2.5 py-1.5';
		case 'sm': return 'text-xs px-3 py-2';
		case 'lg': return 'text-base px-5 py-3';
		case 'xl': return 'text-base px-6 py-3.5';
		default:
			return 'text-sm px-4 py-2.5';
	}
});

const iconSizeClasses = computed(() => {
	switch (props.size) {
		case 'xs': return 'w-3 h-3';
		case 'sm': return 'w-3.5 h-3.5';
		case 'lg': return 'w-5 h-5';
		case 'xl': return 'w-6 h-6';
		default:
			return 'w-4 h-4';
	}
});

const loadingStyle = computed<string>(() => {
	return '';
});

const disabled = computed(() => {
	return !props.abortable && props.isLoading;
});
</script>
