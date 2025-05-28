<template>
  <div
    class="flex flex-col px-4 sm:px-8 md:px-16 lg:px-32 xl:px-48 2xl:px-72 box-border pt-14 min-h-screen max-h-screen overflow-auto"
    ref="chatContainer">
    <div class="flex flex-col gap-y-5 flex-1 pt-14">
      <li v-for="chatElement in messages" :key="chatElement.content"
        :class="['flex items-start gap-2.5', chatElement.isUser ? 'flex-row-reverse' : 'flex-row']">
        <img class="w-8 h-8 rounded-full" :src="chatElement.isUser ? '/java.svg' : '/trump.jpg'" alt="avatar">
        <div
          :class="['flex flex-col leading-1.5 p-4 border-gray-200 rounded-e-xl rounded-es-xl ', chatElement.isUser ? 'bg-blue-100' : 'bg-gray-100']">
          <div class="flex items-center space-x-2 rtl:space-x-reverse">
            <span class="text-sm font-semibold text-gray-900 ">{{ chatElement.username }}</span>
            <LoadingIcon :textColor="'text-gray-900'"
              v-if="isLoading && !chatElement.isUser && chatElement.content === ''" />
          </div>
          <div>
            <div class="markdown-content markdown-body text-base font-normal py-2.5 text-gray-900 "
              v-html="renderMarkdown(chatElement.content)">
            </div>
            <button v-if="chatElement.type === 'action' && chatElement.command" type="button"
              @click="commandActionMap[chatElement.command!]"
              class="px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
              {{
              commandContentMap[chatElement.command!]
              }}</button>
          </div>
        </div>
      </li>
    </div>

    <form class="sticky bottom-4 pt-14">
      <button @click.prevent="toggleMode"
        class="absolute left-1 top-2 inline-flex items-center justify-center p-0.5 mb-2 me-2 overflow-hidden text-sm font-medium rounded-lg group focus:ring-4 focus:outline-none focus:ring-cyan-200"
        :class="[
          isCommandMode
            ? 'bg-gradient-to-br from-cyan-500 to-blue-500 text-white'
            : 'bg-gradient-to-br from-cyan-500 to-blue-500 group-hover:from-cyan-500 group-hover:to-blue-500 text-gray-900'
        ]">
        <span class="relative px-3 py-2 transition-all ease-in duration-75 rounded-md hover:text-white" :class="[
            isCommandMode
              ? 'bg-transparent'
              : 'bg-white  group-hover:bg-transparent'
          ]">
          指令模式
        </span>
      </button>
      <div class="absolute right-1 top-2">
        <button @click.prevent="() => handleSendClick('请帮我创建用户', true)" class=" inline-flex items-center justify-center p-0.5
          mb-2 me-2 overflow-hidden font-medium text-gray-900 rounded-lg group bg-gradient-to-br from-purple-500
          to-pink-500 group-hover:from-purple-500 group-hover:to-pink-500 hover:text-white dark:text-white focus:ring-4
          focus:outline-none focus:ring-purple-200 cursor-pointer dark:focus:ring-purple-800">
          <span
            class="px-3 py-2 text-xs transition-all ease-in duration-75 bg-white dark:bg-gray-900 rounded-md group-hover:bg-transparent group-hover:dark:bg-transparent">
            帮我创建用户?
          </span>
        </button>
        <button @click.prevent="() => handleSendClick('请帮我创建部门', true)" class="inline-flex items-center justify-center p-0.5 mb-2 me-2 overflow-hidden font-medium text-gray-900 rounded-lg group bg-gradient-to-br from-purple-500 to-pink-500 group-hover:from-purple-500 group-hover:to-pink-500 hover:text-white dark:text-white focus:ring-4 focus:outline-none focus:ring-purple-200 
        cursor-pointer dark:focus:ring-purple-800">
          <span
            class="px-3 py-2 text-xs transition-all ease-in duration-75 bg-white dark:bg-gray-900 rounded-md group-hover:bg-transparent group-hover:dark:bg-transparent">
            帮我创建部门?
          </span>
        </button>
      </div>
      <div class="w-full border border-gray-200 rounded-lg bg-gray-50">
        <div class="px-4 py-2 bg-white rounded-t-lg">
          <label for="comment" class="sr-only"></label>
          <textarea id="comment" rows="3" v-model="inputMessage"
            class="w-full px-0 text-gray-900 bg-white border-0  focus:ring-0  " placeholder="发送消息" required></textarea>
        </div>
        <div class="flex items-center justify-between px-3 py-2 border-t  border-gray-200">
          <Button :abortable="true" :isLoading="isLoading" :loadingContent="'中止'" :submitContent="'发送'"
            :handleClick="() => handleSendClick(inputMessage, isCommandMode)" />
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
  <UserUpsertModal :id="'user-upsert-modal'" :onSubmit="handleUpsertUserSubmit" :closeModal="() => {
    userUpsertModal!.hide();
  }">
  </UserUpsertModal>
  <DepartmentUpsertModal :id="'department-upsert-modal'" :onSubmit="handleUpsertDepartmentSubmit" :closeModal="() => {
    availableDepartments = undefined
    departmentUpsertModal!.hide();
  }" :availableDepartments="availableDepartments">
  </DepartmentUpsertModal>
</template>

<script setup lang="ts">
import LoadingIcon from "@/components/icons/LoadingIcon.vue";
import useAlertStore from "@/composables/store/useAlertStore";
import DOMPurify from "dompurify";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { marked } from "marked";
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { z } from "zod";
import Button from "../components/Button.vue";
import DepartmentUpsertModal from "../components/DepartmentUpsertModal.vue";
import UserUpsertModal from "../components/UserUpsertModal.vue";
import { useAiChat } from "../composables/ai/useAiChat";
import useUserStore from "../composables/store/useUserStore";
import { useUserUpsert } from "../composables/user/useUserUpsert";
import type { UserUpsertSubmitModel } from "../types/user";
import { useDepartmentQuery } from "@/composables/department/useDepartmentQuery";
import { useDepartmentUpsert } from "@/composables/department/useDepartmentUpsert";
import type { DepartmentUpsertModel } from "@/types/department";

const { messages, chat, isLoading, cancel, actionChat } = useAiChat();
const { user } = useUserStore();
const userUpsertModal = ref<ModalInterface>();
const departmentUpsertModal = ref<ModalInterface>();
const inputMessage = ref("");
const chatContainer = ref<HTMLElement | null>(null);
const alertStore = useAlertStore();
const isCommandMode = ref(false);
const userUpsert = useUserUpsert();
const departmentUpsert = useDepartmentUpsert();

const { availableDepartments, fetchAvailableDepartments } =
	useDepartmentQuery();

const commandActionMap: Record<string, () => void> = {
	CREATE_USER: () => {
		userUpsertModal.value?.show();
	},
	CREATE_DEPARTMENT: () => {
		fetchAvailableDepartments();
		departmentUpsertModal.value?.show();
	},
};

const commandContentMap: Record<string, string> = {
	CREATE_USER: "创建用户",
	CREATE_DEPARTMENT: "创建部门",
};

const toggleMode = () => {
	isCommandMode.value = !isCommandMode.value;
};

marked.setOptions({
	gfm: true,
	breaks: true,
});

const renderMarkdown = (content: string | undefined) => {
	if (!content) return "";

	const restoredContent = content
		.replace(/␣/g, " ")
		.replace(/⇥/g, "\t")
		.replace(/␤/g, "\n");

	const processedContent = restoredContent
		.replace(/^(\s*)(`{3,})/gm, "$1$2")
		.replace(/(\s+)`/g, "$1`");

	const rawHtml = marked(processedContent);
	return DOMPurify.sanitize(rawHtml as string);
};

// watch(messages, (newVal) => {
//   console.log('原始消息:', newVal[newVal.length - 1]);
//   console.log('处理后HTML:', renderMarkdown(newVal[newVal.length - 1]));
// }, { deep: true });

const handleUpsertUserSubmit = async (data: UserUpsertSubmitModel) => {
	await userUpsert.upsertUser(data);
	userUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};

const handleUpsertDepartmentSubmit = async (
	department: DepartmentUpsertModel,
) => {
	await departmentUpsert.upsertDepartment(department);
	departmentUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};

watch(
	messages,
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

const chatByMode = async (message: string, mode: boolean) => {
	inputMessage.value = "";
	messages.value.push({
		content: message,
		type: "chat",
		isUser: true,
		username: user.username!,
	});
	if (mode) {
		await actionChat(message);
	} else {
		await chat(message);
	}
};

const handleSendClick = async (message: string, mode: boolean) => {
	scrollToBottom();
	if (isLoading.value) {
		abortChat();
		return;
	}
	const validInputMessage = z
		.string({ message: "消息不能为空" })
		.min(1, "消息不能为空")
		.parse(message);
	await chatByMode(validInputMessage, mode);
};

onUnmounted(() => {
	cancel();
});

onMounted(async () => {
	initFlowbite();
	const $upsertModalElement: HTMLElement | null =
		document.querySelector("#user-upsert-modal");
	userUpsertModal.value = new Modal(
		$upsertModalElement,
		{},
		{
			id: "user-upsert-modal",
		},
	);
	const $departmentUpsertModalElement: HTMLElement | null =
		document.querySelector("#department-upsert-modal");
	departmentUpsertModal.value = new Modal(
		$departmentUpsertModalElement,
		{},
		{
			id: "department-upsert-modal",
		},
	);
});
</script>

<style lang="css">
@import "github-markdown-css/github-markdown-light.css";

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
