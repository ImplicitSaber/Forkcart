package io.github.implicitsaber.forkcart.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.implicitsaber.forkcart.ForkcartClient;
import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {WorldRenderer.class}, priority = 1500)
public class WorldRendererMixin {
//    @ModifyExpressionValue(method = "setupTerrain(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/Frustum;ZZ)V",
//            require = 0, at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/client/render/ChunkRenderingDataPreparer;method_52836()Z"))
//    private boolean forkcart$updateChunkOcclusionCullingWhileOnTrack(boolean old) {
//        if (ForkcartClient.CFG_ROTATE_CAMERA.get()) {
//            var entity = MinecraftClient.getInstance().cameraEntity;
//            while (entity != null) {
//                entity = entity.getVehicle();
//
//                if (entity instanceof TrackFollowerEntity) {
//                    return true;
//                }
//            }
//        }
//
//        return old;
//    }
}
