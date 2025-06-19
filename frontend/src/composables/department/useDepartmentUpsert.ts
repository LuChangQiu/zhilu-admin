import client from "../../api/client";
import type { DepartmentUpsertModel } from "../../types/DepartmentTypes";

export const useDepartmentUpsert = () => {
	const upsertDepartment = async (
		department: DepartmentUpsertModel,
	): Promise<boolean> => {
		try {
			const response = await client.POST("/department", {
				body: {
					id: department.id,
					name: department.name,
					parentId: department.parentId ?? undefined,
				},
			});
			return response.response.ok;
		} catch (error) {
			console.error("保存部门失败:", error);
			return false;
		}
	};

	return {
		upsertDepartment,
	};
};
