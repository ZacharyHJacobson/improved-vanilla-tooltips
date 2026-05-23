package com.improvedvanillatooltips;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Set;

@Slf4j
@PluginDescriptor(
	name = "Improved Vanilla Tooltips"
)
public class ImprovedVanillaTooltipsPlugin extends Plugin
{
	private static final Set<Integer> SCRIPT_IDS = Set.of(
			526,	//attack style, attack bar, audio icons, zoom, controls, emotes, map, prayer, emotes, brightness, zoom
			38,		//retaliate
			992,	//stats
			7274,	//world switcher, both small and large
			5524,	//skull prevention
			3904,	//settings open
			313,	//master zoom
			2257,	//music zoom
			3927,	//sound zoom
			3928	//area sounds zoom
	);

	private static final int TOOLTIP_ID = 526;

	@Inject
	private Client client;

	@Inject
	private ImprovedVanillaTooltipsConfig config;

	@Subscribe
	public void onScriptPostFired(ScriptPostFired script)
	{
		int scriptID = script.getScriptId();
		//show tooltips immediately
		if(config.instant_tooltips() && SCRIPT_IDS.contains(scriptID)) showTooltip();
		//replace text in prayer tooltips
		if(config.textless_prayer_tooltips() && scriptID == TOOLTIP_ID) replacePrayerTooltip();
	}

	private void showTooltip()
	{
		int old_time = client.getVarcIntValue(VarClientID.TOOLTIP_TIME);
		int new_time = old_time + 1000;
		client.setVarcIntValue(VarClientID.TOOLTIP_TIME, new_time);
	}

	private void replacePrayerTooltip()
	{
		//get tooltip, tooltip components, the prayer being moused over, and tooltip bottom for later
		Widget tooltip = client.getWidget(InterfaceID.Prayerbook.TOOLTIP);
		if(tooltip == null) return;
		Widget[] components = tooltip.getChildren();
		if(components == null || components.length != 3) return;
		Widget background = tooltip.getChild(0);
		Widget border = tooltip.getChild(1);
		Widget text = tooltip.getChild(2);
		if(background == null || border == null || text == null) return;
		MenuEntry[] menu_entries = client.getMenu().getMenuEntries();
		Widget prayer = menu_entries[menu_entries.length - 1].getWidget();
		if(prayer == null) return;
		int tooltip_bottom = tooltip.getHeight() + tooltip.getOriginalY();

		//modify the tooltip to be a solid color, textless, and relocated based on the prayer's position
		background.setTextColor(0xFF0000);
		text.setText("");
		tooltip.setSize((int) config.prayer_dimensions().getWidth(), (int) config.prayer_dimensions().getHeight());
		if(tooltip.getOriginalY() < prayer.getOriginalY())
		{
			tooltip.setOriginalY(tooltip_bottom - (int) config.prayer_dimensions().getHeight());
		}
		//align tooltip horizontally to be centered when possible but left or right justified when not
		int prayer_x = prayer.getOriginalX();
		prayer_x += (prayer.getWidth() / 2);
		prayer_x -= ((int) config.prayer_dimensions().getWidth() / 2);
		prayer_x = Math.max(prayer_x, 0);
		int side_panel_width = client.getWidget(InterfaceID.ToplevelOsrsStretch.SIDE_CONTAINER).getOriginalWidth();
		prayer_x = Math.min(prayer_x, side_panel_width - (int) config.prayer_dimensions().getWidth());
        tooltip.setOriginalX(prayer_x);
		tooltip.revalidate();
		background.revalidate();
		border.revalidate();
		text.revalidate();
	}

	@Provides
	ImprovedVanillaTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ImprovedVanillaTooltipsConfig.class);
	}
}
