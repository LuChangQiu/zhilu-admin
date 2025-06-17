import client from "../../api/client";

export const useAiAction = () => {
	const deleteUserByUsername = async (username: string) => {
		await client.DELETE("/ai/action/user", {
			params: {
				query: {
					username,
				},
			},
		});
	};

	const deleteDepartmentByName = async (name: string) => {
		await client.DELETE("/ai/action/department", {
			params: {
				query: {
					name,
				},
			},
		});
	};

	const deletePositionByName = async (name: string) => {
		await client.DELETE("/ai/action/position", {
			params: {
				query: {
					name,
				},
			},
		});
	};

	const deleteRoleByName = async (name: string) => {
		await client.DELETE("/ai/action/role", {
			params: {
				query: {
					name,
				},
			},
		});
	};

	const deletePermissionByName = async (name: string) => {
		await client.DELETE("/ai/action/permission", {
			params: {
				query: {
					name,
				},
			},
		});
	};

	return {
		deleteUserByUsername,
		deleteDepartmentByName,
		deletePositionByName,
		deleteRoleByName,
		deletePermissionByName,
	};
};
