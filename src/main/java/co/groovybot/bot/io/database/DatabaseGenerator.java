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

package co.groovybot.bot.io.database;

public class DatabaseGenerator {

    public DatabaseGenerator(PostgreSQL postgreSQL) {
        postgreSQL.addDefault(() -> "create table if not exists guilds\n" +
                "(\n" +
                "  id                   bigint                not null\n" +
                "    constraint guilds_pkey\n" +
                "    primary key,\n" +
                "  prefix               varchar,\n" +
                "  volume               integer,\n" +
                "  dj_mode              boolean default false not null,\n" +
                "  dj_role              bigint default null,\n" +
                "  announce_songs       boolean default true,\n" +
                "  blacklisted_channels varchar default '[]' :: character varying,\n" +
                "  commands_channel     bigint,\n" +
                "  auto_leave           boolean default true  not null,\n" +
                "  auto_pause           boolean default false  not null,\n" +
                "  auto_join_channel    bigint,\n" +
                "  search_play          boolean default false not null,\n" +
                "  prevent_dups         boolean default false  not null,\n" +
                "  delete_messages      boolean default true not null\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists lavalink\n" +
                "(\n" +
                "  uri      varchar not null\n" +
                "    constraint lavalink_nodes_pkey\n" +
                "    primary key,\n" +
                "  password varchar\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists playlists\n" +
                "(\n" +
                "  id        bigint\n" +
                "    constraint playlists_pk\n" +
                "    unique,\n" +
                "  author_id bigint,\n" +
                "  name      varchar,\n" +
                "  public    boolean default false not null,\n" +
                "  tracks    varchar,\n" +
                "  count     integer default 0     not null\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists premium\n" +
                "(\n" +
                "  user_id bigint  not null\n" +
                "    constraint premium_pkey\n" +
                "      primary key,\n" +
                "  type    varchar not null\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists queues\n" +
                "(\n" +
                "  guild_id         bigint not null\n" +
                "    constraint table_name_pkey\n" +
                "      primary key,\n" +
                "  current_track    varchar,\n" +
                "  current_position bigint,\n" +
                "  queue            varchar,\n" +
                "  channel_id       bigint,\n" +
                "  text_channel_id  bigint,\n" +
                "  volume           integer,\n" +
                "  bassboost        varchar(7),\n" +
                "  skip_votes       integer,\n" +
                "  loop_queue       boolean,\n" +
                "  loop             boolean,\n" +
                "  shuffle          boolean,\n" +
                "  auto_play        boolean\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists users\n" +
                "(\n" +
                "  user_id    bigint                not null\n" +
                "    constraint users_pkey\n" +
                "    primary key,\n" +
                "  locale     varchar(50),\n" +
                "  friend     boolean default false not null,\n" +
                "  expiration bigint default 0      not null,\n" +
                "  again      bigint default 0      not null\n" +
                ");");

        postgreSQL.addDefault(() -> "create table if not exists websocket\n" +
                "(\n" +
                "  token varchar(64) not null\n" +
                "    constraint websocket_pkey\n" +
                "    primary key\n" +
                ");");

        postgreSQL.createDatabases();
    }
}
