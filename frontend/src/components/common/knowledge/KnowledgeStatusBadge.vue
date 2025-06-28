<template>
  <span :class="`inline-flex items-center px-2 py-1 text-xs font-medium rounded-full ${
    getStatusClass()
  }`">
    {{ getStatusText() }}
  </span>
</template>

<script setup lang="ts">
import { DocStatus } from "@/types/KnowledgeTypes";

const props = defineProps<{
  status?: string;
  enabled?: boolean;
  type: 'status' | 'enabled';
}>();

const getStatusClass = () => {
  if (props.type === 'status') {
    return props.status === DocStatus.SUCCESS 
      ? 'bg-green-100 text-green-800' 
      : 'bg-yellow-100 text-yellow-800';
  }
  
  return props.enabled 
    ? 'bg-blue-100 text-blue-800' 
    : 'bg-gray-100 text-gray-800';
};

const getStatusText = () => {
  if (props.type === 'status') {
    return props.status === DocStatus.SUCCESS ? '解析完成' : '解析中';
  }
  
  return props.enabled ? '已启用' : '已禁用';
};
</script>
