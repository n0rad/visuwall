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

visuwall.mvc.event.navigation = {
	context : 'DIV#navigationContainer',
	
	init : function() {
//		this['|mouseleave']();
	},

	
//	'|mouseenter' : function() {
//		LOG.debug('enter navigationContainer');
////	    $("button").toggle(function () { 
////	        $("#text span").fadeOut("slow"); 
////	        $("#text").slideUp("slow"); 
////	      },function() { 
////	        $("#text span").fadeIn("slow"); 
////	        $("#text").slideDown("slow"); 
////	      });
//		
//		//$('#navigation', this.context).stop(true, true).slideDown('fast', $('#navigation', this.context).fadeIn());
//		$('#navigation', this.context).stop(true, true).slideFadeToggle('slow');
//	},
//
//	'|mouseleave' : function() {
//		LOG.debug('leave navigationContainer');
//		$('#navigation', this.context).stop(true, true).delay(1000).slideFadeToggle('slow');
//		//		$('#navigation', this.context).stop(true, true).delay(1000).slideUp('slow', $('#navigation', this.context).fadeOut());
//	},
	
	
	'#fontSizeSlider|init' : function(element) {
		element.slider({  
	        //handle: '.slider_handle',  
	        value: 0.7,  
	        min: 0.5,  
	        max: 1.2,
	        step: 0.01,
//	        start: function(e, ui) {  
//	            $('#fontSizeSlider').fadeIn('fast', function() { captionVisible = true;});  
//	        },  
//	        stop: function(e, ui) {  
//	      //      if (captionVisible == false) {  
//	              //  $('#fontSizeSlider').fadeIn('fast', function() { captionVisible = true;});  
//	  
//	              //  $('#fontSizeSlider').css('left', ui.handle.css('left')).text(Math.round(ui.value * 15/100 + 8 ));  
//	  
//	                $("#body").animate({fontSize: ui.value * 15/100 + 8 });  
//	            //}  
//	            //$('#fontSizeSlider').fadeOut('fast', function() { captionVisible = false; });  
//	  
//	        },  
	  
	        slide: function(e, ui) { 
//	            $('#fontSizeSlider').css({'left' : ui.handle.css('left')} ).text(Math.round(ui.value * 15/100 + 8 ));  
	        	LOG.debug('New font size : ', ui.value);
	        	$('#projectsTable').css({fontSize: ui.value + 'em'});  
	        }  
	    });
	},
	
	'#wallSelector #select|init' : function(element) {
		element
		.button()
		.click(function() {
			alert( "Running the last action" );
		})
		.next()
			.button( {
				text: false,
				icons: {
					primary: "ui-icon-plusthick"
				}
			})
			.click(function() {
				$.historyLoad('edit/2');
			}).parent().buttonset();
	}

};