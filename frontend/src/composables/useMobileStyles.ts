/**
 * 移动端样式hook，提供通用的移动端样式类
 */
export function useMobileStyles() {
	// 移动端卡片容器样式
	const cardContainerClass =
		"p-4 bg-white rounded-lg shadow border border-gray-100";

	// 卡片头部样式
	const cardHeaderClass = "flex justify-between items-start mb-3";

	// 卡片标题样式
	const cardTitleClass = "font-medium text-gray-900";

	// 卡片内容容器样式
	const cardContentClass = "text-sm text-gray-600 mb-3 space-y-2";

	// 标签名称样式 - 更深的灰色以增强区分度
	const labelClass = "text-xs font-medium text-gray-600";

	// 标签值样式 - 更深的颜色以突出显示值
	const valueClass = "text-sm text-gray-900 mt-0.5";

	// 卡片网格布局
	const gridContainerClass = "grid grid-cols-2 gap-3";

	// 按钮容器样式
	const actionContainerClass = "flex justify-between items-center mt-4";

	// 行动按钮组样式
	const buttonGroupClass = "flex gap-x-2";

	// 主要按钮样式
	const primaryButtonClass =
		"flex items-center justify-center gap-x-1 text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-xs px-3 py-1.5";

	// 危险按钮样式
	const dangerButtonClass =
		"flex items-center justify-center gap-x-1 bg-red-700 hover:bg-red-800 focus:ring-red-300 text-white focus:ring-4 focus:outline-none font-medium rounded-lg text-xs px-3 py-1.5";

	// 次要按钮样式
	const secondaryButtonClass =
		"flex items-center justify-center gap-x-1 text-gray-900 bg-white border border-gray-300 focus:outline-none hover:bg-gray-100 focus:ring-4 focus:ring-gray-100 font-medium rounded-lg text-xs px-3 py-1.5";

	// 状态指示器样式
	const statusIndicatorClass = "flex items-center";

	// 状态指示点样式
	const statusDotClass = "h-2.5 w-2.5 rounded-full me-2";

	// 卡片分组标题
	const sectionTitleClass = "text-sm font-medium text-gray-700 mb-2";

	return {
		cardContainerClass,
		cardHeaderClass,
		cardTitleClass,
		cardContentClass,
		labelClass,
		valueClass,
		gridContainerClass,
		actionContainerClass,
		buttonGroupClass,
		primaryButtonClass,
		dangerButtonClass,
		secondaryButtonClass,
		statusIndicatorClass,
		statusDotClass,
		sectionTitleClass,
	};
}
