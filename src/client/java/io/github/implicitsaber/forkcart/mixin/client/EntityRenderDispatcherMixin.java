package io.github.implicitsaber.forkcart.mixin.client;

import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Unique private boolean onTrackFollower = false;
    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", shift = At.Shift.BEFORE, ordinal = 0, target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void forkcart$rotateEntitiesOnTrackFollower(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (entity instanceof TrackFollowerEntity) return;

        Entity vehicle = entity;
        while (vehicle != null) {
            vehicle = vehicle.getVehicle();

            if (vehicle instanceof TrackFollowerEntity trackFollower) {
                var rotation = new Quaternionf();
                trackFollower.getClientOrientation(rotation, tickDelta);

                matrices.push();
                onTrackFollower = true;

                var dv3d = entity.getLerpedPos(tickDelta).subtract(trackFollower.getLerpedPos(tickDelta));
                var diff = new Vector3d(dv3d.getX(), dv3d.getY(), dv3d.getZ());
                matrices.translate(-diff.x(), -diff.y(), -diff.z());

                matrices.multiply(rotation);

                matrices.translate(diff.x(), diff.y(), diff.z());

                matrices.multiply(RotationAxis.POSITIVE_Y.rotation((-MathHelper.HALF_PI) - yaw * MathHelper.RADIANS_PER_DEGREE));

                return;
            }
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/Entity;DDDFFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/client/render/entity/EntityRenderer;render(Lnet/minecraft/entity/Entity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    private void forkcart$undoTransform(Entity entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (onTrackFollower) {
            onTrackFollower = false;
            matrices.pop();
        }
    }
}
