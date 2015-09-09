package com.logviewer.ui.component;

/**
 * Created by 953682 on 20/04/2015.
 */
@SuppressWarnings("serial")
@com.vaadin.annotations.JavaScript({"tailView.js"})
public class TailView extends com.vaadin.ui.AbstractJavaScriptComponent {

    private int countLines = 0;

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
        countLines++;
    }

    public void clear(){
        countLines = 0;
        setText("");
    }

    public void setScroll(boolean scroll){
        getState().scroll = scroll;
    }

    public int getCountLines() {
        return countLines;
    }

    public void setCountLines(int countLines) {
        this.countLines = countLines;
    }
}
