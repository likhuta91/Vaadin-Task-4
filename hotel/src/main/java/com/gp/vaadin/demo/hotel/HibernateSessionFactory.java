package com.gp.vaadin.demo.hotel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateSessionFactory {
	private static SessionFactory sessionFactory = buildSessionFactory();

	protected static SessionFactory buildSessionFactory() {

		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();

			return sessionFactory;

		} catch (Throwable ex) {

			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}

	public static Session getHibernateSession() {

		final Session session = sessionFactory.openSession();
		return session;
	}
}
