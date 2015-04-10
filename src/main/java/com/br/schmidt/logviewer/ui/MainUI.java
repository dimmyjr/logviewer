package com.br.schmidt.logviewer.ui;

import com.br.schmidt.logviewer.common.io.filter.LogFilter;
import com.br.schmidt.logviewer.service.TailService;
import com.br.schmidt.logviewer.ui.component.TabFile;
import com.br.schmidt.logviewer.ui.util.CustomFilesystemContainer;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.VaadinUI;
import org.vaadin.spring.i18n.I18N;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@VaadinUI
@Push
@Title("LogViewer")
@Theme("default")
public class MainUI extends UI {

    @Autowired
    private I18N i18n;
    @Autowired
    private TailService tailService;

    private TabSheet contentPanel;
    private CustomFilesystemContainer container;
    private Map<File, TabFile> files;
    private Table fileList;

    @Override
    protected void init(final VaadinRequest request) {
        this.files = new HashMap<>();
        buildLayout();
        new InitializerThread().start();
    }

    private void buildLayout() {
        //TODO Get file path from???
        container = new CustomFilesystemContainer(new File("C:\\DevTools-v6_x64\\var\\was7_profile\\dev\\logs\\dev"), new LogFilter(), true);
//        container = new CustomFilesystemContainer(new File("."), new LogFilter(), true);
        contentPanel = new TabSheet();
        contentPanel.addStyleName("framed padded-tabbar");
        contentPanel.setSizeFull();
        HorizontalLayout layout = new HorizontalLayout() {
            {
                final Panel filesPanel = new Panel() {
                    {
                        fileList = new Table(i18n.get("label.logs"), container) {
                            {
                                addStyleName("small compact");
                                setSizeFull();
                                setImmediate(true);
                                setSelectable(true);
                            }
                        };
                        fileList.addValueChangeListener(new Property.ValueChangeListener() {
                            @Override
                            public void valueChange(Property.ValueChangeEvent event) {
                                final File file = (File) event.getProperty().getValue();
                                tail(file);
                            }
                        });
                        setContent(fileList);
                        setWidth("300px");
                        setHeight(100, Unit.PERCENTAGE);
                    }
                };

                addComponent(filesPanel);
                final VerticalLayout contentLayout = new VerticalLayout() {
                    {
                        addComponent(contentPanel);
                        setExpandRatio(contentPanel, 1.0f);
                        setSizeFull();
                    }
                };
                addComponent(contentLayout);
                setExpandRatio(contentLayout, 1.0f);
                setSizeFull();
            }
        };

        setContent(layout);
    }

    private void tail(final File file) {
        if (!files.containsKey(file)) {
            TabFile tabFile = new TabFile(tailService, i18n, file);
            this.files.put(file, tabFile);
            TabSheet.Tab tab = contentPanel.addTab(tabFile, file.getName() + " " + file.length());
            tab.setClosable(true);

            contentPanel.setSelectedTab(tabFile);
        } else {
            contentPanel.setSelectedTab(this.files.get(file));
        }

    }

    class InitializerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(2000);

                    access(new Runnable() {
                        @Override
                        public void run() {
                            for (Map.Entry<File, TabFile> entry : files.entrySet()) {
                                if (contentPanel.getTab(entry.getValue()) == null){
                                    continue;
                                }
                                contentPanel.getTab(entry.getValue()).setCaption(entry.getKey().getName() + " " + entry.getKey().length());
                            }

                            fileList.refreshRowCache();
                        }
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
