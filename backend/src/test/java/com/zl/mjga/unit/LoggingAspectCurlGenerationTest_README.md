# LoggingAspect generateCurlCommand 方法单元测试

## 测试概述

本测试文件 `LoggingAspectCurlGenerationTest.java` 专门针对 `LoggingAspect` 类中的 `generateCurlCommand` 方法进行全面的单元测试，验证该方法在各种场景下生成 curl 命令的正确性。

## 测试架构

测试采用 **嵌套测试类** 的结构，按功能模块组织：

### 1. GET 请求测试 (`GetRequestTests`)
- ✅ 基本 GET 请求 - 无查询参数
- ✅ GET 请求 - 包含查询参数
- ✅ GET 请求 - HTTPS 协议
- ✅ GET 请求 - 自定义端口

### 2. POST 请求测试 (`PostRequestTests`)
- ✅ POST 请求 - JSON 请求体
- ✅ POST 请求 - 空 JSON 请求体
- ✅ POST 请求 - 包含单引号的 JSON

### 3. PUT 和 PATCH 请求测试 (`PutAndPatchRequestTests`)
- ✅ PUT 请求 - JSON 请求体
- ✅ PATCH 请求 - JSON 请求体

### 4. 表单数据请求测试 (`FormDataRequestTests`)
- ✅ POST 请求 - 表单数据
- ✅ POST 请求 - 多值表单参数
- ✅ POST 请求 - 空表单数据

### 5. 请求头处理测试 (`HeaderProcessingTests`)
- ✅ 包含常规请求头
- ✅ 跳过特定请求头
- ✅ 验证跳过的请求头不会出现在 curl 命令中

### 6. 异常情况测试 (`ExceptionHandlingTests`)
- ✅ 读取请求体时发生 IOException
- ✅ 请求参数为 null
- ✅ 请求方法为 null
- ✅ 服务器信息为 null

### 7. 边界用例测试 (`BoundaryTests`)
- ✅ 最小化 GET 请求
- ✅ 复杂查询参数 - 包含特殊字符
- ✅ DELETE 请求 - 不应包含请求体
- ✅ HTTPS 请求 - 标准端口 443
- ✅ JSON 请求体为 null

## 测试覆盖的功能点

### 核心功能验证
1. **HTTP 方法处理**: GET, POST, PUT, PATCH, DELETE
2. **URL 构建**: 协议、主机名、端口、路径、查询参数
3. **请求头处理**: 包含/排除特定请求头
4. **请求体处理**: JSON、表单数据、空请求体
5. **异常处理**: 各种异常情况的优雅处理

### 特殊场景验证
1. **端口处理**: 标准端口省略，非标准端口包含
2. **字符转义**: JSON 中的单引号转义
3. **空值处理**: null 值的安全处理
4. **多值参数**: 表单中同名参数的多个值

## 测试技术特点

### 使用的测试技术
- **JUnit 5**: 现代化的测试框架
- **Mockito**: Mock 对象和行为验证
- **AssertJ**: 流畅的断言 API
- **嵌套测试**: 清晰的测试组织结构

### Mock 策略
- Mock `HttpServletRequest` 对象模拟各种 HTTP 请求场景
- Mock 依赖服务避免外部依赖
- 精确控制测试数据和行为

### 断言策略
- 验证生成的 curl 命令包含预期内容
- 验证不应包含的内容确实被排除
- 验证异常情况的错误消息

## 运行测试

```bash
# 运行所有 generateCurlCommand 相关测试
./gradlew test --tests "com.zl.mjga.unit.LoggingAspectCurlGenerationTest"

# 运行特定测试类别
./gradlew test --tests "com.zl.mjga.unit.LoggingAspectCurlGenerationTest\$GetRequestTests"
```

## 测试价值

这套测试确保了 `generateCurlCommand` 方法在各种复杂场景下都能正确工作，为 AOP 日志功能的 curl 命令生成提供了可靠的质量保证。通过全面的测试覆盖，可以：

1. **防止回归**: 代码修改时及时发现问题
2. **文档作用**: 测试用例本身就是最好的使用文档
3. **重构支持**: 安全地进行代码重构
4. **质量保证**: 确保功能在各种边界条件下正常工作

## 测试结果

所有 **24 个测试用例** 均通过，覆盖了 `generateCurlCommand` 方法的所有主要功能和边界情况。
