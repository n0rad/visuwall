package net.awired.visuwall.plugin.bamboo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.io.ByteStreams;

public class BambooVersionExtractor {

    public static String extractVersion(URL url) throws BambooVersionNotFoundException {
        try {
            // version 2.7.1 build 2101
            InputStream stream = url.openStream();
            byte[] bytes = ByteStreams.toByteArray(stream);
            String page = new String(bytes);
            return page.split("version ")[1].split(" build")[0];
        } catch (IOException e) {
            throw new BambooVersionNotFoundException("Can't extract version from url:" + url, e);
        }
    }

}
