<template>
  <BaseModal title="部门管理" size="md" :closeModal="closeModal">
    <!-- Modal body -->
    <div class="p-4 md:p-5">
      <div class="grid gap-4 mb-4 grid-cols-1">
        <div class="col-span-full">
          <label for="name" class="block mb-2 text-sm font-medium text-gray-900">部门名称</label>
          <input type="text" id="name" v-model="formData.name"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
            required />
        </div>
        <div class="col-span-full">
          <label for="category" class="block mb-2 text-sm font-medium text-gray-900">上级部门</label>
          <select id="category" v-model="formData.parentId"
            class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5">
            <option :value="null">无</option>
            <option v-for="dept in availableDepartments" :key="dept.id" :value="dept.id"
              :selected="dept.id === formData.parentId">{{ dept.name }}</option>
          </select>
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
import type { DepartmentUpsertModel } from "@/types/department";
import { initFlowbite } from "flowbite";
import { onMounted, ref, watch } from "vue";
import { z } from "zod";
import BaseModal from "./BaseModal.vue";

const { department, availableDepartments, onSubmit, closeModal } = defineProps<{
	department?: components["schemas"]["Department"];
	availableDepartments?: components["schemas"]["Department"][];
	closeModal: () => void;
	onSubmit: (department: DepartmentUpsertModel) => Promise<void>;
}>();

const formData = ref<DepartmentUpsertModel>({
	name: "",
	parentId: null,
});

const updateFormData = (newDepartment: typeof department) => {
	if (!newDepartment) {
		formData.value = {
			name: "",
			parentId: null,
		};
		return;
	}

	formData.value = {
		id: newDepartment.id,
		name: newDepartment.name ?? "",
		parentId: newDepartment.parentId,
	};
};

watch(() => department, updateFormData, { immediate: true });

const handleSubmit = async () => {
	const schema = z.object({
		id: z.number().optional(),
		name: z
			.string({
				message: "部门名称不能为空",
			})
			.min(2, "部门名称至少2个字符")
			.max(15, "部门名称最多15个字符"),
		parentId: z.number().nullable().optional(),
	});

	try {
		const validatedData = schema.parse(formData.value);
		await onSubmit(validatedData);
		updateFormData(undefined);
	} catch (error) {
		if (error instanceof z.ZodError) {
			const alertStore = useAlertStore();
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
