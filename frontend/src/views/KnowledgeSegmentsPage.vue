<template>
  <div class="px-2 sm:px-4 pt-6 sm:rounded-lg">
    <Breadcrumbs :names="['知识库管理', '文档管理', '文档分段']" :routes="[
      Routes.KNOWLEDGEVIEW.fullPath(),
      Routes.KNOWLEDGEDOCVIEW.withParams({ libraryId })
    ]" />
    <div class="mb-4 flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">{{ currentDoc?.name || '文档' }} - 分段内容</h1>
    </div>

    <div class="mb-4">
      <Button variant="secondary" size="sm" @click="navigateBack">
        <template #icon>
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18">
            </path>
          </svg>
        </template>
        返回文档列表
      </Button>
    </div>

    <!-- 分段列表 -->
    <div v-if="segments.length > 0" class="space-y-4">
      <SegmentCard v-for="(segment, index) in segments" :key="segment.id" :segment="segment" :index="index" />
    </div>

    <!-- 空状态 -->
    <div v-else class="flex flex-col items-center justify-center py-10">
      <div class="text-gray-500 text-lg mb-4">暂无分段内容</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";

import { SegmentCard } from "@/components/common/knowledge";
import Breadcrumbs from "@/components/layout/Breadcrumbs.vue";
import { Button } from "@/components/ui";

import { useKnowledgeQuery } from "@/composables/knowledge/useKnowledgeQuery";
import { Routes } from "@/router/constants";

import type { LibraryDoc } from "@/types/KnowledgeTypes";

// 路由参数
const route = useRoute();
const router = useRouter();
const libraryId = Number.parseInt(route.params.libraryId as string, 10);
const docId = Number.parseInt(route.params.docId as string, 10);

// 获取文档信息和分段列表
const { docs, segments, fetchLibraryDocs, fetchDocSegments } =
	useKnowledgeQuery();
const currentDoc = ref<LibraryDoc | undefined>();

// 导航回文档列表
const navigateBack = () => {
	router.push(Routes.KNOWLEDGEDOCVIEW.withParams({ libraryId }));
};

// 初始化
onMounted(async () => {
	await fetchLibraryDocs({ libraryId });
	await fetchDocSegments({ libraryDocId: docId });
});

// 监听文档列表变化，找到当前文档
watchEffect(() => {
	if (docs.value && docs.value.length > 0) {
		currentDoc.value = docs.value.find((doc) => doc.id === docId);
	}
});
</script>
