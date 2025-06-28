/**
 * 获取用户头像URL
 * @param avatar 头像路径
 * @returns 完整的头像URL或默认头像
 */
export const getUserAvatarUrl = (avatar?: string): string => {
	if (!avatar) {
		return "/trump.jpg"; // 默认头像
	}

	if (avatar.startsWith("/")) {
		return `${import.meta.env.VITE_STATIC_URL}${avatar}`;
	}

	return avatar; // 如果已经是完整URL则直接返回
};
