<template>
  <div class="w-full">
    <label :for="id" class="block mb-2 text-sm font-medium text-gray-900">{{ label }}</label>
    <select :id="id" :name="name" :value="modelValue"
      @change="$emit('update:modelValue', ($event.target as HTMLSelectElement).value)" :required="required" :class="[
        'bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5',
        error ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'focus:ring-blue-500 focus:border-blue-500'
      ]">
      <option v-if="placeholder" value="" disabled>{{ placeholder }}</option>
      <slot></slot>
    </select>
    <p v-if="error" class="mt-1 text-sm text-red-600">{{ error }}</p>
    <p v-if="hint && !error" class="mt-1 text-sm text-gray-500">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
	/** 选择框标签 */
	label: string;
	/** 选择框ID */
	id: string;
	/** 选择框名称 */
	name?: string;
	/** 选择框占位符 */
	placeholder?: string;
	/** 是否必填 */
	required?: boolean;
	/** 选择框值 */
	modelValue: string | number | boolean;
	/** 错误信息 */
	error?: string;
	/** 提示信息 */
	hint?: string;
}>();

defineEmits<{
	"update:modelValue": [value: string | number | boolean];
}>();
</script>
