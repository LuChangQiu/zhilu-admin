<template>
	<div
		class="h-[calc(100vh-3.5rem)] flex flex-col box-border p-3 overflow-y-auto w-full lg:w-80 xl:w-96 overflow-x-hidden border-gray-200 border-l"
		ref="chatContainer">
		<div class="flex flex-col gap-y-5 flex-1 pb-2">
			<li v-for="chatElement in messages" :key="chatElement.content"
				:class="['flex items-start gap-2.5', chatElement.isUser ? 'flex-row-reverse' : 'flex-row']">
				<Avatar :src="chatElement.isUser ? user.avatar : '/trump.jpg'" size="sm"
					:alt="chatElement.isUser ? '用户头像' : 'AI头像'" />
				<div
					:class="['flex flex-col leading-1.5 p-4 border-gray-200 rounded-e-xl rounded-es-xl max-w-[calc(100%-40px)]', chatElement.isUser ? 'bg-blue-100' : 'bg-gray-100']">
					<div class="flex items-center space-x-2">
						<span class="text-sm font-semibold text-gray-900 ">{{ chatElement.username }}</span>
						<LoadingIcon :textColor="'text-gray-900'"
							v-if="isLoading && !chatElement.isUser && chatElement.content === ''" />
					</div>
					<div>
						<div class="markdown-content markdown-body text-base font-normal py-2.5 text-gray-900 break-words"
							v-html="renderMarkdown(chatElement.content)">
						</div>
						<button
							v-if="chatElement.type === 'action' && (chatElement.command === 'CREATE_USER' || chatElement.command === 'CREATE_DEPARTMENT')"
							type="button" @click="commandActionMap[chatElement.command!]"
							class="px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300">
							{{
							commandContentMap[chatElement.command!]
							}}</button>
						<InputButton size="md"
							bgColor="bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none"
							:content="commandContentMap[chatElement.command!]" :handleSubmit="handleDeleteUserClick"
							v-if="chatElement.command === 'DELETE_USER'" />
						<InputButton size="md"
							bgColor="bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none"
							:content="commandContentMap[chatElement.command!]" :handleSubmit="handleDeleteDepartmentClick"
							v-if="chatElement.command === 'DELETE_DEPARTMENT'" />
					</div>
				</div>
			</li>
		</div>

		<form class="sticky">
			<button @click.prevent="clearConversation"
				class="relative inline-flex items-center justify-center p-0.5 mb-2 me-2 
				overflow-hidden text-sm font-medium text-gray-900 rounded-lg group bg-gradient-to-br from-purple-600 to-blue-500 group-hover:from-purple-600 group-hover:to-blue-500 hover:text-white focus:ring-4 focus:outline-none focus:ring-blue-300 ">
				<span
					class="relative px-3 py-2 text-xs font-medium transition-all ease-in duration-75 bg-white rounded-md group-hover:bg-transparent">
					开启新对话
				</span>
			</button>
			<div class="w-full border border-gray-200 rounded-lg bg-gray-50">
				<div class="px-4 py-2 bg-white rounded-t-lg">
					<label for="comment" class="sr-only"></label>
					<textarea id="comment" rows="3" v-model="inputMessage"
						class="w-full px-0 text-gray-900 bg-white border-0  focus:ring-0" :placeholder="
						commandPlaceholderMap[commandMode]
						" required></textarea>
				</div>
				<div class="flex justify-between px-2 py-2 border-t border-gray-200">
					<select id="countries" v-model="commandMode"
						class="bg-white border border-gray-300 text-gray-900 text-sm rounded-lg block">
						<option selected :value="'execute'">指令模式</option>
						<option :value="'search'">搜索模式</option>
						<option :value="'chat'">询问模式</option>
					</select>
					<TableButton variant="primary" :isLoading="isLoading" :abortable="true"
						@click="() => handleSendClick(inputMessage, commandMode)">
						{{ isLoading ? '中止' : '发送' }}
					</TableButton>
				</div>

			</div>
		</form>
	</div>
	<UserFormDialog :id="'user-upsert-modal'" :onSubmit="handleUpsertUserSubmit" :closeModal="() => {
    userUpsertModal!.hide();
  }">
	</UserFormDialog>
	<UserDeleteModal :id="'user-delete-modal'" :closeModal="() => {
		currentDeleteUsername = undefined
    userDeleteModal!.hide();
  }" :onSubmit="handleDeleteUserSubmit" title="确定删除该用户吗" content="删除用户"></UserDeleteModal>
	<DepartmentFormDialog :id="'department-upsert-modal'" :onSubmit="handleUpsertDepartmentSubmit" :closeModal="() => {
    availableDepartments = undefined
    departmentUpsertModal!.hide();
  }" :availableDepartments="availableDepartments">
	</DepartmentFormDialog>
	<DepartmentDeleteModal :id="'department-delete-modal'" :closeModal="() => {
    currentDeleteDepartmentName = undefined
    departmentDeleteModal!.hide();
  }" :onSubmit="handleDeleteDepartmentSubmit" title="确定删除该部门吗" content="删除部门"></DepartmentDeleteModal>
</template>

<script setup lang="ts">
import { LoadingIcon } from "@/components/icons";
import UserDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import DepartmentDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import DepartmentFormDialog from "@/components/modals/DepartmentFormDialog.vue";
import TableButton from "@/components/tables/TableButton.vue";
import Avatar from "@/components/ui/Avatar.vue";
import InputButton from "@/components/ui/InputButton.vue";
import { useAiAction } from "@/composables/ai/useAiAction";
import { useAiChat } from "@/composables/ai/useAiChat";
import { useDepartmentQuery } from "@/composables/department/useDepartmentQuery";
import { useDepartmentUpsert } from "@/composables/department/useDepartmentUpsert";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import useUserStore from "@/composables/store/useUserStore";
import { useUserUpsert } from "@/composables/user/useUserUpsert";
import type { DepartmentUpsertModel } from "@/types/DepartmentTypes";
import type { UserUpsertSubmitModel } from "@/types/UserTypes";
import DOMPurify from "dompurify";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { marked } from "marked";
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { z } from "zod";

const {
	messages,
	chat,
	isLoading,
	cancel,
	searchAction,
	executeAction,
	clearConversation,
} = useAiChat();
const { user } = useUserStore();
const userUpsertModal = ref<ModalInterface>();
const departmentUpsertModal = ref<ModalInterface>();
const inputMessage = ref("");
const chatContainer = ref<HTMLElement | null>(null);
const alertStore = useAlertStore();
const commandMode = ref<"chat" | "search" | "execute">("execute");
const userUpsert = useUserUpsert();
const departmentUpsert = useDepartmentUpsert();
const userDeleteModal = ref<ModalInterface>();
const { deleteUserByUsername, deleteDepartmentByName } = useAiAction();
const departmentDeleteModal = ref<ModalInterface>();
const currentDeleteUsername = ref<string>();
const currentDeleteDepartmentName = ref<string>();
const actionExcStore = useActionExcStore();
const { availableDepartments, fetchAvailableDepartments } =
	useDepartmentQuery();

const commandPlaceholderMap: Record<string, string> = {
	chat: "随便聊聊",
	search: "搜索创建用户、删除部门等功能",
	execute: "帮我创建一个名为 mjga 的用户",
};

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
	userDeleteModal.value?.show();
};

const handleDeleteDepartmentClick = (input: string) => {
	currentDeleteDepartmentName.value = input;
	departmentDeleteModal.value?.show();
};

const handleUpsertUserSubmit = async (data: UserUpsertSubmitModel) => {
	await userUpsert.upsertUser(data);
	userUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
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
	actionExcStore.notify(true);
};

const handleDeleteUserSubmit = async () => {
	await deleteUserByUsername(currentDeleteUsername.value!);
	userDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleDeleteDepartmentSubmit = async () => {
	await deleteDepartmentByName(currentDeleteDepartmentName.value!);
	departmentDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

watch(
	messages,
	() => {
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

const chatByMode = async (
	message: string,
	mode: "chat" | "search" | "execute",
) => {
	inputMessage.value = "";
	messages.value.push({
		content: message,
		type: "chat",
		isUser: true,
		username: user.username!,
	});
	await nextTick(() => {
		scrollToBottom();
	});
	if (mode === "search") {
		await searchAction(message);
	} else if (mode === "execute") {
		await executeAction(message);
		actionExcStore.notify(true);
	} else {
		await chat(message);
	}
};

const handleSendClick = async (
	message: string,
	mode: "chat" | "search" | "execute",
) => {
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
	if ($upsertModalElement) {
		userUpsertModal.value = new Modal($upsertModalElement, {});
	}
	const $userDeleteModalElement: HTMLElement | null =
		document.querySelector("#user-delete-modal");
	if ($userDeleteModalElement) {
		userDeleteModal.value = new Modal(
			$userDeleteModalElement,
			{},
			{
				id: "user-delete-modal",
			},
		);
	}
	const $departmentDeleteModalElement: HTMLElement | null =
		document.querySelector("#department-delete-modal");
	if ($departmentDeleteModalElement) {
		departmentDeleteModal.value = new Modal($departmentDeleteModalElement, {});
	}
	const $departmentUpsertModalElement: HTMLElement | null =
		document.querySelector("#department-upsert-modal");
	if ($departmentUpsertModalElement) {
		departmentUpsertModal.value = new Modal($departmentUpsertModalElement, {});
	}
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
