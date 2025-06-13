import dayjs from "dayjs";
import localizedFormat from "dayjs/plugin/localizedFormat";
import timezone from "dayjs/plugin/timezone";
import "dayjs/locale/zh-cn";

dayjs.extend(localizedFormat);
dayjs.locale("zh-cn");
dayjs.extend(timezone);
dayjs.tz.setDefault("Asia/Shanghai");

const formatDate = (date?: Date) => {
	if (!date) return undefined;
	return dayjs(date).format("YYYY-MM-DDTHH:mm:ss.SSSZ");
};

export { dayjs, formatDate };
