import client from "@/api/client";

export const useJobUpdate = () => {
	const updateCron = async (trigger: {
		triggerName: string;
		triggerGroup: string;
		cron: string;
	}) => {
		await client.PUT("/scheduler/job/update", {
			params: {
				query: {
					cron: trigger.cron,
				},
			},
			body: {
				name: trigger.triggerName,
				group: trigger.triggerGroup,
			},
		});
	};
	return {
		updateCron,
	};
};
