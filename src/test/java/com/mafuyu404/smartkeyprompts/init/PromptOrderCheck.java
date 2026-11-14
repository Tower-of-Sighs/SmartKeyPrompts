package com.mafuyu404.smartkeyprompts.init;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Standalone JVM checks: exercise the actual snapshot builder without launching Minecraft. */
public final class PromptOrderCheck {
    private static int checks;

    private static KeyPrompt prompt(String name) {
        return new KeyPrompt("test", "key.keyboard.x", name, true);
    }

    private static void check(boolean passed, String message) {
        if (!passed) throw new AssertionError(message);
        checks++;
    }

    @SuppressWarnings("unchecked")
    private static List<KeyPrompt> snapshot(String fieldName) throws Exception {
        Field field = HUD.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (List<KeyPrompt>) field.get(null);
    }

    private static void update() throws Exception {
        Method method = HUD.class.getDeclaredMethod("updateCachedPrompts");
        method.setAccessible(true);
        method.invoke(null);
    }

    public static void main(String[] args) throws Exception {
        KeyPrompt first = prompt("first"), second = prompt("second");
        KeyPrompt category = prompt("category").withOrder(Integer.MAX_VALUE - 1);
        KeyPrompt hide = prompt("hide").withOrder(Integer.MAX_VALUE);
        HUD.KeyPromptList.addAll(List.of(hide, first, category, second));
        update();
        check(snapshot("cachedDefaultPrompts").equals(List.of(first, second, category, hide)), "footer and stable gameplay order");
        check(HUD.KeyPromptList.equals(List.of(hide, first, category, second)), "submission list unchanged");

        first.withOrder(10);
        update();
        check(snapshot("cachedDefaultPrompts").equals(List.of(second, first, category, hide)), "reused prompt order invalidates snapshot");
        first.withOrder(-1);
        update();
        check(snapshot("cachedDefaultPrompts").get(0) == first, "negative order supported without integer subtraction overflow");

        int identityHash = category.hashCode();
        String identity = category.getString();
        Set<KeyPrompt> identities = new HashSet<>();
        identities.add(category);
        category.withOrder(0);
        check(category.hashCode() == identityHash && category.getString().equals(identity), "order does not alter prompt identity");
        check(identities.contains(category) && !identities.add(prompt("category").withOrder(500)), "set de-duplication unchanged");

        KeyPrompt crosshair = prompt("crosshair").atPosition("crosshair").withOrder(-10);
        HUD.KeyPromptList.add(crosshair);
        HUD.KeyPromptList.add(null);
        update();
        check(snapshot("cachedCrosshairPrompts").equals(List.of(crosshair)), "crosshair partition retained and null submissions ignored");
        check(snapshot("cachedDefaultPrompts").equals(List.of(first, category, second, hide)), "equal order preserves submission order");

        hide.withOrder(0);
        first.withOrder(0);
        HUD.KeyPromptList.clear();
        HUD.KeyPromptList.addAll(List.of(hide, first, category, second));
        update();
        check(snapshot("cachedDefaultPrompts").equals(HUD.KeyPromptList), "all default orders retain original behavior");
        List<KeyPrompt> unchanged = snapshot("cachedDefaultPrompts");
        update();
        check(snapshot("cachedDefaultPrompts") == unchanged, "unchanged order reuses immutable render snapshot");
        System.out.println(checks + " native prompt-order checks passed");
    }
}
