package com.logviewer.ui.dialog;

import com.logviewer.Configuration;
import com.logviewer.common.io.filter.LogFilter;
import com.logviewer.ui.component.I18N;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MultiSelectMode;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Set;

/**
 * Created by Dimmy Junior on 16/04/2015.
 */
@org.springframework.stereotype.Component
public class OpenFile {
    private Window dialog;
    private Property.ValueChangeListener changeListener;
    private File[] files;
    private TreeTable table;
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
        this.table = new TreeTable("") {
            {
                addStyleName("small compact");
                setSizeFull();
                setImmediate(true);
                setSelectable(true);
                setMultiSelect(true);
                setMultiSelectMode(MultiSelectMode.DEFAULT);
                addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        if (valueChangeEvent.getProperty().getValue() instanceof File) {
                            files = new File[]{(File) valueChangeEvent.getProperty().getValue()};
                        } else {
                            final Set list = (Set) valueChangeEvent.getProperty().getValue();
                            files = (File[]) list.toArray(new File[list.size()]);
                        }
                    }
                });
            }
        };

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panelContent.setMargin(true);
        panelContent.setSizeFull();
        panelContent.setId("panel-content");


        if (configuration.getRoots().size() > 1) {
            ComboBox comboRoots = new ComboBox(i18n.get("label.load.root"), configuration.getRoots());
            comboRoots.setWidth(100, Sizeable.Unit.PERCENTAGE);
            comboRoots.setInputPrompt(i18n.get("label.load.rootinput"));
            comboRoots.setNullSelectionAllowed(false);
            comboRoots.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    final File root = new File(event.getProperty().getValue().toString());
                    refreshTable(root, table);
                }
            });
            panelContent.addComponent(comboRoots);


        } else {
            final File root = new File(configuration.getRoots().iterator().next());
            refreshTable(root, table);
        }

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
                                        return files;
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

    private void refreshTable(File root, TreeTable table) {
        table.setContainerDataSource(new FilesystemContainer(root, new LogFilter(), true));
        table.setItemIconPropertyId("Icon");
        table.setVisibleColumns(new Object[]{"Name", "Size"});
        table.refreshRowCache();

    }
}
