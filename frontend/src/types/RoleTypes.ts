export interface RoleFormData {
	id?: number;
	name: string;
	code: string;
}

export type RoleUpsertModel = RoleFormData;

export interface RoleData {
	id: number;
	name: string;
	code: string;
	isBound?: boolean;
	permissions?: Array<{
		id: number;
		name: string;
		code: string;
		isBound?: boolean;
	}>;
}
