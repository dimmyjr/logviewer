/*
 * Copyright (c) 2015$ Cardif.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Cardif
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Cardif.
 */
package com.logviewer;

import com.logviewer.ui.MainUI;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by 953682 on 20/04/2015.
 */
@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = MainUI.class)
public class ServletVaadin extends VaadinServlet {


    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(new SessionInitListener() {
            @Override
            public void sessionInit(SessionInitEvent event) throws ServiceException {
                event.getSession().addBootstrapListener(new BootstrapListener() {
                    @Override
                    public void modifyBootstrapPage(BootstrapPageResponse response) {
                        System.out.println("$$$$$$$$$$$$$ start");
                        response.getDocument().head().prependElement("script").attr("type", "text/javascript").attr(
                                "src",
                                "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js");
                        response.getDocument().head().prependElement("script").attr("type", "text/javascript").attr(
                                "src",
                                "js/tail.js");
                    }

                    @Override
                    public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
                    }
                });
            }
        });
    }

}
