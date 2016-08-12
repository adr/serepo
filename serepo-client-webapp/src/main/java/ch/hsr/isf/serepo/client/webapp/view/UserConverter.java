package ch.hsr.isf.serepo.client.webapp.view;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

import ch.hsr.isf.serepo.data.restinterface.common.User;

public class UserConverter implements Converter<String, User> {

	private static final long serialVersionUID = -3889869215572916741L;

	@Override
	public User convertToModel(String value, Class<? extends User> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		throw new ConversionException(String.format("Converstion from %s to %s is currently not supported!",
				getPresentationType().getName(), getModelType().getName()));
	}

	@Override
	public String convertToPresentation(User value, Class<? extends String> targetType, Locale locale)
			throws com.vaadin.data.util.converter.Converter.ConversionException {
		String convertedValue = null;
		if (value != null) {
			convertedValue = String.format("%s <%s>", value.getName(), value.getEmail());
		} else {
			convertedValue = "";
		}
		return convertedValue;
	}

	@Override
	public Class<User> getModelType() {
		return User.class;
	}

	@Override
	public Class<String> getPresentationType() {
		return String.class;
	}

}
