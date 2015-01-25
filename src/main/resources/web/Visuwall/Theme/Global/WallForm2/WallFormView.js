define(['jquery', 'Ajsl/event', 'text!./WallFormView.html',
        './Software/SoftwareView',
        './Project/ProjectView',
        
        'css!./WallFormView.css'],
function($, event, WallFormTemplate, SoftwareView, ProjectView) {

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
				this.projectView = new ProjectView($('#projectTabs', this.context));
				this.softwareView = new SoftwareView($('#softTabs', this.context), this.projectView);
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
