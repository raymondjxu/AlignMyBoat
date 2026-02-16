package me.dontknow09.alignmyboat;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlignMyBoat implements ModInitializer {
	public static final String MOD_ID = "alignmyboat";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static KeyBinding alignBoatKeybind;
	private static final MinecraftClient client = MinecraftClient.getInstance();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		final KeyBinding.Category keyCategory = KeyBinding.Category.create(Identifier.of("alignboat", "options"));

		alignBoatKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.alignboat",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_BACKSLASH,
				keyCategory
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (alignBoatKeybind.wasPressed()) align();
			// this.client = client; something to consider?
		});
	}

	private static double roundYaw(double yaw) {
		yaw = yaw % 360;
		if (yaw > 180 || yaw < -180) {
			double mod = yaw % 180;
			if (mod > 0) yaw = -180 + mod;
			else if (mod < 0) yaw = 180 + mod;
		}

		if (yaw >= 0 && yaw < 22.5) yaw = 0;
		if (yaw >= 22.5 && yaw < 67.5) yaw = 45;
		if (yaw >= 67.5 && yaw < 112.5) yaw = 90;
		if (yaw >= 112.5 && yaw < 157.5) yaw = 135;
		if (yaw >= 157.5 && yaw <= 180) yaw = 180;
		if (yaw <= 0 && yaw > -22.5) yaw = 0;
		if (yaw <= -22.5 && yaw > -67.5) yaw = -45;
		if (yaw <= -67.5 && yaw > -112.5) yaw = -90;
		if (yaw <= -112.5 && yaw > -157.5) yaw = -135;
		if (yaw <= -157.5 && yaw >= -180) yaw = 180;

		return yaw;
	}

	private void align() {
		final double oldYaw = client.player.getHeadYaw();
		final double newYaw = roundYaw(oldYaw);

		LOGGER.info("Yaw {} rounds to {}", oldYaw, newYaw);

		setPlayerYaw(newYaw);
	}

	private void setPlayerYaw(double yaw) {
		final var player = client.player;

		// these are warned to potentially produce null pointer exception. if playern is null (which it shouldn't be), fix ig?
		player.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), (float) yaw, player.getPitch(0));
		player.sendMessage(Text.translatable("alignboat.success", yaw), true);

	}
}