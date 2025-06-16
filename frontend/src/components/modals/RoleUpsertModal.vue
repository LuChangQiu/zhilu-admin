<template>
  <BaseModal title="角色管理" size="md" :closeModal="closeModal">
    <!-- Modal body -->
    <div class="p-4 md:p-5">
      <div class="grid gap-4 mb-4 grid-cols-1">
        <div class="col-span-full">
          <label for="name" class="block mb-2 text-sm font-medium text-gray-900">角色名称</label>
          <input type="text" id="name" v-model="formData.name"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            required />
        </div>
        <div class="col-span-full">
          <label for="code" class="block mb-2 text-sm font-medium text-gray-900">角色代码</label>
          <input type="text" id="code" v-model="formData.code"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            required />
        </div>
      </div>
      <button type="submit" @click="handleSubmit"
        class="w-auto text-sm px-4 py-2 text-white flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-center self-start mt-5">
        保存
      </button>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import type { components } from "@/api/types/schema";
import useAlertStore from "@/composables/store/useAlertStore";
import type { RoleUpsertModel } from "@/types/role";
import { initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { z } from "zod";
import BaseModal from "./BaseModal.vue";

const alertStore = useAlertStore();
const { role, closeModal, onSubmit } = defineProps<{
	role?: components["schemas"]["RoleRespDto"];
	closeModal: () => void;
	onSubmit: (data: RoleUpsertModel) => Promise<void>;
}>();

const formData = ref<RoleUpsertModel>({
	name: "",
	code: "",
});

const updateFormData = (newRole: typeof role) => {
	if (!newRole) {
		formData.value = {
			name: "",
			code: "",
		};
		return;
	}

	formData.value = {
		id: newRole.id,
		name: newRole.name ?? "",
		code: newRole.code ?? "",
	};
};

watch(() => role, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		id: z.number().optional(),
		name: z
			.string({
				message: "角色名称不能为空",
			})
			.min(2, "角色名称至少2个字符")
			.max(15, "角色名称最多15个字符"),
		code: z
			.string({
				message: "角色代码不能为空",
			})
			.min(2, "角色代码至少2个字符")
			.max(15, "角色代码最多15个字符"),
	});

	try {
		const validatedData = schema.parse(formData.value);
		await onSubmit(validatedData);
	} catch (error) {
		if (error instanceof z.ZodError) {
			alertStore.showAlert({
				content: error.errors[0].message,
				level: "error",
			});
		}
	}
};

onMounted(() => {
	initFlowbite();
});
</script>
