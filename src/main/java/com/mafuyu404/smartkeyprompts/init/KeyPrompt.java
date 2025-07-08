package com.mafuyu404.smartkeyprompts.init;

import lombok.Data;

@Data
public class KeyPrompt {
    private String key;
    private String desc;
    private String position;
    private String group;
    private boolean isCustom;

    public KeyPrompt(String id, String key, String desc, boolean isCustom) {
        this.key = key;
        this.desc = desc;
        this.position = "default";
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
