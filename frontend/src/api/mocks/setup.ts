import { setupWorker } from "msw/browser";
import authHandlers from "./authHandlers";
import jobHandlers from "./schedulerHandlers";
import permissionHandlers from "./permissionHandlers";
import roleHandlers from "./roleHandlers";
import userHandlers from "./iamHandlers";
import departmentHandlers from "./departmentHandlers";
import positionHandlers from "./positionHandlers";
import aiHandlers from "./aiHandlers";
export const worker = setupWorker(
	...userHandlers,
	...authHandlers,
	...roleHandlers,
	...permissionHandlers,
	...jobHandlers,
	...departmentHandlers,
	...positionHandlers,
	...aiHandlers,
);
