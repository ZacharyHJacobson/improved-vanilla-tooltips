package com.improvedvanillatooltips;

import java.awt.Dimension;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("improvedvanillatooltips")
public interface ImprovedVanillaTooltipsConfig extends Config
{
	@ConfigItem(
		position = 0,
		keyName = "instant_tooltips",
		name = "Instant Tooltips",
		description = "Remove the delay before showing tooltips."
	)
	default boolean instant_tooltips()
	{
		return true;
	}

	@ConfigItem(
			position = 1,
			keyName = "textless_prayer_tooltips",
			name = "Textless Prayer Tooltips",
			description = "Replace prayer tooltips with colored boxes (shift+m2 -> \"recolor textbox\" to customize)."
	)
	default boolean textless_prayer_tooltips()
	{
		return true;
	}

	@ConfigItem(
			position = 2,
			keyName = "prayer_dimensions",
			name = "Tooltip Size",
			description = "If textless prayer tooltips are enabled, resizes the textbox."

	)
	default Dimension prayer_dimensions()
	{
		return new Dimension(100,50);
	}
}
