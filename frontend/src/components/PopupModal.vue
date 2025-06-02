<template>
  <div :id tabindex="-1"
    class="bg-gray-900/50 hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
    <div class="relative p-4 w-full max-w-xs sm:max-w-sm md:max-w-md max-h-full">
      <div class="relative bg-white rounded-lg shadow-sm">
        <button type="button" @click="closeModal"
          class="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center">
          <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
          </svg>
          <span class="sr-only">Close modal</span>
        </button>
        <div class="p-4 md:p-5 text-center flex flex-col items-center gap-y-3 sm:gap-y-4">
          <svg class="w-12 h-12 sm:w-16 sm:h-16 mx-auto text-red-600 mt-4 sm:mt-5" fill="none" stroke="currentColor"
            viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <h3 class="mb-2 sm:mb-3 text-base sm:text-lg font-normal text-gray-500">
            {{ title }}
          </h3>
          <div class="flex flex-col sm:flex-row sm:justify-center gap-2 w-full sm:w-auto">
            <button type="button" @click="onSubmit"
              class="w-full sm:w-auto text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-xs sm:text-sm px-4 py-2 sm:px-5 sm:py-2.5 text-center">
              是
            </button>
            <button type="button" @click="closeModal"
              class="w-full sm:w-auto py-2 sm:py-2.5 px-4 sm:px-5 text-xs sm:text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100">否</button>
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script setup lang="ts">
import { initFlowbite } from "flowbite";
import { onMounted } from "vue";

defineProps<{
	title: string;
	id: string;
	closeModal: () => void;
	onSubmit: () => Promise<void>;
}>();

onMounted(() => {
	initFlowbite();
});
</script>
