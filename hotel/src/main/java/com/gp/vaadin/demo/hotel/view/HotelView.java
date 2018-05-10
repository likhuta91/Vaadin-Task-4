package com.gp.vaadin.demo.hotel.view;

import java.util.List;
import java.util.Set;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Iterator;

import com.gp.vaadin.demo.hotel.CategoryService;
import com.gp.vaadin.demo.hotel.Hotel;
import com.gp.vaadin.demo.hotel.HotelEditForm;
import com.gp.vaadin.demo.hotel.HotelService;
import com.gp.vaadin.demo.hotel.HotelUI;
import com.gp.vaadin.demo.hotel.MenuNavigator;
import com.gp.vaadin.demo.hotel.PopupUpdateHotelContent;
import com.gp.vaadin.demo.hotel.helper.ButtonHelper;
import com.gp.vaadin.demo.hotel.helper.HotelHelper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;

public class HotelView extends VerticalLayout implements View {

	private static final long serialVersionUID = -530637194012821083L;

	final HotelUI ui;
	final MenuNavigator menuNavigator;

	final HotelService hotelService = HotelService.getInstance();
	final CategoryService categoryService = CategoryService.getInstance();

	final Grid<Hotel> hotelGrid = new Grid<>();
	final HotelEditForm editForm = new HotelEditForm(this);

	final TextField nameFilter = new TextField();
	final TextField addressFilter = new TextField();

	final Button addHotel = ButtonHelper.getAddButton();
	final Button deleteHotel = ButtonHelper.getDeleteButton();
	final Button editHotel = ButtonHelper.getEditButton();

	final VerticalLayout layout = new VerticalLayout();

	final PopupUpdateHotelContent popupContent = PopupUpdateHotelContent.getInstance(this);

	final PopupView popup = new PopupView(null, popupContent);
	final Button bulkUpdateHotel = ButtonHelper.getUpdateButton("Bulk Update");

	public HotelView(HotelUI ui) {

		this.ui = ui;
		this.menuNavigator = new MenuNavigator(ui);

		// grid
		hotelGrid.addColumn(Hotel::getName).setCaption(HotelHelper.NAME);
		hotelGrid.addColumn(Hotel::getAddress).setCaption(HotelHelper.ADDRESS);
		hotelGrid.addColumn(Hotel::getRating).setCaption(HotelHelper.RATING);
		hotelGrid.addColumn(hotel -> Instant.ofEpochMilli(new Date(hotel.getOperatesFrom()).getTime())
				.atZone(ZoneId.systemDefault()).toLocalDate()).setCaption(HotelHelper.OPERATES_FROM);
		hotelGrid.addColumn(Hotel::getCategoryName).setCaption(HotelHelper.CATEGORY);

		hotelGrid.addColumn(Hotel::getDescription).setCaption(HotelHelper.DESCRIPTION);
		Grid.Column<Hotel, String> htmlColumn = hotelGrid.addColumn(
				hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>hotel info</a>", new HtmlRenderer());
		htmlColumn.setCaption(HotelHelper.URL);

		hotelGrid.setWidth(100, Unit.PERCENTAGE);
		hotelGrid.setHeight("100%");

		hotelGrid.setSelectionMode(SelectionMode.MULTI);

		// controls
		HorizontalLayout controls = new HorizontalLayout();
		controls.addComponents(nameFilter, addressFilter, addHotel, deleteHotel, editHotel, bulkUpdateHotel, popup);

		// content
		HorizontalLayout content = new HorizontalLayout();
		content.addComponents(hotelGrid, editForm);
		content.setWidth("100%");
		content.setHeight(500, Unit.PIXELS);
		content.setExpandRatio(hotelGrid, 0.7f);
		content.setExpandRatio(editForm, 0.3f);
		content.setComponentAlignment(editForm, Alignment.MIDDLE_CENTER);

		layout.addComponents(menuNavigator.getMenuBar(), controls, content);

		// dimensions
		editForm.setWidth("80%");
		editForm.setHeight("100%");

		// listeners
		addHotel.addClickListener(e -> {
			editForm.setHotel(new Hotel());
			editForm.setVisible(true);
		});

		deleteHotel.addClickListener(e -> {
			Set<Hotel> delCandidate = hotelGrid.getSelectedItems();
			hotelService.delete(delCandidate);
			deleteHotel.setEnabled(false);
			editForm.setVisible(false);
			updateList();
		});

		editHotel.addClickListener(c -> {
			Iterator<Hotel> iterator = hotelGrid.getSelectedItems().iterator();
			editForm.setHotel(iterator.next());
			editForm.setVisible(true);
		});

		popup.setHideOnMouseOut(false);
		bulkUpdateHotel.setEnabled(false);

		bulkUpdateHotel.addClickListener(e -> {
			Set<Hotel> updateCandidate = hotelGrid.getSelectedItems();
			popupContent.setHotels(updateCandidate);
			popup.setPopupVisible(true);
		});

		hotelGrid.asMultiSelect().addValueChangeListener(e -> {
			if (e.getValue().size() == 1) {
				deleteHotel.setEnabled(true);
				editHotel.setEnabled(true);
				bulkUpdateHotel.setEnabled(false);
			} else if (e.getValue().size() > 1) {
				deleteHotel.setEnabled(true);
				bulkUpdateHotel.setEnabled(true);
				editHotel.setEnabled(false);
				editForm.setVisible(false);
			} else {
				deleteHotel.setEnabled(false);
				editHotel.setEnabled(false);
				bulkUpdateHotel.setEnabled(false);
			}
		});

		// filters
		nameFilter.setPlaceholder("Enter name");
		nameFilter.addValueChangeListener(e -> updateList());
		nameFilter.setValueChangeMode(ValueChangeMode.LAZY);

		addressFilter.setPlaceholder("Enter address");
		addressFilter.addValueChangeListener(e -> updateList());
		addressFilter.setValueChangeMode(ValueChangeMode.LAZY);

		controls.addComponents(nameFilter, addressFilter, addHotel, deleteHotel, editHotel, bulkUpdateHotel, popup);
		addComponent(layout);
	}

	public void updateList() {
		List<Hotel> hotelList = hotelService.findAll(nameFilter.getValue(), addressFilter.getValue());
		hotelGrid.setItems(hotelList);
		popup.setPopupVisible(false);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		updateList();
		editForm.setVisible(false);
		Notification.show("Welcome in hotel menu!");
	}

}
