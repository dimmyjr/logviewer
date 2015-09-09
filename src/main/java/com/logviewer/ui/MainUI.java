package com.logviewer.ui;

import com.logviewer.Configuration;
import com.logviewer.service.TailService;
import com.logviewer.ui.component.Highlight;
import com.logviewer.ui.component.I18N;
import com.logviewer.ui.component.OpenFile;
import com.logviewer.ui.component.TabFile;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.Property;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.*;

/**
 * @author Diego Schmidt
 * @since 03/10/2014
 */
@SpringUI
@Push(value = PushMode.MANUAL, transport = Transport.LONG_POLLING)
@Title("LogViewer")
@Theme("default")
@Slf4j
public class MainUI extends UI {
    @Autowired
    private I18N i18n;
    @Autowired
    private TailService tailService;
    @Autowired
    private OpenFile openFile;
    @Autowired
    private Configuration configuration;
    @Autowired
    private Highlight highlight;

    private static final ThemeResource ICON_GREEN = new ThemeResource("img/circle-green_.png");
    private static final ThemeResource ICON_YELLOW = new ThemeResource("img/circle-yellow_.png");

    private TabSheet contentPanel;
    private Map<File, TabFile> files;
    private MenuBar.MenuItem menuRecents;

    @Override
    protected void init(final VaadinRequest request) {
        this.files = new HashMap<File, TabFile>();
        buildLayout();
        new InitializerThread().start();
    }

    private void buildLayout() {
        contentPanel = new TabSheet();
        contentPanel.addStyleName("framed padded-tabbar");
        contentPanel.setSizeFull();
        this.contentPanel.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                TabSheet tabSheet = (TabSheet) event.getComponent();
                if (tabSheet.getComponentCount() > 1) {
                    ((TabFile) tabSheet.getSelectedTab()).loadFullFile();
                }
            }
        });

        HorizontalLayout layout = new HorizontalLayout() {
            {
                final VerticalLayout contentLayout = new VerticalLayout() {
                    {
                        addComponent(new HorizontalLayout(){
                            {
                                setHeightUndefined();
                                addComponent(new Image(null, new ThemeResource("img/logo.png")));
                            }

                        });
                        addComponent(new MenuBar() {
                            {
                                setWidth(100, Unit.PERCENTAGE);
                                addItem(i18n.get("label.button.addfile"), new Command() {
                                    @Override
                                    public void menuSelected(MenuItem menuItem) {
                                        openFile.showDialog(getUI(), new Property.ValueChangeListener() {
                                            @Override
                                            public void valueChange(Property.ValueChangeEvent event) {
                                                final File[] files = (File[]) event.getProperty().getValue();

                                                for (File file : files) {
                                                    configuration.addRecentFile(file.getAbsolutePath());
                                                    tail(file);
                                                }

                                                loadRecentFiles();

                                            }
                                        });
                                    }
                                });

                                menuRecents = addItem(i18n.get("label.button.addfilerecent"), null);
                                loadRecentFiles();
                            }
                        });

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
//
//        if (file.length() > 20000){
//
//        }

        if (!files.containsKey(file)) {
            TabFile tabFile = createTabFile(file);
            contentPanel.setSelectedTab(tabFile);
        } else {
            contentPanel.setSelectedTab(this.files.get(file));
        }

    }

    private TabFile createTabFile(File file) {
        TabFile tabFile = new TabFile(tailService, i18n, highlight, file);
        this.files.put(file, tabFile);
        TabSheet.Tab tab = contentPanel.addTab(tabFile, tabFile.getFileName(), ICON_GREEN);
        tab.setDescription(file.getAbsolutePath());
        tab.setClosable(true);
        tabFile.setTab(tab);
        return tabFile;
    }

    private void loadRecentFiles() {
        final Set<String> files = configuration.getRecentFiles();

        if (menuRecents.getChildren() != null) {
            menuRecents.getChildren().clear();
        }

        for (final String file : files) {
            menuRecents.addItem(file, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem menuItem) {
                    tail(new File(file));
                }
            });
        }

    }


    class InitializerThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(1000);

                    access(new Runnable() {
                        @Override
                        public void run() {
                            List<File> remove = new ArrayList<File>();
                            boolean push = false;

                            for (Map.Entry<File, TabFile> entry : files.entrySet()) {
                                if (contentPanel.getTab(entry.getValue()) == null) {
                                    remove.add(entry.getKey());
                                    continue;
                                }

                                if (contentPanel.getTab(entry.getValue()).getComponent() != contentPanel.getSelectedTab()
                                        && entry.getValue().isFileChange()) {
                                    if (!contentPanel.getTab(entry.getValue()).getIcon().equals(ICON_YELLOW)) {
                                        contentPanel.getTab(entry.getValue()).setIcon(ICON_YELLOW);
                                        push = true;
                                    }
                                }

                                if (contentPanel.getTab(entry.getValue()).getComponent() == contentPanel.getSelectedTab()
                                        && contentPanel.getTab(entry.getValue()).getIcon().equals(ICON_YELLOW)) {
                                    contentPanel.getTab(entry.getValue()).setIcon(ICON_GREEN);
                                    entry.getValue().setFileChange(false);
                                    push = true;
                                }

                            }

                            for (File file : remove) {
                                files.remove(file);
                            }
                            remove.clear();

                            if (push) {
                                push();
                            }

                        }
                    });
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

}
