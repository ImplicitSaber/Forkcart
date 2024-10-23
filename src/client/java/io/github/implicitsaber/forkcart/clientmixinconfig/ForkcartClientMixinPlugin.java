package io.github.implicitsaber.forkcart.clientmixinconfig;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class ForkcartClientMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return switch(mixinClassName) {
            case "io.github.implicitsaber.forkcart.mixin.client.GameRendererMixin",
                 "io.github.implicitsaber.forkcart.mixin.client.CameraMixin" -> !FabricLoader.getInstance().isModLoaded("connectormod");
            case "io.github.implicitsaber.forkcart.mixin.client.CameraMixinForge" -> FabricLoader.getInstance().isModLoaded("connectormod");
            default -> true;
        };
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

}
