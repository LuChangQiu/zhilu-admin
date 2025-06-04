import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

export default [
	http.post("/ai/chat", () => {
		const response = new HttpResponse(
			`data: ${faker.lorem.sentence({ min: 10, max: 100 })}\n\n`,
			{
				headers: {
					"Content-Type": "text/event-stream",
				},
			},
		);
		return response;
	}),
	http.post("/ai/action/search", () => {
		const response = HttpResponse.json({
			action: "DELETE_USER",
		});
		return response;
	}),
	http.delete("/ai/action/user", () => {
		return HttpResponse.json({ success: true });
	}),
	http.get("/ai/llm/page-query", () => {
		const generateLlm = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			name: faker.lorem.word(),
			modelName: faker.lorem.word(),
			apiKey: faker.string.uuid(),
			url: faker.internet.url(),
			type: faker.helpers.arrayElement(["CHAT", "EMBEDDING"]),
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
