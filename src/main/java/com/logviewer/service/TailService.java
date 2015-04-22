package com.logviewer.service;

import java.io.File;

import com.logviewer.common.Callback;
import com.logviewer.model.Tail;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
public interface TailService {

	Tail startTail(final Tail tail);

	Tail startTail(final File file, final Callback<String> callback);

	Tail startTail(final File file, final int numberOfLastLines, final Callback<String> callback);

	boolean stopTail(Tail tail);

}
