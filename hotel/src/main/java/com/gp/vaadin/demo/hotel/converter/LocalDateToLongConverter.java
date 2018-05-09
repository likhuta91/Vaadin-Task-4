package com.gp.vaadin.demo.hotel.converter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

@SuppressWarnings("serial")
public class LocalDateToLongConverter implements Converter<LocalDate, Long> {

	@Override
	public Result<Long> convertToModel(LocalDate value, ValueContext context) {
		if (value == null) {
			return Result.ok(null);
		}

		return Result.ok(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime());
	}

	@Override
	public LocalDate convertToPresentation(Long value, ValueContext context) {
		if (value == null) {
			return null;
		}

		return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate();
	}

}
