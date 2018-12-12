package co.groovybot.bot.core.audio.spotify.request;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.PlaylistTrack;
import com.wrapper.spotify.requests.data.AbstractDataRequest;

import java.io.IOException;

public class GetNormalPlaylistsTracksRequest extends AbstractDataRequest {

    private GetNormalPlaylistsTracksRequest(final GetNormalPlaylistsTracksRequest.Builder builder) {
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

        public GetNormalPlaylistsTracksRequest.Builder playlistId(final String playlistId) {
            assert (playlistId != null);
            assert (!playlistId.equals(""));
            return setPathParameter("playlist_id", playlistId);
        }

        public GetNormalPlaylistsTracksRequest.Builder fields(final String fields) {
            assert (fields != null);
            assert (!fields.equals(""));
            return setQueryParameter("fields", fields);
        }

        public GetNormalPlaylistsTracksRequest.Builder limit(final Integer limit) {
            assert (1 <= limit && limit <= 100);
            return setQueryParameter("limit", limit);
        }

        public GetNormalPlaylistsTracksRequest.Builder offset(final Integer offset) {
            assert (offset >= 0);
            return setQueryParameter("offset", offset);
        }

        public GetNormalPlaylistsTracksRequest.Builder market(final CountryCode market) {
            assert (market != null);
            return setQueryParameter("market", market);
        }

        @Override
        public GetNormalPlaylistsTracksRequest build() {
            setPath("/v1/playlists/{playlist_id}/tracks");
            return new GetNormalPlaylistsTracksRequest(this);
        }
    }
}
