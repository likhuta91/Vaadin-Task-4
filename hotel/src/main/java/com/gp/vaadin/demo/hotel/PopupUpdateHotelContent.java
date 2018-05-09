package com.gp.vaadin.demo.hotel;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;

public class PopupUpdateHotelContent extends VerticalLayout {

	final HotelService hotelService = HotelService.getInstance();
	final CategoryService categoryService = CategoryService.getInstance();
	final HotelView hotelView;

	private static final long serialVersionUID = 1L;
	final VerticalLayout layout = new VerticalLayout();
	final List<String> editableField = new ArrayList<>(
			Arrays.asList("Name", "Address", "Rating", "Operates From", "Category", "Description", "Url"));
	private Set<Hotel> hotels;
	private ComboBox<String> fieldBox;
	private Object valueTextField;

	private Binder<Hotel> binder = new Binder<>(Hotel.class);

	private TextField name = new TextField();
	private TextField address = new TextField();
	private TextField rating = new TextField();
	private DateField operatesFrom = new DateField();
	private NativeSelect<Category> category = new NativeSelect<>();
	private TextArea description = new TextArea();
	private TextField url = new TextField();

	final Button update = ButtonHelper.getUpdateButton("Update");
	final Button cancel = ButtonHelper.getCloseButton("Cancel");

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

		name.setWidth(100, Sizeable.Unit.PERCENTAGE);
		address.setWidth(100, Sizeable.Unit.PERCENTAGE);
		rating.setWidth(100, Sizeable.Unit.PERCENTAGE);
		operatesFrom.setWidth(100, Sizeable.Unit.PERCENTAGE);
		category.setWidth(100, Sizeable.Unit.PERCENTAGE);
		description.setWidth(100, Sizeable.Unit.PERCENTAGE);
		url.setWidth(100, Sizeable.Unit.PERCENTAGE);

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

		fieldBox = new ComboBox<>(null, this.editableField);
		fieldBox.setCaption("Please select field");
		fieldBox.setWidth(100, Sizeable.Unit.PERCENTAGE);

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponents(update, cancel);
		layout.addComponents(fieldBox, name, buttons);
		addComponent(layout);

		update.addClickListener(e -> {

			for (Hotel hotel : hotels) {				
				String nameField = fieldBox.getValue().toLowerCase();
				Field f = null;
				Hotel h =null;
				try {
					h =hotel.clone();
					
					if (nameField.equals("rating")) {
						f = h.getClass().getDeclaredField("rating");
						f.setAccessible(true);
						f.set(h, Integer.parseInt(valueTextField.toString()));
					} else if (nameField.equals("category")) {
						f = h.getClass().getDeclaredField("category");
						f.setAccessible(true);
						f.set(h, (Category) valueTextField);
					} else if (nameField.equals("operates from")) {
						f = h.getClass().getDeclaredField("operatesFrom");
						f.setAccessible(true);
						f.set(h, Date.from(operatesFrom.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant())
								.getTime());
					} else {
						f = h.getClass().getDeclaredField(nameField);
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

				binder.readBean(h);
				if (binder.isValid()) {
					try {
						binder.writeBean(h);
					} catch (ValidationException e1) {
						e1.printStackTrace();
					}
					hotelService.save(h);
					hotelview.updateList();
				} else {
					System.out.println(h.toString()+"toString");
					  Notification.show("Unable to save! Please review errors and fix them.",
							  Type.WARNING_MESSAGE); }

			}
			
		});

		cancel.addClickListener(e -> {
			hotelview.updateList();
		});

		fieldBox.addValueChangeListener(e -> {

			layout.removeAllComponents();

			if (e.getValue().equals("Name")) {
				layout.addComponents(fieldBox, name, buttons);
			} else if (e.getValue().equals("Operates From")) {
				layout.addComponents(fieldBox, operatesFrom, buttons);
			} else if (e.getValue().equals("Address")) {
				layout.addComponents(fieldBox, address, buttons);
			} else if (e.getValue().equals("Rating")) {
				layout.addComponents(fieldBox, rating, buttons);
			} else if (e.getValue().equals("Category")) {
				category.setItems(categoryService.findAll());
				layout.addComponents(fieldBox, category, buttons);
			} else if (e.getValue().equals("Description")) {
				layout.addComponents(fieldBox, description, buttons);
			} else if (e.getValue().equals("Url")) {
				layout.addComponents(fieldBox, url, buttons);
			}
			addComponent(layout);
		});

		binder.addValueChangeListener(e -> {
			valueTextField = e.getValue();
			System.out.println(e.getValue() + " -------------e.getValue();");
		});
	}

	/*private void save() { 
	 * if (binder.isValid()) {
	 *  try { //
	  binder.writeBean(hotel); 
	  } catch (ValidationException e) {
	  Notification.show("Unable to save!" + e.getMessage(),
	  Type.HUMANIZED_MESSAGE); 
	  } 
	  hotelService.save(hotel);
	   exit(); 
	   } else {
	  Notification.show("Unable to save! Please review errors and fix them.",
	  Type.ERROR_MESSAGE); }
	  
	  }*/

	/*
	 * final HotelService hotelService = HotelService.getInstance(); final
	 * CategoryService categoryService = CategoryService.getInstance(); final
	 * HotelView hotelView;
	 * 
	 * private static final long serialVersionUID = 1L; final VerticalLayout layout
	 * = new VerticalLayout(); final List<String> editableField = new ArrayList<>(
	 * Arrays.asList("Name", "Address", "Rating", "Operates From", "Category",
	 * "Description")); private Set<Hotel> hotels; private List<Category> category;
	 * 
	 * private ComboBox<String> fieldBox; private TextField textField = new
	 * TextField(); private String valueTextField; private NativeSelect<Category>
	 * categoryNativeSelect = new NativeSelect<>(); private DateField operatesFrom =
	 * new DateField();
	 * 
	 * final Button update = ButtonHelper.getUpdateButton("Update"); final Button
	 * cancel = ButtonHelper.getCloseButton("Cancel");
	 * 
	 * private static PopupUpdateHotelContent instance;
	 * 
	 * public static PopupUpdateHotelContent getInstance(HotelView hotelview) {
	 * 
	 * if (instance == null) { instance = new PopupUpdateHotelContent(hotelview); }
	 * return instance; }
	 * 
	 * public void setHotels(Set<Hotel> hotels) { this.hotels = hotels; }
	 * 
	 * private PopupUpdateHotelContent(HotelView hotelview) { this.hotelView =
	 * hotelview;
	 * 
	 * fieldBox = new ComboBox<>(null, this.editableField);
	 * fieldBox.setCaption("Please select field"); textField.setWidth(100,
	 * Sizeable.Unit.PERCENTAGE); fieldBox.setWidth(100, Sizeable.Unit.PERCENTAGE);
	 * categoryNativeSelect.setWidth(100, Sizeable.Unit.PERCENTAGE);
	 * operatesFrom.setWidth(100, Sizeable.Unit.PERCENTAGE);
	 * 
	 * HorizontalLayout buttons = new HorizontalLayout();
	 * buttons.addComponents(update, cancel); layout.addComponents(fieldBox,
	 * textField, buttons); addComponent(layout);
	 * 
	 * textField.addValueChangeListener(e -> { valueTextField = e.getValue(); });
	 * 
	 * update.addClickListener(e -> { for (Hotel hotel : hotels) { String nameField
	 * = fieldBox.getValue().toLowerCase(); Field f = null;
	 * 
	 * try { if (nameField.equals("rating")) { f =
	 * hotel.getClass().getDeclaredField("rating"); f.setAccessible(true);
	 * f.set(hotel, Integer.parseInt(valueTextField)); } else
	 * if(nameField.equals("category")){ f =
	 * hotel.getClass().getDeclaredField("category"); f.setAccessible(true);
	 * f.set(hotel, categoryNativeSelect.getSelectedItem().get()); } else
	 * if(nameField.equals("operates from")){ f =
	 * hotel.getClass().getDeclaredField("operatesFrom"); f.setAccessible(true);
	 * f.set(hotel,
	 * Date.from(operatesFrom.getValue().atStartOfDay(ZoneId.systemDefault()).
	 * toInstant()).getTime()); } else { f =
	 * hotel.getClass().getDeclaredField(nameField); f.setAccessible(true);
	 * f.set(hotel, valueTextField); }
	 * 
	 * } catch (NoSuchFieldException | SecurityException e1) { e1.printStackTrace();
	 * } catch (IllegalArgumentException e1) { e1.printStackTrace(); } catch
	 * (IllegalAccessException e1) { e1.printStackTrace(); } catch
	 * (IndexOutOfBoundsException e1) { e1.printStackTrace(); }
	 * 
	 * hotelService.save(hotel); }
	 * 
	 * hotelview.updateList();
	 * 
	 * });
	 * 
	 * cancel.addClickListener(e -> { hotelview.updateList(); });
	 * 
	 * fieldBox.addValueChangeListener(e -> {
	 * 
	 * layout.removeAllComponents();
	 * 
	 * if (e.getValue().equals("Operates From")) { layout.addComponents(fieldBox,
	 * operatesFrom, buttons); } else if (e.getValue().equals("Category")) {
	 * category = categoryService.findAll();
	 * categoryNativeSelect.setItems(category); layout.addComponents(fieldBox,
	 * categoryNativeSelect, buttons); } else { layout.addComponents(fieldBox,
	 * textField, buttons); } addComponent(layout); }); }
	 */

}
