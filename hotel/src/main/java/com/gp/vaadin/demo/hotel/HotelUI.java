package com.gp.vaadin.demo.hotel;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

import com.gp.vaadin.demo.hotel.view.CategoryView;
import com.gp.vaadin.demo.hotel.view.HotelView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SpringUI
@SuppressWarnings("serial")
@Theme("mytheme")
public class HotelUI extends UI {

	final HotelService hotelService = HotelService.getInstance();
	private Navigator navigator;

	final VerticalLayout layout = new VerticalLayout();

	public static final String HOTEL = "Hotel";
	public static final String CATEGORY = "Category";

	@WebListener
	public static class HotelContextLoaderListener extends ContextLoaderListener {
	}

	@Configuration
	@EnableVaadin
	public static class HotelConfiguration {

	}

	@Override
	protected void init(VaadinRequest vaadinRequest) {

		navigator = new Navigator(this, this);

		HotelView hotelView = new HotelView(this);
		CategoryView categoryView = new CategoryView(this);

		navigator.addView("", hotelView);
		navigator.addView(CATEGORY, categoryView);
		navigator.addView(HOTEL, hotelView);

	}

	@WebServlet(urlPatterns = "/*", name = "HotelUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = HotelUI.class, productionMode = false)
	public static class HotelUIServlet extends SpringVaadinServlet {

	}

}
