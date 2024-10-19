package io.github.implicitsaber.forkcart.sound;

import io.github.implicitsaber.forkcart.Forkcart;
import io.github.implicitsaber.forkcart.entity.TrackFollowerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;

public class LiftingTrackFollowerSoundInstance extends MovingSoundInstance {

    private final TrackFollowerEntity entity;

    public LiftingTrackFollowerSoundInstance(TrackFollowerEntity entity) {
        super(Forkcart.CHAIN_LIFT_SOUND, SoundCategory.NEUTRAL, SoundInstance.createRandom());
        this.entity = entity;
        this.repeat = true;
        this.repeatDelay = 0;
        this.volume = 0;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
    }

    @Override
    public boolean shouldAlwaysPlay() {
        return true;
    }

    @Override
    public void tick() {
        if(this.entity.isRemoved()) {
            this.setDone();
            return;
        }
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        if(!this.entity.isChainLifting()) this.volume = 0;
        else this.volume = 1;
    }

}
