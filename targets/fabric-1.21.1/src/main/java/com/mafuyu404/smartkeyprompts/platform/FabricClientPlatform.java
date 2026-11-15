package com.mafuyu404.smartkeyprompts.platform;

import com.mafuyu404.smartkeyprompts.Config;
import com.mafuyu404.smartkeyprompts.init.FabricKeybindings;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.fabricmc.loader.api.FabricLoader;

/** Implements the client SPI for Minecraft 1.21.1 on Fabric. */
public final class FabricClientPlatform implements ClientPlatform {

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
        return net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getBoundKeyOf(mapping);
    }

    @Override
    public void setKey(KeyMapping mapping, InputConstants.Key key, InputConstants.Key modifier) {
        mapping.setKey(key);
    }

    @Override
    public void resetKey(KeyMapping mapping) {
        mapping.setKey(mapping.getDefaultKey());
    }

    @Override
    public boolean isModifierKey(InputConstants.Key key) {
        return false;
    }

    @Override
    public KeyMapping controlKey() {
        return FabricKeybindings.CONTROL_KEY;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public CompoundTag saveBlockEntity(BlockEntity blockEntity, Level level) {
        return blockEntity.saveWithoutMetadata(level.registryAccess());
    }

    @Override
    public CompoundTag itemTag(net.minecraft.world.item.ItemStack stack) {
        var data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return data == null ? null : data.copyTag();
    }

    @Override
    public void openKeyBindings() {
        var minecraft = net.minecraft.client.Minecraft.getInstance();
        minecraft.setScreen(new net.minecraft.client.gui.screens.options.controls.KeyBindsScreen(null, minecraft.options));
    }

    @Override
    public boolean isKeyBindingsOpen() {
        return net.minecraft.client.Minecraft.getInstance().screen instanceof net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
    }

    @Override
    public void drawKeyTexture(PoseStack poseStack, int x, int y, int width, int height,
                               int texX, int texY, int texWidth, int texHeight, boolean pressed) {
        ComponentKeyTexture.drawUnpressedKey(poseStack, x, y, width, height,
                texX, texY, texWidth, texHeight, pressed);
    }
}
