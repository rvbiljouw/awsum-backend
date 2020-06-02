package me.rvbiljouw.awsum.spotify.util;

import com.wrapper.spotify.model_objects.specification.ArtistSimplified;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author rvbiljouw
 */
public class Formatting {

    public static String formatArtistNames(ArtistSimplified... artists) {
        return Arrays.stream(artists)
                .map(ArtistSimplified::getName)
                .collect(Collectors.joining(", "));
    }

}
