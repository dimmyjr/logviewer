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
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
@Push
@Title("LogViewer")
public class MainUI extends UI {

	public static final int SCROLL_TOP = 1000000;
	public static final int NUMBER_OF_LAST_LINES = 300;
	private boolean autoScroll;
	private CheckBox autoScrollCheckbox;
	private Button.ClickListener clickListener;
	private CustomFilesystemContainer container;
	private Button startStopButton;
	private Tail tail;

	@Autowired
	private I18N i18n;
	private TailFileProperty tailFileProperty;

	@Autowired
	private TailService tailService;

	@Override
	protected void init(final VaadinRequest request) {
		//TODO Get file path from???
		container = new CustomFilesystemContainer(new File("."), new LogFilter(), true);

		HorizontalLayout layout = new HorizontalLayout() {
			{
				final Label fileView = new Label("", ContentMode.PREFORMATTED);
				final Panel contentPanel = new Panel() {
					{
						setContent(fileView);
						setSizeFull();
					}
				};

				final Panel filesPanel = new Panel() {
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
				final VerticalLayout contentLayout = new VerticalLayout() {
					{
						addComponent(new Panel() {
							{
								addComponent(new HorizontalLayout() {
									{
										setSpacing(true);
										startStopButton = new Button("") {
											{
												setVisible(false);
											}
										};
										addComponent(startStopButton);
										addComponent(new Button(i18n.get("label.button.clear")) {
											{
												addClickListener(new ClickListener() {
													@Override
													public void buttonClick(final ClickEvent event) {
														clear();
													}
												});
											}
										});
										autoScrollCheckbox = new CheckBox(i18n.get("label.auto_scroll"), true) {
											{
												addValueChangeListener(new ValueChangeListener() {
													@Override
													public void valueChange(final Property.ValueChangeEvent event) {
														autoScroll((Boolean) event.getProperty().getValue());
													}
												});
											}
										};
										addComponent(autoScrollCheckbox);
									}
								});
							}
						});
						addComponent(contentPanel);
						setExpandRatio(contentPanel, 1.0f);
						setSizeFull();
					}
				};
				addComponent(contentLayout);
				setExpandRatio(contentLayout, 1.0f);
				setSizeFull();
			}
		};

		setContent(layout);
	}

	private void autoScroll(final boolean value) {
		autoScroll = value;
	}

	private void clear() {
		tailFileProperty.clear();
	}

	@Override
	public void detach() {
		tailService.stopTail(tail);
		super.detach();
	}

	private void tail(final File file, final Label fileView, final Panel finalContentPanel, final Table fileList) {
		autoScroll(true);
		autoScrollCheckbox.setValue(true);

		tailFileProperty = new TailFileProperty();
		fileView.setPropertyDataSource(tailFileProperty);

		tailService.stopTail(tail);

		startStopButton.setVisible(true);
		startStopButton.setCaption(i18n.get("label.button.stop"));

		if (clickListener != null) {
			startStopButton.removeClickListener(clickListener);
		}
		clickListener = new Button.ClickListener() {
			@Override
			public void buttonClick(final Button.ClickEvent event) {
				startStopTail(file, tailFileProperty, finalContentPanel, fileList);
			}
		};
		startStopButton.addClickListener(clickListener);
		startStopTail(file, tailFileProperty, finalContentPanel, fileList);
	}

	private void startStopTail(final File file, final TailFileProperty tailFileProperty, final Panel finalContentPanel,
			final Table fileList) {
		if (tail != null && tail.isStarted()) {
			stopTail();
		} else {
			startTail(file, tailFileProperty, finalContentPanel, fileList);
		}
	}

	private void startTail(final File file, final TailFileProperty tailFileProperty, final Panel contentPanel,
			final Table fileList) {
		startStopButton.setCaption(i18n.get("label.button.stop"));
		tail = tailService.startTail(file, NUMBER_OF_LAST_LINES, new Callback<String>() {
			@Override
			public void execute(final String line) {
				access(new Runnable() {
					@Override
					public void run() {
						tailFileProperty.setValue(line + "\n");
						if (autoScroll) {
							contentPanel.setScrollTop(SCROLL_TOP);
						}
						fileList.refreshRowCache();
					}
				});
			}
		});
	}

	private void stopTail() {
		tailService.stopTail(tail);
		startStopButton.setCaption(i18n.get("label.button.start"));
	}
}
