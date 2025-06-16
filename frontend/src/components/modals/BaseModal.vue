<template>
  <div tabindex="-1" aria-hidden="true"
    class="bg-gray-900/50 hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
    <div class="relative p-4 w-full" :class="[maxWidthClass]">
      <!-- Modal content -->
      <div class="relative bg-white rounded-lg shadow">
        <!-- Modal header -->
        <div v-if="title" class="flex items-center justify-between p-4 md:p-5 border-b rounded-t border-gray-200">
          <h3 class="text-base sm:text-lg font-semibold text-gray-900">
            {{ title }}
          </h3>
          <button type="button" @click="closeModal"
            class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center">
            <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
            </svg>
            <span class="sr-only">关闭</span>
          </button>
        </div>
        <!-- Modal body -->
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

export type ModalSize = "xs" | "sm" | "md" | "lg" | "xl";

const props = defineProps<{
	/** 模态框标题 */
	title?: string;
	/** 模态框大小 */
	size?: ModalSize;
	/** 关闭模态框的回调函数 */
	closeModal: () => void;
}>();

/** 根据size属性计算最大宽度类名 */
const maxWidthClass = computed(() => {
	const sizes: Record<ModalSize, string> = {
		xs: "max-w-xs",
		sm: "max-w-sm",
		md: "max-w-md",
		lg: "max-w-lg",
		xl: "max-w-xl",
	};

	return sizes[props.size || "md"];
});
</script>
