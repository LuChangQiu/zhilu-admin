<template>
  <div class="space-y-4">
    <div v-for="(item, index) in items ?? []" :key="getItemKey(item, index)"
      class="p-4 bg-white rounded-lg shadow relative border border-gray-100">
      <div class="flex justify-between items-start mb-3">
        <!-- 标题区域 -->
        <div class="font-medium text-gray-900">
          <slot name="title" :item="item"></slot>
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
import { ref } from "vue";

/** 通用对象类型 */
type ItemRecord = Record<string, unknown>;

const props = defineProps<{
	/** 数据项数组 */
	items: T[] | undefined;
	/** 数据项ID字段名 */
	idField?: string;
	/** 数据项唯一键字段名 */
	keyField?: string;
}>();

/**
 * 获取数据项的唯一键
 * @param item 数据项
 * @param index 索引
 * @returns 唯一键
 */
const getItemKey = (item: T, index: number): string | number => {
	if (props.keyField) {
		const key = (item as ItemRecord)[props.keyField];
		if (key !== undefined) return String(key);
	}

	if (props.idField) {
		const id = (item as ItemRecord)[props.idField];
		if (id !== undefined) return String(id);
	}

	const id = (item as ItemRecord).id;
	return id !== undefined ? String(id) : index;
};
</script>
