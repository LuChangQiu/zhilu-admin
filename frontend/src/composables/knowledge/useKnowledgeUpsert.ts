import client from "@/api/client";
import type {
	DocUpdateModel,
	LibraryUpsertModel,
} from "@/types/KnowledgeTypes";

export const useKnowledgeUpsert = () => {
	const upsertLibrary = async (library: LibraryUpsertModel) => {
		await client.POST("/knowledge/library", {
			body: {
				id: library.id,
				name: library.name,
				description: library.description,
			},
		});
	};

	const deleteLibrary = async (libraryId: number) => {
		await client.DELETE("/knowledge/library", {
			params: {
				query: {
					libraryId,
				},
			},
		});
	};

	const uploadDoc = async (libraryId: number, file: File) => {
		await client.POST("/knowledge/doc/upload", {
			body: {
				libraryId: libraryId.toString(),
				file: file as unknown as string,
			},
			bodySerializer: (body) => {
				const formData = new FormData();
				for (const [key, value] of Object.entries(body!)) {
					formData.set(key, value as unknown as string);
				}
				return formData;
			},
			parseAs: "text",
		});
	};

	const deleteDoc = async (libraryDocId: number) => {
		await client.DELETE("/knowledge/doc", {
			params: {
				query: {
					libraryDocId,
				},
			},
		});
	};

	const updateDoc = async (doc: DocUpdateModel) => {
		await client.PUT("/knowledge/doc", {
			body: {
				id: doc.id,
				libId: doc.libId,
				enable: doc.enable,
			},
		});
	};

	return {
		upsertLibrary,
		deleteLibrary,
		uploadDoc,
		deleteDoc,
		updateDoc,
	};
};
