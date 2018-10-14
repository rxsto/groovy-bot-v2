package io.groovybot.bot.io.database;

public class DatabaseGenerator {

    public DatabaseGenerator(PostgreSQL postgreSQL) {
        postgreSQL.addDefault(() -> "create table if not exists guilds\n" +
                "(\n" +
                "  id      bigint                not null\n" +
                "    constraint guilds_pkey\n" +
                "    primary key,\n" +
                "  prefix  varchar,\n" +
                "  volume  integer,\n" +
                "  dj_mode boolean default false not null\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists queues\n" +
                "(\n" +
                "  guild_id         bigint not null\n" +
                "    constraint table_name_pkey\n" +
                "    primary key,\n" +
                "  current_track    varchar,\n" +
                "  current_position bigint,\n" +
                "  queue            varchar,\n" +
                "  channel_id       bigint,\n" +
                "  text_channel_id  bigint\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists premium\n" +
                "(\n" +
                "  user_id       bigint               not null\n" +
                "    constraint premium_pkey\n" +
                "    primary key,\n" +
                "  patreon_token varchar,\n" +
                "  type          integer              not null,\n" +
                "  \"check\"       boolean default true not null,\n" +
                "  refresh_token varchar,\n" +
                "  patreon_id    varchar\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists users\n" +
                "(\n" +
                "  id     bigint not null\n" +
                "    constraint users_pkey\n" +
                "    primary key,\n" +
                "  locale varchar(50)\n" +
                ");\n");
        postgreSQL.addDefault(() -> "create table if not exists stats\n" +
                "(\n" +
                "  playing integer,\n" +
                "  servers integer,\n" +
                "  users   integer,\n" +
                "  id      bigint not null\n" +
                "    constraint stats_pk\n" +
                "    primary key\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists lavalink_nodes\n" +
                "(\n" +
                "  uri      varchar not null\n" +
                "    constraint lavalink_nodes_pkey\n" +
                "    primary key,\n" +
                "  password varchar\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists keys\n" +
                "(\n" +
                "  id   serial not null\n" +
                "    constraint keys_pkey\n" +
                "    primary key,\n" +
                "  type varchar,\n" +
                "  key  varchar\n" +
                ");");
        postgreSQL.addDefault(() -> "create table if not exists playlists\n" +
                "(\n" +
                "  id       serial not null\n" +
                "    constraint playlists_pkey\n" +
                "    primary key,\n" +
                "  owner_id bigint,\n" +
                "  tracks   varchar,\n" +
                "  name     varchar\n" +
                ");\n");
        postgreSQL.createDatabases();
    }
}
