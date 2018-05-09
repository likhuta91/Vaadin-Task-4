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

public class CategoryService {

	private static CategoryService instance;
	private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
	private Session session = null;

	private CategoryService() {
	}

	public static CategoryService getInstance() {

		if (instance == null) {
			instance = new CategoryService();
		}
		return instance;
	}

	public synchronized List<Category> findAll() {

		session = HibernateSessionFactory.getHibernateSession();
		ArrayList<Category> arrayList = new ArrayList<>();
		List<Category> allCategory = null;

		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
		criteria.from(Category.class);
		allCategory = session.createQuery(criteria).getResultList();

		for (Category category : allCategory) {
			try {
				arrayList.add(category.clone());
			} catch (CloneNotSupportedException ex) {
				LOGGER.log(Level.SEVERE, null, ex);
			}
		}
		Collections.sort(arrayList, new Comparator<Category>() {

			@Override
			public int compare(Category o1, Category o2) {
				return (int) (o2.getId() - o1.getId());
			}
		});

		if (session.isOpen()) {
			session.close();
		}

		return arrayList;
	}

	public synchronized void delete(Set<Category> value) {

		try {

			session = HibernateSessionFactory.getHibernateSession();

			for (Category category : value) {
				deleteCategoryFromHotel(category);

				session.beginTransaction();
				Category c = session.get(Category.class, category.getId());
				session.delete(c);
				session.flush();
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			session.getTransaction().rollback();
			LOGGER.log(Level.SEVERE, "Category is not delete." + e.getMessage());

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
	}

	public synchronized void deleteCategoryFromHotel(Category category) {

		try {

			category = session.get(Category.class, category.getId());

			for (Hotel hotel : category.getHotels()) {

				session.beginTransaction();
				hotel.setCategory(null);
				session.merge(hotel);
				session.flush();
				session.getTransaction().commit();
			}

		} catch (Exception e) {
			session.getTransaction().rollback();
			LOGGER.log(Level.SEVERE, "Category in hotel is not delete." + e.getMessage());
		}
	}

	public synchronized void save(Category entry) {

		session = HibernateSessionFactory.getHibernateSession();

		if (entry == null) {
			LOGGER.log(Level.SEVERE, "Category is null.");
			return;
		}

		try {
			session.beginTransaction();
			session.saveOrUpdate(entry);
			session.flush();
			session.getTransaction().commit();

		} catch (Exception e) {
			session.getTransaction().rollback();
			LOGGER.log(Level.SEVERE, "Category is not save." + e.getMessage());

		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}

	}

}
