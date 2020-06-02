package me.rvbiljouw.awsum.spotify.api;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.specification.Track;
import me.rvbiljouw.awsum.spotify.SpotifyClient;
import me.rvbiljouw.awsum.spotify.exception.SpotifyException;
import me.rvbiljouw.awsum.spotify.model.SpotifySong;
import me.rvbiljouw.awsum.spotify.util.Formatting;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

/**
 * @author rvbiljouw
 */
public final class SpotifySongApi {
    private final SpotifyClient client;
    private final SpotifyApi api;

    public SpotifySongApi(SpotifyClient client, SpotifyApi api) {
        this.client = client;
        this.api = api;
    }

    public SpotifySong getSongByUri(String uri) throws SpotifyException {
        try {
            return trackToSong(api.getTrack(uri).build().execute());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new SpotifyException("Failed to retrieve song.", e);
        }
    }

    private static SpotifySong trackToSong(Track track) {
        final SpotifySong song = new SpotifySong();
        song.setArtist(Formatting.formatArtistNames(track.getArtists()));
        song.setTitle(track.getName());
        song.setUri(track.getUri());
        return song;
    }

}
