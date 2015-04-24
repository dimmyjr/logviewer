package com.logviewer.ui.component;

import com.logviewer.Configuration;
import com.logviewer.common.io.filter.LogFilter;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.vaadin.ui.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.vaadin.spring.i18n.I18N;

import java.io.File;

/**
 * Created by 953682 on 16/04/2015.
 */
@org.springframework.stereotype.Component
public class OpenFile {
    private Window dialog;
    private FilesystemContainer container;
    private Property.ValueChangeListener changeListener;
    private File file;
    @Autowired
    private I18N i18n;
    @Autowired
    private Configuration configuration;



    public void showDialog(UI currentUI, Property.ValueChangeListener changeListener) {
        this.changeListener = changeListener;

        this.dialog = new Window(i18n.get("label.load.title"), buildContetDialog());
        this.dialog.setId("load-file");
        this.dialog.setModal(true);
        this.dialog.setDraggable(true);
        this.dialog.setResizable(false);
        this.dialog.setWidth(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.setHeight(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.center();
        currentUI.addWindow(dialog);
    }


    private Component buildContetDialog() {
		final TreeTable table = new TreeTable("") {
            {
                addStyleName("small compact");
                setSizeFull();
                setImmediate(true);
                setSelectable(true);
                addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        file = (File) valueChangeEvent.getProperty().getValue();
                    }
                });
            }
        };

        ComboBox comboRoots = new ComboBox(i18n.get("label.load.root"), configuration.getRoots());
        comboRoots.setWidth(100, Sizeable.Unit.PERCENTAGE);
        comboRoots.setInputPrompt(i18n.get("label.load.rootinput"));
        comboRoots.setNullSelectionAllowed(false);
        comboRoots.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                final File root = new File(event.getProperty().getValue().toString());
                table.setContainerDataSource(new FilesystemContainer(root, new LogFilter(), true));
                table.setItemIconPropertyId("Icon");
                table.setVisibleColumns(new Object[]{"Name", "Size"});
                table.refreshRowCache();
            }
        });

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
		panelContent.setMargin(true);
		panelContent.setSizeFull();
        panelContent.setId("panel-content");
        panelContent.addComponent(comboRoots);
        panelContent.addComponent(table);
		panelContent.setExpandRatio(table, 1.0f);
        panelContent.addComponent(new HorizontalLayout() {
            {
                setSpacing(true);
                addComponent(new Button(i18n.get("label.load.ok"), new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        changeListener.valueChange(new Property.ValueChangeEvent() {
                            @Override
                            public Property getProperty() {
                                return new Property() {
                                    @Override
                                    public Object getValue() {
                                        return file;
                                    }

                                    @Override
                                    public void setValue(Object o) throws ReadOnlyException {

                                    }

                                    @Override
                                    public Class getType() {
                                        return null;
                                    }

                                    @Override
                                    public boolean isReadOnly() {
                                        return false;
                                    }

                                    @Override
                                    public void setReadOnly(boolean b) {

                                    }
                                };
                            }
                        });
                        dialog.close();
                    }
                }));

                addComponent(new Button(i18n.get("label.load.cancel"), new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        dialog.close();
                    }
                }));
            }
        });

        return panelContent;
    }
}
