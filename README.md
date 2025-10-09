# 灵活按键提示

本模组提供了灵活的可用按键提示功能，以及衍生的便捷按键修改，可在一定程度上改善玩家的游玩体验，并优化按键管理。

### 如何使用

对于普通玩家，只需正常下载使用即可。

对于开发者，若希望在游戏中新增一组按键提示，有四种方法：
- 由目标模组主动与本模组联动，从而添加按键支持。
- 使用KubeJS调用本模组提供的方法，从而添加按键支持。
- 使用数据包添加按键支持，但存在一定的局限性，详见[数据包扩展指南](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide)。
- 由本模组主动适配后添加。

### 主要功能

本模组用于在合适的时机选择性地展示相关按键，为此，提供了灵活展示按键提示的接口供模组或整合包开发者使用。

```java
// 展示已注册按键绑定中的某个按键。
show(String group, String desc);
// 展示自定义按键提示。
custom(String group, String key, String desc);
// 将已注册按键绑定中的某个按键，使用另外的别名展示。
alias(String group, String desc, String alias);
```

只需在客户端Tick事件中按条件约束执行这三个方法，就能添加简单的按键提示。详细的开发实例请看后文。

此外也支持使用数据包添加按键提示支持，但条件判断上存在一定的限制，部分复杂语法也无法使用，仅适用于简单适配的场景。

如需使用，请查阅[数据包扩展指南](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide)。

效果如下：

![img](https://resource-api.xyeidc.com/client/pics/aba3356e)

三个方法中的group均代表按键组标识号，例如，本模组在为JEI提供按键提示支持时，使用了"jei_skp"的标识号，并默认禁用此按键组。

你可以随时在配置文件中使用标识号来禁用对应的按键组，或者使用。

此外，当屏幕上存在按键提示时，按下控制热键（默认为K）+鼠标左键，即可打开相应的按键绑定设置界面，而不需要在所有按键绑定中寻找。

效果如下：

![img](https://resource-api.xyeidc.com//client/pics/f016a6c2)

注意，只有在对已注册的按键绑定添加提示时（show, alias）适用此功能，对于自定义按键提示（custom）不会有任何效果。

此外，这个功能也可以主动触发，用于打开自定义按键绑定设置界面，并只显示给定的按键绑定：

```java
ConfigAction.modifyKey(List<String> keyDescList);
```

按住控制热键时，按键提示会被锁定，无需顾虑抓时机的问题。

最后，按下控制热键+鼠标右键，即可切换按键提示HUD的显示与否和显示位置。按住控制热键时还可以通过鼠标滚轮调整HUD大小。

### 开发实例

创建按键提示所使用的"key"和"desc"字段均为语言文件中的对应字段。
例如在Minecraft的语言文件\assets\minecraft\lang\en_us.json中查找"key"字段：
```json
{
  "key.keyboard.f11": "F11",
  "key.keyboard.left.shift": "Left Shift",
  "key.mouse.left": "Left Button"
}
```
在TaCZ的语言文件\assets\tacz\lang\zh_cn.json中查找"desc"字段：
```json
{
  "key.tacz.fire_select.desc": "开火模式",
  "key.tacz.inspect.desc": "检视",
  "key.tacz.interact.desc": "持械时互动"
}
```

对于组合键，可以像这样：
```java
SmartKeyPrompts.custom(modid, "key.keyboard.left.shift+key.mouse.left", "批量转移物品");
```

在模组中使用：

```java
@SubscribeEvent
public static void tick(TickEvent.ClientTickEvent event) {
    if (!ModList.get().isLoaded("immersive_aircraft")) return;
    Player player = Minecraft.getInstance().player;
    if (player == null || Minecraft.getInstance().screen != null) return;
    String vehicle = Utils.getVehicleType(player);
    if (vehicle != null && vehicle.startsWith("immersive_aircraft:")) {
        SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.inventory"), "immersive_aircraft.slot.upgrade");
        SmartKeyPrompts.show(modid, "key.immersive_aircraft.dismount");
        String keyUse = Utils.getKeyByDesc("key.immersive_aircraft.fallback_use");
        SmartKeyPrompts.custom(modid, Objects.equals(keyUse, "key.keyboard.unknown") ? "key.mouse.right" : keyUse, "item.immersive_aircraft.item.weapon");
        if (vehicle.equals("immersive_aircraft:biplane")) {
            SmartKeyPrompts.custom(modid, Utils.getKeyByDesc("key.jump"), "item.immersive_aircraft.engine");
        }
    }
}

```

在KubeJS中使用：

```javascript
let SmartKeyPrompts = Java.loadClass("com.mafuyu404.smartkeyprompts.SmartKeyPrompts");
let Utils = Java.loadClass("com.mafuyu404.smartkeyprompts.init.Utils");

ClientEvents.tick(event => {
    let player = event.player;
    if (["key.left", "key.right", "key.forward", "key.back"].map(desc => Utils.isKeyPressedOfDesc(desc)).includes(true)) {
        SmartKeyPrompts.show("parcool", "key.parcool.Dodge");
    }
    if (!player.onGround() && !player.isInWater()) {
        SmartKeyPrompts.show("parcool", "key.parcool.Breakfall");
        SmartKeyPrompts.show("parcool", "key.parcool.ClingToCliff");
    }
    if (player.isSprinting()) {
        SmartKeyPrompts.show("parcool", "key.parcool.FastRun");
    }
    if (Utils.isKeyPressedOfDesc("key.parcool.FastRun")) {
        SmartKeyPrompts.show("parcool", Utils.getKeyByDesc("key.parcool.Dodge"));
        SmartKeyPrompts.custom("parcool", Utils.getKeyByDesc("key.sneak"), "parcool.action.CatLeap");
    }
});
```

通过查阅[数据包扩展指南](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide)，也可以使用数据包简单添加按键支持：

```json
{
  "modid": "tacz_skp",
  "vars": {
    "modLoaded": "isModLoaded('tacz')",
    "hasTaczGun": "mainHandItem == 'tacz:modern_kinetic_gun'",
    "isNotInVehicle": "!isInVehicle()"
  },
  "entries": [
    {
      "when": {
        "modLoaded": "true",
        "hasTaczGun": "true",
        "isNotInVehicle": "true"
      },
      "then": [
        "show('tacz_skp', 'key.tacz.shoot.desc')",
        "show('tacz_skp', 'key.tacz.zoom.desc')",
        "show('tacz_skp', 'key.tacz.reload.desc')"
      ]
    }
  ]
}
```

拓展用法示例：当准星指向目标实体时，将实体互动信息的按键提示展示在准星下方：

```java
@SubscribeEvent
public static void tick(TickEvent.ClientTickEvent event) {
    if (!ModList.get().isLoaded(modid)) return;
    if (Utils.getTargetedEntityType() == "minecraft:pig") {
        SmartKeyPrompts.addDesc("key.pig.feed").forKey("key.mouse.right").withCustom(true).atPosition("crosshair").toGroup(modid);
    }
}
```

拓展方法中，"custom"和"position"字段都可以自定义，前者决定这个按键提示能否出现在筛选后的按键绑定设置界面中，后者则决定显示的位置，这是一个固定值，不再调整HUD位置的功能影响。

在案例中可看到，本模组还提供了诸多关于按键操作的便捷方法，用于简化开发流程。除此之外，还有诸如获取视线所指实体等更为通用的条件判断工具。

### 其它

如果有希望适配的模组，欢迎告知。

如果有新的按键组希望添加到本模组中，请告知或直接推送到仓库。

对于整合包开发者，推荐搭配KeyBindJS使用。

未来计划：
- 更多可供选择的按键提示位置，如跟随鼠标。
- 双击和长按按键的显示支持。
- HUD美化。
- 可能往更多版本移植。

已适配模组列表详见[官方文档](https://doc.sighs.cc/docs/SmartKeyPrompts/support-mod)。