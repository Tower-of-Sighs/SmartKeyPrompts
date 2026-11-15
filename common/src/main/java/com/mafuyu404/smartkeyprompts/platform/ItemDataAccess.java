package com.mafuyu404.smartkeyprompts.platform;

import net.minecraft.world.item.ItemStack;

/**
 * Exposes item data queries without binding shared expressions to a component API.
 *
 * <p>Missing or invalid inputs yield {@code false} for predicates and {@code null}
 * for value queries. Targets without data components must explicitly define their
 * supported expression surface; legacy item NBT is not equivalent to components.
 */
public interface ItemDataAccess {
    /**
     * Checks for a component on an item stack.
     *
     * @param stack the item stack, or {@code null}
     * @param componentId the namespaced component identifier
     * @return whether the component exists
     */
    boolean hasComponent(ItemStack stack, String componentId);

    /**
     * Compares the string representation of a component with an expected value.
     *
     * @param stack the item stack, or {@code null}
     * @param componentId the namespaced component identifier
     * @param expectedValue the expected string representation
     * @return whether the value matches
     */
    boolean checkComponentValue(ItemStack stack, String componentId, String expectedValue);

    /**
     * Returns a component's string representation.
     *
     * @param stack the item stack, or {@code null}
     * @param componentId the namespaced component identifier
     * @return the component value, or {@code null} when unavailable
     */
    String getComponentValue(ItemStack stack, String componentId);

    /**
     * Checks for a key in an item's custom data.
     *
     * @param stack the item stack, or {@code null}
     * @param key the custom data key
     * @return whether the key exists
     */
    boolean hasCustomDataKey(ItemStack stack, String key);

    /**
     * Returns the string representation of all components.
     *
     * @param stack the item stack, or {@code null}
     * @return the representation, or {@code null} for an empty or invalid stack
     */
    String getComponentsAsString(ItemStack stack);

    /**
     * Returns the string representation of custom item data.
     *
     * @param stack the item stack, or {@code null}
     * @return the representation, or {@code null} when custom data is absent
     */
    String getCustomDataAsString(ItemStack stack);

    /** Clears cached component type lookups. */
    void clearCache();
}
