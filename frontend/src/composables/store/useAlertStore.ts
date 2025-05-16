import { StorageSerializers, useStorage } from "@vueuse/core";
import { defineStore } from "pinia";
import { computed } from "vue";
import type { AlertProps } from "../../types/alert";

const useAlertStore = defineStore("alertStore", () => {
	const alertStorage = useStorage<AlertProps>(
		"alert-storage",
		{
			content: undefined,
			level: undefined,
			isShow: undefined,
			timer: undefined,
		},
		localStorage,
		{
			serializer: StorageSerializers.object,
		},
	);
	alertStorage.value.isShow = false;
	const showAlert = ({
		content: newContent,
		level: newLevel,
	}: { content: string; level: "info" | "success" | "warning" | "error" }) => {
		clearTimeout(alertStorage.value.timer);
		alertStorage.value = {
			content: newContent,
			level: newLevel,
			isShow: true,
			timer: setTimeout(() => {
				alertStorage.value.isShow = false;
			}, 3000),
		};
	};

	const levelClassName = computed(() => {
		if (!alertStorage.value.level) {
			return;
		}
		return {
			info: "text-blue-800 bg-blue-50   ",
			success: "text-green-800  bg-green-50  ",
			warning: "text-yellow-800  bg-yellow-50  ",
			error: "text-red-800  bg-red-50  ",
		}[alertStorage.value.level];
	});

	return {
		alertStorage,
		showAlert,
		levelClassName,
	};
});

export default useAlertStore;
