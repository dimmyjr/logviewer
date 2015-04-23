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

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by 953682 on 22/04/2015.
 */
@Component
public class Configuration {

    public enum Keys {
        RECENT_FILES,
    }

    private Properties properties;
    private List<String> recentFiles;

    public void addRecentFile(String value){
        properties.put(Keys.RECENT_FILES.name().toLowerCase(), value);
    }

    public List<String> getRecentFiles(){
        final String files = properties.get(Keys.RECENT_FILES.name().toLowerCase()).toString();
        return (files != null) ? Arrays.asList(files.split(",")) : new ArrayList<String>();
    }


}
