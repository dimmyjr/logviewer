package com.logviewer.ui.component;

import com.google.common.collect.Lists;
import com.logviewer.common.Callback;
import com.logviewer.common.io.input.ReversedLinesFileReader;
import com.logviewer.model.Tail;
import com.logviewer.service.TailService;
import com.logviewer.ui.dialog.CounterException;
import com.logviewer.ui.dialog.FilterLog;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ResourceReference;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by 953682 on 09/04/2015.
 */
@Slf4j
public class TabFile extends VerticalLayout {

    private static final int NUMBER_OF_LAST_LINES = 1;
    private static final int NUMBER_MAX_LINES = 2000;
    private static final String MYKEY = "download";

    private final TailService tailService;
    private final I18N i18n;
    private final CounterException counterException;
    private final FilterLog filterLog;
    private final Highlight highlight;


    private final File file;
    private final String fileId;
    private Tail tail;
    private TailView fileContent;
    private TabSheet.Tab tab;
    private boolean autoScroll;
    private boolean fileChange;


    public TabFile(final TailService tailService, final I18N i18, Highlight highlight, final File file) {
        this.tailService = tailService;
        this.highlight = highlight;
        this.i18n = i18;
        this.file = file;
        this.counterException = new CounterException(i18n, UI.getCurrent());
        this.filterLog = new FilterLog(i18n, UI.getCurrent());

        this.fileId = UUID.randomUUID().toString();
        this.fileContent = new TailView(null);
        this.fileContent.setId(this.fileId);
        this.fileContent.setStyleName("logcontent");

        addComponent(new Label(file.getAbsolutePath()));
        addComponent(new MenuBar() {
            {
                setWidth(100, Unit.PERCENTAGE);
                setMargin(true);
                final MenuItem chkAutoScroll = addItem(i18n.get("label.auto_scroll"), new Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {
                        autoScroll = menuItem.isChecked();
                        fileContent.setScroll(autoScroll);
                    }
                });
                chkAutoScroll.setCheckable(true);
                chkAutoScroll.setChecked(true);
                autoScroll = true;

                addItem(i18n.get("label.button.clear"), new Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {
                        clear();
                    }
                });

                addItem(i18n.get("label.button.download"), new Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {
                        downloadFile();
                    }
                });

                addItem(i18n.get("label.button.filters"), new Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {
                        showFilterLog();
                    }
                });

                addItem(i18n.get("label.button.exceptions"), new Command() {
                    @Override
                    public void menuSelected(MenuItem menuItem) {
                        showCountExceptions();
                    }
                });

            }
        });
        addComponent(new Panel(fileContent) {
            {
                setSizeFull();
            }
        });
        setExpandRatio(getComponent(getComponentCount() - 1), 1.0f);
        setSizeFull();
        startTail(file);
    }

    private void downloadFile() {
        FileResource res = new FileResource(this.file);
        setResource(MYKEY, res);
        ResourceReference rr = ResourceReference.create(res, this, MYKEY);
        Page.getCurrent().open(rr.getURL(), null);
    }

    private void showCountExceptions() {
        counterException.showDialog();
    }

    private void showFilterLog() {
        filterLog.showDialog();
    }

    public void loadFullFile() {
        try {
            StringBuffer sb = new StringBuffer();
            ReversedLinesFileReader fileReader = new ReversedLinesFileReader(new RandomAccessFile(file, "r"));
            int i = 0;
            final List<String> previousLines = Lists.newArrayList();
            String lineRead;

            while (i++ < NUMBER_MAX_LINES && (lineRead = fileReader.readLine()) != null) {
                previousLines.add(lineRead);
            }

            for (String line : Lists.reverse(previousLines)) {
                counterException.checkException(line);
                sb.append(highlight.checkAndFormat(line) + "\n");
            }

            this.fileContent.setText(sb.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    public String getFileName() {
        return file.getName();
    }

    public boolean isFileChange() {
        return fileChange;
    }

    public void setFileChange(boolean fileChange) {
        this.fileChange = fileChange;
    }

    private void startTail(final File file) {
        loadFullFile();
        tail = tailService.startTail(file, NUMBER_OF_LAST_LINES, new Callback<String>() {

                                         StringBuilder sb = new StringBuilder();
                                         long count = new Date().getTime();

                                         @Override
                                         public void execute(final String line, final boolean endFile) {
                                             fileChange = true;
                                             fileContent.setScroll(autoScroll);

                                             if (isTabSelected()) {
                                                 if (filterLog.checkFilter(line)) {
                                                     sb.append(highlight.checkAndFormat(line) + "\n");
                                                 }

                                                 counterException.checkException(line);
                                                 if ((new Date().getTime() - count) > 1000 || endFile) {
                                                     getUI().access(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             fileContent.add(sb.toString());
                                                             getUI().push();
                                                         }
                                                     });
                                                     sb.setLength(0);
                                                     count = new Date().getTime();
                                                 }

                                             }
                                         }
                                     }

        );
    }

    private void clear() {
        fileContent.clear();
    }

    @Override
    public void detach() {
        tailService.stopTail(tail);
        super.detach();
    }

    public void setTab(TabSheet.Tab tab) {
        this.tab = tab;
    }

    private boolean isTabSelected() {
        log.debug("tab.getComponent()" + tab.getComponent());
        log.debug("tab.getComponent().getParent()" + tab.getComponent().getParent());
        log.debug("((TabSheet) tab.getComponent().getParent()).getSelectedTab()" + ((TabSheet) tab.getComponent().getParent()).getSelectedTab());
        log.debug("TabFile.this" + TabFile.this);

        return ((TabSheet) tab.getComponent().getParent()).getSelectedTab().equals(TabFile.this);
    }


}
