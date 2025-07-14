<template>
  <div class="flex justify-center items-center my-4">
    <a class="group relative w-full max-w-xs overflow-hidden rounded-lg border border-gray-200 shadow-lg transition-all duration-300 hover:shadow-xl hover:scale-[1.02]"
      :href="href" target="_blank">
      <div class="absolute top-0 right-0 bg-blue-600 text-white text-xs px-2 py-1 rounded-bl-lg z-10">{{ label }}</div>
      <img :src="imageSrc" :alt="imageAlt"
        class="w-full h-52 transition-transform duration-500 group-hover:scale-105">
      <div
        class="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/70 to-transparent p-3 transform transition-all duration-300">
        <span class="text-white text-sm font-medium group-hover:underline flex items-center">
          {{ text }}
          <svg class="w-4 h-4 ml-1 transition-transform duration-300 group-hover:translate-x-1" fill="none"
            stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
          </svg>
        </span>
      </div>
    </a>
  </div>
</template>

<script setup lang="ts">
defineProps({
	href: {
		type: String,
		required: true,
	},
	imageSrc: {
		type: String,
		required: true,
	},
	imageAlt: {
		type: String,
		default: "promotion",
	},
	label: {
		type: String,
		default: "官方教程",
	},
	text: {
		type: String,
		required: true,
	},
});
</script>
