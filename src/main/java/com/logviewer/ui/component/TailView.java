/*
 * Copyright (c) 2015$ Cardif.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Cardif
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Cardif.
 */
package com.logviewer.ui.component;

/**
 * Created by 953682 on 20/04/2015.
 */
@SuppressWarnings("serial")
@com.vaadin.annotations.JavaScript({"tailView.js"})
public class TailView extends com.vaadin.ui.AbstractJavaScriptComponent {

    public TailView(final String text) {
        add(text);
    }

    @Override
    protected TailViewState getState() {
        return (TailViewState) super.getState();
    }

    public void setText(String text){
        getState().text = text;
        getState().value = null;
    }

    public void add(String value){
        getState().value = value;
    }

    public void clear(){
        setText("");
    }

    public void setScroll(boolean scroll){
        getState().scroll = scroll;
    }
}
