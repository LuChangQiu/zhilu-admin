<template>
  <CardBase>
    <template #title>{{ library.name }}</template>
    <template #header-actions>
      <slot name="actions-top"></slot>
    </template>
    <template #content>
      <p class="text-sm text-gray-600 line-clamp-2">
        {{ library.description || '暂无描述' }}
      </p>
    </template>
    <template #footer-left>
      <span class="text-xs text-gray-500">
        创建时间: {{ formatDateString(library.createTime) }}
      </span>
    </template>
    <template #footer-actions>
      <slot name="actions-bottom"></slot>
    </template>
  </CardBase>
</template>

<script setup lang="ts">
import CardBase from "@/components/common/CardBase.vue";
import type { Library } from "@/types/KnowledgeTypes";
import { formatDateString } from "@/utils/dateUtil";

const props = defineProps<{
	library: Library;
}>();
</script>
