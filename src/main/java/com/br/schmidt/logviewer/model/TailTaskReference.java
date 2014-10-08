package com.br.schmidt.logviewer.model;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
public class TailTaskReference implements Tail {

	private ScheduledFuture<?> task;

	public TailTaskReference(final ScheduledFuture<?> task) {
		this.task = task;
	}

	public ScheduledFuture<?> getTask() {
		return task;
	}

	@Override
	public boolean stop() {
		if (task != null) {
			return task.cancel(true);
		}
		return false;
	}
}
