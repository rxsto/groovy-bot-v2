package io.groovybot.bot.commands.owner;

import io.groovybot.bot.core.command.*;
import io.groovybot.bot.core.command.permission.Permissions;
import io.groovybot.bot.core.entity.User;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FriendsCommand extends Command {
    public FriendsCommand() {
        super(new String[]{"friends", "friend"}, CommandCategory.DEVELOPER, Permissions.ownerOnly(), "Lets you add some friends!", "");
        registerSubCommand(new AddCommand());
        registerSubCommand(new RemoveCommand());
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        List<Long> friends = new ArrayList<>();

        try {
            Connection connection = event.getBot().getPostgreSQL().getDataSource().getConnection();

            PreparedStatement friendsStatement = connection.prepareStatement("SELECT user_id FROM users WHERE friend = TRUE");
            ResultSet friendsResult = friendsStatement.executeQuery();

            if (!friendsResult.next())
                return send(error(event.translate("command.friends.nofriends.title"), event.translate("command.friends.nofriends.description")));

            StringBuilder friendsNames = new StringBuilder();

            while (friendsResult.next()) {
                net.dv8tion.jda.core.entities.User friend = event.getBot().getShardManager().getUserById(friendsResult.getLong("user_id"));
                friendsNames.append(friend.getAsMention()).append(",");
            }

            friendsNames.replace(friendsNames.lastIndexOf(","), friendsNames.lastIndexOf(",") + 1, "");
            return send(info(event.translate("command.friends.list.title"), friendsNames.toString()));
        } catch (SQLException e) {
            log.error("[FriendsCommand] Error while querying all friends!", e);
            return send(error(event));
        }
    }

    private class AddCommand extends SubCommand {

        public AddCommand() {
            super(new String[]{"add"}, Permissions.ownerOnly(), "Lets you add an user to your friends!", "<@user>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (event.getMessage().getMentionedMembers().get(0) == null)
                return send(error(event.translate("command.friends.nomention.title"), event.translate("command.friends.nomention.description")));
            User user = event.getBot().getUserCache().get(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
            if (!user.isFriend()) {
                user.setFriend(true);
                return send(success(event.translate("command.friends.added.title"), String.format(event.translate("command.friends.added.description"), event.getMessage().getMentionedMembers().get(0).getAsMention())));
            }
            return send(error(event.translate("command.friends.already.title"), String.format(event.translate("command.friends.already.description"), event.getMessage().getMentionedMembers().get(0).getAsMention())));
        }
    }

    private class RemoveCommand extends SubCommand {

        public RemoveCommand() {
            super(new String[]{"remove", "rm", "delete"}, Permissions.ownerOnly(), "Lets you remove an user from your friends!", "<@user>");
        }

        @Override
        public Result run(String[] args, CommandEvent event) {
            if (event.getMessage().getMentionedMembers().get(0) == null)
                return send(error(event.translate("command.friends.nomention.title"), event.translate("command.friends.nomention.description")));
            User user = event.getBot().getUserCache().get(event.getMessage().getMentionedMembers().get(0).getUser().getIdLong());
            if (user.isFriend()) {
                user.setFriend(false);
                return send(success(event.translate("command.friends.removed.title"), String.format(event.translate("command.friends.removed.description"), event.getMessage().getMentionedMembers().get(0).getAsMention())));
            }
            return send(error(event.translate("command.friends.not.title"), String.format(event.translate("command.friends.not.description"), event.getMessage().getMentionedMembers().get(0).getAsMention())));
        }
    }
}
