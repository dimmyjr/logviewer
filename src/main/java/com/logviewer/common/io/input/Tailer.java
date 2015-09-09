package com.logviewer.common.io.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

/**
 * Simple implementation of the unix "tail -f" functionality.
 * <p>
 * <h2>1. Create a TailerListener implementation</h3>
 * <p>
 * First you need to create a {@link org.apache.commons.io.input.TailerListener} implementation
 * ({@link org.apache.commons.io.input.TailerListenerAdapter} is provided for convenience so that you don't have to
 * implement every method).
 * </p>
 * <p/>
 * <p>For example:</p>
 * <pre>
 *  public class MyTailerListener extends TailerListenerAdapter {
 *      public void handle(String line) {
 *          System.out.println(line);
 *      }
 *  }
 * </pre>
 * <p/>
 * <h2>2. Using a Tailer</h2>
 * <p/>
 * You can create and use a Tailer in one of three ways:
 * <ul>
 * <li>Using one of the static helper methods:
 * <ul>
 * <li>{@link Tailer#create(File, TailerListener)}</li>
 * <li>{@link Tailer#create(File, TailerListener, long)}</li>
 * <li>{@link Tailer#create(File, TailerListener, long, boolean)}</li>
 * </ul>
 * </li>
 * <li>Using an {@link java.util.concurrent.Executor}</li>
 * <li>Using an {@link Thread}</li>
 * </ul>
 * <p/>
 * An example of each of these is shown below.
 * <p/>
 * <h3>2.1 Using the static helper method</h3>
 * <p/>
 * <pre>
 *      TailerListener listener = new MyTailerListener();
 *      Tailer tailer = Tailer.create(file, listener, delay);
 * </pre>
 * <p/>
 * <h3>2.2 Use an Executor</h3>
 * <p/>
 * <pre>
 *      TailerListener listener = new MyTailerListener();
 *      Tailer tailer = new Tailer(file, listener, delay);
 *
 *      // stupid executor impl. for demo purposes
 *      Executor executor = new Executor() {
 *          public void execute(Runnable command) {
 *              command.run();
 *           }
 *      };
 *
 *      executor.execute(tailer);
 * </pre>
 * <p/>
 * <p/>
 * <h3>2.3 Use a Thread</h3>
 * <pre>
 *      TailerListener listener = new MyTailerListener();
 *      Tailer tailer = new Tailer(file, listener, delay);
 *      Thread thread = new Thread(tailer);
 *      thread.setDaemon(true); // optional
 *      thread.start();
 * </pre>
 * <p/>
 * <h2>3. Stop Tailing</h3>
 * <p>Remember to stop the tailer when you have done with it:</p>
 * <pre>
 *      tailer.stop();
 * </pre>
 *
 * @version $Id: Tailer.java 1348698 2012-06-11 01:09:58Z ggregory $
 * @see org.apache.commons.io.input.TailerListener
 * @see org.apache.commons.io.input.TailerListenerAdapter
 * @since 2.0
 */
@Slf4j
public class Tailer implements Runnable {

    private static final int DEFAULT_BUFSIZE = 4096;
    private static final int DEFAULT_DELAY_MILLIS = 1000;
    private static final int DEFAULT_NUMBER_OF_LAST_LINES = 0;
    private static final String RAF_MODE = "r";
    /**
     * The amount of time to wait for the file to be updated.
     */
    private final long delayMillis;
    /**
     * Whether to tail from the end or start of file
     */
    private final boolean end;
    /**
     * The file which will be tailed.
     */
    private final File file;
    /**
     * Buffer on top of RandomAccessFile.
     */
    private final byte inbuf[];
    /**
     * The listener to notify of events when tailing.
     */
    private final TailerListener listener;
    private final int numberOfLastLines;
    /**
     * Whether to close and reopen the file whilst waiting for more input.
     */
    private final boolean reOpen;
    private long position = 0;
    /**
     * The tailer will run as long as this value is true.
     */
    private volatile boolean run = true;

    /**
     * Creates a Tailer for the given file, starting from the beginning, with the default delay of 1.0s.
     *
     * @param file     The file to follow.
     * @param listener the TailerListener to use.
     */
    public Tailer(File file, TailerListener listener) {
        this(file, listener, DEFAULT_DELAY_MILLIS);
    }

    /**
     * Creates a Tailer for the given file, starting from the beginning.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     */
    public Tailer(File file, TailerListener listener, long delayMillis) {
        this(file, listener, delayMillis, false);
    }

    /**
     * Creates a Tailer for the given file, with a delay other than the default 1.0s.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     */
    public Tailer(File file, TailerListener listener, long delayMillis, boolean end) {
        this(file, listener, delayMillis, end, DEFAULT_NUMBER_OF_LAST_LINES);
    }

    /**
     * Creates a Tailer for the given file, with a delay other than the default 1.0s.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @param reOpen      if true, close and reopen the file between reading chunks
     */
    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        this(file, listener, delayMillis, end, reOpen, DEFAULT_NUMBER_OF_LAST_LINES);
    }

    /**
     * Creates a Tailer for the given file.
     *
     * @param file              the file to follow.
     * @param listener          the TailerListener to use.
     * @param delayMillis       the delay between checks of the file for new content in milliseconds.
     * @param end               Set to true to tail from the end of the file, false to tail from the beginning of the
     *                          file.
     * @param numberOfLastLines Number of previous lines to read.
     */
    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, int numberOfLastLines) {
        this(file, listener, delayMillis, end, false, numberOfLastLines);
    }

    /**
     * Creates a Tailer for the given file.
     *
     * @param file              the file to follow.
     * @param listener          the TailerListener to use.
     * @param delayMillis       the delay between checks of the file for new content in milliseconds.
     * @param end               Set to true to tail from the end of the file, false to tail from the beginning of the
     *                          file.
     * @param reOpen            if true, close and reopen the file between reading chunks
     * @param numberOfLastLines Number of previous lines to read.
     */
    public Tailer(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen,
                  int numberOfLastLines) {
        this.file = file;
        this.delayMillis = delayMillis;
        this.end = end;
        this.numberOfLastLines = numberOfLastLines;

        this.inbuf = new byte[DEFAULT_BUFSIZE];

        // Save and prepare the listener
        this.listener = listener;
        listener.init(this);
        this.reOpen = reOpen;
    }

    /**
     * Creates and starts a Tailer for the given file.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @param bufSize     buffer size.
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, int bufSize) {
        Tailer tailer = new Tailer(file, listener, delayMillis, end, bufSize);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    /**
     * Creates and starts a Tailer for the given file.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @param reOpen      whether to close/reopen the file between chunks
     * @param bufSize     buffer size.
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen,
                                int bufSize) {
        Tailer tailer = new Tailer(file, listener, delayMillis, end, reOpen, bufSize);
        Thread thread = new Thread(tailer);
        thread.setDaemon(true);
        thread.start();
        return tailer;
    }

    /**
     * Creates and starts a Tailer for the given file with default buffer size.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end) {
        return create(file, listener, delayMillis, end, DEFAULT_BUFSIZE);
    }

    /**
     * Creates and starts a Tailer for the given file with default buffer size.
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @param end         Set to true to tail from the end of the file, false to tail from the beginning of the file.
     * @param reOpen      whether to close/reopen the file between chunks
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener, long delayMillis, boolean end, boolean reOpen) {
        return create(file, listener, delayMillis, end, reOpen, DEFAULT_BUFSIZE);
    }

    /**
     * Creates and starts a Tailer for the given file, starting at the beginning of the file
     *
     * @param file        the file to follow.
     * @param listener    the TailerListener to use.
     * @param delayMillis the delay between checks of the file for new content in milliseconds.
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener, long delayMillis) {
        return create(file, listener, delayMillis, false);
    }

    /**
     * Creates and starts a Tailer for the given file, starting at the beginning of the file
     * with the default delay of 1.0s
     *
     * @param file     the file to follow.
     * @param listener the TailerListener to use.
     * @return The new tailer
     */
    public static Tailer create(File file, TailerListener listener) {
        return create(file, listener, DEFAULT_DELAY_MILLIS, false);
    }

    /**
     * Return the delay in milliseconds.
     *
     * @return the delay in milliseconds.
     */
    public long getDelay() {
        return delayMillis;
    }

    /**
     * Return the file.
     *
     * @return the file
     */
    public File getFile() {
        return file;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public long getPosition() {
        return position;
    }

    /**
     * Follows changes in the file, calling the TailerListener's handle method for each new line.
     */
    public void run() {
        RandomAccessFile reader = null;
        try {
            long last = 0; // The last time the file was checked for changes
            run = true;
            // Open the file
            while (run && reader == null) {
                try {
                    reader = new RandomAccessFile(file, RAF_MODE);
                } catch (FileNotFoundException e) {
                    listener.fileNotFound();
                }

                if (reader == null) {
                    Thread.sleep(delayMillis);
                } else {
                    // The current position in the file
                    if (position <= 0) {
                        position = end ? file.length() : 0;

                        if (end && numberOfLastLines > 0) {
                            ReversedLinesFileReader fileReader = new ReversedLinesFileReader(reader);
                            int i = 0;
                            final List<String> previousLines = Lists.newArrayList();
                            String lineRead = "";
                            while (i++ < numberOfLastLines && (lineRead = fileReader.readLine()) != null) {
                                previousLines.add(lineRead);
                            }
                            for (String line : Lists.reverse(previousLines)) {
                                listener.handle(line, false);
                            }
                        }
                    }

                    last = System.currentTimeMillis();
                    reader.seek(position);
                }
            }

            while (run) {

                boolean newer = FileUtils.isFileNewer(file, last); // IO-279, must be done first

                // Check the file length to see if it was rotated
                long length = file.length();

                if (length < position) {

                    // File was rotated
                    listener.fileRotated();

                    // Reopen the reader after rotation
                    try {
                        // Ensure that the old file is closed iff we re-open it successfully
                        RandomAccessFile save = reader;
                        reader = new RandomAccessFile(file, RAF_MODE);
                        position = 0;
                        // close old file explicitly rather than relying on GC picking up previous RAF
                        IOUtils.closeQuietly(save);
                    } catch (FileNotFoundException e) {
                        // in this case we continue to use the previous reader and position values
                        listener.fileNotFound();
                    }
                    continue;
                } else {

                    // File was not rotated

                    // See if the file needs to be read again
                    if (length > position) {

                        // The file has more content than it did last time
                        position = readLines(reader);
                        last = System.currentTimeMillis();

                    } else if (newer) {

                        /*
                         * This can happen if the file is truncated or overwritten with the exact same length of
                         * information. In cases like this, the file position needs to be reset
                         */
                        position = 0;
                        reader.seek(position); // cannot be null here

                        // Now we can read new lines
                        position = readLines(reader);
                        last = System.currentTimeMillis();
                    }
                }
                if (reOpen) {
                    IOUtils.closeQuietly(reader);
                }

                Thread.sleep(delayMillis);

                if (run && reOpen) {
                    reader = new RandomAccessFile(file, RAF_MODE);
                    reader.seek(position);
                }
            }

        } catch (Exception e) {
            listener.handle(e);
            log.error("ERRO READ: read file impossible!", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Allows the tailer to complete its current loop and return.
     */
    public void stop() {
        this.run = false;
    }

    /**
     * Read new lines.
     *
     * @param reader The file to read
     * @return The new position after the lines have been read
     * @throws java.io.IOException if an I/O error occurs.
     */
    private long readLines(RandomAccessFile reader) throws IOException {
        StringBuilder sb = new StringBuilder();

        long pos = reader.getFilePointer();
        long rePos = pos; // position to re-read

        int num;
        boolean seenCR = false;
        while (run && ((num = reader.read(inbuf)) != -1)) {
            for (int i = 0; i < num; i++) {
                byte ch = inbuf[i];
                switch (ch) {
                    case '\n':
                        seenCR = false; // swallow CR before LF
                        listener.handle(sb.toString(), i + 1 == num);
                        sb.setLength(0);
                        rePos = pos + i + 1;
                        break;
                    case '\r':
                        if (seenCR) {
                            sb.append('\r');
                        }
                        seenCR = true;
                        break;
                    default:
                        if (seenCR) {
                            seenCR = false; // swallow final CR
                            listener.handle(sb.toString(), true);
                            sb.setLength(0);
                            rePos = pos + i + 1;
                        }
                        sb.append((char) ch); // add character, not its ascii value
                }
            }

            pos = reader.getFilePointer();
        }

        reader.seek(rePos); // Ensure we can re-read if necessary
        return rePos;
    }

}

