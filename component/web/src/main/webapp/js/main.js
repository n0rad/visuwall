define([ 'js!Underscore.js!order', //
         'js!jquery-1.4.3.js!order', //
         'js!Backbone.js!order'], function() {

	define('backbone', function() {
		Backbone.noConflict();
	});

	define('jquery', function() {
		var jquery = $;
		$.noConflict();
		return jquery;
	});

	define('underscore', function() {
//		var underscore = _;
//		underscore.noConflict();
		return _;
	});
	
	define('jqueryui', ['js!jquery/ui/jquery.ui.core.js!order', //
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

	define('log', [ 'Ajsl/Log' ], function(Log) {
		return window.console;
	});

	return {
		start : function(data) {
			curl([ 'Visuwall' ], function(Visuwall) {
				new Visuwall(data);
			});
		}
	};
});
