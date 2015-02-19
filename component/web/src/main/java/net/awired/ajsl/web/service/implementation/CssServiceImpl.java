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
package net.awired.ajsl.web.service.implementation;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import net.awired.ajsl.core.io.FileFlatTree;
import net.awired.ajsl.core.io.PathFileFilter;
import net.awired.ajsl.core.lang.StringUtils;
import net.awired.ajsl.web.service.interfaces.CssService;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.yahoo.platform.yui.compressor.CssCompressor;

@Service
public class CssServiceImpl implements CssService {

	private static final String CSS_DIRECTORY = "css";

	@Autowired
	private ApplicationContext context;

	@Override
	public String getCssData() throws Exception {
		Map<File, String> cssFiles = this.getCssFiles();

		StringBuilder builder = new StringBuilder();
		for (File file2 : cssFiles.keySet()) {
			FileReader reader = new FileReader(file2);
			StringUtils.inputStreamReaderToStringBuilder(reader, builder);
		}

		Reader reader = new StringReader(builder.toString());
		StringWriter writer = new StringWriter();

		CssCompressor compressor = new CssCompressor(reader);
		compressor.compress(writer, 1);
		return writer.toString();
	}

	@Override
	public Map<File, String> getCssFiles() throws Exception {
		Map<File, String> cssFiles = new HashMap<File, String>();

		Resource res = context.getResource(CSS_DIRECTORY);
		File file = res.getFile();

		PathFileFilter pathfilter = new PathFileFilter();
		pathfilter.setGlobalFilter(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory()
						&& !pathname.getName().equals(".svn")) {
					return true;
				}
				if (pathname.getName().endsWith(".css")) {
					return true;
				}
				return false;
			}
		});

		cssFiles = FileFlatTree.getTree(file, CSS_DIRECTORY, pathfilter, null);

		return cssFiles;
	}

	@Override
	public String getCssLinks(String prefix) throws Exception {
		StringBuilder result = new StringBuilder();

		Map<File, String> cssFiles = getCssFiles();

		for (File file : cssFiles.keySet()) {
			result.append("<link rel=\"stylesheet\" href=\"");
			result.append(prefix);
			result.append(cssFiles.get(file));
			result.append("\" type=\"text/css\">\n");
		}
		return result.toString();
	}

}
