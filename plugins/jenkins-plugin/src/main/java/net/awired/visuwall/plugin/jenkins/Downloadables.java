package net.awired.visuwall.plugin.jenkins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class Downloadables {

    public static String getContent(URL url) throws IOException {
        InputStream stream = null;
        try {
            stream = url.openStream();
            byte[] content = ByteStreams.toByteArray(stream);
            String xml = new String(content);
            return xml;
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
}
