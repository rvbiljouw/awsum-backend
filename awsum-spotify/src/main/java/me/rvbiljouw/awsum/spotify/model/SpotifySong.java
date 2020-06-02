package me.rvbiljouw.awsum.spotify.model;

/**
 * @author rvbiljouw
 */
public final class SpotifySong {
    private String uri;
    private String artist;
    private String title;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
