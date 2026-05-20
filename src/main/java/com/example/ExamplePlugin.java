package com.example;

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
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	private static final int PRAYER_MOUSEOVER_ID = 526;

	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	@Subscribe
	public void onScriptPostFired(ScriptPostFired script)
	{
		if(script.getScriptId() == PRAYER_MOUSEOVER_ID)
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
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
