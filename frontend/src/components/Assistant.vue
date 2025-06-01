<template>
	<div
		class="flex flex-col box-border px-3 scroll-auto overflow-auto fixed top-0 right-0 w-64 border-gray-200 border-l min-h-screen"
		ref="chatContainer">
		<div class="flex flex-col gap-y-5 flex-1">
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
						<button
							v-if="chatElement.type === 'action' && (chatElement.command === 'CREATE_USER' || chatElement.command === 'CREATE_DEPARTMENT')"
							type="button" @click="commandActionMap[chatElement.command!]"
							class="px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800">
							{{
							commandContentMap[chatElement.command!]
							}}</button>
						<InputButton
							bgColor="bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none"
							size="sm" :content="commandContentMap[chatElement.command!]" :handleSubmit="handleDeleteUserClick"
							v-if="chatElement.command === 'DELETE_USER'" />
						<InputButton
							bgColor="bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none"
							size="sm" :content="commandContentMap[chatElement.command!]" :handleSubmit="handleDeleteDepartmentClick"
							v-if="chatElement.command === 'DELETE_DEPARTMENT'" />
					</div>
				</div>
			</li>
		</div>

		<form class="sticky">
			<div class="w-full border border-gray-200 rounded-lg bg-gray-50">
				<div class="px-4 py-2 bg-white rounded-t-lg">
					<label for="comment" class="sr-only"></label>
					<textarea id="comment" rows="3" v-model="inputMessage"
						class="w-full px-0 text-gray-900 bg-white border-0  focus:ring-0  " placeholder="发送消息" required></textarea>
				</div>
				<div class="flex justify-between px-2 py-2 border-t border-gray-200">
					<form>
						<select id="countries" v-model="isCommandMode"
							class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg block">
							<option selected :value="false">询问模式</option>
							<option :value="true">指令模式</option>
						</select>
					</form>
					<Button :abortable="true" :isLoading="isLoading" :loadingContent="'中止'" :submitContent="'发送'"
						:handleClick="() => handleSendClick(inputMessage, isCommandMode)" />
				</div>

			</div>
		</form>
	</div>
	<UserUpsertModal :id="'user-upsert-modal'" :onSubmit="handleUpsertUserSubmit" :closeModal="() => {
    userUpsertModal!.hide();
  }">
	</UserUpsertModal>
	<UserDeleteModal :id="'user-delete-modal'" :closeModal="() => {
    currentDeleteUsername = undefined
    userDeleteModal!.hide();
  }" :onSubmit="handleDeleteUserSubmit" title="确定删除该用户吗" content="删除用户"></UserDeleteModal>
	<DepartmentUpsertModal :id="'department-upsert-modal'" :onSubmit="handleUpsertDepartmentSubmit" :closeModal="() => {
    availableDepartments = undefined
    departmentUpsertModal!.hide();
  }" :availableDepartments="availableDepartments">
	</DepartmentUpsertModal>
	<DepartmentDeleteModal :id="'department-delete-modal'" :closeModal="() => {
    currentDeleteDepartmentName = undefined
    departmentDeleteModal!.hide();
  }" :onSubmit="handleDeleteDepartmentSubmit" title="确定删除该部门吗" content="删除部门"></DepartmentDeleteModal>
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
import UserDeleteModal from "@/components/PopupModal.vue";
import { useAiAction } from "@/composables/ai/useAiAction";
import DepartmentDeleteModal from "@/components/PopupModal.vue";
import InputButton from "@/components/InputButton.vue";

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
const userDeleteModal = ref<ModalInterface>();
const { deleteUserByUsername, deleteDepartmentByName } = useAiAction();
const departmentDeleteModal = ref<ModalInterface>();
const currentDeleteUsername = ref<string>();
const currentDeleteDepartmentName = ref<string>();

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
	DELETE_USER: () => {
		userDeleteModal.value?.show();
	},
	DELETE_DEPARTMENT: () => {
		departmentDeleteModal.value?.show();
	},
};

const commandContentMap: Record<string, string> = {
	CREATE_USER: "创建用户",
	CREATE_DEPARTMENT: "创建部门",
	DELETE_USER: "删除用户",
	DELETE_DEPARTMENT: "删除部门",
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

const handleDeleteUserClick = (input: string) => {
	currentDeleteUsername.value = input;
	nextTick(() => {
		userDeleteModal.value?.show();
	});
};

const handleDeleteDepartmentClick = (input: string) => {
	currentDeleteDepartmentName.value = input;
	nextTick(() => {
		departmentDeleteModal.value?.show();
	});
};

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

const handleDeleteUserSubmit = async () => {
	await deleteUserByUsername(currentDeleteUsername.value!);
	userDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};

const handleDeleteDepartmentSubmit = async () => {
	await deleteDepartmentByName(currentDeleteDepartmentName.value!);
	departmentDeleteModal.value?.hide();
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
	const $userDeleteModalElement: HTMLElement | null =
		document.querySelector("#user-delete-modal");
	userDeleteModal.value = new Modal(
		$userDeleteModalElement,
		{},
		{
			id: "user-delete-modal",
		},
	);
	const $departmentDeleteModalElement: HTMLElement | null =
		document.querySelector("#department-delete-modal");
	departmentDeleteModal.value = new Modal(
		$departmentDeleteModalElement,
		{},
		{
			id: "department-delete-modal",
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
