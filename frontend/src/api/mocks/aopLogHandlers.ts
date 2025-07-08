import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

// 生成AOP日志数据
const generateAopLog = () => ({
  id: faker.number.int({ min: 1, max: 1000 }),
  className: faker.helpers.arrayElement([
    "com.example.controller.UserController",
    "com.example.service.UserService",
    "com.example.controller.RoleController",
    "com.example.service.RoleService",
    "com.example.controller.DepartmentController",
    "com.example.service.DepartmentService",
  ]),
  methodName: faker.helpers.arrayElement([
    "findById",
    "save",
    "update",
    "delete",
    "findAll",
    "findByName",
    "pageQuery",
  ]),
  methodArgs: JSON.stringify([
    { name: "id", value: faker.number.int({ min: 1, max: 100 }) },
    { name: "name", value: faker.person.fullName() },
  ]),
  returnValue: JSON.stringify({
    id: faker.number.int({ min: 1, max: 100 }),
    name: faker.person.fullName(),
    success: true,
  }),
  executionTime: faker.number.int({ min: 10, max: 5000 }),
  success: faker.datatype.boolean(0.9), // 90%成功率
  errorMessage: faker.helpers.maybe(() => faker.lorem.sentence(), { probability: 0.1 }),
  userId: faker.number.int({ min: 1, max: 100 }),
  username: faker.internet.userName(),
  ipAddress: faker.internet.ip(),
  userAgent: faker.internet.userAgent(),
  curl: `curl -X GET "${faker.internet.url()}" -H "Authorization: Bearer ${faker.string.alphanumeric(32)}"`,
  createTime: faker.date.recent({ days: 30 }).toISOString(),
});

export default [
  // 分页查询AOP日志
  http.get("/aop-log/page-query", () => {
    const mockData = {
      data: faker.helpers.multiple(generateAopLog, { count: 10 }),
      total: 100,
    };
    return HttpResponse.json(mockData);
  }),

  // 查询单条日志详情
  http.get("/aop-log/:id", ({ params }) => {
    const id = params.id;
    return HttpResponse.json({
      ...generateAopLog(),
      id: Number(id),
    });
  }),

  // 删除单条日志
  http.delete("/aop-log/:id", ({ params }) => {
    console.log(`Captured a "DELETE /aop-log/${params.id}" request`);
    return HttpResponse.json({ success: true });
  }),

  // 批量删除日志
  http.delete("/aop-log/batch", async ({ request }) => {
    const ids = await request.json();
    console.log(`Captured a "DELETE /aop-log/batch" request with ids: ${ids}`);
    return HttpResponse.json(ids.length);
  }),

  // 删除指定时间前的日志
  http.delete("/aop-log/before", ({ params }) => {
    const { beforeTime } = Object.fromEntries(
      new URL(params.request.url).searchParams
    );
    console.log(`Captured a "DELETE /aop-log/before" request with time: ${beforeTime}`);
    return HttpResponse.json(faker.number.int({ min: 5, max: 50 }));
  }),
]; 
