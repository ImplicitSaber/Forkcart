package io.github.implicitsaber.forkcart.mixin.client;

import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import net.minecraft.client.sound.MovingMinecartSoundInstance;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MovingMinecartSoundInstance.class)
public abstract class MovingMinecartSoundInstanceMixin extends MovingSoundInstance {
    @Shadow @Final private AbstractMinecartEntity minecart;

    protected MovingMinecartSoundInstanceMixin(SoundEvent soundEvent, SoundCategory soundCategory, Random random) {
        super(soundEvent, soundCategory, random);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void forkcart$adjustSoundWhenOnTrack(CallbackInfo info) {
        if (!this.isDone() && minecart.getVehicle() instanceof TrackFollowerEntity trackFollower) {
            float amp = (float) trackFollower.getClientMotion().length();
            this.volume = MathHelper.lerp(MathHelper.clamp(amp, 0, 0.5f), 0, 0.7f);
        }
    }
}
