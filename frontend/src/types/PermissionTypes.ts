export interface PermissionFormData {
	id?: number;
	name: string;
	code: string;
}

export type PermissionUpsertModel = PermissionFormData;

export interface PermissionData {
	id: number;
	name: string;
	code: string;
	isBound?: boolean;
}
