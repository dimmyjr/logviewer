/*
 * Copyright (c) 2015$ Cardif.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Cardif
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Cardif.
 */
package com.logviewer.ui.dialog;

import com.logviewer.ui.component.I18N;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dimmy Junior on 31/07/2015.
 */
public class FilterLog {
    private Window dialog;
    private I18N i18n;
    private UI currentUI;
    private List<String> filters;
    private Table table;

    public FilterLog(final I18N i18n, final UI currentUI) {
        this.i18n = i18n;
        this.currentUI = currentUI;
        this.filters = new ArrayList<String>();
    }

    public void showDialog() {
        if (this.dialog == null) {
            this.dialog = new Window(i18n.get("label.filter.log.title"), buildContetDialog());
            this.dialog.setId("load-file");
            this.dialog.setModal(false);
            this.dialog.setDraggable(true);
            this.dialog.setResizable(true);
            this.dialog.setWidth(50, Sizeable.Unit.PERCENTAGE);
            this.dialog.setHeight(50, Sizeable.Unit.PERCENTAGE);
            this.dialog.center();
        }
        currentUI.addWindow(dialog);
    }

    private com.vaadin.ui.Component buildContetDialog() {
        final TextField txtFilter = new TextField(){
            {
                setWidth(100, Unit.PERCENTAGE);
            }
        };

        table = new Table("");

        table.addStyleName("small compact");
        table.setSizeFull();
        table.setImmediate(true);
        table.setSelectable(true);
        table.setEditable(false);
        table.addContainerProperty("Filter", String.class, null);
        loadTable();

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panelContent.setMargin(true);
        panelContent.setSizeFull();
        panelContent.setId("panel-content");
        panelContent.addComponent(new HorizontalLayout(){
            {
                addComponent(txtFilter);
                addComponent(new Button("Add", new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        filters.add(txtFilter.getValue());
                        loadTable();
                    }
                }));
                setExpandRatio(txtFilter, 1f);
                setWidth(100, Unit.PERCENTAGE);
            }
        });
        panelContent.addComponent(table);
        panelContent.setExpandRatio(table, 1.0f);

        return panelContent;
    }

    public void loadTable() {
        table.removeAllItems();

        for (String filter : filters) {
            table.addItem(new Object[]{filter}, filter);
        }

    }

    public boolean checkFilter(String input) {
        if (filters.isEmpty()) {
            return true;
        }

        for (String filter : filters) {
            if (input.trim().contains(filter)) {
                return true;
            }

        }
        return false;
    }

}
