import client from "@/api/client";
import type { components } from "@/api/types/schema";
import { useErrorHandling } from "@/composables/common/useErrorHandling";
import { usePagination } from "@/composables/common/usePagination";
import { useSorting } from "@/composables/common/useSorting";
import type { AopLogQueryParams } from "@/types/AlertTypes";
import dayjs from "dayjs";
import { ref } from "vue";

/**
 * AOP日志查询 Composable
 * @returns 日志查询相关的状态和方法
 */
export function useAopLogQuery() {
	const { currentPage, pageSize, total, updatePaginationState } =
		usePagination();
	const { sortBy, handleSort, getSortField } = useSorting();

	const logs = ref<components["schemas"]["AopLogRespDto"][]>([]);
	const currentLog = ref<components["schemas"]["AopLogRespDto"]>();
	const loading = ref(false);

	/**
	 * 分页查询日志列表
	 * @param params 查询参数
	 */
	const fetchLogs = async (params: AopLogQueryParams = {}) => {
		try {
			loading.value = true;

			// 处理日期范围
			const queryParams: AopLogQueryParams = { ...params };

			const response = await client.GET("/aop-log/page-query", {
				params: {
					query: {
						pageRequestDto: {
							page: currentPage.value,
							size: pageSize.value,
							sortBy: sortBy.value,
						},
						queryDto: queryParams,
					},
				},
			});

			if (response.data) {
				logs.value = response.data.data || [];
				updatePaginationState({ total: response.data.total || 0 });
			}

			return logs.value;
		} finally {
			loading.value = false;
		}
	};

	/**
	 * 获取单条日志详情
	 * @param id 日志ID
	 */
	const fetchLogDetail = async (id: number) => {
		try {
			loading.value = true;

			const response = await client.GET("/aop-log/{id}", {
				params: {
					path: {
						id,
					},
				},
			});

			if (response.data) {
				currentLog.value = response.data;
			}

			return currentLog.value;
		} finally {
			loading.value = false;
		}
	};

	/**
	 * 格式化日期时间
	 * @param dateTime 日期时间字符串
	 */
	const formatDateTime = (dateTime?: string) => {
		if (!dateTime) return "";
		return dayjs(dateTime).format("YYYY-MM-DD HH:mm:ss");
	};

	/**
	 * 格式化执行时间
	 * @param time 执行时间（毫秒）
	 */
	const formatExecutionTime = (time?: number) => {
		if (!time) return "";
		if (time < 1000) return `${time}ms`;
		return `${(time / 1000).toFixed(2)}s`;
	};

	/**
	 * 格式化JSON字符串
	 * @param jsonString JSON字符串
	 */
	const formatJson = (jsonString?: string) => {
		if (!jsonString) return "";
		try {
			const obj = JSON.parse(jsonString);
			return JSON.stringify(obj, null, 2);
		} catch (e) {
			return jsonString;
		}
	};

	return {
		logs,
		currentLog,
		loading,
		currentPage,
		pageSize,
		total,
		fetchLogs,
		fetchLogDetail,
		handleSort,
		getSortField,
		formatDateTime,
		formatExecutionTime,
		formatJson,
	};
}
