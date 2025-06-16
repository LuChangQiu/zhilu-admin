export interface UserFormData {
	id?: number;
	username: string;
	password?: string;
	enable: boolean;
	avatar?: string;
	realName?: string;
	age?: number;
	gender?: number;
	email?: string;
	telephone?: string;
}

export type UserUpsertSubmitModel = UserFormData;

// 用户类型，根据实际情况扩展
export interface UserData {
	id: number;
	username: string;
	enable: boolean;
	avatar?: string;
	realName?: string;
	age?: number;
	gender?: number;
	email?: string;
	telephone?: string;
	roles?: Array<{
		id: number;
		name: string;
		code: string;
		isBound?: boolean;
	}>;
}

export type User = UserData | null;
