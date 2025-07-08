export type AlertLevel = "info" | "success" | "warning" | "error";

export interface AopLogQueryParams {
	id?: number;
	className?: string;
	methodName?: string;
	success?: boolean;
	userId?: number;
	ipAddress?: string;
	startTime?: string;
	endTime?: string;
	minExecutionTime?: number;
	maxExecutionTime?: number;
}

export interface AopLog {
	id?: number;
	className?: string;
	methodName?: string;
	methodArgs?: string;
	returnValue?: string;
	executionTime?: number;
	success?: boolean;
	errorMessage?: string;
	userId?: number;
	username?: string;
	ipAddress?: string;
	userAgent?: string;
	curl?: string;
	createTime?: string;
}
