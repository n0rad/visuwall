define(['require', //	
        'js!Underscore.js!order', //
        'js!jquery-1.4.3.js!order', //
        //'js!Backbone.js!order'
        ], function(require) {

//	define('backbone', function() {
//		Backbone.noConflict();
//	});

	
	
	define('jquery', ['css!jquery.marquee.min.css',
	                  'css!jquery.selectBox.css',
	                  
	                  'js!jquery/jquery.ajsl.js',
	                  'js!jquery/jquery.cookie.js',
	                  'js!jquery/jquery.marquee.js',
	                  'js!jquery/jquery.timeago.js',
	                  'js!jquery/jquery.selector.js',
	                  'js!jquery/jquery.selectBox.js',
	                  'js!jquery/jquery.countdown.js',
	                  'js!jquery/jquery.textfill.0.1.js',
	                  'js!jquery/jquery.qtip-1.0.0-rc3.js',
	                  'js!jquery/jquery.selectbox-0.6.1.js',
	                  'js!jquery/jquery.history.js!order',
	                  'js!jquery/jquery.history.extended.js!order'
	                  ], function() {
		var jquery = $;
		$.noConflict();
		return jquery;
	});

	define('underscore', function() {
//		var underscore = _;
//		underscore.noConflict();
		return _;
	});
	
	define('jqueryui', ['css!jquery/jquery-ui-1.8.11.custom.css', //
	                    
	                    'js!jquery/ui/jquery.ui.core.js!order', //
	                    'js!jquery/ui/jquery.ui.widget.js!order', //
	                    'js!jquery/ui/jquery.effects.core.js!order', //

	                    'js!jquery/ui/sub/jquery.ui.tabs.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.mouse.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.dialog.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.slider.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.button.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.position.js!order', //
	                    'js!jquery/ui/sub/jquery.multiselect.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.sortable.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.draggable.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.resizable.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.droppable.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.accordion.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.datepicker.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.selectable.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.progressbar.js!order', //
	                    'js!jquery/ui/sub/jquery.ui.autocomplete.js!order', //
	                    
	                    
	                    'js!jquery/ui/effect/jquery.effects.clip.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.fold.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.drop.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.fade.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.slide.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.scale.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.blind.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.shake.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.bounce.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.explode.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.pulsate.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.transfer.js!order', //
	                    'js!jquery/ui/effect/jquery.effects.highlight.js!order' //

	                    ], function() {
		
	});

	define('log', [ 'Ajsl/Log' ], function(log) {
		return log;
	});

	return {
		start : function(data) {
			require([ 'Visuwall' ], function(Visuwall) {
				new Visuwall(data);
			});
		}
	};
});
