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

package co.groovybot.bot.commands.general;

import co.groovybot.bot.core.command.*;
import co.groovybot.bot.core.command.permission.Permissions;

public class ShardCommand extends Command {

    public ShardCommand() {
        super(new String[]{"shard", "shards"}, CommandCategory.GENERAL, Permissions.everyone(), "Shows you information about the shard Groovy is running on", "[-list]");
        registerSubCommand(new ListCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        return send(small(String.format(event.translate("command.shard"), event.getJDA().getShardInfo().getShardId() + 1)));
    }

    private static class ListCommand extends SubCommand {

        ListCommand() {
            super(new String[]{"list", "l", "-l", "--list"}, Permissions.adminOnly(), "Shows you information about every shard", "");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            int up = event.getBot().getShardManager().getShardsRunning();
            int down = event.getBot().getShardManager().getShardsTotal() - event.getBot().getShardManager().getShardsRunning();
            return send(small(String.format(event.translate("command.shard.list"), down, up)));
        }
    }
}
