package io.github.implicitsaber.forkcart.item;

import io.github.implicitsaber.forkcart.block.TrackTiesBlockEntity;
import io.github.implicitsaber.forkcart.component.OriginComponent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class TrackItem extends Item {

    private final Type type;

    public TrackItem(Settings settings, Type type) {
        super(settings);
        this.type = type;
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() != null && !context.getPlayer().canModifyBlocks()) {
            return super.useOnBlock(context);
        }

        var world = context.getWorld();
        var pos = context.getBlockPos();
        var stack = context.getStack();

        if (world.getBlockEntity(pos) instanceof TrackTiesBlockEntity) {
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }

            var origin = OriginComponent.get(stack);
            if (origin != null) {
                var oPos = origin.pos();
                if (!pos.equals(oPos) && world.getBlockEntity(oPos) instanceof TrackTiesBlockEntity oTies) {
                    oTies.setNext(pos, type);

                    world.playSound(null, pos, SoundEvents.ENTITY_IRON_GOLEM_REPAIR, SoundCategory.BLOCKS, 1.5f, 0.7f);
                }

                OriginComponent.remove(stack);
            } else {
                new OriginComponent(pos).set(stack);
            }
        } else {
            var origin = OriginComponent.get(stack);
            if (origin != null) {
                if (world.isClient()) {
                    return ActionResult.CONSUME;
                }

                OriginComponent.remove(stack);
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        tooltip.add(Text.translatable("item.forkcart.track.desc").formatted(Formatting.GRAY, Formatting.ITALIC));
        tooltip.add(Text.translatable("item.forkcart.track." + type.name().toLowerCase(Locale.ROOT) + ".desc")
                .formatted(Formatting.GRAY, Formatting.ITALIC));

        var origin = OriginComponent.get(stack);
        if (origin != null) origin.appendTooltip(tooltip);
    }
    public Type getType() {
        return type;
    }

    public enum Type {

        STANDARD,
        CHAIN,
        STATION,
        BRAKE

    }

}
