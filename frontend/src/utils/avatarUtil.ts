export const getUserAvatarUrl = (avatar?: string): string | undefined => {
	if (avatar?.startsWith("/")) {
		return `${import.meta.env.VITE_STATIC_URL}${avatar}`;
	}
};
