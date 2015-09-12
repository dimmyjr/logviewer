package com.logviewer.service.impl;

import org.junit.Test;

import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Dimmy Junior on 09/09/2015.
 */
public class TailServiceImplTest {

    @Test
    public void testLoadLastMinut() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));

        final String PATH = "C:\\DEVTOO~1\\var\\WAS7_P~1\\DEV_PE\\logs";
        TailServiceImpl tailService = new TailServiceImpl();
        final Map<String, String> lastMinute = tailService.loadLastMinute(PATH, 10);
        for (Map.Entry<String, String> entry : lastMinute.entrySet()) {
            System.out.println("###############################################################################\n");
            System.out.println("#########   " + String.format("%-57s", entry.getKey()) + " #########\n");
            System.out.println("###############################################################################\n\n");
            System.out.println(entry.getValue());
        }


    }


}