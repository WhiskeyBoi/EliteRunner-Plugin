package com.EliteRunners;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.client.input.MouseManager;
import com.EliteRunners.EliteRunnersConfig;
import com.EliteRunners.TranslateMouseListener;
import com.EliteRunners.TranslateMouseWheelListener;
import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Provider;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.menus.MenuManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;
import okhttp3.*;
import net.runelite.api.GraphicID;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Projectile;
import net.runelite.api.Renderable;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.NpcUtil;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import com.google.common.annotations.VisibleForTesting;



import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;














@Slf4j
@PluginDescriptor(
		name = "EliteRunners plugin",
		description = "Gives a multitude of options to facilitate essence and bone running",
		tags = {"runner", "running"},
		enabledByDefault = false
)
public class EliteRunnersPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private EliteRunnersConfig config;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private TranslateMouseListener mouseListener;

	@Inject
	private NpcUtil npcUtil;

	private boolean hideOthers;
	private boolean hideOthers2D;
	private boolean hideFriends;
	private boolean hideFriendsChatMembers;
	private boolean hideClanMembers;
	private boolean hideNPCs;

	private boolean hideLocalPlayer;
	private boolean hideLocalPlayer2D;
	@Inject
	private TranslateMouseWheelListener mouseWheelListener;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Hooks hooks;

	public static NavigationButton panel;
	private final ArrayListMultimap<String, Integer> indexes = ArrayListMultimap.create();
	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Provides
	EliteRunnersConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(EliteRunnersConfig.class);
	}


	@Override
	protected void startUp() {
		mouseManager.registerMouseListener(0, (MouseListener) mouseListener);
		mouseManager.registerMouseWheelListener(0, mouseWheelListener);
		client.setStretchedEnabled(config.Stretch());
		hooks.registerRenderableDrawListener(drawListener);

		updateConfig();
	}


	@Override
	protected void shutDown() throws Exception {
		client.setStretchedEnabled(false);
		client.invalidateStretching(true);
		mouseManager.unregisterMouseListener((MouseListener) mouseListener);
		mouseManager.unregisterMouseWheelListener(mouseWheelListener);
		hooks.unregisterRenderableDrawListener(drawListener);
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals(EliteRunnersConfig.GROUP)) {
			return;
		}

		updateConfig();
	}

	private void updateConfig() {
		client.setStretchedIntegerScaling(false);
		client.setStretchedKeepAspectRatio(true);
		client.setStretchedFast(true);
		client.setScalingFactor(35);
		client.setStretchedEnabled(config.Stretch());
		client.invalidateStretching(true);
		hideOthers = config.HideOthers();
		hideOthers2D = config.HideOthers();
		hideFriends = false;
		hideFriendsChatMembers = false;
		hideClanMembers = false;
		hideNPCs = false;
		hideLocalPlayer = false;
		hideLocalPlayer2D = false;

	}

	public void onClientTick(final ClientTick clientTick) {
		if (config.OfferAll()) {
			final MenuEntry[] menuEntries = client.getMenuEntries();
			int index = 0;
			indexes.clear();
			for (MenuEntry entry : menuEntries) {
				final String option = Text.removeTags(entry.getOption()).toLowerCase();
				indexes.put(option, index++);
			}

			index = 0;
			for (MenuEntry menuEntry : menuEntries) {
				index++;
				final String option = Text.removeTags(menuEntry.getOption()).toLowerCase();
				final String target = Text.removeTags(menuEntry.getTarget()).toLowerCase();

				if (option.equals("offer")) {
					final int i = index(indexes, menuEntries, index, option, target);
					final int id = index(indexes, menuEntries, i, "offer-all", target);

					if (i >= 0 && id >= 0) {
						final MenuEntry entry = menuEntries[id];
						menuEntries[id] = menuEntries[i];
						menuEntries[i] = entry;

						client.setMenuEntries(menuEntries);

						indexes.clear();
						int idx = 0;
						for (MenuEntry e : menuEntries) {
							final String o = Text.removeTags(e.getOption()).toLowerCase();
							indexes.put(o, idx++);
						}
					}
				}
			}
		}
	}

	public static int index(final ArrayListMultimap<String, Integer> optionIndexes, final MenuEntry[] entries, final int limit, final String option, final String target) {
		List<Integer> indexes = optionIndexes.get(option);
		for (int i = indexes.size() - 1; i >= 0; --i) {
			final int idx = indexes.get(i);
			MenuEntry entry = entries[idx];
			String entryTarget = Text.removeTags(entry.getTarget()).toLowerCase();
			if (idx <= limit && entryTarget.equals(target))
				return idx;
		}
		return -1;
	}

	@VisibleForTesting
	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof Player)
		{
			Player player = (Player) renderable;
			Player local = client.getLocalPlayer();

			if (player.getName() == null)
			{
				// player.isFriend() and player.isFriendsChatMember() npe when the player has a null name
				return true;
			}
			// Allow hiding local self in pvp, which is an established meta.
			// It is more advantageous than renderself due to being able to still render local player 2d
			if (player == local)
			{
				return !(drawingUI ? hideLocalPlayer2D : hideLocalPlayer);
			}

			if (player.isFriend())
			{
				return !hideFriends;
			}
			if (player.isFriendsChatMember())
			{
				return !hideFriendsChatMembers;
			}
			if (player.isClanMember())
			{
				return !hideClanMembers;
			}

			return !(drawingUI ? hideOthers2D : hideOthers);
		}
		else if (renderable instanceof NPC)
		{
			NPC npc = (NPC) renderable;

			switch (((GraphicsObject) renderable).getId())
			{
				case GraphicID.MELEE_NYLO_DEATH:
				case GraphicID.RANGE_NYLO_DEATH:
				case GraphicID.MAGE_NYLO_DEATH:
				case GraphicID.MELEE_NYLO_EXPLOSION:
				case GraphicID.RANGE_NYLO_EXPLOSION:
				case GraphicID.MAGE_NYLO_EXPLOSION:
					return false;
				default:
					return true;
			}
		}

		return true;
	}
}






