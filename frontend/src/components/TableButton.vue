<template>
  <button :class="[
      'flex items-center justify-center gap-x-1 whitespace-nowrap font-medium rounded-lg focus:ring-4 focus:outline-none',
      sizeClasses,
      colorClasses,
      disabled ? 'opacity-50 cursor-not-allowed' : '',
      className
    ]" :disabled="disabled" @click="handleClick" type="button">
    <slot name="icon"></slot>
    <span>
      <slot></slot>
    </span>
  </button>
</template>

<script setup lang="ts">
import { computed } from "vue";

export type ButtonVariant =
	| "primary"
	| "secondary"
	| "success"
	| "danger"
	| "warning"
	| "info";
export type ButtonSize = "xs" | "sm" | "md" | "lg";

const props = defineProps<{
	/** 按钮变体类型 */
	variant?: ButtonVariant;
	/** 按钮尺寸 */
	size?: ButtonSize;
	/** 是否禁用 */
	disabled?: boolean;
	/** 自定义CSS类名 */
	className?: string;
	/** 是否为移动端尺寸 */
	isMobile?: boolean;
}>();

const emit = defineEmits<{
	click: [event: MouseEvent];
}>();

/** 按钮颜色样式映射 */
const colorClasses = computed(() => {
	const variants: Record<ButtonVariant, string> = {
		primary: "text-white bg-blue-700 hover:bg-blue-800 focus:ring-blue-300",
		secondary:
			"text-gray-900 bg-white border border-gray-300 hover:bg-gray-100 focus:ring-gray-100",
		success: "text-white bg-green-700 hover:bg-green-800 focus:ring-green-300",
		danger: "text-white bg-red-700 hover:bg-red-800 focus:ring-red-300",
		warning:
			"text-gray-900 bg-yellow-400 hover:bg-yellow-500 focus:ring-yellow-300",
		info: "text-white bg-cyan-700 hover:bg-cyan-800 focus:ring-cyan-300",
	};

	return variants[props.variant || "primary"];
});

/** 按钮尺寸样式映射 */
const sizeClasses = computed(() => {
	// 移动端尺寸
	if (props.isMobile) {
		const sizes: Record<ButtonSize, string> = {
			xs: "text-xs px-2 py-1",
			sm: "text-xs px-3 py-1.5",
			md: "text-sm px-3 py-2",
			lg: "text-sm px-4 py-2.5",
		};
		return sizes[props.size || "sm"];
	}

	// PC端尺寸
	const sizes: Record<ButtonSize, string> = {
		xs: "text-xs px-3 py-1.5",
		sm: "text-sm px-3 py-2",
		md: "text-sm px-4 py-2.5",
		lg: "text-base px-5 py-3",
	};

	return sizes[props.size || "md"];
});

/** 处理点击事件 */
const handleClick = (event: MouseEvent) => {
	if (!props.disabled) {
		emit("click", event);
	}
};
</script>
