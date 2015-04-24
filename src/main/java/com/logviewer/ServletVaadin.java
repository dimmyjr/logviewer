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

}
