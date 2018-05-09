package com.gp.vaadin.demo.hotel.view;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.gp.vaadin.demo.hotel.Category;
import com.gp.vaadin.demo.hotel.CategoryEditForm;
import com.gp.vaadin.demo.hotel.CategoryService;
import com.gp.vaadin.demo.hotel.HotelUI;
import com.gp.vaadin.demo.hotel.MenuNavigator;
import com.gp.vaadin.demo.hotel.helper.ButtonHelper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

public class CategoryView extends VerticalLayout implements View {

	private static final long serialVersionUID = 4968100371210880557L;

	final HotelUI ui;
	final MenuNavigator menuNavigator;

	final CategoryService categoryService = CategoryService.getInstance();

	final Grid<Category> categoryGrid = new Grid<>();
	final CategoryEditForm editForm = new CategoryEditForm(this);

	final Button addCategory = ButtonHelper.getAddButton("Add category");
	final Button deleteCategory = ButtonHelper.getDeleteButton("Delete category");
	final Button editCategory = ButtonHelper.getEditButton("Edit category");

	final VerticalLayout layout = new VerticalLayout();

	public CategoryView(HotelUI ui) {

		this.ui = ui;
		this.menuNavigator = new MenuNavigator(ui);

		// grid
		categoryGrid.addColumn(Category::getName).setCaption("Name");

		categoryGrid.setSelectionMode(SelectionMode.MULTI);

		categoryGrid.setWidth(500, Unit.PIXELS);
		categoryGrid.setHeightMode(HeightMode.UNDEFINED);

		// controls
		HorizontalLayout controls = new HorizontalLayout();
		controls.addComponents(addCategory, deleteCategory, editCategory);

		// content
		HorizontalLayout content = new HorizontalLayout();
		content.addComponents(categoryGrid, editForm);
		content.setComponentAlignment(editForm, Alignment.TOP_CENTER);

		layout.addComponents(menuNavigator.getMenuBar(), controls, content);

		// dimensions
		editForm.setWidth(400, Unit.PIXELS);

		// listeners
		addCategory.addClickListener(e -> {
			editForm.setCategory(new Category());
			editForm.setVisible(true);
		});

		deleteCategory.addClickListener(e -> {
			Set<Category> delCandidate = categoryGrid.getSelectedItems();
			categoryService.delete(delCandidate);
			deleteCategory.setEnabled(false);
			editForm.setVisible(false);
			updateList();
		});

		editCategory.addClickListener(c -> {
			Iterator<Category> iterator = categoryGrid.getSelectedItems().iterator();
			editForm.setCategory(iterator.next());
			editForm.setVisible(true);
		});

		categoryGrid.asMultiSelect().addValueChangeListener(e -> {
			if (e.getValue().size() == 1) {
				deleteCategory.setEnabled(true);
				editCategory.setEnabled(true);
			} else if (e.getValue().size() > 1) {
				deleteCategory.setEnabled(true);
				editCategory.setEnabled(false);
				editForm.setVisible(false);
			} else {
				deleteCategory.setEnabled(false);
				editCategory.setEnabled(false);
			}
		});

		addComponent(layout);
	}

	public void updateList() {
		List<Category> categoryList = categoryService.findAll();
		categoryGrid.setItems(categoryList);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		updateList();
		editForm.setVisible(false);
		Notification.show("Welcome in category menu!");
	}

}