## Smart Key Prompts

This mod provides a **flexible and dynamic key prompt system**, along with extended features for **customizable key modification**.
It improves the overall player experience and helps streamline key management.

---

### How to Use

For **regular players**, simply install and play—no setup required.

For **developers**, there are four ways to add new key prompts in-game:

* The target mod actively integrates with Smart Key Prompts.
* Use **KubeJS** to call the API provided by this mod.
* Add support via **datapacks** (with certain limitations; see the [Datapack Extension Guide](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide)).
* Added directly through **built-in adaptation** within this mod.

---

### Main Features

This mod selectively displays relevant key prompts at appropriate times.
It provides flexible APIs for mod or modpack developers to show context-based key hints.

```java
// Display an existing registered keybinding.
show(String group, String desc);

// Display a fully custom key prompt.
custom(String group, String key, String desc);

// Display an existing registered keybinding under an alias.
alias(String group, String desc, String alias);
```

Simply call these methods under certain conditions (e.g., during the client tick event) to display contextual key prompts.
For detailed implementation examples, see below.

Datapack-based prompts are also supported, though with limited condition logic and syntax—best suited for simpler use cases.
See [Datapack Extension Guide](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide) for more information.

Example output:

![img](https://resource-api.xyeidc.com/client/pics/aba3356e)

Each method’s `group` parameter represents a key prompt group ID.
For example, this mod uses `"jei_skp"` as the group ID when integrating with JEI, which is disabled by default.
You can freely enable or disable any group in the configuration file using its group ID.

When key prompts are visible on screen, pressing **Control Key (default: K) + Left Click** will open the corresponding keybinding menu directly—no need to search through the full list.

Example:

![img](https://resource-api.xyeidc.com//client/pics/f016a6c2)

Note that this shortcut only works for prompts linked to registered keybindings (`show`, `alias`),
and **not** for fully custom prompts (`custom`).

Additionally, you can programmatically trigger the configuration interface for specific keybindings:

```java
ConfigAction.modifyKey(List<String> keyDescList);
```

Holding the control key locks the current key prompts, making them persist temporarily.

Lastly, **Control Key + Right Click** toggles HUD visibility and position.
While holding Control, you can use the mouse wheel to adjust the HUD size.

---

### Flexible Prompting System

The `"key"` and `"desc"` fields refer to entries in the language files.

For example, in Minecraft’s default language file `\assets\minecraft\lang\en_us.json`:

```json
{
  "key.keyboard.f11": "F11",
  "key.keyboard.left.shift": "Left Shift",
  "key.mouse.left": "Left Button"
}
```

And in TaCZ’s language file `\assets\tacz\lang\zh_cn.json`:

```json
{
  "key.tacz.fire_select.desc": "Fire Mode",
  "key.tacz.inspect.desc": "Inspect",
  "key.tacz.interact.desc": "Interact while armed"
}
```

Example usage in a mod:

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

Usage in **KubeJS**:

```javascript
ClientEvents.tick(event => {
    let player = event.player;
    if (["key.left", "key.right", "key.forward", "key.back"].map(desc => SKP$KeyUtils.isKeyPressedOfDesc(desc)).includes(true)) {
        SKP$PromptUtils.show("parcool", "key.parcool.Dodge");
    }
    if (!player.onGround() && !player.isInWater()) {
        SKP$PromptUtils.show("parcool", "key.parcool.Breakfall");
        SKP$PromptUtils.show("parcool", "key.parcool.ClingToCliff");
    }
    if (player.isSprinting()) {
        SKP$PromptUtils.show("parcool", "key.parcool.FastRun");
    }
    if (SKP$KeyUtils.isKeyPressedOfDesc("key.parcool.FastRun")) {
        SKP$PromptUtils.show("parcool", SKP$KeyUtils.getKeyByDesc("key.parcool.Dodge"));
        SKP$PromptUtils.custom("parcool", SKP$KeyUtils.getKeyByDesc("key.sneak"), "parcool.action.CatLeap");
    }
});
```

For **combination keys**, you can use:

```java
SmartKeyPrompts.custom(modid, "key.keyboard.left.shift+key.mouse.left", "Transfer items in bulk");
```

Datapack-based configuration example (see the [guide](https://doc.sighs.cc/docs/SmartKeyPrompts/datapack-guide)):

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

Extended example: show interaction prompts below the crosshair when targeting an entity.

```java
@SubscribeEvent
public static void tick(TickEvent.ClientTickEvent event) {
    if (!ModList.get().isLoaded(modid)) return;
    if (PlayerUtils.getTargetedEntityType() == "minecraft:pig") {
        PromptUtils.addDesc("key.pig.feed").forKey("key.mouse.right").withCustom(true).atPosition("crosshair").toGroup(modid);
    }
}
```

Both `"custom"` and `"position"` can be customized.
The `"custom"` flag determines whether the prompt appears in the filtered keybinding menu,
and `"position"` controls where the prompt is displayed (fixed, unaffected by HUD repositioning).

Smart Key Prompts also provides various utility methods—such as key detection and entity targeting—to simplify mod development.

---

### Flexible Disabling

When holding the control key (default: K), prompts become **locked**, and their full identifiers (`group:desc`) are displayed.

In KubeJS, you can enable or disable prompts dynamically:

```javascript
SKP$PromptUtils.disablePromptByGroup(String group)
SKP$PromptUtils.enablePromptByGroup(String group)
SKP$PromptUtils.disablePromptByDesc(String desc)
SKP$PromptUtils.enablePromptByDesc(String desc)
SKP$PromptUtils.disablePrompt(String group, String desc)
SKP$PromptUtils.enablePrompt(String group, String desc)
```

You can also disable or re-enable specific keybindings:

```javascript
SKP$KeyUtils.isKeyDisabled(String desc)
SKP$KeyUtils.getDisabledKeyMappingList()
SKP$KeyUtils.disableKeyMapping(String desc)
SKP$KeyUtils.disableKeyMapping(List<String> list)
SKP$KeyUtils.disableAllKeyMapping()
SKP$KeyUtils.enableKeyMapping(String desc)
SKP$KeyUtils.enableKeyMapping(List<String> list)
SKP$KeyUtils.enableAllKeyMapping()
```

For example, calling `disableKeyMapping("key.jump")` prevents the player from jumping.

---

### Other Information

If you’d like this mod to support another mod, feel free to reach out.
You can also submit new key prompt groups directly to the repository.

**Recommended companion mod:** [KeyBindJS](https://www.curseforge.com/minecraft/mc-mods/keybindjs)

**Planned features:**

* More prompt positions (e.g., follow mouse).
* Support for double-tap and long-press keys.
* HUD visual improvements.
* Potential backports to more Minecraft versions.

See the full list of supported mods in the [official documentation](https://doc.sighs.cc/docs/SmartKeyPrompts/support-mod).