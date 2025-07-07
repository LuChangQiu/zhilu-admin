import { faker } from "@faker-js/faker";
import { http, HttpResponse } from "msw";

// 生成模拟的知识库数据
const generateLibrary = () => ({
  id: faker.number.int({ min: 1, max: 100 }),
  name: faker.lorem.words(2),
  description: faker.lorem.sentence(),
  createTime: faker.date.recent().toISOString()
});

// 生成模拟的文档数据
const generateDoc = (libId: number) => ({
  id: faker.number.int({ min: 1, max: 1000 }),
  libId,
  name: faker.system.fileName(),
  identify: faker.string.uuid(),
  path: faker.system.filePath(),
  meta: {},
  enable: faker.datatype.boolean(),
  status: faker.helpers.arrayElement(["SUCCESS", "INDEXING"]),
  createTime: faker.date.recent().toISOString(),
  updateTime: faker.date.recent().toISOString()
});

// 生成模拟的文档段落数据
const generateSegment = (docId: number) => ({
  id: faker.number.int({ min: 1, max: 10000 }),
  docId,
  embeddingId: faker.string.uuid(),
  content: faker.lorem.paragraphs(),
  tokenUsage: faker.number.int({ min: 10, max: 1000 })
});

export default [
  // 获取知识库列表
  http.get("/knowledge/libraries", () => {
    const libraries = faker.helpers.multiple(generateLibrary, { count: 5 });
    return HttpResponse.json(libraries);
  }),

  // 获取文档列表
  http.get("/knowledge/docs", ({ request }) => {
    const url = new URL(request.url);
    const libraryId = Number(url.searchParams.get("libraryId"));
    
    if (Number.isNaN(libraryId)) {
      return new HttpResponse(null, { status: 400 });
    }
    
    const docs = faker.helpers.multiple(() => generateDoc(libraryId), { count: 8 });
    return HttpResponse.json(docs);
  }),

  // 获取文档段落
  http.get("/knowledge/segments", ({ request }) => {
    const url = new URL(request.url);
    const libraryDocId = Number(url.searchParams.get("libraryDocId"));
    
    if (Number.isNaN(libraryDocId)) {
      return new HttpResponse(null, { status: 400 });
    }
    
    const segments = faker.helpers.multiple(() => generateSegment(libraryDocId), { count: 12 });
    return HttpResponse.json(segments);
  }),

  // 创建/更新知识库
  http.post("/knowledge/library", async () => {
    return HttpResponse.json({ success: true });
  }),

  // 删除知识库
  http.delete("/knowledge/library", () => {
    return HttpResponse.json({ success: true });
  }),

  // 更新文档
  http.put("/knowledge/doc", async () => {
    return HttpResponse.json({ success: true });
  }),

  // 删除文档
  http.delete("/knowledge/doc", () => {
    return HttpResponse.json({ success: true });
  }),

  // 上传文档
  http.post("/knowledge/doc/upload", async () => {
    return HttpResponse.text("upload-success");
  }),
]; 
