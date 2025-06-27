<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <Breadcrumbs :names="['知识库管理', '文档管理', '内容管理']"
      :routes="[Routes.KNOWLEDGEVIEW.fullPath(), Routes.KNOWLEDGEDOCVIEW.withParams({ libraryId: libraryId })]" />
    <div class="mb-4">
      <h1 class="text-2xl font-semibold text-gray-900 mb-2">{{ currentDoc?.name || '文档' }} - 分段内容</h1>
      <p class="text-sm text-gray-500">
        共 {{ segments.length }} 个分段
      </p>
    </div>

    <!-- 分段列表 -->
    <div v-if="segments.length === 0" class="flex flex-col items-center justify-center py-10">
      <div class="text-gray-500 text-lg">暂无分段内容</div>
    </div>
    <div v-else class="space-y-4">
      <div v-for="(segment, index) in segments" :key="segment.id"
        class="bg-white border border-gray-200 rounded-lg shadow-sm p-4">
        <div class="flex justify-between items-start mb-2">
          <h5 class="text-lg font-semibold text-gray-900">分段 #{{ index + 1 }}</h5>
          <div class="text-xs text-gray-500">
            ID: {{ segment.id }}
          </div>
        </div>
        <div class="text-sm text-gray-500 mb-2">
          <div class="flex flex-wrap gap-2">
            <span class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-blue-100 text-blue-800">
              Embedding ID: {{ segment.embeddingId || '无' }}
            </span>
            <span
              class="inline-flex items-center px-2 py-1 text-xs font-medium rounded-full bg-green-100 text-green-800">
              Token 使用量: {{ segment.tokenUsage || 0 }}
            </span>
          </div>
        </div>
        <div class="border-t border-gray-200 pt-3 mt-3">
          <h6 class="text-sm font-medium text-gray-900 mb-2">内容:</h6>
          <pre
            class="text-sm text-gray-700 whitespace-pre-wrap bg-gray-50 p-3 rounded-lg max-h-60 overflow-y-auto">{{ segment.content }}</pre>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { useRoute } from "vue-router";

import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import { useKnowledgeQuery } from "@/composables/knowledge/useKnowledgeQuery";
import { Routes } from "@/router/constants";

import type { Library, LibraryDoc } from "@/types/KnowledgeTypes";

// 路由参数
const route = useRoute();
const libraryId = ref<number>(
	Number.parseInt(route.params.libraryId as string, 10),
);
const docId = ref<number>(Number.parseInt(route.params.docId as string, 10));

// 获取知识库信息
const { libraries, fetchLibraries } = useKnowledgeQuery();
const currentLibrary = ref<Library | undefined>();

// 获取文档信息
const { docs, fetchLibraryDocs } = useKnowledgeQuery();
const currentDoc = ref<LibraryDoc | undefined>();

// 获取分段列表
const { segments, fetchDocSegments } = useKnowledgeQuery();

// 初始化
onMounted(async () => {
	// 获取知识库列表、文档列表和分段列表
	await fetchLibraries();
	await fetchLibraryDocs({ libraryId: libraryId.value });
	await fetchDocSegments({ libraryDocId: docId.value });
});

// 监听知识库列表变化，找到当前知识库
watchEffect(() => {
	if (libraries.value && libraries.value.length > 0) {
		currentLibrary.value = libraries.value.find(
			(lib) => lib.id === libraryId.value,
		);
	}
});

// 监听文档列表变化，找到当前文档
watchEffect(() => {
	if (docs.value && docs.value.length > 0) {
		currentDoc.value = docs.value.find((doc) => doc.id === docId.value);
	}
});
</script>
