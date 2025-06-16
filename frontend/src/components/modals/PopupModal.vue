<template>
  <BaseModal :closeModal="closeModal" size="sm">
    <div class="p-5 md:p-6 text-center">
      <svg class="w-14 h-14 sm:w-16 sm:h-16 mx-auto text-red-600 mb-4" fill="none" stroke="currentColor"
        viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
      </svg>
      <h3 class="mb-4 text-base sm:text-lg font-medium text-gray-800">
        {{ title }}
      </h3>
      <div class="flex justify-center items-center space-x-3 sm:space-x-4">
        <button type="button" @click="onSubmit"
          class="text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center min-w-[80px]">
          是
        </button>
        <button type="button" @click="closeModal"
          class="py-2.5 px-5 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100 min-w-[80px]">否</button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import BaseModal from "./BaseModal.vue";

const { title, id, closeModal, onSubmit } = defineProps<{
	title: string;
	id: string;
	closeModal: () => void;
	onSubmit: () => Promise<void>;
}>();
</script>
