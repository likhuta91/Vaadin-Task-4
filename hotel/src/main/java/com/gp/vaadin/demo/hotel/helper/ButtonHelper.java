package com.gp.vaadin.demo.hotel.helper;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public class ButtonHelper {

	public static Button getAddButton(String caption) {
		Button add = new Button(caption);
		add.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		add.setIcon(VaadinIcons.PLUS);
		return add;
	}

	public static Button getEditButton(String caption) {
		Button edit = new Button(caption);
		edit.setEnabled(false);
		edit.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		edit.setIcon(VaadinIcons.EDIT);
		return edit;
	}

	public static Button getDeleteButton(String caption) {
		Button delete = new Button(caption);
		delete.setEnabled(false);
		delete.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		delete.setIcon(VaadinIcons.TRASH);
		return delete;
	}

	public static Button getSaveButton(String caption) {
		Button save = new Button(caption);
		save.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		save.setIcon(VaadinIcons.CHECK);
		save.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return save;
	}

	public static Button getCloseButton(String caption) {
		Button close = new Button(caption);
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.CLOSE);
		close.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return close;
	}
	
	public static Button getUpdateButton(String caption) {
		Button close = new Button(caption);
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.REFRESH);
		close.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return close;
	}
	
/*	public static Button getUpdateButton(String caption) {
		Button close = new Button(caption);
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.REFRESH);
		close.setWidth(50, Sizeable.Unit.PERCENTAGE);
		return close;
	}
	
	public static Button getCancelButton(String caption) {
		Button close = new Button(caption);
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.REFRESH);
		close.setWidth(50, Sizeable.Unit.PERCENTAGE);
		return close;
	}*/

}
