export interface UserUpsertSubmitModel {
	id?: number;
	username: string;
	password?: string;
	enable: boolean;
	avatar?: string;
}

export type User = UserRolePermissionModel | null;
