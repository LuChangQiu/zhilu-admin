<template>
  <span
    :class="[
      'inline-flex items-center px-2 py-1 text-xs font-medium rounded-full',
      success
        ? 'bg-green-100 text-green-800'
        : 'bg-red-100 text-red-800'
    ]"
  >
    <span
      :class="[
        'w-2 h-2 mr-1 rounded-full',
        success ? 'bg-green-500' : 'bg-red-500'
      ]"
    ></span>
    {{ success ? '成功' : '失败' }}
  </span>
</template>

<script setup lang="ts">
defineProps<{
	success: boolean;
}>();
</script> 
