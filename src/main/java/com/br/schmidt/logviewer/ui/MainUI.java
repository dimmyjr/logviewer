package com.br.schmidt.logviewer.ui;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import com.br.schmidt.logviewer.common.Callback;
import com.br.schmidt.logviewer.common.io.filter.LogFilter;
import com.br.schmidt.logviewer.model.Tail;
import com.br.schmidt.logviewer.service.TailService;
import com.br.schmidt.logviewer.ui.property.TailFileProperty;
import com.br.schmidt.logviewer.ui.util.CustomFilesystemContainer;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
@Push
@Title("LogViewer")
public class MainUI extends UI {

	private CustomFilesystemContainer container;
	private Tail tail;

	@Autowired
	private I18N i18n;

	@Autowired
	private TailService tailService;

	@Override
	protected void init(final VaadinRequest request) {
		//TODO Get file path from???
		container = new CustomFilesystemContainer(new File("."), new LogFilter(), true);

		HorizontalLayout layout = new HorizontalLayout() {
			{
				final Label fileView = new Label("", ContentMode.PREFORMATTED);
				final Panel contentPanel = new Panel(i18n.get("label.content")) {
					{
						setContent(fileView);
						setSizeFull();
					}
				};

				final Panel filesPanel = new Panel(i18n.get("label.files")) {
					{
						final Table fileList = new Table(i18n.get("label.logs"), container) {
							{
								setSizeFull();
								setImmediate(true);
								setSelectable(true);
							}
						};
						fileList.addValueChangeListener(new Property.ValueChangeListener() {
							@Override
							public void valueChange(Property.ValueChangeEvent event) {
								final File file = (File) event.getProperty().getValue();
								tail(file, fileView, contentPanel, fileList);
							}
						});
						setContent(fileList);
						setWidth("300px");
					}
				};

				addComponent(filesPanel);
				addComponent(contentPanel);
				setExpandRatio(contentPanel, 1.0f);
				setSizeFull();
			}
		};

		setContent(layout);
	}

	@Override
	public void detach() {
		tailService.stopTail(tail);
		super.detach();
	}

	private void tail(final File file, final Label fileView, final Panel finalContentPanel, final Table fileList) {
		final TailFileProperty tailFileProperty = new TailFileProperty();
		fileView.setPropertyDataSource(tailFileProperty);

		tailService.stopTail(tail);

		tail = tailService.startTail(file, new Callback<String>() {
			@Override
			public void execute(final String line) {
				access(new Runnable() {
					@Override
					public void run() {
						tailFileProperty.setValue(line + "\n");
						finalContentPanel.setScrollTop(1000000);
						finalContentPanel.markAsDirty();
						fileList.refreshRowCache();
					}
				});
			}
		});

	}

}
