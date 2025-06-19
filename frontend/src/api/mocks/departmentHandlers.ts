import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

export default [
	http.get("/department/page-query", () => {
		const generateDepartment = () => {
			// 20% 的概率生成 parentId 为 null 的数据
			const hasParent = faker.datatype.boolean(0.8);
			return {
				id: faker.number.int({ min: 1, max: 100 }),
				name: faker.company.name(),
				parentId: hasParent ? faker.number.int({ min: 1, max: 100 }) : null,
				isBound: faker.datatype.boolean(),
				parentName: hasParent ? faker.company.name() : null,
			};
		};
		const mockData = {
			data: faker.helpers.multiple(generateDepartment, { count: 10 }),
			total: 30,
		};
		return HttpResponse.json(mockData);
	}),
	http.get("/department/query-available", () => {
		const generateDepartment = () => {
			// 20% 的概率生成 parentId 为 null 的数据
			const hasParent = faker.datatype.boolean(0.8);
			return {
				id: faker.number.int({ min: 1, max: 30 }),
				name: faker.company.name(),
				parentId: hasParent ? faker.number.int({ min: 1, max: 30 }) : null,
				parentName: hasParent ? faker.company.name() : null,
			};
		};
		const mockData = faker.helpers.multiple(generateDepartment, { count: 30 });

		return HttpResponse.json(mockData);
	}),
	http.post("/department", () => {
		console.log("Captured department upsert");
		return HttpResponse.json();
	}),
	http.get("/department/query-sub", ({ request }) => {
		const generateDepartment = () => {
			// 20% 的概率生成 parentId 为 null 的数据
			const hasParent = faker.datatype.boolean(0.8);
			return {
				id: faker.number.int({ min: 1, max: 30 }),
				name: faker.company.name(),
				parentId: hasParent ? faker.number.int({ min: 1, max: 30 }) : null,
				parentName: hasParent ? faker.company.name() : null,
			};
		};
		const mockData = faker.helpers.multiple(generateDepartment, {
			count: 30,
		});

		return HttpResponse.json(mockData);
	}),
	http.delete("/department", () => {
		console.log("Captured department delete");
		return HttpResponse.json();
	}),
];
