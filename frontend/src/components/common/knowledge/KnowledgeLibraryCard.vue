<template>
  <div class="bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-shadow">
    <div class="p-4">
      <div class="flex justify-between items-start">
        <h5 class="text-xl font-semibold tracking-tight text-gray-900 mb-1 truncate">{{ library.name }}</h5>
        <div class="flex space-x-2">
          <slot name="actions-top"></slot>
        </div>
      </div>
      <p class="text-sm text-gray-600 mb-3 line-clamp-2">
        {{ library.description || '暂无描述' }}
      </p>
      <div class="flex justify-between items-center">
        <span class="text-xs text-gray-500">
          创建时间: {{ formatDateString(library.createTime) }}
        </span>
        <slot name="actions-bottom"></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Library } from "@/types/KnowledgeTypes";
import { formatDateString } from '@/utils/dateUtil';

const props = defineProps<{
  library: Library;
}>();
</script>
