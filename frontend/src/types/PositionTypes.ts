export interface PositionFormData {
	id?: number;
	name: string;
}

export type PositionUpsertModel = PositionFormData;

export interface PositionData {
	id: number;
	name: string;
	isBound?: boolean;
}
