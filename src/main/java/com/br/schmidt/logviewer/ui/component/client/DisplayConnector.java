package com.br.schmidt.logviewer.ui.component.client;

import com.br.schmidt.logviewer.ui.component.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.ui.AbstractComponentConnector;
import com.vaadin.shared.ui.Connect;

/**
 * @author Diego Schmidt
 * @since 16/10/2014
 */
@Connect(Display.class)
public class DisplayConnector extends AbstractComponentConnector {
	private final DisplayServerRpc serverRpc = RpcProxy.create(DisplayServerRpc.class, this);

	public DisplayConnector() {
		registerRpc(DisplayClientRpc.class, new DisplayClientRpc() {
		});
	}

	@Override
	protected Widget createWidget() {
		return GWT.create(DisplayWidget.class);
	}

	@Override
	public DisplayWidget getWidget() {
		return (DisplayWidget) super.getWidget();
	}

	@Override
	public DisplayState getState() {
		return (DisplayState) super.getState();
	}

	@OnStateChange("text")
	void updateText() {
		getWidget().add(new Label(getState().text));
		;
	}
}
