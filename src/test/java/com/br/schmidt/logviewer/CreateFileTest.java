package com.br.schmidt.logviewer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

/**
 * @author Diego Schmidt
 * @since 07/10/2014
 */
public class CreateFileTest {

	public static void main(String[] args) throws InterruptedException {
		File file = new File(FileConstants.FILENAME);

		try {
			FileUtils.deleteQuietly(file);
			int i = 0;
			while (true) {
				Files.append(i++ + " - " + new Date() + "\n", file, Charset.defaultCharset());
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
