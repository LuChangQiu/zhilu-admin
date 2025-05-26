<template>
  <div :id tabindex="-1"
    class="bg-gray-900/50 /80 hidden overflow-y-auto overflow-x-hidden fixed top-0 right-0 left-0 z-50 justify-center items-center w-full md:inset-0 h-[calc(100%-1rem)] max-h-full">
    <div class="relative p-4 w-full max-w-md max-h-full">
      <div class="relative bg-white rounded-lg shadow-sm ">
        <button type="button" @click="closeModal"
          class="absolute top-3 end-2.5 text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center  ">
          <svg class="w-3 h-3" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 14">
            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6" />
          </svg>
          <span class="sr-only">Close modal</span>
        </button>
        <div class="p-4 md:p-5 text-center flex flex-col items-center gap-y-3">
          <svg class="w-16 h-16 mx-auto text-red-600 mt-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path>
          </svg>
          <h3 class="mb-3 text-lg font-normal text-gray-500 ">
            {{ title }}
          </h3>
          <span>
            <button type="button" @click="onSubmit"
              class="text-white bg-red-600 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300  font-medium rounded-lg text-sm inline-flex items-center px-5 py-2.5 text-center">
              是
            </button>
            <button type="button" @click="closeModal"
              class="py-2.5 px-5 ms-3 text-sm font-medium text-gray-900 focus:outline-none bg-white rounded-lg border border-gray-200 hover:bg-gray-100 hover:text-blue-700 focus:z-10 focus:ring-4 focus:ring-gray-100      ">否</button>
          </span>
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
	onSubmit: (event: Event) => Promise<void>;
}>();

onMounted(() => {
	initFlowbite();
});
</script>
