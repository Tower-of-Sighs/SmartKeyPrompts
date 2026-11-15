# SmartKeyPrompts 开发规范

项目 mod id 为 smartkeyprompts，Java 包名 / Gradle group 为 com.mafuyu404.smartkeyprompts。功能与目标配置见 [README](README.md)。本工程已完成初始化，一次性初始化脚本与模板已移除；不得重新初始化并覆盖维护者编辑的文档。初始化不是构建、兼容性或运行行为验证，禁止编造已通过的测试和功能。

## 架构与修改边界

- 默认将没有确证 loader / Minecraft 版本差异的代码与资源放在 `common/`，允许引用 Minecraft 和 loader 类型；平台类型依赖本身不是拆分理由。
- 只有阅读实际源码/API 确证差异后，才将相应实现移到 target；差异记录在 target 的 `commonJava { exclude 'path/to/File.java' }`，只排除共享来源文件，不排除 target 的替代实现。
- `shared.gradle` 直接将 common 原文件接入 target 源码集。禁止复制源码到 build、依赖 common jar、重复添加 common 源码根或使用 `project(':common')`。
- 不以运行时版本判断、反射分发或 classpath 同名类覆盖隐藏兼容性问题。每个产物只对应一个 loader 和 Minecraft 版本，不发布 universal jar。
- 单 target 改动限定在该目标；common 改动需验证所有受影响目标。不得顺手修改其他版本或未经验证统一 API。
- 共享资源放 common；metadata、Mixin 配置、access widener / transformer 和版本专属资源放 target。同路径资源明确唯一归属，不依赖覆盖顺序。
- 共用模组元数据放根 `gradle.properties`，版本、loader 和 JDK 放 target 属性。普通本地 jar 放目标 `libs/`，不假定存在传递依赖。
- 插件和运行配置归 target；通用源码接入和发布逻辑分别复用 `gradle/target-conventions/shared.gradle`、`publish.gradle`。

## 当前迁移边界

- 已迁入 `neoforge-1.21.1`、`fabric-1.21.1`、`fabric-1.20.1`、`forge-1.20.1` 的业务代码。其余 target 无对应上游版本，保留工程骨架且 `ci.enabled=false`。各目标构建与运行状态以实际验证结果为准。
- `common` 独立构建使用主 target 的 NeoForge 1.21.1 / JDK 21 / OELib 类路径；它不证明 Forge 1.20.1 或其它版本兼容。
- `ClientPlatform` 是已确证差异的客户端 SPI；`ItemDataAccess` 只用于 1.21.1 数据组件实现；每个可运行 target 必须提供一个 `META-INF/services/...ClientPlatform` 实现，common 不反向引用 target 类。
- `SmartKeyPrompts` 保留共享脚本 API；loader 入口为 `NeoForgeSmartKeyPrompts`、`ForgeSmartKeyPrompts` 或两版 Fabric 共用的 `FabricSmartKeyPrompts`。客户端服务仅在客户端惰性解析。

## IDE 与构建

- 单版本开发独立打开目标目录，使用其 wrapper 与 `ci.properties` 指定的 Gradle JDK。当前四个目标的 Gradle JVM 均用 JDK 21；Forge 1.20.1 游戏/编译工具链为 JDK 17，Fabric 1.20.1 因官方 OELib 0.3.0 字节码要求而使用 JDK 21。
- 根目录和 `idea-workspace/` 都完整导入 `ci.enabled=true` 的四个真实 Java target。目标代码分别使用自己的 Minecraft/loader 类路径，支持原生 `build`、`test`、`runClient`；不再以空模块代理已迁入版本。
- `gradle/target-workspace.settings.gradle` 自动发现版本。禁用 CI 的骨架仍使用独立 wrapper 入口，不能当作已迁入模块；启用新版本前核对插件与工作区 Gradle/JDK 的兼容性。
- common 原始文件按各 target 排除规则直接接入；不要另行导入独立 common 验证工程，不复制源码、不引入 common jar。共享文件的 IDE 上下文以其归属模块为准；目标专属代码各自索引。
- 与 target 同名的共享 API 变体须使用独立 common 源码根，只由适用目标登记，不能仅靠逐文件 exclude 控制 IDEA 索引。旧 KubeJS 实现在 `common/src/kubejsLegacy/java`，只接入两版 1.20.1；NeoForge 的新版插件保留在 target。
- 顶部共享配置包含 `Build <版本>` 和 `Run Client <版本>`，由 `syncIdeaTargetBuilds` 更新两个入口的 `.run/`。不要写入维护者专属 `.idea` 配置。Fabric 子项目需显式启用 IDE 运行配置生成。
- 多模块构建使用工作区 wrapper/JDK，原 target 独立构建入口保留。根 `build` 默认构建所有启用 target，`-Ptarget=<版本>` 只导入该目标。
- 根和 CI 自动扫描 targets，禁止维护固定版本名单。`ci.enabled` 是验证配置，不是兼容性证据。新增目标须提供完整的 `ci.properties` 并核对自动发现结果。
- 标准验证：target 目录执行 `clean build`；共享层可执行根 `./gradlew -p common build`，但单独 common 成功不代表目标矩阵通过。产物需检查 metadata、共享类、资源及版本范围。

## 运行验证与开发测试

- 用户已指定使用 LDLib2 AutoTest 做本轮回归验收。`/skp guide` 默认展示可练习的 HUD，引导期间临时接管普通提示，指令直接列出按键翻译键并固定在左上角展示，玩家按住原有控制键再点击左键打开只含指定操作的紧凑编辑列表，关闭设置继续练习或显式结束引导。LDLib2 只能存在于 `-PldTest` 启用的独立开发测试源码集和开发模组中，普通编译/运行、生产 jar、源码 jar 和玩家 metadata 均不得引入。
- 普通 HUD 存在可改绑动作时，在提示末尾显示控制键加左键入口；指令引导同样将入口放在末尾，不重复显示。入口文字跟随玩家实际控制键绑定。
- LDLib2 场景操作真实客户端与集成服务器，使用结构化报告和截图确认结果；必须同时检查 Gradle 退出码、`report.json` 顶层状态和相关截图，不能以无异常或构建成功代替运行验证。
- Java/Mixin 改动和新增启动期注册需重新构建、重启测试实例；数据/资源重载在相应场景中触发并等待真实完成。
- KubeJS 仅作为原有可选集成保留编译 API，不自动加入开发运行。普通运行不加载测试工具，LDLib2 只由 `-PldTest` 启用。
- 游戏或依赖无法启动时记录具体阻塞，不伪称验证完成。首次 Minecraft EULA 必须由用户阅读并接受。

## 发布、文档与交付

- 发布前逐目标完成构建与对应 client/server 行为验证，检查最终 jar；发布平台 ID 通过 Gradle 属性配置，token 只用环境变量。
- `mod_license` 元数据声明不自动替换 LICENSE 正文，维护者需核对正文、依赖和素材授权。不凭许可证参数声称完成法律授权检查。
- 修改功能、支持范围、依赖、工具链或工作区配置后同步 README 与 AGENTS.md；文档归维护者管理，不自动重写整个文件。
- 不提交密钥、IDE 缓存、运行世界和构建输出。交付说明区分已验证、未验证和阻塞；不要将模板历史测试作为本项目的验证记录。
