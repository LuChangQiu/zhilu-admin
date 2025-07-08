import { setupWorker } from "msw/browser";
import aiHandlers from "./aiHandlers";
import aopLogHandlers from "./aopLogHandlers";
import authHandlers from "./authHandlers";
import departmentHandlers from "./departmentHandlers";
import userHandlers from "./iamHandlers";
import knowledgeHandlers from "./knowledgeHandlers";
import permissionHandlers from "./permissionHandlers";
import positionHandlers from "./positionHandlers";
import roleHandlers from "./roleHandlers";
import jobHandlers from "./schedulerHandlers";

export const worker = setupWorker(
	...userHandlers,
	...authHandlers,
	...roleHandlers,
	...permissionHandlers,
	...jobHandlers,
	...departmentHandlers,
	...positionHandlers,
	...aiHandlers,
	...knowledgeHandlers,
	...aopLogHandlers,
);
