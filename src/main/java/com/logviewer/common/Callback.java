package com.logviewer.common;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
public interface Callback<T> {

	void execute(T value);

}
