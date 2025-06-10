<template>
  <div class="space-y-4">
    <div v-for="(item, index) in items ?? []" :key="index"
      class="p-4 bg-white rounded-lg shadow relative border border-gray-100">
      <div class="flex justify-between items-start mb-3">
        <!-- 标题区域 -->
        <div class="flex items-center">
          <slot name="checkbox" :item="item" v-if="hasCheckbox"></slot>
          <div class="font-medium text-gray-900">
            <slot name="title" :item="item"></slot>
          </div>
        </div>
        <!-- 状态区域 -->
        <div>
          <slot name="status" :item="item"></slot>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="text-sm text-gray-600 mb-3 space-y-2">
        <slot name="content" :item="item"></slot>
      </div>

      <!-- 标签/分类区域 -->
      <div v-if="$slots.tags" class="flex flex-wrap gap-2 mb-3">
        <slot name="tags" :item="item"></slot>
      </div>

      <!-- 操作按钮区域 -->
      <div class="flex justify-between items-center mt-4">
        <slot name="actions" :item="item"></slot>
      </div>
    </div>
  </div>
</template>

<script setup generic="T" lang="ts">
defineProps<{
	items: T[] | undefined;
	hasCheckbox?: boolean;
}>();
</script>
