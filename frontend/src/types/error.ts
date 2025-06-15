class ValidationError extends Error {
	constructor(message: string) {
		super(message);
		this.name = "ValidationError";
	}
}

class HttpError extends Error {
	status: number;
	detail?: string;
	constructor(message: string, status: number, detail?: string) {
		super(message);
		this.name = "HttpError";
		this.status = status;
		this.detail = detail;
	}
}

class UnAuthError extends HttpError {
	constructor(status: number) {
		super("当前用户身份认证异常", status);
		this.name = "UnAuthError";
	}
}

class ForbiddenError extends HttpError {
	constructor(status: number) {
		super("您没有对应的权限", status);
		this.name = "ForbiddenError";
	}
}

class RequestError extends HttpError {
	constructor(status: number) {
		super("请求发生异常，请检查您的输入或稍后再试", status);
		this.name = "RequestError";
	}
}

class InternalServerError extends HttpError {
	constructor(status: number, detail: string) {
		super(detail, status, detail);
		this.name = "InternalServerError";
	}
}

export {
	UnAuthError,
	ForbiddenError,
	RequestError,
	InternalServerError,
	ValidationError,
};
