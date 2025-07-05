package com.mafuyu404.smartkeyprompts.init;

public class KeyPrompt {
    public String key;
    public String desc;
    public String position = "default";
    public String group;
    public boolean isCustom = false;

    public KeyPrompt(String id, String key, String desc, boolean isCustom) {
        this.key = key;
        this.desc = desc;
        this.isCustom = isCustom;
        this.group = id;
    }

    public KeyPrompt forKey(String key) {
        this.key = key;
        return this;
    }

    public KeyPrompt atPosition(String position) {
        this.position = position;
        return this;
    }

    public KeyPrompt withCustom(boolean isCustom) {
        this.isCustom = isCustom;
        return this;
    }

    public void toGroup(String id) {
        this.group = id;
        HUD.addCache(this);
    }

    public String getString() {
        return this.group + ":" + this.desc + "/" + this.key;
    }
}
