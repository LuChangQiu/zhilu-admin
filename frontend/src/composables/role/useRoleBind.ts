import client from "@/api/client";

export const useRoleBind = () => {
	const bindRole = async ({
		userId,
		roleIds,
	}: { userId: number; roleIds: number[] }) => {
		await client.POST("/iam/role/bind", {
			body: {
				userId,
				roleIds,
			},
		});
	};

	const unbindRole = async (userId: number, roleIds: number[]) => {
		await client.POST("/iam/role/unbind", {
			body: {
				userId,
				roleIds,
			},
		});
	};
	return {
		bindRole,
		unbindRole,
	};
};
