<template>
  <div class="bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-shadow">
    <div class="p-4">
      <div class="flex justify-between items-start">
        <div>
          <h5 class="text-xl font-semibold tracking-tight text-gray-900 mb-1 truncate">{{ doc.name }}</h5>
          <div class="flex items-center mb-2">
            <KnowledgeStatusBadge :status="doc.status" type="status" class="mr-2" />
            <KnowledgeStatusBadge :enabled="doc.enable" type="enabled" />
          </div>
        </div>
        <div class="flex space-x-2">
          <slot name="toggle-switch"></slot>
        </div>
      </div>
      <div class="flex justify-between items-center">
        <span class="text-xs text-gray-500">
          上传时间: {{ formatDateString(doc.createTime) }}
        </span>
        <div class="flex space-x-2">
          <slot name="actions"></slot>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { KnowledgeStatusBadge } from '@/components/common/knowledge';
import type { LibraryDoc } from "@/types/KnowledgeTypes";
import { formatDateString } from '@/utils/dateUtil';

const props = defineProps<{
  doc: LibraryDoc;
}>();
</script>
