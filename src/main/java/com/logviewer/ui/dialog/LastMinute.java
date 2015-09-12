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

import com.logviewer.Configuration;
import com.logviewer.service.TailService;
import com.logviewer.ui.component.I18N;
import com.logviewer.ui.component.TailView;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by Dimmy Junior on 11/09/2015.
 */
@org.springframework.stereotype.Component
public class LastMinute {
    private Window dialog;
    @Autowired
    private I18N i18n;
    @Autowired
    private TailService tailService;
    @Autowired
    private Configuration configuration;

    public void showDialog(int minutes) {
        this.dialog = new Window(i18n.get("label.lastminute.title"), buildContetDialog(minutes));
        this.dialog.setId("load-file");
        this.dialog.setModal(true);
        this.dialog.setDraggable(true);
        this.dialog.setResizable(true);
        this.dialog.setWidth(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.setHeight(50, Sizeable.Unit.PERCENTAGE);
        this.dialog.center();
        UI.getCurrent().addWindow(dialog);
    }


    private Component buildContetDialog(int minutes) {
        final Map<String, String> lastMinute = tailService.loadLastMinute(configuration.getRoot(), minutes);
        TabSheet panelContent = new TabSheet(){
            {
                setSizeFull();
                addStyleName("framed padded-tabbar");
            }
        };
        for (Map.Entry<String, String> entry : lastMinute.entrySet()) {
            addTab(panelContent, entry.getValue(), entry.getKey());
        }

        return panelContent;
    }

    private void addTab(final TabSheet panelContent, final String content, final String name) {
        panelContent.addTab(new Panel(new TailView(content) {
            {
                setStyleName("logcontent");
            }
        }) {
            {
                setSizeFull();
            }
        }, name);
    }


}
