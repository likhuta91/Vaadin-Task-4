package com.gp.vaadin.demo.hotel;

import java.time.LocalDate;
import java.util.List;

import com.gp.vaadin.demo.hotel.converter.LocalDateToLongConverter;
import com.gp.vaadin.demo.hotel.helper.ButtonHelper;
import com.gp.vaadin.demo.hotel.view.HotelView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Notification.Type;

public class HotelEditForm extends FormLayout {

	private static final long serialVersionUID = -4322879393284784711L;
	private HotelView hotelView;
	private HotelService hotelService = HotelService.getInstance();
	private CategoryService categoryService = CategoryService.getInstance();
	private Hotel hotel;

	private Binder<Hotel> binder = new Binder<>(Hotel.class);

	private TextField name = new TextField("Name");
	private TextField address = new TextField("Address");
	private TextField rating = new TextField("Rating");
	private DateField operatesFrom = new DateField("Date");
	private NativeSelect<Category> category = new NativeSelect<>("Category");
	private TextArea description = new TextArea("Description");
	private TextField url = new TextField("URL");

	private Button save = ButtonHelper.getSaveButton("Save");
	private Button close = ButtonHelper.getCloseButton("Close");

	@SuppressWarnings("serial")
	public HotelEditForm(HotelView hotelView) {

		// init
		this.hotelView = hotelView;

		operatesFrom.setDefaultValue(LocalDate.now());

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(save, close);
		addComponents(name, address, rating, operatesFrom, category, url, description, buttons);

		name.setWidth(100, Sizeable.Unit.PERCENTAGE);
		address.setWidth(100, Sizeable.Unit.PERCENTAGE);
		rating.setWidth(100, Sizeable.Unit.PERCENTAGE);
		operatesFrom.setWidth(100, Sizeable.Unit.PERCENTAGE);
		category.setWidth(100, Sizeable.Unit.PERCENTAGE);
		description.setWidth(100, Sizeable.Unit.PERCENTAGE);
		url.setWidth(100, Sizeable.Unit.PERCENTAGE);

		buttons.setWidth(100, Sizeable.Unit.PERCENTAGE);

		name.setDescription("Hotel name");
		address.setDescription("Hotel address");
		rating.setDescription("Hotel rating from 1 to 5");
		operatesFrom.setDescription("Hotel opening date");
		category.setDescription("Hotel category");
		description.setDescription("Hotel description");
		url.setDescription("Website address of the hotel");

		binder.forField(name).asRequired("Please enter a name").bind(Hotel::getName, Hotel::setName);
		binder.forField(address).asRequired("Please enter a address").bind(Hotel::getAddress, Hotel::setAddress);

		binder.forField(rating).asRequired("Please enter a rating").withValidator(new Validator<String>() {

			@Override
			public ValidationResult apply(String value, ValueContext context) {
				try {
					int result = Integer.parseInt(value);
					if (result >= 0 && result <= 5) {
						return ValidationResult.ok();
					} else {
						return ValidationResult.error("Enter an integer from 0 to 5");
					}
				} catch (NumberFormatException e) {
					return ValidationResult.error("Enter an integer from 0 to 5");
				}
			}
		}).withConverter(new StringToIntegerConverter("Invalid format")).bind(Hotel::getRating, Hotel::setRating);

		binder.forField(operatesFrom).asRequired("Please enter the opening date")
				.withValidator(new Validator<LocalDate>() {

					@Override
					public ValidationResult apply(LocalDate verifiedTime, ValueContext context) {
						LocalDate currentTime = LocalDate.now();

						if (verifiedTime.compareTo(currentTime) < 0) {
							return ValidationResult.ok();
						} else {
							return ValidationResult.error("Invalid date");
						}
					}
				}).withConverter(new LocalDateToLongConverter()).bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);
		binder.forField(category).asRequired("Please enter a category").bind(Hotel::getCategory, Hotel::setCategory);
		binder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);
		binder.forField(url).asRequired("Please enter a website address").bind(Hotel::getUrl, Hotel::setUrl);

		category.setItems(categoryService.findAll());

		save.addClickListener(e -> save());
		close.addClickListener(e -> setVisible(false));
	}

	private void save() {
		if (binder.isValid()) {
			try {
				binder.writeBean(hotel);
			} catch (ValidationException e) {
				Notification.show("Unable to save!" + e.getMessage(), Type.HUMANIZED_MESSAGE);
			}
			hotelService.save(hotel);
			exit();
		} else {
			Notification.show("Unable to save! Please review errors and fix them.", Type.ERROR_MESSAGE);
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
		this.category = category;
	}

	public NativeSelect<Category> getCategory() {
		return category;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		setSelectedItemCategoryComponent(updateItemsCategoryComponent());
		binder.readBean(this.hotel);
	}

	public void setSelectedItemCategoryComponent(List<Category> categoryList) {
		int indexSelectedCategory = -1;
		for (int i = 0; i < categoryList.size(); i++) {

			if (this.hotel.getCategory() != null && categoryList.get(i).getId() == this.hotel.getCategory().getId()) {
				indexSelectedCategory = i;
			}
		}
		if (indexSelectedCategory != -1) {
			category.setSelectedItem(categoryList.get(indexSelectedCategory));
		} else {
			category.setSelectedItem(null);
		}
	}

	public List<Category> updateItemsCategoryComponent() {
		List<Category> categoryList = categoryService.findAll();
		category.setItems(categoryList);
		return categoryList;
	}

}
