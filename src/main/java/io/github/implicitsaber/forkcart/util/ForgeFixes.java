package io.github.implicitsaber.forkcart.util;

import net.fabricmc.loader.api.FabricLoader;

public class ForgeFixes {

    private static boolean connector = false;
    private static boolean checked = false;

    public static boolean isConnector() {
        if(checked) return connector;
        connector = FabricLoader.getInstance().isModLoaded("connector");
        checked = true;
        return connector;
    }

}
