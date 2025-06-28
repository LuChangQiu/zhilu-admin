import type { components } from "@/api/types/schema";

export type Library = components["schemas"]["Library"];
export type LibraryDoc = components["schemas"]["LibraryDoc"];
export type LibraryDocSegment = components["schemas"]["LibraryDocSegment"];

export interface LibraryUpsertModel {
	id?: number;
	name: string;
	description?: string;
}

export interface DocUpdateModel {
	id: number;
	libId: number;
	enable: boolean;
}

export interface LibraryQueryParams {
	page: number;
	size: number;
}

export interface DocQueryParams {
	libraryId: number;
}

export interface SegmentQueryParams {
		libraryDocId?: number;
		docId?: number;
	}

export enum DocStatus {
	SUCCESS = "SUCCESS",
	INDEXING = "INDEXING",
}
