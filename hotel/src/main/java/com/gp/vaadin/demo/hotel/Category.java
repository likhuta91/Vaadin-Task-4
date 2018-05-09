package com.gp.vaadin.demo.hotel;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "CATEGORY")
public class Category implements Serializable, Cloneable {

	private static final long serialVersionUID = -6488058194245265804L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", insertable = true, updatable = false)
	private Long id;

	@OneToMany(orphanRemoval = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "category", targetEntity = Hotel.class)
	private List<Hotel> hotels;

	@Version
	@Column(name = "OPTLOCK", insertable = true, updatable = true)
	private Long version;

	@Column(name = "NAME", insertable = true, updatable = true)
	private String name;

	public List<Hotel> getHotels() {
		return hotels;
	}

	public void setHotels(List<Hotel> hotels) {
		this.hotels = hotels;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	protected Category clone() throws CloneNotSupportedException {
		return (Category) super.clone();
	}

	public Category(String name) {
		super();
		this.name = name;
	}

	public Category() {
	}

}
