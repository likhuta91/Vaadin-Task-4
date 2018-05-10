package com.gp.vaadin.demo.hotel.helper;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;

public class ButtonHelper {

	public static Button getAddButton() {
		Button add = new Button("Add");
		add.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		add.setIcon(VaadinIcons.PLUS);
		return add;
	}

	public static Button getEditButton() {
		Button edit = new Button("Edit");
		edit.setEnabled(false);
		edit.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		edit.setIcon(VaadinIcons.EDIT);
		return edit;
	}

	public static Button getDeleteButton() {
		Button delete = new Button("Delete");
		delete.setEnabled(false);
		delete.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		delete.setIcon(VaadinIcons.TRASH);
		return delete;
	}

	public static Button getSaveButton() {
		Button save = new Button("Save");
		save.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		save.setIcon(VaadinIcons.CHECK);
		save.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return save;
	}

	public static Button getCloseButton() {
		Button close = new Button("Close");
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.CLOSE);
		close.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return close;
	}

	public static Button getUpdateButton() {
		Button close = new Button("Update");
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.REFRESH);
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
	
	public static Button getCancelButton() {
		Button close = new Button("Cancel");
		close.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
		close.setIcon(VaadinIcons.CLOSE);
		close.setWidth(100, Sizeable.Unit.PERCENTAGE);
		return close;
	}

}
