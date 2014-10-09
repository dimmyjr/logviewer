package com.br.schmidt.logviewer.ui.property;

import com.vaadin.data.util.AbstractProperty;

/**
 * @author Diego Schmidt
 * @since 07/10/2014
 */
public class TailFileProperty extends AbstractProperty<String> {

	private StringBuilder buffer = new StringBuilder();

	@Override
	public String getValue() {
		return buffer.toString();
	}

	@Override
	public void setValue(final String newValue) throws ReadOnlyException {
		buffer.append(newValue);
		fireValueChange();
	}

	@Override
	public Class<? extends String> getType() {
		return String.class;
	}

	public void clear() {
		buffer.setLength(0);
		fireValueChange();
	}
}
