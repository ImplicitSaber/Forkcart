package io.github.implicitsaber.forkcart.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LoreBlockItem extends BlockItem {

    private final List<Text> lore;

    public LoreBlockItem(Block block, Settings settings, List<Text> lore) {
        super(block, settings);
        this.lore = lore;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        tooltip.addAll(lore);
    }

}
