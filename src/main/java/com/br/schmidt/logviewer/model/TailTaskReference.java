package com.br.schmidt.logviewer.model;

import com.br.schmidt.logviewer.common.io.input.Tailer;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
public class TailTaskReference implements Tail {

	private final Tailer tailer;
	private boolean started;

	public TailTaskReference(final Tailer tailer) {
		this.tailer = tailer;
		this.started = true;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public boolean stop() {
		if (tailer != null) {
			tailer.stop();
			started = false;
			return true;
		}
		started = false;
		return false;
	}
}
