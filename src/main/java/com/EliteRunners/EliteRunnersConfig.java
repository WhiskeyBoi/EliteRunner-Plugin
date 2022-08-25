package com.EliteRunners;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Units;

@ConfigGroup(EliteRunnersConfig.GROUP)
public interface EliteRunnersConfig extends Config
{
	String GROUP = "EliteRunners";
	@ConfigSection(
		name = "Time Trial Mode",
		description = "This mode enables you to make an official solo run time trial",
		position = 0
	)
	String settings = "settings";
	////////////////////////////
	//---------Trials---------//
	////////////////////////////
		@ConfigItem(
				position = 1,
				keyName = "DiscordName",
				name = "Discord Tag",
				description = "Your discord username (#xxxx included)",
				section = settings
		)
		default String DiscordName() { return "EliteRunners";}

		@ConfigItem(
				position = 2,
				keyName = "StartTrial",
				name = "Activate Time Trial Mode",
				description = "Check to get the time trial mode ready",
				section = settings
		)
		default boolean StartTrial() { return false; }
	/////////////////////////////////
	//---------Runner Mode---------//
	/////////////////////////////////
	@ConfigSection(
			name = "Essence Runner Mode",
			description = "Every essence running functionalities you need",
			position = 100
	)
	String EssRunMode = "EssRunMode";

		@ConfigItem(
				position = 101,
				keyName = "Stretch",
				name = "Screen Stretcher",
				description = "Stretches your menu for better reach and visibility",
				section = EssRunMode
		)
		default boolean Stretch() { return false; }

		@ConfigItem(
				position = 102,
				keyName = "NoDrag",
				name = "No Drag",
				description = "Makes sure you don't move an Item you wish to click on",
				section = EssRunMode
		)
		default boolean NoDrag() { return false; }

		@ConfigItem(
				position = 103,
				keyName = "HideOthers",
				name = "Hide Others",
				description = "Hides every player not in your clan or friend list",
				section = EssRunMode
		)
		default boolean HideOthers() { return false; }

		@ConfigItem(
				position = 104,
				keyName = "Highlights",
				name = "Enable Highlight",
				description = "Will highlight binding necklaces, rings of dueling and trade window when needed",
				section = EssRunMode
		)
		default boolean Highlights() { return false; }

		@ConfigItem(
				position = 105,
				keyName = "ShiftClick",
				name = "Enable Shift Click Shortcuts",
				description = "Allows shift click shortcuts for cape, talismans, potions, necklaces, rings, essence, pouches, banks, etc.",
				section = EssRunMode
		)
		default boolean ShiftClick() { return false; }

		@ConfigItem(
				position = 106,
				keyName = "Swap",
				name = "Enable Partial Menu Entry Swapper",
				description = "Swaps bank deposits, ring of elements,  ring of dueling, amulet of eternal glory, and other teleportation means",
				section = EssRunMode
		)
		default boolean Swap() { return false; }

		@ConfigItem(
				position = 107,
				keyName = "OfferAll",
				name = "Offer All Items",
				description = "Allows you to offer every item of a kind by clicking on one of them",
				section = EssRunMode
		)
		default boolean OfferAll() { return false; }

		@ConfigItem(
				position = 108,
				keyName = "TradeSpam",
				name = "Enable Trade Spam",
				description = "When receiving a trade offer, it will be spammed instead",
				section = EssRunMode
		)
		default boolean TradeSpam() { return false; }
}
