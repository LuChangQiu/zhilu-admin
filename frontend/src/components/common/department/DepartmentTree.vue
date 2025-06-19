<template>
  <div class="department-tree">
    <!-- 空状态 -->
    <div v-if="!departmentTree || departmentTree.length === 0" class="flex flex-col items-center justify-center py-8">
      <svg class="w-12 h-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"
        xmlns="http://www.w3.org/2000/svg">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
          d="M9 13h6m-3-3v6m-9 1V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z"></path>
      </svg>
      <p class="mt-2 text-sm text-gray-500">暂无部门数据</p>
    </div>

    <div v-else>
      <!-- 操作按钮区域 -->
      <div class="flex justify-end mb-3 gap-2">
        <TableButton variant="secondary" size="xs" @click="toggleAllNodes" :title="isAllExpanded ? '收起所有部门' : '展开所有部门'">
          <template #icon>
            <svg v-if="isAllExpanded" class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
            </svg>
            <svg v-else class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
            </svg>
          </template>
          {{ isAllExpanded ? '收起所有' : '展开所有' }}
        </TableButton>
      </div>

      <!-- 树形结构内容区域 -->
      <div class="max-h-[500px] overflow-y-auto p-2 bg-gray-50 border border-gray-200 rounded shadow-inner">
        <TreeNodeComponent v-for="node in departmentTree" :key="node.id" :node="node" :expand-all="expandAll"
          @add-child="handleAddChild" @edit="handleEditNode" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import TableButton from "@/components/tables/TableButton.vue";
import type { DepartmentTreeNode } from "@/composables/department/useDepartmentQuery";
import { computed, defineEmits, defineProps, ref, watch } from "vue";
import TreeNodeComponent from "./TreeNodeComponent.vue";

// 定义组件属性
const props = defineProps<{
	departmentTree: DepartmentTreeNode[];
}>();

// 定义事件
const emit = defineEmits<{
	(e: "add-child", node: DepartmentTreeNode): void;
	(e: "edit", node: DepartmentTreeNode): void;
}>();

// 控制所有节点展开/折叠状态
const expandAll = ref<boolean>(true);

// 通过计算属性提供展开状态
const isAllExpanded = computed(() => expandAll.value);

// 监听树数据变化，当数据改变时重置展开状态
watch(
	() => props.departmentTree,
	() => {
		// 当树数据变化时，默认展开所有节点
		expandAll.value = true;
	},
	{ deep: true },
);

// 切换所有节点的展开/折叠状态
const toggleAllNodes = () => {
	expandAll.value = !expandAll.value;
};

// 处理添加子部门
const handleAddChild = (node: DepartmentTreeNode) => {
	emit("add-child", node);
};

// 处理编辑节点
const handleEditNode = (node: DepartmentTreeNode) => {
	emit("edit", node);
};
</script>
