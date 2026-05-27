package com.responsivetooltips;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.Menu;
import net.runelite.api.events.MenuOpened;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.ColorUtil;

import java.awt.*;
import java.util.Set;

import static com.responsivetooltips.ResponsiveTooltipsConfig.PRAYERS_GROUP;

@Slf4j
@PluginDescriptor(
	name = "Responsive Tooltips"
)
public class ResponsiveTooltipsPlugin extends Plugin
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
	private static final String DEFAULT_COLOR = "#ffffa0";

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ResponsiveTooltipsConfig config;

	@Inject
	private ColorPickerManager colorPickerManager;

	@Subscribe
	public void onScriptPostFired(ScriptPostFired script)
	{
		int scriptID = script.getScriptId();
		//show tooltips immediately
		if(config.instantTooltips() && SCRIPT_IDS.contains(scriptID)) showTooltip();
		//replace text in prayer tooltips
		if(config.textlessPrayerTooltips() && scriptID == TOOLTIP_ID) replacePrayerTooltip();
	}

	private void showTooltip()
	{
		int old_time = client.getVarcIntValue(VarClientID.TOOLTIP_TIME);
		int new_time = old_time + 1000;
		client.setVarcIntValue(VarClientID.TOOLTIP_TIME, new_time);
	}

	/**
	 * Replaces prayer tooltip color, text, and position.
	 */
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
		Widget prayer = getPrayerWidget();
		if(background == null || border == null || text == null || prayer == null) return;
		int tooltip_bottom = tooltip.getHeight() + tooltip.getOriginalY();

		//modify the tooltip to be a solid color, textless, and relocated based on the prayer's position
		background.setTextColor(loadColor(parsePrayerName(prayer)).getRGB());
		text.setText("");
		tooltip.setSize(getCustomPrayerWidth(), getCustomPrayerHeight());
		if(tooltip.getOriginalY() < prayer.getOriginalY())
		{
			tooltip.setOriginalY(tooltip_bottom - getCustomPrayerHeight());
		}

		//align tooltip horizontally to be centered when possible but left or right justified when not
		int prayer_x = prayer.getOriginalX();
		prayer_x += (prayer.getWidth() / 2);
		prayer_x -= (getCustomPrayerWidth() / 2);
		prayer_x = Math.max(prayer_x, 0);
		Widget side_container = client.getWidget(InterfaceID.ToplevelOsrsStretch.SIDE_CONTAINER);
		if(side_container == null) return;
		prayer_x = Math.min(prayer_x, side_container.getOriginalWidth() - getCustomPrayerWidth());
        tooltip.setOriginalX(prayer_x);
		tooltip.revalidate();
		background.revalidate();
		border.revalidate();
		text.revalidate();
	}

	/**
	 * Only gets widget if prayer panel is open and a prayer is moused over.
	 * @return prayer container
	 */
	private Widget getPrayerWidget()
	{
		MenuEntry[] menu_entries = client.getMenu().getMenuEntries();
		Widget prayer = menu_entries[menu_entries.length - 1].getWidget();
		if (prayer == null) return null;
		Widget panel = prayer.getParent();
		if (panel == null) return null;
		return (panel.getId() == InterfaceID.Prayerbook.CONTAINER) ? prayer: null;
	}

	private int getCustomPrayerWidth()
	{
		return (int) config.prayerDimensions().getWidth();
	}

	private int getCustomPrayerHeight()
	{
		return (int) config.prayerDimensions().getHeight();
	}

	/**
	 * Adds options to the right click menus for prayers to recolor the tooltip.
	 *
	 * @param event menu information
	 */
	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		//add tooltip color picking option when shift+right-clicking a prayer
		if (!config.textlessPrayerTooltips() || !client.isKeyPressed(KeyCode.KC_SHIFT)) return;
		Widget prayer = getPrayerWidget();
		if (prayer == null) return;
		String prayer_name = parsePrayerName(prayer);
		Menu colorMenu =  client.getMenu().createMenuEntry(event.getMenuEntries().length - 1)
			.setOption("Tooltip color")
			.setTarget(prayer_name)
			.setType(MenuAction.RUNELITE)
			.createSubMenu();

		//color options used by current visible prayers
		Widget[] available_prayers = prayer.getParent().getStaticChildren();
		if(available_prayers == null || available_prayers.length < 1) return;
		Set<String> configured_colors = new java.util.TreeSet<>(Set.of());
		for (Widget available_prayer : available_prayers)
		{
			if(!available_prayer.isHidden())
			{
				String color = configManager.getConfiguration(PRAYERS_GROUP, parsePrayerName(available_prayer));
				if(color != null) configured_colors.add(color);
			}
		}
		for (String color : configured_colors)
		{
			colorMenu.createMenuEntry(0)
					.setOption(ColorUtil.prependColorTag("Color", Color.decode(color)))
					.setType(MenuAction.RUNELITE)
					.onClick(entry->
                            configManager.setConfiguration(PRAYERS_GROUP, prayer_name, color));
		}

		//pick a new color
		colorMenu.createMenuEntry(0)
				.setOption("Pick")
				.setType(MenuAction.RUNELITE)
				.onClick( entry->
                        SwingUtilities.invokeLater(() ->
                        {
                            Color old_color = loadColor(prayer_name);
                            RuneliteColorPicker colorPicker = colorPickerManager.create(client, old_color, prayer_name + " Tooltip Color", true);
                            colorPicker.setOnClose(new_color ->
configManager.setConfiguration(PRAYERS_GROUP, prayer_name, new_color));
                            colorPicker.setVisible(true);
                        }));

		//reset to default
		if (configManager.getConfiguration(PRAYERS_GROUP, prayer_name) != null)
		{
			colorMenu.createMenuEntry(0).setOption("Reset").setType(MenuAction.RUNELITE).onClick( entry->
                    configManager.unsetConfiguration(PRAYERS_GROUP, prayer_name));
		}
	}

	private String parsePrayerName(Widget prayer)
	{
		return prayer.getName().replace("<col=ff9040>", "").replace("</col>", "");
	}

	/**
	 * Loads custom or default tooltip color for a prayer
	 * @param prayer_name the trimmed name of the prayer, see parsePrayerName()
	 * @return the prayer's tooltip color as a Color
	 */
	private Color loadColor(String prayer_name)
	{
		String color = configManager.getConfiguration(PRAYERS_GROUP, prayer_name);
		if(color == null) color = DEFAULT_COLOR;
		return Color.decode(color);
	}

	@Provides
	ResponsiveTooltipsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ResponsiveTooltipsConfig.class);
	}
}
