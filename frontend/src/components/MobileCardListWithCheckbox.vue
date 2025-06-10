<template>
  <div class="space-y-4">
    <div v-for="(item, index) in items ?? []" :key="getItemKey(item, index)"
      class="p-4 bg-white rounded-lg shadow relative border border-gray-100">
      <div class="flex justify-between items-start mb-3">
        <!-- 标题区域 -->
        <div class="flex items-center">
          <input :id="'mobile-checkbox-' + getItemId(item)" :value="getItemId(item)" type="checkbox"
            v-model="checkedItems"
            class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2 mr-3">
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
import { ref, watch } from 'vue';

/** 通用对象类型 */
type ItemRecord = Record<string, unknown>;

const props = defineProps<{
  /** 数据项数组 */
  items: T[] | undefined;
  /** 数据项ID字段名 */
  idField?: string;
  /** 数据项唯一键字段名 */
  keyField?: string;
  /** 选中项的值数组 */
  modelValue?: (string | number)[];
}>();

const emit = defineEmits<{
  'update:modelValue': [checkedItems: (string | number)[]];
}>();

const checkedItems = ref<(string | number)[]>(props.modelValue || []);

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
  
  const id = getItemId(item);
  return id !== undefined ? id : index;
};

/**
 * 获取数据项的ID
 * @param item 数据项
 * @returns ID值
 */
const getItemId = (item: T): string | number => {
  if (props.idField) {
    return (item as ItemRecord)[props.idField] as string | number;
  }
  return (item as ItemRecord).id as string | number || item as unknown as string | number;
};

// 监听选中项变化
watch(checkedItems, (newVal) => {
  emit('update:modelValue', newVal);
});

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
  if (newVal) {
    checkedItems.value = newVal;
  }
}, { deep: true });
</script>
