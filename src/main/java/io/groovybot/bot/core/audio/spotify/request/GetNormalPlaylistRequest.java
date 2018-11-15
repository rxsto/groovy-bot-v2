package io.groovybot.bot.core.audio.spotify.outdated.request;

import com.neovisionaries.i18n.CountryCode;
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

        public Builder market(final CountryCode market) {
            assert (market != null);
            return setQueryParameter("market", market);
        }

        @Override
        public GetNormalPlaylistRequest build() {
            setPath("/v1/playlists/{playlist_id}");
            return new GetNormalPlaylistRequest(this);
        }
    }
}
