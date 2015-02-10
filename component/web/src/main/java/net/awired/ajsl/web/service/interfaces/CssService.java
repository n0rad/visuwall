package net.awired.ajsl.web.service.interfaces;

import java.io.File;
import java.util.Map;

public interface CssService {

    String getCssLinks(String prefix) throws Exception;

    Map<File, String> getCssFiles() throws Exception;

    String getCssData() throws Exception;
}
