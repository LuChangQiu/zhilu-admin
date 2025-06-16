import client from "../../api/client";
import type { UserUpsertSubmitModel } from "../../types/UserTypes";

export const useUserUpsert = () => {
	const uploadUserAvatar = async (file: File) => {
		const { data } = await client.POST("/iam/avatar/upload", {
			body: {
				file: file as unknown as string,
			},
			bodySerializer: (body) => {
				const formData = new FormData();
				formData.set("file", body?.file as unknown as string);
				return formData;
			},
			parseAs: "text",
		});
		return data;
	};
	const upsertUser = async (user: UserUpsertSubmitModel) => {
		await client.POST("/iam/user", {
			body: {
				id: user.id,
				username: user.username,
				password: user.password,
				enable: user.enable,
				avatar: user.avatar,
			},
		});
	};

	return {
		uploadUserAvatar,
		upsertUser,
	};
};
