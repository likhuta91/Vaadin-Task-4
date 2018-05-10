package com.gp.vaadin.demo.hotel;

import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
	
	final HotelService hotelService = HotelService.getInstance();
	final CategoryService categoryService = CategoryService.getInstance();
	final HotelView hotelView;

	
	final VerticalLayout layout = new VerticalLayout();
	final List<String> editableField = new ArrayList<>(
			Arrays.asList(HotelHelper.NAME,HotelHelper.ADDRESS, HotelHelper.RATING, HotelHelper.OPERATES_FROM, HotelHelper.CATEGORY, HotelHelper.DESCRIPTION, HotelHelper.URL));
	private Set<Hotel> hotels;
	private ComboBox<String> fieldBox;
	private Object valueTextField;

	private BinderHotelEditForm binderHotelEditForm = new BinderHotelEditForm(false);

	final Button update = ButtonHelper.getUpdateButton();
	final Button cancel = ButtonHelper.getCancelButton();

	private static PopupUpdateHotelContent instance;

	public static PopupUpdateHotelContent getInstance(HotelView hotelview) {

		if (instance == null) {
			instance = new PopupUpdateHotelContent(hotelview);
		}
		return instance;
	}

	public void setHotels(Set<Hotel> hotels) {
		this.hotels = hotels;
	}

	private PopupUpdateHotelContent(HotelView hotelview) {
		this.hotelView = hotelview;

		fieldBox = new ComboBox<>(null, this.editableField);
		fieldBox.setCaption("Please select field");
		fieldBox.setWidth(100, Sizeable.Unit.PERCENTAGE);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(update, cancel);
		layout.addComponents(fieldBox, binderHotelEditForm.getName(), buttons);
		addComponent(layout);

		update.addClickListener(e -> {

			for (Hotel hotel : hotels) {
				String nameField = fieldBox.getValue();
				Field f = null;
				Hotel h = null;
				try {
					h = hotel.clone();

					if (nameField.equals(HotelHelper.RATING)) {
						f = h.getClass().getDeclaredField(HotelHelper.RATING.toLowerCase());
						f.setAccessible(true);
						f.set(h, Integer.parseInt(valueTextField.toString()));
					} else if (nameField.equals(HotelHelper.CATEGORY)) {
						f = h.getClass().getDeclaredField(HotelHelper.CATEGORY.toLowerCase());
						f.setAccessible(true);
						f.set(h, (Category) valueTextField);
					} else if (nameField.equals(HotelHelper.OPERATES_FROM)) {
						f = h.getClass().getDeclaredField("operatesFrom");
						f.setAccessible(true);
						f.set(h, Date.from(binderHotelEditForm.getOperatesFrom().getValue()
								.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
					} else {
						f = h.getClass().getDeclaredField(nameField.toLowerCase());
						f.setAccessible(true);
						f.set(h, valueTextField.toString());
					}

				} catch (NoSuchFieldException | SecurityException e1) {
					e1.printStackTrace();
				} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				} catch (IndexOutOfBoundsException e1) {
					e1.printStackTrace();
				} catch (CloneNotSupportedException e2) {
					e2.printStackTrace();
				}

				binderHotelEditForm.getBinder().readBean(h);
				if (binderHotelEditForm.getBinder().isValid()) {
					try {
						binderHotelEditForm.getBinder().writeBean(h);
					} catch (ValidationException e1) {
						e1.printStackTrace();
					}
					hotelService.save(h);
					hotelview.updateList();
				} else {
					System.out.println(h.toString() + "toString");
					Notification.show("Unable to save! Please review errors and fix them.", Type.WARNING_MESSAGE);
				}
			}
			
/*			binderHotelEditForm.getName().clear();
			binderHotelEditForm.getOperatesFrom().clear();
			binderHotelEditForm.getRating().clear();
			binderHotelEditForm.getAddress().clear();
			binderHotelEditForm.getCategory().clear();
			binderHotelEditForm.getDescription().clear();
			binderHotelEditForm.getUrl().clear();*/

		});

		cancel.addClickListener(e -> {
			hotelview.updateList();
		});

		fieldBox.addValueChangeListener(e -> {

			layout.removeAllComponents();

			if (e.getValue().equals(HotelHelper.NAME)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getName(), buttons);
			} else if (e.getValue().equals(HotelHelper.OPERATES_FROM)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getOperatesFrom(), buttons);
			} else if (e.getValue().equals(HotelHelper.ADDRESS)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getAddress(), buttons);
			} else if (e.getValue().equals(HotelHelper.RATING)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getRating(), buttons);
			} else if (e.getValue().equals(HotelHelper.CATEGORY)) {
				binderHotelEditForm.getCategory().setItems(categoryService.findAll());
				layout.addComponents(fieldBox, binderHotelEditForm.getCategory(), buttons);
			} else if (e.getValue().equals(HotelHelper.DESCRIPTION)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getDescription(), buttons);
			} else if (e.getValue().equals(HotelHelper.URL)) {
				layout.addComponents(fieldBox, binderHotelEditForm.getUrl(), buttons);
			}

		});

		binderHotelEditForm.getBinder().addValueChangeListener(e -> {
			valueTextField = e.getValue();
		});
	}

}
