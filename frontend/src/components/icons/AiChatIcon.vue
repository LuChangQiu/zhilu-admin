<template>
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
    stroke-linecap="round" stroke-linejoin="round" :class="['lucide lucide-bot-icon lucide-bot ai-breath', size]">
    <path d="M12 8V4H8" />
    <rect width="16" height="12" x="4" y="8" rx="2" />
    <path d="M2 14h2" />
    <path d="M20 14h2" />
    <path d="M15 13v2" />
    <path d="M9 13v2" />
  </svg>
</template>

<script setup lang="ts">
const { size = 'w-6 h-6' } = defineProps<{
  size?: string;
}>();
</script>

<style scoped>
.ai-breath {
  animation: ai-breath 1.8s infinite ease-in-out;
  filter: drop-shadow(0 0 6px #60a5fa);
}

@keyframes ai-breath {

  0%,
  100% {
    filter: drop-shadow(0 0 6px #60a5fa) brightness(1);
    stroke: #60a5fa;
  }

  50% {
    filter: drop-shadow(0 0 16px #2563eb) brightness(1.3);
    stroke: #2563eb;
  }
}
</style>
