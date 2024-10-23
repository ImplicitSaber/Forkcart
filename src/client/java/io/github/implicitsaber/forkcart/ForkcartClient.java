package io.github.implicitsaber.forkcart;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.implicitsaber.forkcart.block.entity.TrackTiesBlockEntityRenderer;
import io.github.implicitsaber.forkcart.config.Config;
import io.github.implicitsaber.forkcart.config.ConfigOption;
import io.github.implicitsaber.forkcart.util.ForgeHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;

public class ForkcartClient implements ClientModInitializer {
	public static final Config CONFIG = new Config("forkcart_client",
			() -> FabricLoader.getInstance().getConfigDir()
					.resolve("forkcart").resolve("forkcart_client.properties"));

	public static final ConfigOption.BooleanOption CFG_ROTATE_CAMERA = CONFIG.optBool("rotate_camera", true);
	public static final ConfigOption.IntOption CFG_TRACK_RESOLUTION = CONFIG.optInt("track_resolution", 3, 1, 16);
	public static final ConfigOption.IntOption CFG_TRACK_RENDER_DISTANCE = CONFIG.optInt("track_render_distance", 8, 4, 32);

	@Override
	public void onInitializeClient() {
		try {
			CONFIG.load();
		} catch (IOException e) {
			Forkcart.LOGGER.error("Error loading client config on mod init", e);
		}

		BlockRenderLayerMap.INSTANCE.putBlock(Forkcart.TRACK_TIES, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Forkcart.SWITCH_TIES, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Forkcart.INVISIBLE_TIES, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(Forkcart.SHUTTLE_TIES, RenderLayer.getCutout());

		BlockEntityRendererFactories.register(Forkcart.TRACK_TIES_BE, TrackTiesBlockEntityRenderer::new);
		EntityRendererRegistry.register(Forkcart.TRACK_FOLLOWER, EmptyEntityRenderer::new);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(
					LiteralArgumentBuilder.<FabricClientCommandSource>literal("forkcartc")
							.then(CONFIG.command(LiteralArgumentBuilder.literal("config"),
									FabricClientCommandSource::sendFeedback))
		));

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			if(ForgeHelper.isConnector()) {
				client.inGameHud.getChatHud().addMessage(Text.translatable("forkcart.msg.connector").formatted(Formatting.RED));
			}
		});
	}
}