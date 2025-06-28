import client from "@/api/client";
import type {
	DocQueryParams,
	Library,
	LibraryDoc,
	LibraryDocSegment,
	SegmentQueryParams,
} from "@/types/KnowledgeTypes";
import { ref } from "vue";

export const useKnowledgeQuery = () => {
	const libraries = ref<Library[]>([]);
	const docs = ref<LibraryDoc[]>([]);
	const segments = ref<LibraryDocSegment[]>([]);
	const doc = ref<LibraryDoc | null>(null);

	const fetchLibraries = async () => {
		const { data } = await client.GET("/knowledge/libraries", {});
		libraries.value = data || [];
	};

	const fetchLibraryDocs = async (params: DocQueryParams) => {
		const { data } = await client.GET("/knowledge/docs", {
			params: {
				query: {
					libraryId: params.libraryId,
				},
			},
		});
		docs.value = data || [];
	};

	const fetchDocSegments = async (params: SegmentQueryParams) => {
		const { data } = await client.GET("/knowledge/segments", {
			params: {
				query: {
					libraryDocId: params.libraryDocId || params.docId || 0,
				},
			},
		});
		segments.value = data || [];
	};

	return {
		libraries,
		fetchLibraries,
		docs,
		fetchLibraryDocs,
		doc,
		segments,
		fetchDocSegments,
	};
};
