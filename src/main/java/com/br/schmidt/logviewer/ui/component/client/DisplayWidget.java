package com.br.schmidt.logviewer.ui.component.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Diego Schmidt
 * @since 16/10/2014
 */
public class DisplayWidget extends HTMLPanel {

	public DisplayWidget() {
		super("");
	}

	public DisplayWidget(final String html) {
		super(html);
	}

	public DisplayWidget(final SafeHtml safeHtml) {
		super(safeHtml);
	}

	public DisplayWidget(final String tag, final String html) {
		super(tag, html);
	}

}
