package com.gp.vaadin.demo.hotel;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

@DesignRoot
public class MenuNavigator {

	final MenuBar menuBar = new MenuBar();
	final HotelUI ui;

	@SuppressWarnings("unused")
	public MenuNavigator(HotelUI ui) {

		this.ui = ui;

		MenuBar.Command command = new MenuBar.Command() {
			private static final long serialVersionUID = 1345082422081018206L;

			@Override
			public void menuSelected(MenuItem selectedItem) {

				if (selectedItem.getText().equals(HotelUI.HOTEL)) {
					ui.getUI().getNavigator().navigateTo(HotelUI.HOTEL);
				} else if (selectedItem.getText().equals(HotelUI.CATEGORY)) {
					ui.getUI().getNavigator().navigateTo(HotelUI.CATEGORY);
				}

			}
		};

		MenuItem hotelItem = menuBar.addItem(HotelUI.HOTEL, VaadinIcons.INSTITUTION, command);
		MenuItem categoryItem = menuBar.addItem(HotelUI.CATEGORY, VaadinIcons.FILE_TREE_SMALL, command);
		menuBar.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

}