package com.br.schmidt.logviewer.ui.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.vaadin.data.util.FilesystemContainer;

/**
 * @author Diego Schmidt
 * @since 07/10/2014
 */
public class CustomFilesystemContainer extends FilesystemContainer {

	public CustomFilesystemContainer(final File root) {
		super(root);
	}

	public CustomFilesystemContainer(final File root, final boolean recursive) {
		super(root, recursive);
	}

	public CustomFilesystemContainer(final File root, final String extension, final boolean recursive) {
		super(root, extension, recursive);
	}

	public CustomFilesystemContainer(final File root, final FilenameFilter filter, final boolean recursive) {
		super(root, filter, recursive);
	}

	@Override
	public Collection<String> getContainerPropertyIds() {
		return FluentIterable.from(super.getContainerPropertyIds()).filter(new Predicate<String>() {
			@Override
			public boolean apply(final String input) {
				return !PROPERTY_ICON.equals(input) && !PROPERTY_LASTMODIFIED.equals(input);
			}
		}).toList();
	}

}
