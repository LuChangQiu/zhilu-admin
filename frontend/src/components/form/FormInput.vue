<template>
  <div class="w-full">
    <label :for="id" class="block mb-2 text-sm font-medium text-gray-900">{{ label }}</label>
    <input :type="type" :id="id" :name="name" :value="modelValue"
      @input="$emit('update:modelValue', ($event.target as HTMLInputElement).value)" :placeholder="placeholder"
      :required="required" :autocomplete="autocomplete" :class="[
        'bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg block w-full p-2.5',
        error ? 'border-red-500 focus:ring-red-500 focus:border-red-500' : 'focus:ring-blue-500 focus:border-blue-500'
      ]" />
    <p v-if="error" class="mt-1 text-sm text-red-600">{{ error }}</p>
    <p v-if="hint && !error" class="mt-1 text-sm text-gray-500">{{ hint }}</p>
  </div>
</template>

<script setup lang="ts">
defineProps<{
	/** 输入框标签 */
	label: string;
	/** 输入框ID */
	id: string;
	/** 输入框名称 */
	name?: string;
	/** 输入框类型 */
	type?: string;
	/** 输入框占位符 */
	placeholder?: string;
	/** 是否必填 */
	required?: boolean;
	/** 自动完成属性 */
	autocomplete?: string;
	/** 输入框值 */
	modelValue: string;
	/** 错误信息 */
	error?: string;
	/** 提示信息 */
	hint?: string;
}>();

defineEmits<{
	"update:modelValue": [value: string];
}>();
</script>
