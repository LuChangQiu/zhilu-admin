import client from "@/api/client";
export const usePermissionBind = () => {
	const bindPermission = async ({
		roleId,
		permissionIds,
	}: {
		roleId: number;
		permissionIds: number[];
	}) => {
		await client.POST("/iam/permission/bind", {
			body: {
				roleId,
				permissionIds,
			},
		});
	};

	const unbindPermission = async ({
		roleId,
		permissionIds,
	}: {
		roleId: number;
		permissionIds: number[];
	}) => {
		await client.POST("/iam/permission/unbind", {
			body: {
				roleId,
				permissionIds,
			},
		});
	};
	return {
		bindPermission,
		unbindPermission,
	};
};
