<template>
  <div class="flex flex-col px-96 box-border pt-14 min-h-screen max-h-screen overflow-auto" ref="chatContainer">
    <div class="flex flex-col gap-y-5 flex-1 pt-14">
      <li v-for="chatElement in chatElements" :key="chatElement.content"
        :class="['flex items-start gap-2.5', chatElement.isUser ? 'flex-row-reverse' : 'flex-row']">
        <img class="w-8 h-8 rounded-full" src="/trump.jpg" alt="Jese image">
        <div
          :class="['flex flex-col leading-1.5 p-4 border-gray-200 rounded-e-xl rounded-es-xl ', chatElement.isUser ? 'bg-blue-100' : 'bg-gray-100']">
          <div class="flex items-center space-x-2 rtl:space-x-reverse">
            <span class="text-sm font-semibold text-gray-900 ">{{ chatElement.username }}</span>
            <LoadingIcon :textColor="'text-gray-900'"
              v-if="isLoading && !chatElement.isUser && chatElement.content === ''" />
          </div>
          <div class="markdown-content markdown-body text-base font-normal py-2.5 text-gray-900 "
            v-html="renderMarkdown(chatElement.content)">
          </div>
        </div>
      </li>
    </div>

    <form class="sticky bottom-4 mt-14">
      <div class="w-full border border-gray-200 rounded-lg bg-gray-50  ">
        <div class="px-4 py-2 bg-white rounded-t-lg ">
          <label for="comment" class="sr-only"></label>
          <textarea id="comment" rows="3" v-model="inputMessage"
            class="w-full px-0 text-gray-900 bg-white border-0  focus:ring-0  " placeholder="发送消息" required></textarea>
        </div>
        <div class="flex items-center justify-between px-3 py-2 border-t  border-gray-200">
          <Button :abortable="true" :isLoading="isLoading" :loadingContent="'中止'" :submitContent="'发送'"
            :handleClick="handleSendClick" />
          <div class="flex ps-0 space-x-1 rtl:space-x-reverse sm:ps-2">
            <button type="button"
              class="inline-flex justify-center items-center p-2 text-gray-500 rounded-sm cursor-pointer hover:text-gray-900 hover:bg-gray-100   ">
              <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none"
                viewBox="0 0 12 20">
                <path stroke="currentColor" stroke-linejoin="round" stroke-width="2"
                  d="M1 6v8a5 5 0 1 0 10 0V4.5a3.5 3.5 0 1 0-7 0V13a2 2 0 0 0 4 0V6" />
              </svg>
              <span class="sr-only">Attach file</span>
            </button>
            <button type="button"
              class="inline-flex justify-center items-center p-2 text-gray-500 rounded-sm cursor-pointer hover:text-gray-900 hover:bg-gray-100   ">
              <svg class="w-4 h-4" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="currentColor"
                viewBox="0 0 20 18">
                <path
                  d="M18 0H2a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V2a2 2 0 0 0-2-2Zm-5.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3Zm4.376 10.481A1 1 0 0 1 16 15H4a1 1 0 0 1-.895-1.447l3.5-7A1 1 0 0 1 7.468 6a.965.965 0 0 1 .9.5l2.775 4.757 1.546-1.887a1 1 0 0 1 1.618.1l2.541 4a1 1 0 0 1 .028 1.011Z" />
              </svg>
              <span class="sr-only">Upload image</span>
            </button>
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import LoadingIcon from "@/components/icons/LoadingIcon.vue";
import useAlertStore from "@/composables/store/useAlertStore";
import DOMPurify from 'dompurify';
import { marked } from 'marked';
import { computed, nextTick, onUnmounted, ref, watch } from "vue";
import { z } from "zod";
import Button from "../components/Button.vue";
import { useAiChat } from "../composables/ai/useAiChat";
import useUserStore from "../composables/store/useUserStore";

const { messages, chat, isLoading, cancel } = useAiChat();
const { user } = useUserStore();
const inputMessage = ref("");
const chatContainer = ref<HTMLElement | null>(null);
const alertStore = useAlertStore();

marked.setOptions({
  gfm: true,        
  breaks: true,    
});


const renderMarkdown = (content: string) => {
  if (!content) return '';
  
  const restoredContent = content
    .replace(/␣/g, ' ')    
    .replace(/⇥/g, '\t')   
    .replace(/␤/g, '\n');  
  
  
  const processedContent = restoredContent
    .replace(/^(\s*)(`{3,})/gm, '$1$2') 
    .replace(/(\s+)`/g, '$1`');         

  const rawHtml = marked(processedContent);
  return DOMPurify.sanitize(rawHtml as string);
};
const chatElements = computed(() => {
	return messages.value.map((message, index) => {
		return {
			content: message,
			username: index % 2 === 0 ? user.username : "知路智能体",
			isUser: index % 2 === 0,
		};
	});
});

// watch(messages, (newVal) => {
//   console.log('原始消息:', newVal[newVal.length - 1]);
//   console.log('处理后HTML:', renderMarkdown(newVal[newVal.length - 1]));
// }, { deep: true });

watch(
	chatElements,
	async () => {
		await nextTick();
		scrollToBottom();
	},
	{ deep: true },
);

const scrollToBottom = () => {
	if (chatContainer.value) {
		chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
	}
};

const abortChat = () => {
	cancel();
};

const sendMessage = async () => {
	try {
		const validInputMessage = z
			.string({ message: "消息不能为空" })
			.min(1, "消息不能为空")
			.parse(inputMessage.value);
		scrollToBottom();
		inputMessage.value = "";
		await chat(validInputMessage);
	} catch (error) {
		if (error instanceof z.ZodError) {
			alertStore.showAlert({
				level: "error",
				content: error.errors[0].message,
			});
		} else {
			throw error;
		}
	}
};

const handleSendClick = async () => {
	if (isLoading.value) {
		abortChat();
	} else {
		sendMessage();
	}
};

onUnmounted(() => {
	cancel();
});
</script>

<style lang="css">
@import "github-markdown-css/github-markdown.css";

.markdown-body {
  background: transparent !important;
}

.markdown-body pre code {
  white-space: pre !important;
  tab-size: 2;
}

.markdown-body p {
  white-space: pre-wrap;
}
</style>
