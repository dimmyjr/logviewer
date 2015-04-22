package com.logviewer.service.impl;

import java.io.File;

import com.logviewer.common.Callback;
import com.logviewer.common.io.input.Tailer;
import com.logviewer.common.io.input.TailerListener;
import com.logviewer.model.Tail;
import com.logviewer.model.TailTaskReference;
import com.logviewer.service.TailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.logviewer.common.io.input.TailerListenerAdapter;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
@Service
public class TailServiceImpl implements TailService {

	public static final int DELAY = 1000;

	@Autowired
	private ThreadPoolTaskScheduler scheduler;

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;

	@Override
	public Tail startTail(final File file, final int numberOfLastLines, final Callback<String> callback) {

		final TailerListener listener = new TailerListenerAdapter() {
			@Override
			public void handle(final String line) {
				callback.execute(line);
			}
		};

		Tailer tailer = new Tailer(file, listener, DELAY, true, numberOfLastLines);
		taskExecutor.execute(tailer);

		return new TailTaskReference(tailer);
	}

	@Override
	public Tail startTail(final Tail tail) {
		if (tail instanceof TailTaskReference) {
			final TailTaskReference tailTaskReference = (TailTaskReference) tail;
			taskExecutor.execute(tailTaskReference.getTailer());
			tailTaskReference.setStarted(true);
		}
		return tail;
	}

	@Override
	public Tail startTail(final File file, final Callback<String> callback) {
		return startTail(file, 0, callback);
	}

	@Override
	public boolean stopTail(final Tail tail) {
		if (tail != null) {
			return tail.stop();
		}
		return false;
	}

}
