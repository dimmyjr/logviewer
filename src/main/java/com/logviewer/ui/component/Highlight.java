package com.logviewer.ui.component;

import org.springframework.stereotype.Component;

/**
 * Created by Dimmy Junior on 05/08/2015.
 */
@Component
public class Highlight {
    private static final String ERROR_SINTAX = " ERROR ";
    private static final String WARNING_SINTAX = " WARN ";

    private static final String ERROR_STYLE = "error";
    private static final String WARNING_STYLE = "warning";

    private static final String FORMAT_HIGH = "<span class=\"%1$s\">%2$s</span>";
    private static final String NORMAL = "<xmp>%s</xmp>";


    public String checkAndFormat(String input) {
        if (input.contains(ERROR_SINTAX)) {
            return String.format(FORMAT_HIGH, ERROR_STYLE, input);
        } else if (input.contains(WARNING_SINTAX)) {
            return String.format(FORMAT_HIGH, WARNING_STYLE, input);
        } else {
            return String.format(NORMAL, input);
        }
    }
}
