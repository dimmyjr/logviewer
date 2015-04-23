package com.logviewer.ui.component;

import com.logviewer.common.io.filter.LogFilter;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
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
    private String path;
    private Property.ValueChangeListener changeListener;
    private File file;
    @Autowired
    private I18N i18n;


    public void showDialog(UI currentUI, String path, Property.ValueChangeListener changeListener) {
        this.path = path;
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
        this.container = new FilesystemContainer(new File(this.path), new LogFilter(), true);
		TreeTable table = new TreeTable("", container) {
            {
                addStyleName("small compact");
                setSizeFull();
                setImmediate(true);
                setSelectable(true);
                setItemIconPropertyId("Icon");
                setVisibleColumns(new Object[]{"Name", "Size"});
                addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        file = (File) valueChangeEvent.getProperty().getValue();
                    }
                });
            }
        };

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
		panelContent.setMargin(true);
		panelContent.setSizeFull();
        panelContent.setId("panel-content");
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
