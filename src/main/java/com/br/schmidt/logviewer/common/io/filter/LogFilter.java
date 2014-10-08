package com.br.schmidt.logviewer.common.io.filter;

import java.io.File;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
public class LogFilter implements java.io.FilenameFilter {
	@Override
	public boolean accept(final File dir, final String name) {
		final File file = new File(dir, name);
		return !file.isDirectory() && name.endsWith("log");
	}
}
