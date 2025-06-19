import client from "@/api/client";

export const useDepartmentDelete = () => {
	const deleteDepartment = async (departmentId: number): Promise<boolean> => {
		try {
			const response = await client.DELETE("/department", {
				params: {
					query: {
						id: departmentId,
					},
				},
			});
			return response.response.ok;
		} catch (error) {
			console.error("删除部门失败:", error);
			return false;
		}
	};
	return {
		deleteDepartment,
	};
};

export default useDepartmentDelete;
