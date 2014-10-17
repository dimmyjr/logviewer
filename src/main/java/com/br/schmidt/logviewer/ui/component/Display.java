package com.br.schmidt.logviewer.ui.component;

import com.br.schmidt.logviewer.ui.component.client.DisplayClientRpc;
import com.br.schmidt.logviewer.ui.component.client.DisplayServerRpc;
import com.br.schmidt.logviewer.ui.component.client.DisplayState;
import com.vaadin.ui.AbstractComponent;

/**
 * @author Diego Schmidt
 * @since 16/10/2014
 */
public class Display extends AbstractComponent {
	public Display() {
		registerRpc(new DisplayServerRpc() {
			private DisplayClientRpc getClientRpc() {
				return getRpcProxy(DisplayClientRpc.class);
			}
		});
	}

	@Override
	protected DisplayState getState() {
		return (DisplayState) super.getState();
	}

	public void setText(String text) {
		getState().text = text;
	}

	public String getText() {
		return getState().text;
	}
}
