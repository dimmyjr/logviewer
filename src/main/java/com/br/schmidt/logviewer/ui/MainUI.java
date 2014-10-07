package com.br.schmidt.logviewer.ui;

import java.io.File;

import javax.inject.Inject;

import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.data.util.TextFileProperty;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
@Title("LogViewer")
public class MainUI extends UI {

	private static class FilenameFilter implements java.io.FilenameFilter {
		@Override
		public boolean accept(final File dir, final String name) {
			final File file = new File(dir, name);
			return !file.isDirectory() && name.endsWith("log");
		}
	}

	private FilesystemContainer container;
	private Table fileList;
	private Label fileView;

	@Inject
	private I18N i18n;

	@Override
	protected void init(final VaadinRequest request) {
		//TODO Get file path from???
		container = new FilesystemContainer(new File("."), new FilenameFilter(), true);

		fileList = new Table("Logs", container);
		fileView = new Label("", ContentMode.PREFORMATTED);

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		setContent(splitPanel);
		splitPanel.addComponent(fileList);
		splitPanel.addComponent(fileView);

		fileList.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				fileView.setPropertyDataSource(new TextFileProperty((File) event.getProperty().getValue()));
			}
		});

		fileList.setImmediate(true);
		fileList.setSelectable(true);
	}

}
