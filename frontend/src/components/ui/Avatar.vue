<template>
	<div class="rounded-full border border-gray-200 flex items-center justify-center overflow-hidden flex-shrink-0"
		:class="sizeClass">
		<img v-if="processedSrc" :src="processedSrc" class="w-full h-full object-cover" :alt="alt">
		<div v-else class="w-full h-full bg-gray-100"></div>
	</div>
</template>

<script setup lang="ts">
import { getUserAvatarUrl } from "@/utils/avatarUtil";
import { computed } from "vue";

const {
	src = "",
	alt = "用户头像",
	size = "md",
} = defineProps<{
	src?: string;
	alt?: string;
	size?: "sm" | "md" | "lg";
}>();

const sizeClass = computed(() => {
	switch (size) {
		case "sm":
			return "w-8 h-8";
		case "lg":
			return "w-12 h-12";
		default:
			return "w-10 h-10";
	}
});

const processedSrc = computed(() => {
	if (!src) {
		return "";
	}
	if (src === "/trump.jpg") {
		return src;
	}
	return getUserAvatarUrl(src);
});
</script>
