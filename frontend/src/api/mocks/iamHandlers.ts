import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";
import { ERole } from "../../router/constants";

export default [
	http.get("/iam/user", () => {
		const generatePermission = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: `perm_${faker.lorem.words({ min: 1, max: 1 })}`,
			name: faker.lorem.words({ min: 1, max: 1 }),
		});

		const generateRole = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: faker.helpers.arrayElement([
				ERole.ADMIN,
				"editor",
				"viewer",
				"manager",
			]),
			name: faker.person.jobTitle(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
		});

		const generateDepartment = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: `dept_${faker.lorem.word()}`,
			name: faker.company.name(),
			parentId: faker.number.int({ min: 1, max: 30 }),
			enable: faker.datatype.boolean(),
		});

		const generateUser = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			username: faker.internet.email(),
			password: faker.internet.password(),
			enable: faker.datatype.boolean(),
			roles: faker.helpers.multiple(generateRole, {
				count: { min: 1, max: 3 },
			}),
			createTime: faker.date.past().getTime(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
			departments: faker.helpers.multiple(generateDepartment, {
				count: { min: 0, max: 3 },
			}),
		});

		return HttpResponse.json(generateUser());
	}),
	http.get("/iam/users", () => {
		const generatePermission = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: `perm_${faker.lorem.word()}`,
			name: faker.lorem.words({ min: 1, max: 3 }),
		});

		const generateRole = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: [ERole.ADMIN, "editor", "viewer", "manager"],
			name: faker.person.jobTitle(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
		});

		const generateDepartment = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: `dept_${faker.lorem.word()}`,
			name: faker.company.name(),
			parentId: faker.number.int({ min: 1, max: 30 }),
			enable: faker.datatype.boolean(),
		});

		const generateUser = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			username: faker.internet.email(),
			password: faker.internet.password(),
			enable: faker.datatype.boolean(),
			roles: faker.helpers.multiple(generateRole, {
				count: { min: 1, max: 3 },
			}),
			createTime: faker.date.past().getTime(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
			departments: faker.helpers.multiple(generateDepartment, {
				count: { min: 0, max: 3 },
			}),
		});

		const mockData = {
			data: faker.helpers.multiple(generateUser, { count: 10 }),
			total: 30,
		};
		return HttpResponse.json(mockData);
	}),
	http.post("/api/users/:userId/departments", () => {
		console.log('Captured a "POST /api/users/:userId/departments" request');
		return HttpResponse.json({ success: true });
	}),
	http.delete("/api/users/:userId/departments", () => {
		console.log('Captured a "DELETE /api/users/:userId/departments" request');
		return HttpResponse.json({ success: true });
	}),
	http.post("/iam/user", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
	http.delete("/iam/user", ({ params }) => {
		console.log(`Captured a "DELETE /posts/${params.id}" request`);
		return HttpResponse.json();
	}),
	http.post("/iam/me", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
	http.post("/iam/role/bind", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
	http.post("/iam/role/unbind", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
	http.get("/iam/me", () => {
		const generatePermission = () => ({
			id: faker.number.int({ min: 1, max: 1000 }),
			code: `perm_${faker.lorem.word()}`,
			name: faker.lorem.words({ min: 1, max: 3 }),
		});

		const generateRole = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: [ERole.ADMIN, "editor", "viewer", "manager"],
			name: faker.person.jobTitle(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
		});

		const generateDepartment = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			code: `dept_${faker.lorem.word()}`,
			name: faker.company.name(),
			parentId: faker.number.int({ min: 1, max: 30 }),
			enable: faker.datatype.boolean(),
		});

		const generateUser = () => ({
			id: faker.number.int({ min: 1, max: 100 }),
			username: faker.internet.email(),
			password: faker.internet.password(),
			enable: faker.datatype.boolean(),
			roles: faker.helpers.multiple(generateRole, {
				count: { min: 1, max: 3 },
			}),
			createTime: faker.date.past().getTime(),
			permissions: faker.helpers.multiple(generatePermission, {
				count: { min: 1, max: 5 },
			}),
			departments: faker.helpers.multiple(generateDepartment, {
				count: { min: 0, max: 3 },
			}),
		});
		const mockData = generateUser();
		return HttpResponse.json(mockData);
	}),
	http.post("/iam/department/unbind", () => {
		console.log("Captured a 'POST /department/unbind' request");
		return HttpResponse.json();
	}),
	http.post("/iam/department/bind", () => {
		console.log("Captured a 'POST /department/bind' request");
		return HttpResponse.json();
	}),
	http.post("/iam/position/bind", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
	http.post("/iam/position/unbind", () => {
		console.log('Captured a "POST /posts" request');
		return HttpResponse.json();
	}),
];
