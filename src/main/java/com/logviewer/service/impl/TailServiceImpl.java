package com.logviewer.service.impl;

import com.google.common.collect.Lists;
import com.logviewer.common.Callback;
import com.logviewer.common.io.filter.LogFilter;
import com.logviewer.common.io.input.ReversedLinesFileReader;
import com.logviewer.common.io.input.Tailer;
import com.logviewer.common.io.input.TailerListener;
import com.logviewer.common.io.input.TailerListenerAdapter;
import com.logviewer.model.Tail;
import com.logviewer.model.TailTaskReference;
import com.logviewer.service.TailService;
import com.vaadin.data.util.FilesystemContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Diego Schmidt
 * @since 08/10/2014
 */
@Service
@Slf4j
public class TailServiceImpl implements TailService {

    public static final int DELAY = 1000;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    public TailServiceImpl() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
    }

    @Override
    public Tail startTail(final File file, final int numberOfLastLines, final Callback<String> callback) {

        final TailerListener listener = new TailerListenerAdapter() {
            @Override
            public void handle(final String line, boolean endFile) {
                callback.execute(line, endFile);
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

    @Override
    public void loadLastMinut(final String path, int count) {
        try {
            StringBuffer sb = new StringBuffer();

            Long nowMinus = new Date().getTime() - (60L * 1000L * count);
            final FilesystemContainer container = new FilesystemContainer(new File(path),
                                                                          new LogFilter(),
                                                                          true);

            for (File file : container.getItemIds()) {
                if (file.isDirectory() || !(file.lastModified() >= nowMinus)) {
                    continue;
                }

                sb.append("###############################################################################\n");
                sb.append("#########   " + String.format("%-57s", file.getName()) + " #########\n");
                sb.append("###############################################################################\n\n");

                ReversedLinesFileReader fileReader = new ReversedLinesFileReader(new RandomAccessFile(file, "r"));
                final List<String> previousLines = Lists.newArrayList();
                String lineRead;

                while ((lineRead = fileReader.readLine()) != null) {
                    final Date date = extractDate(lineRead);
                    if (date != null && date.getTime() < nowMinus) {
                        for (String line : Lists.reverse(previousLines)) {
                            sb.append(line + "\n");
                        }
                        previousLines.clear();

                        break;
                    }
                    previousLines.add(lineRead);
                }
            }

            System.out.println(sb.toString());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private Date extractDate(String line) throws ParseException {
        int count = 0;
        String[] allMatches = new String[2];
        Matcher m = Pattern.compile("(0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d\\s\\d\\d:\\d\\d").matcher(
                line);
        if (m.find()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            return dateFormat.parse(m.group());
        }
        return null;
    }


}
