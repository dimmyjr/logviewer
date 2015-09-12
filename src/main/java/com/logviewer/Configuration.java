package com.logviewer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dimmy Junior on 22/04/2015.
 */
@Component
@Slf4j
public class Configuration {

    private static final String PATH_ROOTS_KEY = "${logviewer.path.roots}";

    public enum Keys {
        RECENT_FILES
    }

    @Value(PATH_ROOTS_KEY)
    private String roots;

    private Set<String> recentFiles;


    @PostConstruct
    public void load() {
        if ((roots == null || roots.isEmpty() || roots.equalsIgnoreCase(PATH_ROOTS_KEY))
                && !getWebSpherePath().isEmpty()) {
            roots = getWebSpherePath().concat("/").concat("logs");
        }

        this.recentFiles = new HashSet<String>();
    }

    protected String getWebSpherePath() {
        return System.getProperty("user.install.root") == null ? "" : System.getProperty("user.install.root");
    }

    public void addRecentFile(String value) {
        recentFiles.add(value);
    }

    public Set<String> getRecentFiles() {
        return recentFiles;
    }

    public Set<String> getRoots() {
        return (roots != null) ? new HashSet<String>(Arrays.asList(roots.split(","))) : new HashSet<String>();
    }

    public String getRoot() {
        if (roots != null && !roots.isEmpty() && !roots.contains(",")) {
            return roots;
        }

        throw new RuntimeException("has more 1 roots");
    }

}
