<template>
  <CardBase>
    <template #title>{{ doc.name }}</template>
    <template #subtitle>
      <KnowledgeStatusBadge :status="doc.status" type="status" class="mr-2" />
      <KnowledgeStatusBadge :enabled="doc.enable" type="enabled" />
    </template>
    <template #header-actions>
      <slot name="toggle-switch"></slot>
    </template>
    <template #footer-left>
      <span class="text-xs text-gray-500">
        {{ formatDateString(doc.createTime) }}
      </span>
    </template>
    <template #footer-actions>
      <slot name="actions"></slot>
    </template>
  </CardBase>
</template>

<script setup lang="ts">
import CardBase from "@/components/common/CardBase.vue";
import { KnowledgeStatusBadge } from "@/components/common/knowledge";
import type { LibraryDoc } from "@/types/KnowledgeTypes";
import { formatDateString } from "@/utils/dateUtil";

const props = defineProps<{
	doc: LibraryDoc;
}>();
</script>
