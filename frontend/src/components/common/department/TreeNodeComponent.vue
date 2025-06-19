<template>
  <div class="relative mb-0.5">
    <div class="flex items-center py-1.5 px-2 cursor-pointer hover:bg-gray-100 rounded transition-colors group"
      @click="toggleExpand">
      <!-- 折叠/展开图标 -->
      <span class="mr-1.5 text-gray-500 cursor-pointer flex-shrink-0" v-if="node.children && node.children.length > 0">
        <svg v-if="localExpanded" class="w-4 h-4 transform transition-transform duration-200" fill="none"
          stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"></path>
        </svg>
        <svg v-else class="w-4 h-4 transform transition-transform duration-200" fill="none" stroke="currentColor"
          viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </span>
      <span class="mr-1.5 text-gray-500 invisible flex-shrink-0" v-else>
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
        </svg>
      </span>

      <!-- 部门名称 -->
      <span class="text-sm font-medium text-gray-800 flex-grow cursor-pointer truncate" :title="node.name">
        {{ node.name }}
      </span>

      <!-- 操作按钮 -->
      <div class="ml-auto hidden group-hover:flex flex-shrink-0">
        <button @click.stop="$emit('add-child', node)"
          class="text-gray-500 hover:text-blue-600 focus:outline-none transition-colors p-0.5" title="添加子部门">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
          </svg>
        </button>

        <button @click.stop="$emit('edit', node)"
          class="text-gray-500 hover:text-blue-600 focus:outline-none transition-colors ml-1 p-0.5" title="编辑部门">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"
            xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
              d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z">
            </path>
          </svg>
        </button>
      </div>
    </div>
    <transition enter-active-class="transition-[height,opacity] duration-300 ease-in-out overflow-hidden"
      leave-active-class="transition-[height,opacity] duration-300 ease-in-out overflow-hidden" @enter="expandEnter"
      @after-enter="expandAfterEnter" @leave="expandLeave">
      <div v-if="localExpanded && node.children && node.children.length > 0"
        class="pl-4 border-l border-gray-200 ml-2 overflow-hidden">
        <TreeNodeComponent v-for="childNode in node.children" :key="childNode.id" :node="childNode"
          :expand-all="expandAll" @add-child="$emit('add-child', $event)" @edit="$emit('edit', $event)" />
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import type { DepartmentTreeNode } from "@/composables/department/useDepartmentQuery";
import { defineEmits, defineProps, ref, watch } from "vue";

// 定义组件属性
const props = defineProps<{
	node: DepartmentTreeNode;
	expandAll: boolean;
}>();

// 定义事件
defineEmits<{
	(e: "add-child", node: DepartmentTreeNode): void;
	(e: "edit", node: DepartmentTreeNode): void;
}>();

// 本地展开状态，默认展开
const localExpanded = ref(true);

// 监听expandAll属性变化
watch(
	() => props.expandAll,
	(newVal) => {
		localExpanded.value = newVal;
	},
	{ immediate: true },
);

// 切换展开状态
const toggleExpand = () => {
	localExpanded.value = !localExpanded.value;
};

// 展开动画处理函数
const expandEnter = (element: Element) => {
	const el = element as HTMLElement;
	el.style.height = "0";
	el.style.opacity = "0";
};

const expandAfterEnter = (element: Element) => {
	const el = element as HTMLElement;
	el.style.height = "";
	el.style.opacity = "";
};

const expandLeave = (element: Element) => {
	const el = element as HTMLElement;
	el.style.height = `${el.scrollHeight}px`;

	// 强制回流
	void el.offsetHeight;

	el.style.height = "0";
	el.style.opacity = "0";
};
</script>

<style scoped>
.department-tree-node {
  position: relative;
  margin-bottom: 2px;
}

.expand-enter-active,
.expand-leave-active {
  transition: height 0.3s ease, opacity 0.3s ease;
  overflow: hidden;
}
</style>
