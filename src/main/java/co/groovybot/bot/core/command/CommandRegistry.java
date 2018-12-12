package io.groovybot.bot.core.command;

import io.groovybot.bot.commands.general.*;
import io.groovybot.bot.commands.music.*;
import io.groovybot.bot.commands.owner.CloseCommand;
import io.groovybot.bot.commands.owner.EvalCommand;
import io.groovybot.bot.commands.owner.FriendsCommand;
import io.groovybot.bot.commands.owner.UpdateCommand;
import io.groovybot.bot.commands.settings.*;

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
                //new KeyCommand(),
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
                new TrendsCommand()
        );
    }
}
