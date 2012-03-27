define(['jquery', 'Ajsl/event', 'text!Visuwall/Theme/Global/WallForm2/WallFormView.html',
        'Visuwall/Theme/Global/WallForm2/Software/SoftwareView',
        
        'css!Visuwall/Theme/Global/WallForm2/WallFormView.css'],
function($, event, WallFormTemplate, SoftwareView) {

	function WallFormView(context, closePromise) {
		this.context = $(context);
		this.closePromise = closePromise;
		var self = this;

		this.events = {
			'INPUT.cancel|click' : function() {
				self.hideForm();
			},
			'#wallForm|submit' : function() {
				self.submitForm();
				return false;
			}
		};
	}
	
	WallFormView.prototype = {
			displayForm : function() {
				this.context.html(WallFormTemplate).slideDown('slow');
				event.register(this.events, this.context);
				this.softwareView = new SoftwareView($('#softTabs', this.context));
			},
			hideForm : function() {
				this.context.slideUp('fast').html("");
				this.closePromise.reject();
			},
			submitForm : function() {
				this.closePromise.resolved();
			}
	};
	
	return WallFormView;
});
