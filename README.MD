# JFrameX 项目重构说明

## 重构内容

### 1. 国际化Key重构

#### 重构原则
- 移除了所有包含 `title`、`label` 等描述性词汇的key
- 使用更简洁、语义化的命名规范
- 确保key可以作为权限ID使用
- 保持中英文key的一致性

#### 主要变更
- `app.mainframe.title` → `app.mainframe`
- `app.login.title` → `app.login`
- `app.menu.file.title` → `app.menu.file`
- `app.menu.edit.title` → `app.menu.edit`
- `app.menu.view.title` → `app.menu.view`
- `app.menu.user.title` → `app.menu.user`
- `app.menu.options.title` → `app.menu.options`
- `app.menu.help.title` → `app.menu.help`
- `app.setting.title` → `app.setting`

### 2. 权限控制改进

#### Mate注解重构
- 将 `permissionId` 属性改为 `id`
- 权限ID与国际化key保持一致
- 便于权限管理和维护

#### 权限常量更新
- 更新 `PermissionConstants.java` 中的常量定义
- 权限ID直接使用国际化key
- 提供更好的权限控制粒度

### 3. 代码结构优化

#### 忽略的文件
- 忽略 `JFrameUI/src/main/java/cn/ximuli/jframex/ui/demo/` 目录
- 忽略 `JFrameUI/src/main/java/cn/ximuli/jframex/ui/test/` 目录
- 这些目录中的代码不会影响权限控制和国际化功能

#### 更新的文件
- `Mate.java` - 注解定义更新
- `PermissionUtil.java` - 权限工具类更新
- `UICreator.java` - UI创建器更新
- `PermissionConstants.java` - 权限常量更新
- 所有使用Mate注解的组件类

### 4. 国际化文件更新

#### 中文国际化文件
- `JFrameUI/src/main/resources/i18n/messages_zh.properties`
- `JFrameCommon/src/main/resources/i18n/messages_zh_CN.properties`

#### 英文国际化文件
- `JFrameUI/src/main/resources/i18n/messages_en.properties`
- `JFrameCommon/src/main/resources/i18n/messages_en_US.properties`

## 使用说明

### 权限控制
```java
@Mate(value = "app.menu.file", id = "app.menu.file")
public class FileMenu extends JMenu {
    // 组件实现
}
```

### 国际化使用
```java
// 获取国际化文本
String text = I18nHelper.getMessage("app.menu.file");

// 权限检查
boolean hasPermission = PermissionUtil.hasPermission(user, "app.menu.file");
```

## 注意事项

1. 所有新的权限ID都应该与国际化key保持一致
2. 避免使用包含 `title`、`label` 等描述性词汇的key
3. 权限ID应该具有明确的语义含义
4. demo和test目录中的代码不会被权限控制影响

## 后续工作

1. 更新所有使用旧国际化key的代码
2. 完善权限控制测试
3. 更新文档和注释
4. 进行全面的功能测试
