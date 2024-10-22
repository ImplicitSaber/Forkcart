package io.github.implicitsaber.forkcart.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.implicitsaber.forkcart.ForkcartClient;
import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow protected abstract void setPos(Vec3d pos);

    @Shadow @Final private Quaternionf rotation;

    @Inject(method = "update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V"))
    private void forkcart$updateCamPosWhileRiding(BlockView area, Entity self, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info) {
        var vehicle = self.getVehicle();
        if (vehicle != null) {
            var tf = vehicle.getVehicle();
            if (tf instanceof TrackFollowerEntity trackFollower) {
                var world = self.getWorld();
                var diff = self.getPos().add(0, self.getStandingEyeHeight(), 0).subtract(trackFollower.getPos());
                var camPos = new Vector3d(diff.getX(), diff.getY(), diff.getZ());
                if (world.isClient()) {
                    var rot = new Quaternionf();
                    trackFollower.getClientOrientation(rot, tickDelta);
                    rot.transform(camPos);

                    this.setPos(new Vec3d(camPos.x(), camPos.y(), camPos.z()).add(trackFollower.getLerpedPos(tickDelta)));

                    if (ForkcartClient.CFG_ROTATE_CAMERA.get()) {
                        rot.mul(RotationAxis.POSITIVE_Y.rotationDegrees(90 + vehicle.getYaw(tickDelta)).mul(rotation, rotation), rotation);
                    }
                }
            }
        }
    }

    @Redirect(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"))
    private Quaternionf forkcart$useCorrectEquation(Quaternionf instance, float angleY, float angleX, float angleZ, @Local(ordinal = 0, argsOnly = true) float yaw, @Local(ordinal = 1, argsOnly = true) float pitch) {
        return instance.rotationYXZ((float) Math.PI - yaw * (float) (Math.PI / 180.0), -pitch * (float) (Math.PI / 180.0), 0.0F);
    }

}
