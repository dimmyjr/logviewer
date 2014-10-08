package com.br.schmidt.logviewer.service.impl;

import java.io.File;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.br.schmidt.logviewer.common.Callback;
import com.br.schmidt.logviewer.common.model.Tail;
import com.br.schmidt.logviewer.service.TailService;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
@Service
public class TailServiceImpl implements TailService {

	private static class TailRef implements Tail {

		private TailRef(final ScheduledFuture<?> task) {
			this.task = task;
		}

		public ScheduledFuture<?> getTask() {
			return task;
		}

		private ScheduledFuture<?> task;

		@Override
		public boolean stop() {
			if (task != null) {
				return task.cancel(true);
			}
			return false;
		}
	}

	public static final int DELAY = 1000;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	@Override
	public Tail startTail(final File file, final Callback<String> callback) {

		final TailerListener listener = new TailerListenerAdapter() {
			@Override
			public void handle(final String line) {
				callback.execute(line);
			}
		};

		Tailer tailer = new Tailer(file, listener, DELAY, true);
		final ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(tailer, DELAY);

		return new TailRef(scheduledFuture);
	}

	@Override
	public boolean stopTail(final Tail tail) {
		if (tail != null) {
			return tail.stop();
		}
		return false;
	}

}
