package com.logviewer;

import java.io.File;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.apache.commons.io.input.TailerListenerAdapter;

/**
 * @author Diego Schmidt
 * @since 07/10/2014
 */
public class TailerTest {

	private static class TestTailerListenerAdapter extends TailerListenerAdapter {
		@Override
		public void handle(final String line) {
			System.out.println(line);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		TailerListener listener = new TestTailerListenerAdapter();
		File file = new File(FileConstants.FILENAME);
		Tailer tailer = Tailer.create(file, listener, 1000, true, true);

		while (true) {
			Thread.sleep(5000);
		}
	}

}
