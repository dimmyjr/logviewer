package com.br.schmidt.logviewer.ui.component;

import com.br.schmidt.logviewer.common.Callback;
import com.br.schmidt.logviewer.model.Tail;
import com.br.schmidt.logviewer.service.TailService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.spring.i18n.I18N;

import java.io.File;
import java.util.UUID;

/**
 * Created by 953682 on 09/04/2015.
 */
public class TabFile extends VerticalLayout {

    private static final String JS_ADD = "document.getElementById('%s').innerHTML += '%s<br/>';";
    private static final String JS_CLEAR = "document.getElementById('%s').innerHTML = '';";
    private static final int NUMBER_OF_LAST_LINES = 10;

    private final TailService tailService;
    private final I18N i18n;

    private final File file;
    private final String fileId;
    private Tail tail;
    private Label fileContent;
    private CheckBox chkAutoScroll;



    public TabFile(final TailService tailService, final I18N i18n, final File file) {
        this.tailService = tailService;
        this.i18n = i18n;
        this.file = file;

        this.fileId = UUID.randomUUID().toString();
        this.fileContent = new Label("", ContentMode.PREFORMATTED);
        this.fileContent.setId(this.fileId);

        this.chkAutoScroll = new CheckBox(i18n.get("label.auto_scroll"));

        addComponent(new HorizontalLayout()
        {
            {

                setSpacing(true);
                addComponent(chkAutoScroll);
                addComponent(new Button(i18n.get("label.button.clear"), new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        clear();
                    }
                }));
            }
        });
        addComponent(new Panel(fileContent){
            {
                setSizeFull();
            }
        });
        setExpandRatio(getComponent(getComponentCount()-1), 1.0f);
        setSizeFull();
        startTail(file);
    }


    private void startTail(final File file) {
        tail = tailService.startTail(file, NUMBER_OF_LAST_LINES, new Callback<String>() {
            @Override
            public void execute(final String line) {
                getUI().access(new Runnable() {
                    @Override
                    public void run() {
                        JavaScript.getCurrent().execute(String.format(JS_ADD, fileId, line));

                        if (chkAutoScroll.getValue()) {
                            getUI().scrollIntoView(fileContent);
                        }
                    }
                });
            }
        });
    }

    private void clear() {
        JavaScript.getCurrent().execute(String.format(JS_CLEAR, fileId));
    }

    @Override
    public void detach() {
        tailService.stopTail(tail);
        super.detach();
    }
}
