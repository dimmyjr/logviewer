/*
 * Copyright (c) 2015$ Cardif.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Cardif
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Cardif.
 */
package com.logviewer;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * Created by 953682 on 22/04/2015.
 */
@Component
@Slf4j
public class Configuration {

    public enum Keys {
        RECENT_FILES
    }

    private static final String FILE_NAME = "/logviewer-conf.properties";
    private static final int MAX_RECENT_FILE = 10;


    @Value("${logviewer.config.path}")
    private String configPath;

    @Value("${logviewer.path.roots}")
    private String roots;

    private Properties properties;
    private List<String> recentFiles;


    @PostConstruct
    public void load() {
        properties = new Properties();

        try {
            if (new File(configPath + FILE_NAME).exists()) {
                properties.load(new FileInputStream(configPath + FILE_NAME));
            }
        } catch (IOException e) {
            log.error("Configuration file not found");
        }
    }

    private void save(){
        try {
            properties.store(new FileOutputStream(configPath + FILE_NAME), null);
        } catch (Exception e) {
            log.error("Configuration file not found");
        }
    }

    public void addRecentFile(String value) {
        final Set<String> files = getRecentFiles();
        if (files.size() > MAX_RECENT_FILE){
            files.remove(0);
        }
        files.add(value);
        properties.put(Keys.RECENT_FILES.name().toLowerCase(), Joiner.on(",").join(files));
        save();
    }

    public Set<String> getRecentFiles() {
        final Object files = properties.get(Keys.RECENT_FILES.name().toLowerCase());
        return (files != null) ? new HashSet<String>(Arrays.asList(files.toString().split(",")))  : new HashSet<String>();
    }

    public Set<String> getRoots() {
        return (roots != null) ? new HashSet<String>(Arrays.asList(roots.split(",")))  : new HashSet<String>();
    }

}
