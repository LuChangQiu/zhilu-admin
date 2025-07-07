<template>
	<div class="relative overflow-x-auto shadow-md sm:rounded-lg">
		<table class="w-full text-sm text-left rtl:text-right text-gray-500 whitespace-nowrap">
			<thead class="text-xs text-gray-700 uppercase bg-gray-50">
				<tr>
					<th v-if="hasCheckbox" scope="col" class="p-4 w-4">
						<div class="flex items-center">
							<input id="checkbox-all-search" type="checkbox" v-model="allChecked" @change="handleAllCheckedChange"
								class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
							<label for="checkbox-all-search" class="sr-only">checkbox</label>
						</div>
					</th>
					<th v-for="(column, index) in columns" :key="index" scope="col" :class="[
              'px-6 py-3', 
              column.sortable ? 'cursor-pointer' : '',
              column.class || ''
            ]" @click="column.sortable ? handleSortClick(column.field) : null">
						<div class="flex items-center">
							<span>{{ column.title }}</span>
							<slot v-if="column.sortable" name="sort-icon" :field="column.field"></slot>
						</div>
					</th>
				</tr>
			</thead>
			<tbody>
				<tr v-for="(item, rowIndex) in items" :key="getItemKey(item, rowIndex)"
					class="bg-white border-b border-gray-200 hover:bg-gray-50">
					<td v-if="hasCheckbox" class="w-4 p-4">
						<div class="flex items-center">
							<input :id="`checkbox-table-search-${rowIndex}`" :value="getItemId(item)" type="checkbox"
								v-model="checkedItems"
								class="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 rounded-sm focus:ring-blue-500 focus:ring-2">
							<label :for="`checkbox-table-search-${rowIndex}`" class="sr-only">checkbox</label>
						</div>
					</td>
					<td v-for="(column, colIndex) in columns" :key="colIndex" :class="[
              'px-6 py-4', 
              column.class || '', 
              colIndex === 0 ? 'font-medium text-gray-900' : ''
            ]">
						<slot :name="column.field" :item="item" :index="rowIndex">
							<div class="max-w-sm whitespace-nowrap overflow-hidden text-ellipsis">
								{{ getItemValue(item, column.field) }}
							</div>
						</slot>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</template>

<script setup generic="T" lang="ts">
import { defineEmits, ref, watch } from "vue";

/**
 * 表格列配置接口
 */
export interface Column {
	/** 列标题 */
	title: string;
	/** 数据字段名 */
	field: string;
	/** 是否可排序 */
	sortable?: boolean;
	/** 自定义CSS类名 */
	class?: string;
}

/** 通用对象类型 */
type ItemRecord = Record<string, unknown>;

const props = defineProps<{
	/** 数据项数组 */
	items: T[];
	/** 列配置数组 */
	columns: Column[];
	/** 是否显示复选框 */
	hasCheckbox?: boolean;
	/** 数据项ID字段名 */
	idField?: string;
	/** 数据项唯一键字段名 */
	keyField?: string;
	/** 选中项的值数组，用于v-model绑定 */
	modelValue?: (string | number)[];
}>();

const emit = defineEmits<{
	"update:modelValue": [checkedItems: (string | number)[]];
	sort: [field: string];
	"all-checked-change": [checked: boolean];
}>();

const checkedItems = ref<(string | number)[]>(props.modelValue || []);
const allChecked = ref(false);

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
	return (
		((item as ItemRecord).id as string | number) ||
		(item as unknown as string | number)
	);
};

/**
 * 获取数据项的字段值
 * @param item 数据项
 * @param field 字段名
 * @returns 字段值
 */
const getItemValue = (item: T, field: string): string => {
	if (!field) return "";

	return String(
		field
			.split(".")
			.reduce<unknown>(
				(obj, key) =>
					obj &&
					typeof obj === "object" &&
					key in (obj as Record<string, unknown>)
						? (obj as Record<string, unknown>)[key]
						: "",
				item as ItemRecord,
			),
	);
};

/**
 * 处理全选/取消全选
 */
const handleAllCheckedChange = () => {
	if (allChecked.value) {
		checkedItems.value = props.items.map(getItemId);
	} else {
		checkedItems.value = [];
	}
	emit("all-checked-change", allChecked.value);
	emit("update:modelValue", checkedItems.value);
};

/**
 * 处理排序点击
 * @param field 排序字段
 */
const handleSortClick = (field: string) => {
	emit("sort", field);
};

// 监听选中项变化
watch(checkedItems, (newVal) => {
	emit("update:modelValue", newVal);
	// 更新全选状态
	if (props.items.length > 0) {
		allChecked.value = newVal.length === props.items.length;
	} else {
		allChecked.value = false;
	}
});

// 监听modelValue变化
watch(
	() => props.modelValue,
	(newVal) => {
		if (newVal) {
			checkedItems.value = newVal;
		}
	},
	{ deep: true },
);

// 监听items变化，重置选中状态
watch(
	() => props.items,
	() => {
		if (allChecked.value) {
			checkedItems.value = props.items.map(getItemId);
			emit("update:modelValue", checkedItems.value);
		}
	},
	{ deep: true },
);
</script>
