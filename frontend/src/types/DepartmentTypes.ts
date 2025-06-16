export interface DepartmentFormData {
	id?: number;
	name: string;
	parentId?: number | null;
}

export type DepartmentUpsertModel = DepartmentFormData;

export interface DepartmentData {
	id: number;
	name: string;
	parentId?: number;
	parentName?: string;
	isBound?: boolean;
}
