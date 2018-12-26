/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergej Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package co.groovybot.bot.io;

import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;

@Log4j2
public class FileManager {

    private final String[] DIRECTORIES = {
            "config",
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
