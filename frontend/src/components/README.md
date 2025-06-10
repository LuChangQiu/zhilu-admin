# 表单布局组件说明

本项目提供了四种表单布局组件和一个通用按钮组件，用于不同场景下的数据展示和交互：

## 1. TableFormLayout.vue

PC端表格布局组件，适用于不需要checkbox的普通表格展示。

### 使用方法

```vue
<TableFormLayout 
  :items="dataItems" 
  :columns="columns"
  :keyField="'id'"
  @sort="handleSortClick">
  <!-- 自定义列内容 -->
  <template #fieldName="{ item }">
    {{ item.fieldValue }}
  </template>
  <!-- 排序图标 -->
  <template #sort-icon="{ field }">
    <SortIcon :sortField="getSortField(field)" />
  </template>
</TableFormLayout>
```

### 属性

- `items`: 要展示的数据数组
- `columns`: 列配置数组，每个列对象包含 `title`、`field`、`sortable`(可选)、`class`(可选)
- `idField`: (可选) 指定数据项的ID字段名，默认为 'id'
- `keyField`: (可选) 指定数据项的唯一键字段名，用于v-for的key
- `hasCheckbox`: (可选) 是否显示复选框，默认为false

### 事件

- `sort`: 当点击可排序列时触发，参数为字段名
- `update:checkedItems`: 当选中项变化时触发，参数为选中项的ID数组
- `all-checked-change`: 当全选/取消全选时触发，参数为是否全选

## 2. TableFormLayoutWithCheckbox.vue

PC端表格布局组件，带有checkbox功能，适用于需要多选的表格（如绑定关系管理页面）。

### 使用方法

```vue
<TableFormLayoutWithCheckbox 
  :items="dataItems" 
  :columns="columns"
  :keyField="'id'"
  v-model="checkedIds"
  @all-checked-change="allChecked = $event">
  <!-- 自定义列内容 -->
  <template #fieldName="{ item }">
    {{ item.fieldValue }}
  </template>
</TableFormLayoutWithCheckbox>
```

### 属性

- `items`: 要展示的数据数组
- `columns`: 列配置数组
- `idField`: (可选) 指定数据项的ID字段名，默认为 'id'
- `keyField`: (可选) 指定数据项的唯一键字段名，用于v-for的key
- `modelValue`: (可选) 选中项的ID数组，支持v-model双向绑定

### 事件

- `update:modelValue`: 当选中项变化时触发，参数为选中项的ID数组
- `sort`: 当点击可排序列时触发，参数为字段名
- `all-checked-change`: 当全选/取消全选时触发，参数为是否全选

## 3. MobileCardList.vue

移动端卡片布局组件，适用于不需要checkbox的普通卡片展示。

### 使用方法

```vue
<MobileCardList 
  :items="dataItems"
  :keyField="'id'">
  <!-- 标题区域 -->
  <template #title="{ item }">
    {{ item.title }}
  </template>
  <!-- 状态区域 -->
  <template #status="{ item }">
    <div class="flex items-center">
      <div class="h-2.5 w-2.5 rounded-full me-2" :class="getStatusClass(item)"></div>
      <span>{{ getStatusText(item) }}</span>
    </div>
  </template>
  <!-- 内容区域 -->
  <template #content="{ item }">
    <div>
      <p class="text-xs font-medium text-gray-600">标签</p>
      <p class="text-sm text-gray-900">{{ item.value }}</p>
    </div>
  </template>
  <!-- 操作按钮区域 -->
  <template #actions="{ item }">
    <div class="flex gap-x-2">
      <TableButton variant="primary" size="xs" isMobile @click="handleEdit(item)">
        <template #icon>
          <svg class="w-3 h-3" fill="currentColor" viewBox="0 0 20 20">
            <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
          </svg>
        </template>
        编辑
      </TableButton>
      <TableButton variant="danger" size="xs" isMobile @click="handleDelete(item)">删除</TableButton>
    </div>
  </template>
</MobileCardList>
```

### 属性

- `items`: 要展示的数据数组
- `idField`: (可选) 指定数据项的ID字段名，默认为 'id'
- `keyField`: (可选) 指定数据项的唯一键字段名，用于v-for的key

### 插槽

- `title`: 卡片标题
- `status`: 状态指示器
- `content`: 卡片主要内容
- `tags`: (可选) 标签/分类
- `actions`: 操作按钮

## 4. MobileCardListWithCheckbox.vue

移动端卡片布局组件，带有checkbox功能，适用于需要多选的卡片（如绑定关系管理页面）。

### 使用方法

```vue
<MobileCardListWithCheckbox 
  :items="dataItems" 
  :keyField="'id'"
  v-model="checkedIds">
  <!-- 与MobileCardList用法相同 -->
  <template #title="{ item }">{{ item.title }}</template>
  <template #content="{ item }">{{ item.content }}</template>
</MobileCardListWithCheckbox>
```

### 属性

- `items`: 要展示的数据数组
- `idField`: (可选) 指定数据项的ID字段名，默认为 'id'
- `keyField`: (可选) 指定数据项的唯一键字段名，用于v-for的key
- `modelValue`: (可选) 选中项的ID数组，支持v-model双向绑定

### 事件

- `update:modelValue`: 当选中项变化时触发，参数为选中项的ID数组

## 5. TableButton.vue

通用按钮组件，适用于表格和卡片中的操作按钮。

### 使用方法

```vue
<!-- 基本用法 -->
<TableButton @click="handleClick">默认按钮</TableButton>

<!-- 带图标 -->
<TableButton variant="primary" @click="handleEdit">
  <template #icon>
    <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
      <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z"></path>
    </svg>
  </template>
  编辑
</TableButton>

<!-- 不同变体 -->
<TableButton variant="secondary">次要按钮</TableButton>
<TableButton variant="success">成功按钮</TableButton>
<TableButton variant="danger">危险按钮</TableButton>
<TableButton variant="warning">警告按钮</TableButton>
<TableButton variant="info">信息按钮</TableButton>

<!-- 不同尺寸 -->
<TableButton size="xs">超小按钮</TableButton>
<TableButton size="sm">小按钮</TableButton>
<TableButton size="md">中按钮</TableButton>
<TableButton size="lg">大按钮</TableButton>

<!-- 移动端尺寸 -->
<TableButton size="sm" isMobile>移动端按钮</TableButton>

<!-- 禁用状态 -->
<TableButton disabled>禁用按钮</TableButton>
```

### 属性

- `variant`: (可选) 按钮变体类型，可选值为 'primary'、'secondary'、'success'、'danger'、'warning'、'info'，默认为 'primary'
- `size`: (可选) 按钮尺寸，可选值为 'xs'、'sm'、'md'、'lg'，默认为 'md'
- `disabled`: (可选) 是否禁用，默认为 false
- `className`: (可选) 自定义CSS类名
- `isMobile`: (可选) 是否为移动端尺寸，默认为 false

### 事件

- `click`: 当按钮被点击时触发

## 使用建议

1. 对于普通的数据展示页面（如用户管理、岗位管理等），使用 `TableFormLayout` 和 `MobileCardList`
2. 对于需要多选功能的页面（如角色绑定、部门绑定等），使用 `TableFormLayoutWithCheckbox` 和 `MobileCardListWithCheckbox`
3. 对于表格和卡片中的操作按钮，使用 `TableButton` 组件
4. 根据屏幕尺寸自动切换布局：
   ```vue
   <!-- 移动端卡片布局 -->
   <div class="md:hidden">
     <MobileCardList :items="items" :keyField="'id'">
       <!-- 插槽内容 -->
     </MobileCardList>
   </div>
   
   <!-- PC端表格布局 -->
   <div class="hidden md:block">
     <TableFormLayout :items="items" :columns="columns" :keyField="'id'">
       <!-- 插槽内容 -->
     </TableFormLayout>
   </div>
   ```
