package com.gp.vaadin.demo.hotel;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "HOTEL")
public class Hotel implements Serializable, Cloneable {

	private static final long serialVersionUID = 7853489838650889748L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = false)
	private Long id;

	@Version
	@Column(name = "OPTLOCK", insertable = true, updatable = true)
	private Long version;

	@Column(name = "NAME", insertable = true, updatable = true)
	private String name;

	@Column(name = "ADDRESS", insertable = true, updatable = true)
	private String address;

	@Column(name = "RATING", insertable = true, updatable = true)
	private int rating;

	@Column(name = "OPERATES_FROM", insertable = true, updatable = true)
	private long operatesFrom;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, targetEntity = Category.class, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "CATEGORY", insertable = true, updatable = true)
	private Category category;

	@Column(name = "DESCRIPTION", insertable = true, updatable = true)
	private String description;

	@Column(name = "URL", insertable = true, updatable = true)
	private String url;

	public boolean isPersisted() {
		return id != null;
	}

	@Override
	public String toString() {
		return name + " " + rating + "stars " + address;
	}

	@Override
	protected Hotel clone() throws CloneNotSupportedException {
		return (Hotel) super.clone();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public long getOperatesFrom() {
		return operatesFrom;
	}

	public void setOperatesFrom(long operatesFrom) {
		this.operatesFrom = operatesFrom;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public String getCategoryName() {
		if (category != null) {
			return category.getName();
		} else {
			return "No category";
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Hotel(String name, String address, int rating, long operatesFrom, Category category, String url,
			String description) {
		super();
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.operatesFrom = operatesFrom;
		this.category = category;
		this.description = description;
		this.url = url;
	}

	public Hotel() {
		this("", "", 0, 0, null, "", "");
	}

}
