import { URL, fileURLToPath } from "node:url";

import tailwindcss from "@tailwindcss/vite";
import vue from "@vitejs/plugin-vue";
import { defineConfig } from "vite";
import { loadEnv } from "vite";
import vueDevTools from "vite-plugin-vue-devtools";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
	const env = loadEnv(mode, process.cwd());
	return {
		server: {
			port: Number(env.VITE_APP_PORT),
		},
		preview: {
			port: Number(env.VITE_APP_PORT),
		},
		plugins: [vue(), vueDevTools(), tailwindcss()],
		build: {
			sourcemap: Boolean(env.VITE_SOURCE_MAP),
		},
		resolve: {
			alias: {
				"@": fileURLToPath(new URL("./src", import.meta.url)),
			},
		},
	};
});
