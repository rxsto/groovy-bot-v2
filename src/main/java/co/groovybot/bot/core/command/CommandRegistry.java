/*
 * Groovy Bot - The core component of the Groovy Discord music bot
 *
 * Copyright (C) 2018  Oskar Lang & Michael Rittmeister & Sergeij Herdt & Yannick Seeger & Justus Kliem & Leon Kappes
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

package co.groovybot.bot.core.command;

import co.groovybot.bot.commands.general.*;
import co.groovybot.bot.commands.music.*;
import co.groovybot.bot.commands.owner.CloseCommand;
import co.groovybot.bot.commands.owner.EvalCommand;
import co.groovybot.bot.commands.owner.FriendsCommand;
import co.groovybot.bot.commands.owner.UpdateCommand;
import co.groovybot.bot.commands.settings.*;
import co.groovybot.bot.commands.settings.AutoPlayCommand;
import co.groovybot.bot.commands.settings.SwitchCommand;

public class CommandRegistry {

    public CommandRegistry(CommandManager manager) {
        manager.registerCommands(
                new HelpCommand(),
                new PingCommand(),
                new InfoCommand(),
                new InviteCommand(),
                new SupportCommand(),
                new DonateCommand(),
                new VoteCommand(),
                new StatsCommand(),
                new ShardCommand(),
                new PrefixCommand(),
                new LanguageCommand(),
                new PlayCommand(),
                new PauseCommand(),
                new ResumeCommand(),
                new SkipCommand(),
                new PreviousCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new VolumeCommand(),
                new NowPlayingCommand(),
                new QueueCommand(),
                new ControlCommand(),
                new SearchCommand(),
                new ResetCommand(),
                new ClearCommand(),
                new SeekCommand(),
                new DjModeCommand(),
                new LoopCommand(),
                new StopCommand(),
                new ShuffleCommand(),
                new MoveCommand(),
                new RemoveCommand(),
                new AnnounceCommand(),
                new UpdateCommand(),
                new PlaylistCommand(),
                new AutoPlayCommand(),
                new CloseCommand(),
                new JumpCommand(),
                new UptimeCommand(),
                new LyricsCommand(),
                new BlackListCommand(),
                new BotChannelCommand(),
                new SwitchCommand(),
                new PartnerCommand(),
                new EvalCommand(),
                new AutoLeaveCommand(),
                new BassBoostCommand(),
                new FriendsCommand(),
                new TrendsCommand(),
                new AutoPauseCommand(),
                new AutoJoinCommand(),
                new SettingsCommand(),
                new PremiumCommand(),
                new DuplicatesCommand(),
                new VoteSkipCommand(),
                new DeleteMessagesCommand(),
                new VoicefixCommand()
        );
    }
}
