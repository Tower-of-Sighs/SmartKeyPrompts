package com.mafuyu404.smartkeyprompts.platform;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.init.ModKeybindings;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

/** Implements the client SPI for Minecraft 1.20.1 on Forge. */
public final class ForgeClientPlatform implements ClientPlatform {

    @Override
    public Config config() {
        return OELibConfig.get();
    }

    @Override
    public void setScale(double scale) {
        OELibConfig.UNIT.set(Config::scale, scale);
    }

    @Override
    public void setPosition(int position) {
        OELibConfig.UNIT.set(Config::position, position);
    }

    @Override
    public InputConstants.Key key(KeyMapping mapping) {
        return mapping.getKey();
    }

    @Override
    public void setKey(KeyMapping mapping, InputConstants.Key key, InputConstants.Key modifier) {
        var keyModifier = net.minecraftforge.client.settings.KeyModifier.getModifier(modifier);
        mapping.setKeyModifierAndCode(keyModifier == null || key.equals(InputConstants.UNKNOWN)
                ? net.minecraftforge.client.settings.KeyModifier.NONE : keyModifier, key);
    }

    @Override
    public void resetKey(KeyMapping mapping) {
        mapping.setToDefault();
    }

    @Override
    public boolean isModifierKey(InputConstants.Key key) {
        return net.minecraftforge.client.settings.KeyModifier.isKeyCodeModifier(key);
    }

    @Override
    public KeyMapping controlKey() {
        return ModKeybindings.CONTROL_KEY;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public CompoundTag saveBlockEntity(BlockEntity blockEntity, Level level) {
        return blockEntity.saveWithoutMetadata();
    }

    @Override
    public CompoundTag itemTag(net.minecraft.world.item.ItemStack stack) {
        return stack.getTag();
    }

    @Override
    public void openKeyBindings() {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        minecraft.setScreen(new net.minecraft.client.gui.screens.controls.KeyBindsScreen(null, minecraft.options));
    }

    @Override
    public boolean isKeyBindingsOpen() {
        return net.minecraft.client.Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.controls.KeyBindsScreen;
    }

    @Override
    public void drawKeyTexture(PoseStack poseStack, int x, int y, int width, int height,
                               int texX, int texY, int texWidth, int texHeight, boolean pressed) {
        LegacyKeyTexture.drawUnpressedKey(poseStack, x, y, width, height,
                texX, texY, texWidth, texHeight, pressed);
    }
}
