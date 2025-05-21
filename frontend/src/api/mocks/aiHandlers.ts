import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

export default [
	http.post("/ai/chat", () => {
		const response = HttpResponse.json({
			message: faker.lorem.sentence(1000),
		});
		return response;
	}),
];
