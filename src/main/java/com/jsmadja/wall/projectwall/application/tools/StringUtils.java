package com.jsmadja.wall.projectwall.application.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StringUtils {

    public static StringBuilder inputStreamToStringBuilder(InputStream in, int minimumCapacity) {
        StringBuilder builder = new StringBuilder(minimumCapacity);
        inputStreamToStringBuilder(in, builder);
        return builder;
    }

    public static StringBuilder inputStreamToStringBuilder(InputStream in) {
        return inputStreamToStringBuilder(in, 16);
    }

    public static void inputStreamToStringBuilder(InputStream in, StringBuilder builder) {
        byte[] b = new byte[4096];
        try {
            for (int n; (n = in.read(b)) != -1;) {
                builder.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void inputStreamReaderToStringBuilder(InputStreamReader reader, StringBuilder builder) {
        char[] b = new char[4096];
        try {
            for (int n; (n = reader.read(b)) != -1;) {
                builder.append(new String(b, 0, n));
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
