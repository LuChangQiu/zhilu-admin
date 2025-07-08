import client from "@/api/client";
import { useActionExcStore } from "@/composables/store/useActionExcStore";
import useAlertStore from "@/composables/store/useAlertStore";
import { ref } from "vue";

/**
 * AOP日志删除 Composable
 * @returns 日志删除相关的状态和方法
 */
export function useAopLogDelete() {
	const alertStore = useAlertStore();
	const actionExcStore = useActionExcStore();
	const loading = ref(false);

	/**
	 * 删除单条日志
	 * @param id 日志ID
	 */
	const deleteLog = async (id: number) => {
		try {
			loading.value = true;

			await client.DELETE("/aop-log/{id}", {
				params: {
					path: {
						id,
					},
				},
			});

			alertStore.showAlert({
				level: "success",
				content: "日志删除成功",
			});

			actionExcStore.notify(true);
			return true;
		} finally {
			loading.value = false;
		}
	};

	/**
	 * 批量删除日志
	 * @param ids 日志ID列表
	 */
	const batchDeleteLogs = async (ids: number[]) => {
		try {
			loading.value = true;

			const response = await client.DELETE("/aop-log/batch", {
				body: ids,
			});

			alertStore.showAlert({
				level: "success",
				content: `成功删除 ${response.data || 0} 条日志`,
			});

			actionExcStore.notify(true);
			return true;
		} finally {
			loading.value = false;
		}
	};

	/**
	 * 删除指定时间前的日志
	 * @param beforeTime 时间点
	 */
	const deleteLogsBefore = async (beforeTime: string) => {
		try {
			loading.value = true;

			const response = await client.DELETE("/aop-log/before", {
				params: {
					query: {
						beforeTime,
					},
				},
			});

			alertStore.showAlert({
				level: "success",
				content: `成功删除 ${response.data || 0} 条日志`,
			});

			actionExcStore.notify(true);
			return true;
		} finally {
			loading.value = false;
		}
	};

	return {
		loading,
		deleteLog,
		batchDeleteLogs,
		deleteLogsBefore,
	};
}
