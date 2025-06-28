<template>
  <div class="bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-shadow">
    <div class="p-4">
      <div class="flex justify-between items-start mb-3">
        <div class="flex-1">
          <h5 :class="[titleClass || 'text-xl font-semibold tracking-tight text-gray-900 mb-1 truncate']">
            <slot name="title"></slot>
          </h5>
          <div v-if="$slots.subtitle" class="flex items-center mb-2">
            <slot name="subtitle"></slot>
          </div>
        </div>
        <div v-if="$slots['header-actions']" class="flex space-x-2">
          <slot name="header-actions"></slot>
        </div>
      </div>

      <div v-if="$slots.content" class="text-sm text-gray-600 mb-3 space-y-2">
        <slot name="content"></slot>
      </div>

      <div class="flex justify-between items-center">
        <slot name="footer-left">
          <span v-if="$slots.timestamp" class="text-xs text-gray-500">
            <slot name="timestamp"></slot>
          </span>
        </slot>
        <div v-if="$slots['footer-actions']" class="flex space-x-2">
          <slot name="footer-actions"></slot>
        </div>
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  titleClass: {
    type: String,
    default: ''
  }
});
</script>
