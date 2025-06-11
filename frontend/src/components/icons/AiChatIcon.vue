<template>
  <div class="ai-icon-container relative" :class="containerClass">
    <!-- 主图标 -->
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
      stroke-linecap="round" stroke-linejoin="round" :class="['lucide lucide-bot-icon z-10 relative ai-breath', size]">
      <path d="M12 8V4H8" class="bot-antenna" />
      <rect width="16" height="12" x="4" y="8" rx="2" class="bot-body" />
      <path d="M2 14h2" class="bot-ear" />
      <path d="M20 14h2" class="bot-ear" />
      <path d="M15 13v2" class="bot-eye" />
      <path d="M9 13v2" class="bot-eye" />
    </svg>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
	size?: string;
	containerClass?: string;
}>();

const { size = "w-6 h-6" } = props;
</script>

<style scoped>
.ai-icon-container {
  display: inline-flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
}

.ai-icon-container:hover {
  transform: scale(1.1);
}

/* 机器人图标呼吸效果 */
.ai-breath {
  animation: ai-breath 1.8s infinite ease-in-out;
  filter: drop-shadow(0 0 6px #60a5fa);
  transition: all 0.3s ease;
}

@keyframes ai-breath {

  0%,
  100% {
    filter: drop-shadow(0 0 6px #60a5fa) brightness(1);
    stroke: #60a5fa;
  }

  50% {
    filter: drop-shadow(0 0 12px #2563eb) brightness(1.4);
    stroke: #2563eb;
  }
}

/* 机器人各部分动画 */
.bot-antenna {
  animation: antenna-move 3s infinite ease-in-out;
  transform-origin: bottom;
}

.bot-body {
  animation: body-pulse 3s infinite ease-in-out;
}

.bot-ear {
  animation: ear-wiggle 3s infinite ease-in-out;
  transform-origin: 50% 50%;
}

.bot-eye {
  animation: eye-blink 3.5s infinite;
  transform-origin: 50% 50%;
}

@keyframes antenna-move {

  0%,
  100% {
    transform: rotate(0deg);
  }

  30% {
    transform: rotate(-5deg);
  }

  60% {
    transform: rotate(5deg);
  }
}

@keyframes body-pulse {

  0%,
  100% {
    transform: scale(1);
  }

  50% {
    transform: scale(1.03);
  }
}

@keyframes ear-wiggle {

  0%,
  100% {
    transform: scaleX(1);
  }

  30% {
    transform: scaleX(1.2);
  }

  60% {
    transform: scaleX(0.8);
  }
}

@keyframes eye-blink {

  0%,
  96%,
  100% {
    transform: scaleY(1);
  }

  98% {
    transform: scaleY(0.1);
  }
}
</style>
