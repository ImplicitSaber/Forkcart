package io.github.implicitsaber.forkcart.component;

import com.mojang.serialization.Codec;
import io.github.implicitsaber.forkcart.util.SUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public record OriginComponent(BlockPos pos) {
    public static final Text FIRST_SELECTION = Text.translatable("item.forkcart.track.origin").formatted(Formatting.YELLOW);
    public static final Text HOW_TO_CLEAR = Text.translatable("item.forkcart.track.clear_hint").formatted(Formatting.GOLD, Formatting.ITALIC);

    public static final Codec<OriginComponent> CODEC = BlockPos.CODEC.xmap(OriginComponent::new, OriginComponent::pos);

    public void appendTooltip(List<Text> tooltip) {
        tooltip.add(FIRST_SELECTION);
        tooltip.add(HOW_TO_CLEAR);
    }

    public void set(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        SUtil.putBlockPos(nbt, pos, "origin_pos");
    }

    public static OriginComponent get(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return null;
        if(!nbt.contains("origin_pos", NbtElement.INT_ARRAY_TYPE)) return null;
        BlockPos bp = SUtil.getBlockPos(nbt, "origin_pos");
        if(bp == null) return null;
        return new OriginComponent(bp);
    }

    public static void remove(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if(nbt == null) return;
        nbt.remove("origin_pos");
    }

}
