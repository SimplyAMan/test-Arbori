package com.trivadis.plsql.formatter.settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.logging.LogManager;

//import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

import oracle.dbtools.app.Format;
import oracle.dbtools.app.Persist2XML;

public abstract class ConfiguredTestFormatter {
    private final Format formatter;

    public ConfiguredTestFormatter() {
        super();
        loadLoggingConf();
        formatter = new Format();

        configureFormatter();
    }
    
    private void loadLoggingConf() {
        LogManager manager = LogManager.getLogManager();
        try {
            manager.readConfiguration(Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.conf"));
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Object> getOptions() {
        Map<String, Object> map;
        try {
            URL advancedFormat = ConfiguredTestFormatter.class.getResource("/trivadis_advanced_format.xml"); // symbolic link
//            URL advancedFormat = File.class.getResource("/trivadis_advanced_format.xml"); // symbolic link
            if (advancedFormat == null) {
                throw new IOException("trivadis_advanced_format.xml hasn't been found");
            }

            map = Persist2XML.read(advancedFormat);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }
    
    private String getArboriFileName() {
        URL customFormat = ConfiguredTestFormatter.class.getResource("/trivadis_custom_format.arbori"); // symbolic link
        return customFormat.getFile();
    }

    private void configureFormatter() {
        Map<String, Object> map = getOptions();
        for (String key : map.keySet()) {
            formatter.options.put(key, map.get(key));
        }
        formatter.options.put(formatter.formatProgramURL, getArboriFileName());
    }

    public Format getFormatter() {
        return formatter;
    }

    public void resetOptions() {
        configureFormatter();
    }
    
    public void formatAndAssert(CharSequence expected) {
        formatAndAssert(expected, false);
    }
    
    public void formatAndAssert(CharSequence expected, boolean resetOptions) {
        try {
            String expectedTrimmed = expected.toString().trim();
            String actual = formatter.format(expectedTrimmed);
            Assertions.assertEquals(expectedTrimmed, actual);
//            Assert.assertEquals(expectedTrimmed, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (resetOptions) {
                resetOptions();
            }
        }
    }

}
