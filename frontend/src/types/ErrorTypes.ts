export class BaseError extends Error {
	constructor(message: string) {
		super(message);
		this.name = this.constructor.name;
	}
}

export class ValidationError extends BaseError {}

export class RequestError extends BaseError {
	status: number;

	constructor(status: number) {
		super(`请求错误: ${status}`);
		this.status = status;
	}
}

export class UnAuthError extends BaseError {
	status: number;

	constructor(status: number) {
		super(`未授权: ${status}`);
		this.status = status;
	}
}

export class ForbiddenError extends BaseError {
	status: number;

	constructor(status: number) {
		super(`禁止访问: ${status}`);
		this.status = status;
	}
}

export class InternalServerError extends BaseError {
	detail?: string;
	status: number;

	constructor(status: number, detail?: string) {
		super(`服务器错误: ${status}`);
		this.status = status;
		this.detail = detail;
	}
}
