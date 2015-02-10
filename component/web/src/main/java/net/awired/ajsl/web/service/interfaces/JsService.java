package net.awired.ajsl.web.service.interfaces;

import java.io.File;
import java.util.List;
import java.util.Map;

import net.awired.ajsl.web.domain.JsServiceMap;

public interface JsService {

	String getJsLinks(String prefix) throws Exception;

	String getJsLinks(String prefix, Map<File, String> jsMap) throws Exception;

	JsServiceMap buildServiceMapFromJsMap(Map<File, String> jsMap);

	Map<String, List<String>> getServicesMethods(Map<File, String> jsFiles, Map<String, List<String>> serviceMap) throws Exception;
	
	String buildJsServiceMapString(JsServiceMap jsServiceMap);

	Map<File, String> getJsFiles() throws Exception;

	String getJsData() throws Exception;
}
