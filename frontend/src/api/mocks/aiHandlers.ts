import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

export default [
	http.post("/ai/chat", () => {
		const response = HttpResponse.json({
			message: faker.lorem.sentence({ min: 100, max: 300 }),
		});
		return response;
	}),
];
