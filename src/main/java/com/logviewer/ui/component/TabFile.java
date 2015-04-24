package com.logviewer.ui.component;

import com.logviewer.common.Callback;
import com.logviewer.model.Tail;
import com.logviewer.service.TailService;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.commons.io.FileUtils;
import org.vaadin.spring.i18n.I18N;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by 953682 on 09/04/2015.
 */
public class TabFile extends VerticalLayout {

    private static final String JS_ADD = "document.getElementById('%s').childNodes[0].innerHTML += '%s';";
    private static final String JS_CLEAR = "document.getElementById('%s').childNodes[0].innerHTML = '';";
    private static final String JS_SCROLL = "document.getElementById('%s').parentNode.scrollTop = " +
                                            "document.getElementById('%s').parentNode.scrollHeight;";

    private static final int NUMBER_OF_LAST_LINES = 1;
    private static final int NUMBER_MAX_LINES = 2000;
    private static final String MYKEY = "download";

    private final TailService tailService;
    private final I18N i18n;

    private final File file;
    private final String fileId;
    private Tail tail;
    private TailView fileContent;
    private TabSheet.Tab tab;
    private boolean autoScroll;
    private boolean fileChange;


    public TabFile(final TailService tailService, final I18N i18n, final File file) {
        this.tailService = tailService;
        this.i18n = i18n;
        this.file = file;

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

    public void loadFullFile() {
        try {
            StringBuffer sb = new StringBuffer();
            final List<String> readLines = FileUtils.readLines(file);
            int start = (readLines.size() > NUMBER_MAX_LINES) ? readLines.size() - NUMBER_MAX_LINES : 0;

            for (int i = start; i < readLines.size(); i++) {
                sb.append(readLines.get(i) + "\n");
            }

            this.fileContent.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
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
            public void execute(final String line) {
                fileChange = true;
                fileContent.setScroll(autoScroll);

                if (isTabSelected()) {
                    sb.append(line + "\n");

                    if ((new Date().getTime() - count)  > 1000) {
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
        });
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
        return ((TabSheet) tab.getComponent().getParent()).getSelectedTab().equals(TabFile.this);
    }
}
