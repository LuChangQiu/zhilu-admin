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

	return {
		deleteUserByUsername,
		deleteDepartmentByName,
	};
};
