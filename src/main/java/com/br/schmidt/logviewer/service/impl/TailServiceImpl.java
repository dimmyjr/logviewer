package com.br.schmidt.logviewer.service.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import com.br.schmidt.logviewer.common.Callback;
import com.br.schmidt.logviewer.common.io.input.Tailer;
import com.br.schmidt.logviewer.common.io.input.TailerListener;
import com.br.schmidt.logviewer.common.io.input.TailerListenerAdapter;
import com.br.schmidt.logviewer.model.Tail;
import com.br.schmidt.logviewer.model.TailTaskReference;
import com.br.schmidt.logviewer.service.TailService;

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

		Tailer tailer = new Tailer(file, listener, DELAY, true);
		taskExecutor.execute(tailer);

		return new TailTaskReference(tailer);
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
