package com.logviewer.ui.component;

import com.logviewer.common.io.filter.LogFilter;
import com.vaadin.data.Property;
import com.vaadin.data.util.FilesystemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import java.io.File;

/**
 * Created by 953682 on 16/04/2015.
 */
public class OpenFile {
    private Window dialog;
    private FilesystemContainer container;
    private String path;
    private Property.ValueChangeListener changeListener;


    public void showDialog(UI currentUI, String path, Property.ValueChangeListener changeListener) {
        this.path = path;
        this.changeListener = changeListener;

        this.dialog = new Window("Load", buildContetDialog());
        this.dialog.setId("load-file");
        this.dialog.setModal(true);
        this.dialog.setDraggable(true);
        this.dialog.setResizable(true);
        this.dialog.setWidth(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.setHeight(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.center();
        currentUI.addWindow(dialog);
    }


    private Component buildContetDialog() {
        this.container = new FilesystemContainer(new File(this.path), new LogFilter(), true);

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panelContent.setId("panel-content");
        panelContent.addComponent(new TreeTable("", container) {
            {
                addStyleName("small compact");
                setSizeFull();
                setImmediate(true);
                setSelectable(true);
                setItemIconPropertyId("Icon");
                setVisibleColumns(new Object[]{"Name", "Size"});
                addValueChangeListener(changeListener);
                addValueChangeListener(new ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        dialog.close();
                    }
                });
            }
        });
        panelContent.addComponent(new Button("Close", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                dialog.close();
            }
        }));
        return panelContent;
    }
}
