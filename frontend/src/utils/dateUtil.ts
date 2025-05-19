import dayjs from "dayjs";
import localizedFormat from "dayjs/plugin/localizedFormat";
import timezone from "dayjs/plugin/timezone";
import "dayjs/locale/zh-cn";

dayjs.extend(localizedFormat);
dayjs.locale("zh-cn");
dayjs.extend(timezone);
dayjs.tz.setDefault("Asia/Shanghai");

export default dayjs;
