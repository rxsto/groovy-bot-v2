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

package co.groovybot.bot.core.audio.sources.spotify.request;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Playlist;
import com.wrapper.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

public class GetNormalPlaylistRequest extends AbstractDataRequest {

    private GetNormalPlaylistRequest(final Builder builder) {
        super(builder);
    }

    @Override
    public Playlist execute() throws IOException, SpotifyWebApiException {
        return new Playlist.JsonUtil().createModelObject(getJson());
    }

    public static final class Builder extends AbstractDataRequest.Builder<Builder> {

        public Builder(String accessToken) {
            super(accessToken);
        }

        public Builder playlistId(String playlistId) {
            assert (playlistId != null);
            assert (!playlistId.equals(""));
            return setPathParameter("playlist_id", playlistId);
        }

        @Override
        public GetNormalPlaylistRequest build() {
            setPath("/v1/playlists/{playlist_id}");
            return new GetNormalPlaylistRequest(this);
        }
    }
}
