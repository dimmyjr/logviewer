package com.br.schmidt.logviewer.ui;

import java.io.File;

import javax.inject.Inject;

import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.annotations.Title;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
@Title("LogViewer")
public class MainUI extends UI {

	@Inject
	private I18N i18n;

	@Override
	protected void init(final VaadinRequest request) {
		HorizontalLayout layout = new HorizontalLayout();
		setContent(layout);
		//setContent(new Label(i18n.get("label.hello")));

		FilesystemContainer container = new FilesystemContainer(new File("/DevTools-v6_x64/var/was7_profile/Dev/logs"),
				"log", true);
		Tree tree = new Tree("Files", container);
		ListSelect listSelect = new ListSelect("Files", container);

		layout.addComponent(tree);
		layout.addComponent(listSelect);
	}

}
