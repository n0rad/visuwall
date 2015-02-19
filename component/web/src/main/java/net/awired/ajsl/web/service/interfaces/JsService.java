/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
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
