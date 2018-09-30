package io.groovybot.bot.io;

import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;

@Log4j
public class FileManager {

    private final String[] DIRECTORIES = {
            "config"
    };

    private final String[] FILES = {
            "config/config.json"
    };

    public FileManager() {
        for (String directory : DIRECTORIES) {
            File dir = new File(directory);
            if (!dir.exists())
                dir.mkdirs();
        }

        for (String file : FILES) {
            File fil = new File(file);
            if (!fil.exists()) {
                try {
                    fil.createNewFile();
                } catch (IOException e) {
                    log.error("[FileManager] Error while creating files!", e);
                }
            }
        }
    }
}
