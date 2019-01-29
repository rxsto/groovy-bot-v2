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

package co.groovybot.bot.core.entity;

import co.groovybot.bot.GroovyBot;
import lombok.Getter;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseEntitiy {

    @Getter
    public final Long entityId;

    protected DatabaseEntitiy(Long entityId) {
        this.entityId = entityId;
    }

    public abstract void updateInDatabase() throws Exception;

    protected Connection getConnection() throws SQLException {
        return GroovyBot.getInstance().getPostgreSQL().getDataSource().getConnection();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DatabaseEntitiy))
            return false;
        return ((DatabaseEntitiy) obj).getEntityId().equals(entityId);
    }
}
