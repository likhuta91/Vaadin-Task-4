package com.gp.vaadin.demo.hotel;

import java.util.List;

import com.gp.vaadin.demo.hotel.helper.BinderHotelEditForm;
import com.gp.vaadin.demo.hotel.helper.ButtonHelper;
import com.gp.vaadin.demo.hotel.view.HotelView;
import com.vaadin.data.ValidationException;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class HotelEditForm extends FormLayout {

	private static final long serialVersionUID = -4322879393284784711L;
	private HotelView hotelView;
	final HotelService hotelService = HotelService.getInstance();
	final CategoryService categoryService = CategoryService.getInstance();
	private Hotel hotel;

	private BinderHotelEditForm binderHotelEditForm = new BinderHotelEditForm(true);

	private Button save = ButtonHelper.getSaveButton();
	private Button close = ButtonHelper.getCloseButton();

	public HotelEditForm(HotelView hotelView) {

		// init
		this.hotelView = hotelView;

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(save, close);
		addComponents(binderHotelEditForm.getName(), binderHotelEditForm.getAddress(), binderHotelEditForm.getRating(),
				binderHotelEditForm.getOperatesFrom(), binderHotelEditForm.getCategory(), binderHotelEditForm.getUrl(),
				binderHotelEditForm.getDescription(), buttons);

		buttons.setWidth(100, Sizeable.Unit.PERCENTAGE);

		save.addClickListener(e -> save(hotel));
		close.addClickListener(e -> setVisible(false));
	}

	public void save(Hotel h) {
		if (binderHotelEditForm.getBinder().isValid()) {

			try {
				binderHotelEditForm.getBinder().writeBean(h);
			} catch (ValidationException e) {
				Notification.show("Unable to save!" + e.getMessage(), Type.HUMANIZED_MESSAGE);
			}

			hotelService.save(h);
			exit();
		} else {
			Notification.show("Unable to save! Please review errors and fix them.", Type.WARNING_MESSAGE);
		}
	}

	private void exit() {
		hotelView.updateList();
		setVisible(false);
	}

	public Hotel getHotel() {
		return hotel;
	}

	public void setCategory(NativeSelect<Category> category) {
		this.binderHotelEditForm.setCategory(category);
	}

	public NativeSelect<Category> getCategory() {
		return binderHotelEditForm.getCategory();
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		setSelectedItemCategoryComponent(updateItemsCategoryComponent());
		binderHotelEditForm.getBinder().readBean(this.hotel);
	}

	public void setSelectedItemCategoryComponent(List<Category> categoryList) {
		int indexSelectedCategory = -1;
		for (int i = 0; i < categoryList.size(); i++) {

			if (this.hotel.getCategory() != null && categoryList.get(i).getId() == this.hotel.getCategory().getId()) {
				indexSelectedCategory = i;
			}
		}

		if (indexSelectedCategory != -1) {
			binderHotelEditForm.getCategory().setSelectedItem(categoryList.get(indexSelectedCategory));
		} else {
			binderHotelEditForm.getCategory().setSelectedItem(null);
		}
	}

	public List<Category> updateItemsCategoryComponent() {
		List<Category> categoryList = categoryService.findAll();
		binderHotelEditForm.getCategory().setItems(categoryList);
		return categoryList;
	}

}
