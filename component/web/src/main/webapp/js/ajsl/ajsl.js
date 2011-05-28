/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

ajsl = new function() {
	this.utils = {};

	this.get = function(id) {
		$.ajax({
			  url: url,
			  dataType: 'script',
			  success: success
			});
		
		
		$.when($.ajax("/page1.php"), $.ajax("/page2.php")).done(function(a1,  a2){
		    /* a1 and a2 are arguments resolved for the 
		        page1 and page2 ajax requests, respectively */
		   var jqXHR = a1[2]; /* arguments are [ "success", statusText, jqXHR ] */
		   if ( /Whip It/.test(jqXHR.responseText) ) {
		      alert("First page has 'Whip It' somewhere.");
		   }
		});
		
		$.getScript("res/visuwall/ctrl/controller/", function(data, textStatus) {
			
		});
	};

};