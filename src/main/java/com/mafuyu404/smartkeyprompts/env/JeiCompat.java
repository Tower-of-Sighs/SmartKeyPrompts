package com.mafuyu404.smartkeyprompts.env;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

@OnlyIn(Dist.CLIENT)
public class JeiCompat {
  private static final String MOD_ID = "jei";
  private static boolean INSTALLED = false;

  public static void init() {
    INSTALLED = ModList.get().isLoaded(MOD_ID);
  }

  public static boolean isEnabled() {
    if (INSTALLED) {
      return JeiPlugin.isEnabled();
    }
    return false;
  }
}
