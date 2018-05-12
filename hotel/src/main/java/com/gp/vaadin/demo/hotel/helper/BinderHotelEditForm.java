package com.gp.vaadin.demo.hotel.helper;

import java.time.LocalDate;

import com.gp.vaadin.demo.hotel.Category;
import com.gp.vaadin.demo.hotel.CategoryService;
import com.gp.vaadin.demo.hotel.Hotel;
import com.gp.vaadin.demo.hotel.HotelService;
import com.gp.vaadin.demo.hotel.converter.LocalDateToLongConverter;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.data.ValueContext;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class BinderHotelEditForm {

	final CategoryService categoryService = CategoryService.getInstance();
	final HotelService hotelService = HotelService.getInstance();

	private Binder<Hotel> binder = new Binder<>(Hotel.class);
	private TextField name = new TextField();
	private TextField address = new TextField();
	private TextField rating = new TextField();
	private DateField operatesFrom = new DateField();
	private NativeSelect<Category> category = new NativeSelect<>();
	private TextArea description = new TextArea();
	private TextField url = new TextField();

	@SuppressWarnings("serial")
	public BinderHotelEditForm(boolean withCaption) {

		name.setWidth(100, Sizeable.Unit.PERCENTAGE);
		address.setWidth(100, Sizeable.Unit.PERCENTAGE);
		rating.setWidth(100, Sizeable.Unit.PERCENTAGE);
		operatesFrom.setWidth(100, Sizeable.Unit.PERCENTAGE);
		category.setWidth(100, Sizeable.Unit.PERCENTAGE);
		description.setWidth(100, Sizeable.Unit.PERCENTAGE);
		url.setWidth(100, Sizeable.Unit.PERCENTAGE);

		name.setDescription("Hotel name");
		address.setDescription("Hotel address");
		rating.setDescription("Hotel rating from 1 to 5");
		operatesFrom.setDescription("Hotel opening date");
		category.setDescription("Hotel category");
		description.setDescription("Hotel description");
		url.setDescription("Website address of the hotel");

		if (withCaption) {
			name.setCaption("Name");
			address.setCaption("Address");
			rating.setCaption("Rating");
			operatesFrom.setCaption("Date");
			category.setCaption("Category");
			description.setCaption("Description");
			url.setCaption("URL");
		}

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

	}

	public Binder<Hotel> getBinder() {
		return binder;
	}

	public void setBinder(Binder<Hotel> binder) {
		this.binder = binder;
	}

	public TextField getName() {
		return name;
	}

	public void setName(TextField name) {
		this.name = name;
	}

	public TextField getAddress() {
		return address;
	}

	public void setAddress(TextField address) {
		this.address = address;
	}

	public TextField getRating() {
		return rating;
	}

	public void setRating(TextField rating) {
		this.rating = rating;
	}

	public DateField getOperatesFrom() {
		return operatesFrom;
	}

	public void setOperatesFrom(DateField operatesFrom) {
		this.operatesFrom = operatesFrom;
	}

	public NativeSelect<Category> getCategory() {
		return category;
	}

	public void setCategory(NativeSelect<Category> category) {
		this.category = category;
	}

	public TextArea getDescription() {
		return description;
	}

	public void setDescription(TextArea description) {
		this.description = description;
	}

	public TextField getUrl() {
		return url;
	}

	public void setUrl(TextField url) {
		this.url = url;
	}

}
