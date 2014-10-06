package com.br.schmidt.logviewer.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
public class MyVaadinUI extends UI {

	@Autowired
	private I18N i18n;

	@Override
	protected void init(final VaadinRequest request) {
		setContent(new Label(i18n.get("label.hello")));
	}

}
