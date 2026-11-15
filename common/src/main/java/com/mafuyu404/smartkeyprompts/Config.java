package com.mafuyu404.smartkeyprompts;

import com.mafuyu404.smartkeyprompts.platform.ClientPlatform;

import java.util.List;

/**
 * Stores the HUD scale, anchor and excluded prompt groups.
 *
 * @param scale the scale in the range {@code 0.0} to {@code 10.0}
 * @param position the anchor index in the range {@code 1} to {@code 8}
 * @param blacklist the list of excluded prompt group identifiers
 */
public record Config(double scale, int position, List<String> blacklist) {
    /**
     * Returns the current client configuration supplied by the target.
     *
     * @return the current configuration snapshot
     */
    public static Config get() {
        return ClientPlatform.get().config();
    }
}
