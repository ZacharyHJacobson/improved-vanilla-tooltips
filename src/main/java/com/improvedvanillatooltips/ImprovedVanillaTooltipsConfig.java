package com.improvedvanillatooltips;

import java.awt.Dimension;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(ImprovedVanillaTooltipsConfig.GROUP)
public interface ImprovedVanillaTooltipsConfig extends Config
{
	final String GROUP = "improvedvanillatooltips";
	final String PRAYERS_GROUP = GROUP + ".prayers";
	@ConfigItem(
		position = 0,
		keyName = "instantTooltips",
		name = "Instant Tooltips",
		description = "Remove the delay before showing tooltips."
	)
	default boolean instantTooltips()
	{
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "textlessPrayerTooltips",
			name = "Textless Prayer Tooltips",
			description = "Replace prayer tooltips with colored boxes (shift+m2 -> \"recolor textbox\" to customize)."
	)
	default boolean textlessPrayerTooltips()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "prayerDimensions",
			name = "Tooltip Size",
			description = "If textless prayer tooltips are enabled, resizes the textbox."

	)
	default Dimension prayerDimensions()
	{
		return new Dimension(100,50);
	}
}
