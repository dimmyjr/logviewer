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
		File file2 = new File(FileConstants.FILENAME_2);

		try {
			FileUtils.deleteQuietly(file);
			FileUtils.deleteQuietly(file2);
			int i = 0;
			while (true) {
				Files.append(i++ + " - " + new Date() + "\n", file, Charset.defaultCharset());
				Thread.sleep(300);
				Files.append(i++ + " - " + new Date() + "\n", file2, Charset.defaultCharset());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
