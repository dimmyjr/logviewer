package com.logviewer.ui.component;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 953682 on 28/04/2015.
 */
public class CounterException {
    private static final String REGEX_EXCEPTION = "^([\\w&&\\D]([\\w\\.]*[\\w])?)\\: ";
    private Window dialog;
    private I18N i18n;
    private UI currentUI;
    private Map<String, Integer> exceptions;
    private Table table;

    public CounterException(final I18N i18n, final UI currentUI) {
        this.i18n = i18n;
        this.currentUI = currentUI;
        this.exceptions = new HashMap<String, Integer>();
    }

    public void showDialog() {
        if (this.dialog == null) {
            this.dialog = new Window(i18n.get("label.counter.exceptions.title"), buildContetDialog());
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
        table = new Table("");

        table.addStyleName("small compact");
        table.setSizeFull();
        table.setImmediate(true);
        table.setSelectable(true);
        table.addContainerProperty("Exception", String.class, null);
        table.addContainerProperty("Count", Integer.class, null);
        loadTable();

        VerticalLayout panelContent = new VerticalLayout();
        panelContent.setSpacing(true);
        panelContent.setMargin(true);
        panelContent.setSizeFull();
        panelContent.setId("panel-content");
        panelContent.addComponent(table);
        panelContent.setExpandRatio(table, 1.0f);

        return panelContent;
    }

    public void loadTable() {
        table.removeAllItems();
        int i = 0;
        for (Map.Entry<String, Integer> ex : exceptions.entrySet()) {
            table.addItem(new Object[]{ex.getKey(), ex.getValue()}, i);
            i++;
        }
    }

    public void checkException(final String line){
        final String exception = extractException(line);
        if (exception != null) {
            addException(exception);
        }
    }

    protected String extractException(final String line){
        final Pattern VALID_JAVA_IDENTIFIER = Pattern.compile(REGEX_EXCEPTION);
        final Matcher matcher = VALID_JAVA_IDENTIFIER.matcher(line);
        return matcher.find() ? matcher.group(1) : null;
    }

    private void addException(final String exception){
        if (this.exceptions.get(exception) == null){
            this.exceptions.put(exception, 1);
        } else {
            this.exceptions.put(exception, this.exceptions.get(exception) + 1);
        }
    }
}
