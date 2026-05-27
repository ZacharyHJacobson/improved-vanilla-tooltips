package com.responsivetooltips;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ResponsiveTooltipsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ResponsiveTooltipsPlugin.class);
		RuneLite.main(args);
	}
}