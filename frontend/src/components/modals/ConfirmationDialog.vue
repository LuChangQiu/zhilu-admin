<template>
  <BaseDialog :id="id" :closeModal="closeModal" size="sm">
    <div class="p-5 md:p-6 text-center">
      <svg class="w-14 h-14 sm:w-16 sm:h-16 mx-auto text-red-600 mb-4" fill="none" stroke="currentColor"
        viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
      </svg>
      <h3 class="mb-4 text-base sm:text-lg font-medium text-gray-800">
        {{ title }}
      </h3>
      <p v-if="content" class="mb-4 text-sm text-gray-500">{{ content }}</p>
      <div class="flex justify-center items-center space-x-3 sm:space-x-4">
        <Button variant="danger" @click="onSubmit">
          是
        </Button>
        <Button variant="secondary" @click="closeModal">
          否
        </Button>
      </div>
    </div>
  </BaseDialog>
</template>

<script setup lang="ts">
import { Button } from "@/components/ui";
import BaseDialog from "./BaseDialog.vue";

const props = defineProps<{
	/** 对话框标题 */
	title: string;
	/** 对话框内容 */
	content?: string;
	/** 对话框ID */
	id: string;
	/** 关闭对话框的回调函数 */
	closeModal: () => void;
	/** 确认操作的回调函数 */
	onSubmit: () => Promise<void>;
}>();
</script>
