import "./assets/main.css";

import { createPinia } from "pinia";
import { createApp } from "vue";

import VueDatePicker from "@vuepic/vue-datepicker";
import App from "./App.vue";
import useErrorHandling from "./composables/common/useErrorHandling";
import router from "./router";
import "@vuepic/vue-datepicker/dist/main.css";
import "./assets/datepicker.css";

async function enableMocking() {
	if (import.meta.env.VITE_ENABLE_MOCK === "false") {
		return;
	}

	const { worker } = await import("./api/mocks/setup");

	// `worker.start()` returns a Promise that resolves
	// once the Service Worker is up and ready to intercept requests.
	return worker.start();
}

enableMocking().then(() => {
	const app = createApp(App);
	app.use(createPinia());
	app.use(router);

	const { handleError } = useErrorHandling();
	app.config.errorHandler = (err, instance, info) => {
		handleError(err);
	};

	app.component("VueDatePicker", VueDatePicker);
	app.mount("#app");
});
