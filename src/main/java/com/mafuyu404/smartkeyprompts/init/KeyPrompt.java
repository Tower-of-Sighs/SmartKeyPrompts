package com.mafuyu404.smartkeyprompts.init;

import java.util.Objects;

public class KeyPrompt {
    public String key;
    public String desc;
    public String position = "default";
    public String group;
    public boolean isCustom = false;
    public String keyAlias = null;

    private volatile String cachedKeyString = null;

    public KeyPrompt(String id, String key, String desc, boolean isCustom) {
        this.key = key;
        this.desc = desc;
        this.isCustom = isCustom;
        this.group = id;
    }

    public KeyPrompt(String id, String key, String desc, boolean isCustom, String keyAlias) {
        this.key = key;
        this.desc = desc;
        this.isCustom = isCustom;
        this.group = id;
        this.keyAlias = keyAlias;
    }

    public KeyPrompt forKey(String key) {
        this.key = key;
        this.cachedKeyString = null;
        return this;
    }

    public KeyPrompt withKeyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
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
        this.cachedKeyString = null;
        HUD.addCache(this);
    }

    public String getString() {
        if (cachedKeyString == null) {
            cachedKeyString = this.group + ":" + this.desc + "/" + this.key;
        }
        return cachedKeyString;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeyPrompt keyPrompt = (KeyPrompt) obj;
        return Objects.equals(getString(), keyPrompt.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getString());
    }
}