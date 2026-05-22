package com.improvedvanillatooltips;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ImprovedVanillaTooltipsTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ImprovedVanillaTooltipsPlugin.class);
		RuneLite.main(args);
	}
}