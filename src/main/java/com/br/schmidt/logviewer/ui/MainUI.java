package com.br.schmidt.logviewer.ui;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import com.br.schmidt.logviewer.ui.property.TailFileProperty;
import com.br.schmidt.logviewer.ui.util.CustomFilesystemContainer;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
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

	private static class FilenameFilter implements java.io.FilenameFilter {
		@Override
		public boolean accept(final File dir, final String name) {
			final File file = new File(dir, name);
			return !file.isDirectory() && name.endsWith("log");
		}
	}

	public static final int DELAY = 2000;
	public static final int INITIAL_DELAY = 500;
	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
	private FilesystemContainer container;
	@Inject
	private I18N i18n;
	private ScheduledFuture<?> jobHandle;

	@Override
	protected void init(final VaadinRequest request) {
		//TODO Get file path from???
		container = new CustomFilesystemContainer(new File("."), new FilenameFilter(), true);

		HorizontalLayout layout = new HorizontalLayout() {
			{
				final Label fileView = new Label("", ContentMode.PREFORMATTED);
				final Panel contentPanel = new Panel("Content") {
					{
						setContent(fileView);
						setSizeFull();
					}
				};

				final Panel filesPanel = new Panel("Files") {
					{
						final Table fileList = new Table("Logs", container) {
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
		if (jobHandle != null) {
			jobHandle.cancel(true);
		}
		super.detach();
	}

	private void tail(final File file, final Label fileView, final Panel finalContentPanel, final Table fileList) {
		final TailFileProperty tailFileProperty = new TailFileProperty();
		fileView.setPropertyDataSource(tailFileProperty);

		TailerListener listener = new TailerListenerAdapter() {
			@Override
			public void handle(final String line) {
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
		};

		Tailer tailer = new Tailer(file, listener, DELAY, true);
		jobHandle = executorService.scheduleWithFixedDelay(tailer, INITIAL_DELAY, DELAY, TimeUnit.MILLISECONDS);
	}

}
