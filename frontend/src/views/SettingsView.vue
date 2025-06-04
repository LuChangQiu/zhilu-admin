<template>
  <div class="grid grid-cols-1 px-2 sm:px-4 pt-6 xl:grid-cols-3 xl:gap-4">
    <div class="mb-4 col-span-full xl:mb-2">
      <Breadcrumbs :names="['用户设置']" />
      <h1 class="text-xl font-semibold text-gray-900 sm:text-2xl">用户设置</h1>
    </div>
    <!-- Right Content -->
    <div class="col-span-full xl:col-auto">
      <!-- Placeholder for potential right content -->
    </div>
    <div class="col-span-full xl:col-span-1 xl:row-start-2">
      <div class="p-4 sm:p-6 mb-4 bg-white border border-gray-200 rounded-lg shadow-sm">
        <h3 class="mb-4 text-xl font-semibold">个人信息</h3>
        <form action="#">
          <div class="grid grid-cols-1 sm:grid-cols-6 gap-4 sm:gap-6">
            <div class="col-span-1 sm:col-span-6">
              <label for="current-username" class="block mb-2 text-sm font-medium text-gray-900">用户名</label>
              <input type="text" name="current-username" id="current-username" v-model="userForm.username"
                class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5"
                required>
            </div>
            <div class="col-span-1 sm:col-span-6">
              <label for="current-password" class="block mb-2 text-sm font-medium text-gray-900">密码</label>
              <input type="password" name="current-password" id="current-password" v-model="userForm.password"
                class="shadow-sm bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5"
                placeholder="非必填" required>
            </div>
            <div class="col-span-1 sm:col-span-6">
              <label for="password" class="block mb-2 text-sm font-medium text-gray-900">确认密码</label>
              <input type="password" id="password" v-model="userForm.confirmPassword"
                class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5"
                placeholder="非必填" required>
            </div>
            <div class="col-span-1 sm:col-span-6">
              <label for="category" class="block mb-2 text-sm font-medium text-gray-900">状态</label>
              <select id="category" disabled v-model="userForm.enable" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-500 focus:border-primary-500 block w-full p-2.5 
                opacity-50 cursor-not-allowed">
                <option :value=true>启用</option>
                <option :value=false>禁用</option>
              </select>
            </div>
            <div class="col-span-1 sm:col-span-6">
              <button
                class="text-white bg-primary-700 hover:bg-primary-800 focus:ring-4 focus:ring-primary-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center w-auto"
                @click.prevent="handleUpdateClick" type="submit">保存</button>
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import useUserAuth from "@/composables/auth/useUserAuth";
import useUserStore from "@/composables/store/useUserStore";
import { initFlowbite } from "flowbite";
import { onMounted, ref } from "vue";
import { z } from "zod";
import useAlertStore from "../composables/store/useAlertStore";
import { RouteName } from "../router/constants";
import Breadcrumbs from "@/components/Breadcrumbs.vue";

const { user } = useUserStore();
const { upsertCurrentUser } = useUserAuth();
const alertStore = useAlertStore();

const userForm = ref({
	username: user.username,
	password: user.password,
	enable: user.enable,
	confirmPassword: user.password,
});

onMounted(() => {
	initFlowbite();
});

const handleUpdateClick = async () => {
	let validatedData = undefined;

	validatedData = z
		.object({
			username: z
				.string({
					message: "用户名不能为空",
				})
				.min(4, "用户名长度不能小于4个字符"),
			password: z
				.string()
				.min(5, "密码长度不能小于5个字符")
				.optional()
				.nullable(),
			confirmPassword: z.string().optional().nullable(),
			enable: z.boolean({
				message: "状态不能为空",
			}),
		})
		.refine(
			(data) => {
				if (data.password) {
					return data.password === data.confirmPassword;
				}
				return true;
			},
			{ message: "密码输入不一致。" },
		)
		.parse(userForm.value);
	await upsertCurrentUser(validatedData);
	alertStore.showAlert({
		content: "操作成功",
		level: "success",
	});
};
</script>
