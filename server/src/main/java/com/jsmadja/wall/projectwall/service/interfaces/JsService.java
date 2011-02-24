package com.jsmadja.wall.projectwall.service.interfaces;

import java.io.File;
import java.util.Map;

public interface JsService {

    String getJsLinks(String prefix) throws Exception;

    Map<File, String> getJsFiles() throws Exception;

}
