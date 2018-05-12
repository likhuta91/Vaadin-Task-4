package com.gp.vaadin.demo.hotel;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gp.vaadin.demo.hotel.helper.BinderHotelEditForm;
import com.gp.vaadin.demo.hotel.helper.ButtonHelper;
import com.gp.vaadin.demo.hotel.helper.HotelHelper;
import com.gp.vaadin.demo.hotel.view.HotelView;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

public class PopupUpdateHotelContent extends VerticalLayout {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());

	final HotelService hotelService = HotelService.getInstance();
	final CategoryService categoryService = CategoryService.getInstance();
	final HotelView hotelView;

	final VerticalLayout layout = new VerticalLayout();
	final List<String> editableField = new ArrayList<>(
			Arrays.asList(HotelHelper.NAME, HotelHelper.ADDRESS, HotelHelper.RATING, HotelHelper.OPERATES_FROM,
					HotelHelper.CATEGORY, HotelHelper.DESCRIPTION, HotelHelper.URL));
	private Set<Hotel> hotels;
	private ComboBox<String> fieldBox;

	private BinderHotelEditForm binderHotelEditForm = new BinderHotelEditForm(false);

	final Button update = ButtonHelper.getUpdateButton();
	final Button cancel = ButtonHelper.getCancelButton();

	private static PopupUpdateHotelContent instance;

	private PopupUpdateHotelContent(HotelView hotelview) {
		this.hotelView = hotelview;

		fieldBox = new ComboBox<>(null, this.editableField);
		fieldBox.setPlaceholder("Please select field");
		fieldBox.setEmptySelectionAllowed(false);
		fieldBox.setWidth(100, Sizeable.Unit.PERCENTAGE);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(update, cancel);
		layout.addComponents(fieldBox, binderHotelEditForm.getName(), buttons);
		addComponent(layout);

		update.addClickListener(e -> {

			for (Hotel h : hotels) {

				Hotel hotel = changeFieldValueHotel(h);

				if (hotel != null) {
					save(hotel);
				}
			}
		});

		cancel.addClickListener(e -> {
			hotelview.updateList();
		});

		fieldBox.addValueChangeListener(e -> {

			layout.removeAllComponents();

			if (e.getValue().equals(HotelHelper.NAME)) {
				binderHotelEditForm.getName().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getName(), buttons);
			} else if (e.getValue().equals(HotelHelper.OPERATES_FROM)) {
				binderHotelEditForm.getOperatesFrom().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getOperatesFrom(), buttons);
			} else if (e.getValue().equals(HotelHelper.ADDRESS)) {
				binderHotelEditForm.getAddress().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getAddress(), buttons);
			} else if (e.getValue().equals(HotelHelper.RATING)) {
				binderHotelEditForm.getRating().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getRating(), buttons);
			} else if (e.getValue().equals(HotelHelper.CATEGORY)) {
				binderHotelEditForm.getCategory().clear();
				binderHotelEditForm.getCategory().setItems(categoryService.findAll());
				layout.addComponents(fieldBox, binderHotelEditForm.getCategory(), buttons);
			} else if (e.getValue().equals(HotelHelper.DESCRIPTION)) {
				binderHotelEditForm.getDescription().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getDescription(), buttons);
			} else if (e.getValue().equals(HotelHelper.URL)) {
				binderHotelEditForm.getUrl().clear();
				layout.addComponents(fieldBox, binderHotelEditForm.getUrl(), buttons);
			}
		});
	}

	public static PopupUpdateHotelContent getInstance(HotelView hotelview) {

		if (instance == null) {
			instance = new PopupUpdateHotelContent(hotelview);
		}
		return instance;
	}

	public void setHotels(Set<Hotel> hotels) {
		this.hotels = hotels;
	}

	public void updateCategoryList() {
		binderHotelEditForm.getCategory().setItems(categoryService.findAll());
	}

	public void save(Hotel h) {

		binderHotelEditForm.getBinder().readBean(h);

		if (binderHotelEditForm.getBinder().isValid()) {

			try {
				binderHotelEditForm.getBinder().writeBean(h);
			} catch (ValidationException e) {
				Notification.show("Unable to save!" + e.getMessage(), Type.HUMANIZED_MESSAGE);
			}

			hotelService.save(h);
			hotelView.updateList();
		} else {
			Notification.show("Unable to save! Please review errors and fix them.", Type.WARNING_MESSAGE);

		}
		binderHotelEditForm.getBinder().removeBean();
	}

	public Hotel changeFieldValueHotel(Hotel h) {

		String nameField = fieldBox.getValue();
		if (nameField == null) {
			Notification.show("Unable to save! Please review errors and fix them.", Type.WARNING_MESSAGE);
			return null;
		}
		Field f = null;
		Hotel hotel = null;

		try {

			hotel = h.clone();

			if (nameField.equals(HotelHelper.RATING)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.RATING.toLowerCase());
				f.setAccessible(true);
				f.set(hotel, Integer.parseInt(binderHotelEditForm.getRating().getValue().toString()));

			} else if (nameField.equals(HotelHelper.CATEGORY)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.CATEGORY.toLowerCase());
				f.setAccessible(true);
				f.set(hotel, (Category) binderHotelEditForm.getCategory().getValue());

			} else if (nameField.equals(HotelHelper.OPERATES_FROM)) {
				f = hotel.getClass().getDeclaredField("operatesFrom");
				f.setAccessible(true);
				f.set(hotel, Date.from(binderHotelEditForm.getOperatesFrom().getValue()
						.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());

			} else if (nameField.equals(HotelHelper.DESCRIPTION)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.DESCRIPTION.toLowerCase());
				f.setAccessible(true);
				try {
					f.set(hotel, binderHotelEditForm.getDescription().getValue().toString());
				} catch (NullPointerException ex) {
					f.set(hotel, "");
				}
			} else if (nameField.equals(HotelHelper.NAME)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.NAME.toLowerCase());
				f.setAccessible(true);
				f.set(hotel, binderHotelEditForm.getName().getValue().toString());
			} else if (nameField.equals(HotelHelper.ADDRESS)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.ADDRESS.toLowerCase());
				f.setAccessible(true);
				f.set(hotel, binderHotelEditForm.getAddress().getValue().toString());
			} else if (nameField.equals(HotelHelper.URL)) {
				f = hotel.getClass().getDeclaredField(HotelHelper.URL.toLowerCase());
				f.setAccessible(true);
				f.set(hotel, binderHotelEditForm.getUrl().getValue().toString());
			}

		} catch (NoSuchFieldException | IllegalAccessException | CloneNotSupportedException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
			hotel = null;
		} catch (NumberFormatException | ClassCastException | NullPointerException ex) {
			Notification.show("Unable to save! Please review errors and fix them.", Type.WARNING_MESSAGE);
			hotel = null;
		}

		return hotel;
	}

}
