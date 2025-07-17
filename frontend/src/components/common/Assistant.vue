<template>
	<div
		class="h-[calc(100vh-3.5rem)] flex flex-col box-border p-3 overflow-y-auto w-full lg:w-80 xl:w-96 overflow-x-hidden border-gray-200 border-l"
		ref="chatContainer">
		<div class="flex flex-col gap-y-5 flex-1 pb-2">
			<li v-for="chatElement in messages" :key="chatElement.content"
				:class="['flex items-start gap-2.5', chatElement.isUser ? 'flex-row-reverse' : 'flex-row']">
				<Avatar :src="chatElement.isUser ? user.avatar : undefined" size="sm"
					:alt="chatElement.isUser ? '用户头像' : 'AI头像'" />
				<div
					:class="['flex flex-col leading-1.5 p-4 border-gray-200 max-w-[calc(100%-40px)]', chatElement.isUser ? 'bg-blue-100 rounded-tl-xl rounded-bl-xl rounded-br-xl' : 'bg-gray-100 rounded-e-xl rounded-es-xl']">
					<div class="flex items-center space-x-2">
						<span class="text-sm font-semibold text-gray-900 ">{{ chatElement.username }}</span>
						<LoadingIcon :textColor="'text-gray-900'"
							v-if="isLoading && !chatElement.isUser && chatElement.content === ''" />
						<span v-if="!chatElement.isUser && chatElement.withLibrary"
							class="text-xs bg-blue-100 text-blue-800 px-2 py-0.5 rounded-full">
							{{ chatElement.libraryName }}
						</span>
					</div>
					<div>
						<div class="markdown-content markdown-body text-base font-normal py-2.5 text-gray-900 break-words"
							v-html="renderMarkdown(chatElement.content)">
						</div>
						<button v-if="chatElement.type === 'action' && chatElement.command?.startsWith('CREATE_')" type="button"
							@click="commandActionMap[chatElement.command!]"
							class="px-3 py-2 text-sm font-medium text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300">
							{{
							commandContentMap[chatElement.command!]
							}}</button>
						<InputButton size="md"
							bgColor="bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none"
							:content="commandContentMap[chatElement.command!]" :handleSubmit="commandActionMap[chatElement.command!]"
							v-if="chatElement.command?.startsWith('DELETE_')" />
					</div>
				</div>
			</li>
		</div>

		<form class="sticky">
			<div class="flex items-center justify-between gap-2 mb-2">
				<button @click.prevent="clearConversation"
					class="relative inline-flex items-center justify-center p-0.5 
					overflow-hidden text-sm font-medium text-gray-900 rounded-lg group bg-gradient-to-br from-purple-600 to-blue-500 group-hover:from-purple-600 group-hover:to-blue-500 hover:text-white focus:ring-4 focus:outline-none focus:ring-blue-300 ">
					<span
						class="relative px-3 py-2 text-xs font-medium transition-all ease-in duration-75 bg-white rounded-md group-hover:bg-transparent">
						开启新对话
					</span>
				</button>

				<select v-if="commandMode === 'chat'" v-model="selectedLibraryId"
					class="bg-white border border-gray-300 text-gray-900 text-xs rounded-lg py-2 px-2 flex-1 max-w-48">
					<option :value="undefined">不使用知识库</option>
					<option v-for="library in libraries" :key="library.id" :value="library.id">
						{{ library.name }}
					</option>
				</select>
			</div>
			<div class="w-full border border-gray-200 rounded-lg bg-gray-50">
				<div class="px-4 py-2 bg-white rounded-t-lg">
					<label for="comment" class="sr-only"></label>
					<textarea id="comment" rows="3" v-model="inputMessage"
						class="w-full px-0 text-gray-900 bg-white border-0  focus:ring-0" :placeholder="
						commandPlaceholderMap[commandMode]
						" required></textarea>
				</div>
				<div class="flex justify-between px-2 py-2 border-t border-gray-200">
					<select id="commandMode" v-model="commandMode"
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
	<PositionFormDialog :id="'position-upsert-modal'" :onSubmit="handleUpsertPositionSubmit" :closeModal="() => {
    positionUpsertModal!.hide();
  }">
	</PositionFormDialog>
	<PositionDeleteModal :id="'position-delete-modal'" :closeModal="() => {
    currentDeletePositionName = undefined
    positionDeleteModal!.hide();
  }" :onSubmit="handleDeletePositionSubmit" title="确定删除该岗位吗" content="删除岗位"></PositionDeleteModal>
	<RoleFormDialog :id="'role-upsert-modal'" :onSubmit="handleUpsertRoleSubmit" :closeModal="() => {
    roleUpsertModal!.hide();
  }">
	</RoleFormDialog>
	<RoleDeleteModal :id="'role-delete-modal'" :closeModal="() => {
    currentDeleteRoleName = undefined
    roleDeleteModal!.hide();
  }" :onSubmit="handleDeleteRoleSubmit" title="确定删除该角色吗" content="删除角色"></RoleDeleteModal>
	<PermissionFormDialog :id="'permission-upsert-modal'" :onSubmit="handleUpsertPermissionSubmit" :closeModal="() => {
    permissionUpsertModal!.hide();
  }">
	</PermissionFormDialog>
	<PermissionDeleteModal :id="'permission-delete-modal'" :closeModal="() => {
    currentDeletePermissionName = undefined
    permissionDeleteModal!.hide();
  }" :onSubmit="handleDeletePermissionSubmit" title="确定删除该权限吗" content="删除权限"></PermissionDeleteModal>
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
import usePermissionUpsert from "@/composables/permission/usePermissionUpsert";
import { usePositionUpsert } from "@/composables/position/usePositionUpsert";
import { useRoleUpsert } from "@/composables/role/useRoleUpsert";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import useUserStore from "@/composables/store/useUserStore";
import { useUserUpsert } from "@/composables/user/useUserUpsert";
import type { DepartmentUpsertModel } from "@/types/DepartmentTypes";
import type { PositionUpsertModel } from "@/types/PositionTypes";
import type { PermissionUpsertModel } from "@/types/PermissionTypes";
import type { RoleUpsertModel } from "@/types/RoleTypes";
import type { UserUpsertSubmitModel } from "@/types/UserTypes";
import DOMPurify from "dompurify";
import { Modal, type ModalInterface, initFlowbite } from "flowbite";
import { marked } from "marked";
import { nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { z } from "zod";
import PermissionDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import PermissionFormDialog from "@/components/modals/PermissionFormDialog.vue";
import PositionDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import PositionFormDialog from "@/components/modals/PositionFormDialog.vue";
import RoleDeleteModal from "@/components/modals/ConfirmationDialog.vue";
import RoleFormDialog from "@/components/modals/RoleFormDialog.vue";
import { useKnowledgeQuery } from "@/composables/knowledge/useKnowledgeQuery";
import { UserFormDialog } from "../modals";
import { computed } from "vue";

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
const positionUpsert = usePositionUpsert();
const roleUpsert = useRoleUpsert();
const userDeleteModal = ref<ModalInterface>();
const positionUpsertModal = ref<ModalInterface>();
const positionDeleteModal = ref<ModalInterface>();
const roleUpsertModal = ref<ModalInterface>();
const roleDeleteModal = ref<ModalInterface>();
const permissionUpsertModal = ref<ModalInterface>();
const permissionDeleteModal = ref<ModalInterface>();
const {
	deleteUserByUsername,
	deleteDepartmentByName,
	deletePositionByName,
	deleteRoleByName,
	deletePermissionByName,
} = useAiAction();
const { upsertPermission } = usePermissionUpsert();
const departmentDeleteModal = ref<ModalInterface>();
const currentDeleteUsername = ref<string>();
const currentDeleteDepartmentName = ref<string>();
const currentDeletePositionName = ref<string>();
const currentDeleteRoleName = ref<string>();
const currentDeletePermissionName = ref<string>();
const actionExcStore = useActionExcStore();
const { availableDepartments, fetchAvailableDepartments } =
	useDepartmentQuery();

// 知识库相关
const { libraries, fetchLibraries } = useKnowledgeQuery();
const selectedLibraryId = ref<number | null | undefined>(undefined);
const selectedLibraryName = computed(() => {
	return libraries.value.find(
		(library) => library.id === selectedLibraryId.value,
	)?.name;
});

const commandPlaceholderMap: Record<string, string> = {
	chat: "随便聊聊",
	search: "输入「创建用户、删除部门、创建岗位、创建角色、创建权限」试试看",
	execute: "帮我创建一个名为 mjga 的用户",
};

const commandContentMap: Record<string, string> = {
	CREATE_USER: "创建用户",
	CREATE_DEPARTMENT: "创建部门",
	DELETE_USER: "删除用户",
	DELETE_DEPARTMENT: "删除部门",
	CREATE_POSITION: "创建岗位",
	DELETE_POSITION: "删除岗位",
	CREATE_ROLE: "创建角色",
	DELETE_ROLE: "删除角色",
	CREATE_PERMISSION: "创建权限",
	DELETE_PERMISSION: "删除权限",
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

const handleUpsertPositionSubmit = async (position: PositionUpsertModel) => {
	await positionUpsert.upsertPosition(position);
	positionUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleDeletePositionSubmit = async () => {
	await deletePositionByName(currentDeletePositionName.value!);
	positionDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleUpsertRoleSubmit = async (role: RoleUpsertModel) => {
	await roleUpsert.upsertRole(role);
	roleUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleDeleteRoleSubmit = async () => {
	await deleteRoleByName(currentDeleteRoleName.value!);
	roleDeleteModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleUpsertPermissionSubmit = async (
	permission: PermissionUpsertModel,
) => {
	await upsertPermission(permission);
	permissionUpsertModal.value?.hide();
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
	actionExcStore.notify(true);
};

const handleDeletePermissionSubmit = async () => {
	await deletePermissionByName(currentDeletePermissionName.value!);
	permissionDeleteModal.value?.hide();
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
		// 聊天模式，判断是否使用知识库
		if (selectedLibraryId.value !== undefined) {
			await chat(message, selectedLibraryId.value, selectedLibraryName.value);
		} else {
			await chat(message);
		}
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
	// 加载知识库列表
	await fetchLibraries();

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
			{}
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
	const $positionDeleteModalElement: HTMLElement | null =
		document.querySelector("#position-delete-modal");
	if ($positionDeleteModalElement) {
		positionDeleteModal.value = new Modal($positionDeleteModalElement, {});
	}
	const $positionUpsertModalElement: HTMLElement | null =
		document.querySelector("#position-upsert-modal");
	if ($positionUpsertModalElement) {
		positionUpsertModal.value = new Modal($positionUpsertModalElement, {});
	}
	const $roleDeleteModalElement: HTMLElement | null =
		document.querySelector("#role-delete-modal");
	if ($roleDeleteModalElement) {
		roleDeleteModal.value = new Modal($roleDeleteModalElement, {});
	}
	const $roleUpsertModalElement: HTMLElement | null =
		document.querySelector("#role-upsert-modal");
	if ($roleUpsertModalElement) {
		roleUpsertModal.value = new Modal($roleUpsertModalElement, {});
	}
	const $permissionDeleteModalElement: HTMLElement | null =
		document.querySelector("#permission-delete-modal");
	if ($permissionDeleteModalElement) {
		permissionDeleteModal.value = new Modal($permissionDeleteModalElement, {});
	}
	const $permissionUpsertModalElement: HTMLElement | null =
		document.querySelector("#permission-upsert-modal");
	if ($permissionUpsertModalElement) {
		permissionUpsertModal.value = new Modal($permissionUpsertModalElement, {});
	}
});

const commandActionMap: Record<string, (input: string) => void> = {
	CREATE_USER: () => {
		userUpsertModal.value?.show();
	},
	CREATE_DEPARTMENT: () => {
		fetchAvailableDepartments();
		departmentUpsertModal.value?.show();
	},
	DELETE_USER: (input: string) => {
		currentDeleteUsername.value = input;
		userDeleteModal.value?.show();
	},
	DELETE_DEPARTMENT: (input: string) => {
		currentDeleteDepartmentName.value = input;
		departmentDeleteModal.value?.show();
	},
	CREATE_POSITION: () => {
		positionUpsertModal.value?.show();
	},
	DELETE_POSITION: (input: string) => {
		currentDeletePositionName.value = input;
		positionDeleteModal.value?.show();
	},
	CREATE_ROLE: () => {
		roleUpsertModal.value?.show();
	},
	DELETE_ROLE: (input: string) => {
		currentDeleteRoleName.value = input;
		roleDeleteModal.value?.show();
	},
	CREATE_PERMISSION: () => {
		permissionUpsertModal.value?.show();
	},
	DELETE_PERMISSION: (input: string) => {
		currentDeletePermissionName.value = input;
		permissionDeleteModal.value?.show();
	},
};
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
