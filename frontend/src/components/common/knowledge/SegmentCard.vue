<template>
  <CardBase>
    <template #title>分段 #{{ index + 1 }}</template>
    <template #header-actions>
      <div class="text-xs text-gray-500">
        ID: {{ segment.id }}
      </div>
    </template>
    <template #subtitle>
      <div class="flex flex-wrap gap-2">
        <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800">
          Embedding ID: {{ segment.embeddingId || '无' }}
        </span>
        <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
          Token 使用量: {{ segment.tokenUsage || 0 }}
        </span>
      </div>
    </template>
    <template #content>
      <div class="border-t border-gray-200 pt-3">
        <h6 class="text-sm font-medium text-gray-900 mb-2">内容:</h6>
        <pre
          class="text-sm text-gray-700 whitespace-pre-wrap bg-gray-50 p-3 rounded-lg max-h-60 overflow-y-auto">{{ segment.content }}</pre>
      </div>
    </template>
  </CardBase>
</template>

<script setup lang="ts">
import CardBase from '@/components/common/CardBase.vue';
import type { LibraryDocSegment } from "@/types/KnowledgeTypes";

const props = defineProps<{
  segment: LibraryDocSegment;
  index: number;
}>();
</script>
