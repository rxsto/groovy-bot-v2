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

package co.groovybot.bot.core.audio.sources.spotify.request;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

public class GetNormalPlaylistTracksRequest extends AbstractDataRequest {

    private GetNormalPlaylistTracksRequest(final GetNormalPlaylistTracksRequest.Builder builder) {
        super(builder);
    }

    @SuppressWarnings("unchecked")
    public Paging<PlaylistTrack> execute() throws
            IOException,
            SpotifyWebApiException {
        return new PlaylistTrack.JsonUtil().createModelObjectPaging(getJson());
    }

    public static final class Builder extends AbstractDataRequest.Builder<Builder> {

        public Builder(final String accessToken) {
            super(accessToken);
        }

        public GetNormalPlaylistTracksRequest.Builder playlistId(final String playlistId) {
            assert (playlistId != null);
            assert (!playlistId.equals(""));
            return setPathParameter("playlist_id", playlistId);
        }

        public GetNormalPlaylistTracksRequest.Builder fields(final String fields) {
            assert (fields != null);
            assert (!fields.equals(""));
            return setQueryParameter("fields", fields);
        }

        public GetNormalPlaylistTracksRequest.Builder limit(final Integer limit) {
            assert (1 <= limit && limit <= 100);
            return setQueryParameter("limit", limit);
        }

        public GetNormalPlaylistTracksRequest.Builder offset(final Integer offset) {
            assert (offset >= 0);
            return setQueryParameter("offset", offset);
        }

        public GetNormalPlaylistTracksRequest.Builder market(final CountryCode market) {
            assert (market != null);
            return setQueryParameter("market", market);
        }

        @Override
        public GetNormalPlaylistTracksRequest build() {
            setPath("/v1/playlists/{playlist_id}/tracks");
            return new GetNormalPlaylistTracksRequest(this);
        }
    }
}
