import client from "@/api/client";
import type { components } from "@/api/types/schema";
import { ref } from "vue";

// 定义部门树节点类型
export interface DepartmentTreeNode {
	id: number;
	name: string;
	parentId: number | null;
	children?: DepartmentTreeNode[];
}

export function useDepartmentQuery() {
	// 部门列表数据
	const departments = ref<components["schemas"]["DepartmentRespDto"][]>([]);
	// 可用的部门列表（用于选择上级部门）
	const availableDepartments = ref<components["schemas"]["Department"][]>();
	// 部门树形结构数据
	const departmentTree = ref<DepartmentTreeNode[]>([]);
	// 总记录数
	const total = ref<number>(0);

	// 获取部门列表数据
	const fetchDepartmentWith = async (
		params: {
			name?: string;
		} = {},
		page = 1,
		pageSize = 10,
	) => {
		try {
			const response = await client.GET("/department/page-query", {
				params: {
					query: {
						pageRequestDto: {
							page,
							size: pageSize,
						},
						departmentQueryDto: {
							name: params.name || "",
						},
					},
				},
			});

			if (response.data) {
				departments.value = response.data.data || [];
				total.value = response.data.total || 0;
			}

			return response.data?.data || [];
		} catch (error) {
			console.error("获取部门列表失败:", error);
			return [];
		}
	};

	// 获取可用部门列表（用于选择上级部门）
	const fetchAvailableDepartments = async (excludeId?: number) => {
		try {
			const response = await client.GET("/department/query-available", {
				params: {
					query: {
						id: excludeId,
					},
				},
			});

			if (response.data) {
				availableDepartments.value = response.data;
			}

			return response.data || [];
		} catch (error) {
			console.error("获取可用部门列表失败:", error);
			return [];
		}
	};

	// 构建部门树形结构
	const buildDepartmentTree = (
		departments: components["schemas"]["DepartmentWithParentDto"][],
	): DepartmentTreeNode[] => {
		// 创建映射表，方便快速查找
		const map = new Map<number, DepartmentTreeNode>();
		const roots: DepartmentTreeNode[] = [];

		// 先将所有部门添加到映射表中
		for (const dept of departments) {
			if (dept.id) {
				map.set(dept.id, {
					id: dept.id,
					name: dept.name || "",
					parentId: dept.parentId || null,
					children: [],
				});
			}
		}

		// 构建树形结构
		for (const dept of departments) {
			if (!dept.id) continue;

			const node = map.get(dept.id);
			if (!node) continue;

			if (dept.parentId && map.has(dept.parentId)) {
				// 如果有父节点，将当前节点添加到父节点的children中
				const parent = map.get(dept.parentId);
				parent?.children?.push(node);
			} else {
				// 没有父节点或父节点不存在，作为根节点
				roots.push(node);
			}
		}

		return roots;
	};

	// 获取部门树形结构
	const fetchDepartmentTree = async () => {
		try {
			const response = await client.GET("/department/query-sub", {
				params: {
					query: {
						id: undefined,
					},
				},
			});

			if (response.data) {
				departmentTree.value = buildDepartmentTree(response.data);
			}

			return response.data || [];
		} catch (error) {
			console.error("获取部门树形结构失败:", error);
			return [];
		}
	};

	return {
		departments,
		availableDepartments,
		departmentTree,
		total,
		fetchDepartmentWith,
		fetchAvailableDepartments,
		fetchDepartmentTree,
	};
}
