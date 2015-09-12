package com.logviewer.ui.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Created by Dimmy Junior on 29/07/2015.
 */
@Component
public class I18N extends org.vaadin.spring.i18n.I18N {

    @Autowired
    private MessageSource messageSource;

    /**
     * @param applicationContext the application context to read messages from, never {@code null}.
     */
    public I18N(ApplicationContext applicationContext) {
        super(applicationContext);
    }


    public I18N() {
        this(null);
    }

    public String get(String input) {
        try {
            return messageSource.getMessage(input, null, Locale.getDefault());
        } catch (NoSuchMessageException ex){
            return "!" + input;
        }

    }
}
