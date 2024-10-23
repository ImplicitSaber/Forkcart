package io.github.implicitsaber.forkcart.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.implicitsaber.forkcart.util.ForgeFixes;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V"))
    public void forkcart$quaternionRotate(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci, @Local Camera cam) {
        if(!ForgeFixes.isConnector()) matrices.peek().getPositionMatrix().set(new Matrix4f().rotate(cam.getRotation().conjugate(new Quaternionf())));
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", ordinal = 2, target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    public void forkcart$noPitchYaw1(MatrixStack instance, Quaternionf quaternion) {
        if(ForgeFixes.isConnector()) instance.multiply(quaternion);
    }

    @Redirect(method = "renderWorld", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lorg/joml/Quaternionf;)V"))
    public void forkcart$noPitchYaw2(MatrixStack instance, Quaternionf quaternion) {
        if(ForgeFixes.isConnector()) instance.multiply(quaternion);
    }

}
