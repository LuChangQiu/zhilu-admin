import client from "../../api/client";
import type { DepartmentUpsertModel } from "../../types/DepartmentTypes";

export const useDepartmentUpsert = () => {
	const upsertDepartment = async (department: DepartmentUpsertModel) => {
		await client.POST("/department", {
			body: {
				id: department.id,
				name: department.name,
				parentId: department.parentId ?? undefined,
			},
		});
	};

	return {
		upsertDepartment,
	};
};
