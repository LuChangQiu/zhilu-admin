<template>
  <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center mb-4 gap-y-3 sm:gap-y-0">
    <form
      class="grid grid-cols-2 sm:grid-cols-2 w-full min-w-[200px] sm:w-auto gap-2 xs:gap-3 items-stretch xs:items-center">
      <template v-for="(filter, index) in filters" :key="index">
        <!-- 输入框类型 -->
        <div v-if="filter.type === 'input'" class="flex-grow">
          <label :for="`filter-input-${index}`" class="mb-2 text-sm font-medium text-gray-900 sr-only">{{ filter.label
            }}</label>
          <div class="relative">
            <div class="absolute inset-y-0 start-0 flex items-center ps-3 pointer-events-none">
            </div>
            <input type="search" :id="`filter-input-${index}`" v-model="filterValues[filter.name]"
              class="block w-full p-2.5 text-sm text-gray-900 border border-gray-300 rounded-lg bg-gray-50 focus:ring-blue-500 focus:border-blue-500"
              :placeholder="filter.placeholder || ''" />
          </div>
        </div>

        <!-- 日期范围选择器 -->
        <div v-else-if="filter.type === 'date-range'" class="flex-grow datepicker-container">
          <VueDatePicker v-model="filterValues[filter.name]" locale="zh-CN" range
            :format="filter.format || 'yyyy/MM/dd HH:mm:ss - yyy/MM/dd HH:mm:ss'" :placeholder="filter.placeholder"
            :enable-time-picker="filter.enableTimePicker !== false" :auto-apply="filter.autoApply !== false"
            class="filter-datepicker" teleport="body" />
        </div>

        <!-- 选择器 -->
        <div v-else-if="filter.type === 'select'" class="flex-grow">
          <select :id="`filter-select-${index}`" v-model="filterValues[filter.name]"
            class="w-full xs:w-auto bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block p-2.5">
            <option v-for="option in filter.options" :key="String(option.value)" :value="option.value">
              {{ option.label }}
            </option>
          </select>
        </div>
      </template>
      <div class="col-span-full flex mt-2">
        <Button variant="primary" size="sm" @click.prevent="handleSearch" class="w-full sm:w-1/2">
          <template #icon>
            <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 20">
              <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="m19 19-4-4m0-7A7 7 0 1 1 1 8a7 7 0 0 1 14 0Z" />
            </svg>
          </template>
          <span class="ps-1.5">搜索</span>
        </Button>
      </div>
    </form>

    <!-- 额外操作按钮插槽 -->
    <div class="flex justify-end">
      <slot name="actions"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Button } from "@/components/ui";
import { onMounted, reactive, watch } from "vue";

export interface FilterOption {
	value: string | number | boolean;
	label: string;
}

export interface FilterItem {
	type: "input" | "select" | "date-range";
	name: string;
	label?: string;
	placeholder?: string;
	options?: FilterOption[];
	format?: string;
	enableTimePicker?: boolean;
	autoApply?: boolean;
}

type FilterValues = Record<
	string,
	string | number | boolean | Date[] | undefined
>;

const props = defineProps<{
	filters: FilterItem[];
	initialValues?: FilterValues;
}>();

const emit = defineEmits<{
	search: [values: FilterValues];
	"update:values": [values: FilterValues];
}>();

// 初始化筛选值
const filterValues = reactive<FilterValues>({});

// 初始化默认值
onMounted(() => {
	// 初始化所有筛选项的默认值
	for (const filter of props.filters) {
		if (props.initialValues && props.initialValues[filter.name] !== undefined) {
			filterValues[filter.name] = props.initialValues[filter.name];
		} else {
			// 设置默认值
			switch (filter.type) {
				case "input":
					filterValues[filter.name] = "";
					break;
				case "select":
					filterValues[filter.name] =
						filter.options && filter.options.length > 0
							? filter.options[0].value
							: "";
					break;
				case "date-range":
					filterValues[filter.name] = undefined;
					break;
			}
		}
	}
});

// 监听筛选值变化
watch(
	filterValues,
	(newValues) => {
		emit("update:values", { ...newValues });
	},
	{ deep: true },
);

// 处理搜索
const handleSearch = () => {
	emit("search", { ...filterValues });
};
</script>

<style>
/* 调整日期选择器的高度与其他输入框一致 */
.datepicker-container .dp__main {
  width: 100%;
}

.datepicker-container .dp__input {
  height: 42px;
  /* 与input的p-2.5相匹配 */
  padding: 0.625rem 0.75rem;
  font-size: 0.875rem;
  line-height: 1.25rem;
  border-radius: 0.5rem;
  border: 1px solid #d1d5db;
  /* gray-300 */
  background-color: #f9fafb;
  /* gray-50 */
}

.datepicker-container .dp__input:focus {
  outline: 2px solid transparent;
  outline-offset: 2px;
  border-color: #3b82f6;
  /* blue-500 */
  box-shadow: 0 0 0 1px #3b82f6;
  /* blue-500 */
}

.datepicker-container .dp__input_icon {
  right: 0.75rem;
}
</style>
