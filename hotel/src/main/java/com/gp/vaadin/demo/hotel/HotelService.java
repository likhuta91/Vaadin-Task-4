package com.gp.vaadin.demo.hotel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class HotelService {

	private static HotelService instance;
	private static final Logger LOGGER = Logger.getLogger(HotelService.class.getName());
	private Session session = null;

	private HotelService() {
	}

	public static HotelService getInstance() {

		if (instance == null) {
			instance = new HotelService();
		}
		return instance;
	}

	public synchronized List<Hotel> findAll() {
		return findAll(null, null);
	}

	public synchronized List<Hotel> findAll(String nameFilterValue, String addressFilterValue) {

		session = HibernateSessionFactory.getHibernateSession();
		ArrayList<Hotel> arrayList = new ArrayList<>();
		List<Hotel> allHotel = null;

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Hotel> criteria = builder.createQuery(Hotel.class);
		criteria.from(Hotel.class);
		Query<Hotel> q = session.createQuery(criteria);

		allHotel = q.getResultList();

		for (Hotel hotel : allHotel) {

			boolean namePasses = passesFilter(hotel.getName(), nameFilterValue);
			boolean addessPpasses = passesFilter(hotel.getAddress(), addressFilterValue);

			if (namePasses && addessPpasses) {
				try {
					if (hotel.getCategory() != null) {
						hotel.setCategory(hotel.getCategory().clone());
					}
					arrayList.add(hotel.clone());

				} catch (CloneNotSupportedException ex) {
					LOGGER.log(Level.SEVERE, null, ex);
				}
			}
		}
		Collections.sort(arrayList, new Comparator<Hotel>() {

			@Override
			public int compare(Hotel o1, Hotel o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});

		if (session.isOpen()) {
			session.close();
		}
		return arrayList;
	}

	private boolean passesFilter(String filter, String value) {

		return value == null || value.isEmpty() || filter.toLowerCase().contains(value.toLowerCase());

	}

	public synchronized void delete(Set<Hotel> value) {

		session = HibernateSessionFactory.getHibernateSession();
		try {

			for (Hotel hotel : value) {
				session.beginTransaction();
				Hotel h = session.get(Hotel.class, hotel.getId());
				session.delete(h);
				session.flush();
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			session.getTransaction().rollback();
			LOGGER.log(Level.SEVERE, "Hotel is not delete." + e.getMessage());

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public synchronized void save(Hotel entry) {

		session = HibernateSessionFactory.getHibernateSession();
		if (entry == null) {
			LOGGER.log(Level.SEVERE, "Hotel is null.");
			return;
		}

		try {
			session.beginTransaction();
			session.merge(entry);
			session.flush();
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			LOGGER.log(Level.SEVERE, "Hotel is not save." + e.getMessage());

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

}
