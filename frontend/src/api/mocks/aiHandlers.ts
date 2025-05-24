import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

export default [
	http.post("/ai/chat", () => {
		const response = HttpResponse.json({
			message: faker.lorem.sentence({ min: 100, max: 300 }),
		});
		return response;
	}),
	http.get("/ai/llm/page-query", () => {
		const generateLlm = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			name: faker.lorem.word(),
			modelName: faker.lorem.word(),
			apiKey: faker.string.uuid(),
			url: faker.internet.url(),
			enable: faker.datatype.boolean(),
			priority: faker.number.int({ min: 1, max: 10 }),
		});

		const mockData = {
			data: faker.helpers.multiple(generateLlm, { count: 10 }),
			total: 30,
		};
		return HttpResponse.json(mockData);
	}),
	http.put("/ai/llm", () => {
		return HttpResponse.json({
			message: "Llm updated successfully",
		});
	}),
];
