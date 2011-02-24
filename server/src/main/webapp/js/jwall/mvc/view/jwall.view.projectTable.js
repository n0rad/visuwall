jwall.view.projectTable = {
		
		table : 'projectsTable',
		
		addProjects : function(projects) {
			
		},
		
		addProject : function(project) {
			var projectNumb = this.projectCount() + 1;
			var projectsPerLine = Math.round(Math.sqrt(projectNumb));
		},
		
		removeProject : function(project) {
			alert("removeProject" + project);
		},

		projectCount : function() {
			var td = $(this.table + " td");
			alert(td);
		},
		
		_addrow : function() {
			$('projectsTable > tbody:last').append('<tr>...</tr><tr>...</tr>');
		}
		
		
};