import { defineStore } from "pinia";

export const useActionExcStore = defineStore("refresh", {
	state: () => {
		return {
			callback: (result: boolean) => {
				console.debug("callback", result);
			},
		};
	},
	actions: {
		notify(result: boolean) {
			this.callback(result);
		},
		setCallback(callback: (result: boolean) => void) {
			this.callback = callback;
		},
	},
});
