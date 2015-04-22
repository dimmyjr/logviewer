package com.logviewer.model;

import com.logviewer.common.io.input.Tailer;

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

	public Tailer getTailer() {
		return tailer;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	public void setStarted(final boolean started) {
		this.started = started;
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
