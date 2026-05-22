package com.improvedvanillatooltips;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Improved Vanilla Tooltips"
)
public class ImprovedVanillaTooltipsPlugin extends Plugin
{
	private static final int STYLE_ID = 526;			//also attack bar, audio icons, zoom, controls, emotes, map, prayer, emotes, brightness, zoom
	private static final int RETALIATE_ID = 38;
	private static final int STATS_ID = 992;
	private static final int WORLDSWITCHER_ID = 7274;	//works in small and large menus
	private static final int SKULL_PREVENTION_ID = 5524;
	private static final int SETTINGS_OPEN_ID = 3904;
	private static final int MASTER_ZOOM_ID = 313;
	private static final int MUSIC_ZOOM_ID = 2257;
	private static final int SOUND_ZOOM_ID = 3927;
	private static final int AREASOUNDS_ZOOM_ID = 3928;

	@Inject
	private Client client;

	@Inject
	private ImprovedVanillaTooltipsConfig config;

	@Subscribe
	public void onScriptPostFired(ScriptPostFired script)
	{
		if(script.getScriptId() == STYLE_ID)
		{
			Widget tooltip = client.getWidget(InterfaceID.Prayerbook.TOOLTIP);
			if(tooltip != null)
			{
				int old_time = client.getVarcIntValue(VarClientID.TOOLTIP_TIME);
				int new_time = old_time + 1000;
				client.setVarcIntValue(VarClientID.TOOLTIP_TIME, new_time);
			}
		}
	}

	@Provides
	ImprovedVanillaTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImprovedVanillaTooltipsConfig.class);
	}
}
