<template>
	<div class="rounded-full border border-gray-200 flex items-center justify-center overflow-hidden flex-shrink-0"
		:class="sizeClass">
		<img :src="processedSrc" class="w-full h-full object-cover" :alt="alt">
	</div>
</template>

<script setup lang="ts">
import { getUserAvatarUrl } from "@/utils/avatarUtil";
import { computed } from "vue";

const props = defineProps<{
	/** 头像图片源 */
	src?: string;
	/** 头像替代文本 */
	alt?: string;
	/** 头像尺寸 */
	size?: "xs" | "sm" | "md" | "lg" | "xl";
}>();

/** 尺寸样式映射 */
const sizeClass = computed(() => {
	const sizes = {
		xs: "w-6 h-6",
		sm: "w-8 h-8",
		md: "w-10 h-10",
		lg: "w-12 h-12",
		xl: "w-16 h-16"
	};
	return sizes[props.size || "md"];
});

/** 处理后的图片源 */
const processedSrc = computed(() => {
	return getUserAvatarUrl(props.src);
});
</script>
