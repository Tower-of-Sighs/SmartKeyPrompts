package com.mafuyu404.smartkeyprompts.platform;

import com.mafuyu404.smartkeyprompts.Config;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.KeyMapping;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ServiceLoader;

/**
 * Supplies client operations with confirmed loader or Minecraft API differences.
 *
 * <p>Each target supplies one service provider. The provider is resolved lazily
 * on the client; common initialization on a dedicated server must not call it.
 */
public interface ClientPlatform {
    /**
     * Returns the client service declared by the active target.
     *
     * @return the target's client service
     * @throws IllegalStateException if no target service is installed
     */
    static ClientPlatform get() {
        return Holder.INSTANCE;
    }

    /**
     * Returns the current persisted configuration snapshot.
     *
     * @return the current configuration
     */
    Config config();

    /**
     * Updates the HUD scale in the target's configuration backend.
     *
     * @param scale the scale in the range {@code 0.0} to {@code 10.0}
     */
    void setScale(double scale);

    /**
     * Updates the HUD anchor in the target's configuration backend.
     *
     * @param position the anchor index in the range {@code 1} to {@code 8}
     */
    void setPosition(int position);

    /**
     * Returns the input currently assigned to a key binding.
     *
     * @param mapping the key binding to inspect
     * @return the assigned input, including the unknown input when unbound
     */
    InputConstants.Key key(KeyMapping mapping);

    /**
     * Assigns an input and replaces any previous modifier of a binding.
     *
     * <p>This operation does not save options or rebuild the shared key lookup.
     * Targets without modifier support ignore <code>modifier</code>.
     *
     * @param mapping the key binding to change
     * @param key the new input, including the unknown input for an unbound action
     * @param modifier the modifier input, or the unknown input for no modifier
     */
    void setKey(KeyMapping mapping, InputConstants.Key key, InputConstants.Key modifier);

    /**
     * Restores a binding's original input and original modifier without saving options.
     *
     * @param mapping the key binding to restore
     */
    void resetKey(KeyMapping mapping);

    /**
     * Checks whether an input can modify another key on this target.
     *
     * @param key the input to inspect
     * @return whether this target recognizes the input as a key modifier
     */
    boolean isModifierKey(InputConstants.Key key);

    /**
     * Returns the registered binding that enables HUD editing.
     *
     * @return the HUD editing key binding
     */
    KeyMapping controlKey();

    /**
     * Checks whether an optional integration's mod is installed.
     *
     * @param modId the mod identifier
     * @return {@code true} when the loader reports that mod as loaded
     */
    boolean isModLoaded(String modId);

    /**
     * Serializes a block entity without its identifying metadata.
     *
     * @param blockEntity the block entity to serialize
     * @param level the level providing registry context
     * @return the serialized tag
     */
    CompoundTag saveBlockEntity(BlockEntity blockEntity, Level level);

    /**
     * Reads the item's custom tag in the target's data format.
     *
     * @param stack the item stack
     * @return the tag, or {@code null} when absent
     */
    CompoundTag itemTag(ItemStack stack);

    /** Opens the target's key binding screen for the current options. */
    void openKeyBindings();

    /**
     * Checks whether the key binding screen is open.
     *
     * @return whether the current screen edits key bindings
     */
    boolean isKeyBindingsOpen();

    /**
     * Draws a segment of the pressed or unpressed key texture.
     *
     * @param poseStack the current HUD transform
     * @param x the destination x coordinate
     * @param y the destination y coordinate
     * @param width the destination width
     * @param height the destination height
     * @param texX the source x coordinate in the 32 by 16 texture
     * @param texY the source y coordinate in the texture
     * @param texWidth the source width
     * @param texHeight the source height
     * @param pressed whether to use the pressed texture
     */
    void drawKeyTexture(PoseStack poseStack, int x, int y, int width, int height,
                        int texX, int texY, int texWidth, int texHeight, boolean pressed);

    final class Holder {
        private static final ClientPlatform INSTANCE = ServiceLoader
                .load(ClientPlatform.class, ClientPlatform.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing SmartKeyPrompts client platform"));

        private Holder() {
        }
    }
}
