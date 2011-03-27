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
	        	$("body").css({fontSize: ui.value + 'em'});  
	        }  
	    });
	},
	

	'INPUT#wallEdit|click' : function() {
		$.historyLoad('edit/2');
	//	$("#modal").load("security/getregister", function(data) {
		//	$(this)

//		});
		//return false;
	}
};