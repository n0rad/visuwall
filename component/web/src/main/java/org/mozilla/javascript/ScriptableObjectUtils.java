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
package org.mozilla.javascript;

public class ScriptableObjectUtils {
	
	public static String[] getMethodArrayFromJsObject(Object obj) {
		if (obj instanceof InterpretedFunction) {
			return InterpretedFunctionPropertiesLoader((InterpretedFunction) obj);
		} else  {
			Scriptable arr = (Scriptable) obj;
			Object[] ids = arr.getIds();
			String[] array = new String[ids.length];
			for (int i = 0; i < ids.length; i++) {
				array[i] = (String) ids[i];
			}
			return array;
		}
	}

	
	public static String[] InterpretedFunctionPropertiesLoader(InterpretedFunction obj) {
		String[] stringTable = obj.idata.itsStringTable;
		return stringTable;
	}
}
