<template>
  <div class="bg-white border border-gray-200 rounded-lg shadow-sm p-4">
    <div class="flex justify-between items-start mb-2">
      <h5 class="text-lg font-semibold text-gray-900">分段 #{{ index + 1 }}</h5>
      <div class="text-xs text-gray-500">
        ID: {{ segment.id }}
      </div>
    </div>
    <div class="text-sm text-gray-500 mb-2">
      <div class="flex flex-wrap gap-2">
        <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800">
          Embedding ID: {{ segment.embeddingId || '无' }}
        </span>
        <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
          Token 使用量: {{ segment.tokenUsage || 0 }}
        </span>
      </div>
    </div>
    <div class="border-t border-gray-200 pt-3 mt-3">
      <h6 class="text-sm font-medium text-gray-900 mb-2">内容:</h6>
      <pre
        class="text-sm text-gray-700 whitespace-pre-wrap bg-gray-50 p-3 rounded-lg max-h-60 overflow-y-auto">{{ segment.content }}</pre>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { LibraryDocSegment } from "@/types/KnowledgeTypes";

const props = defineProps<{
  segment: LibraryDocSegment;
  index: number;
}>();
</script>
