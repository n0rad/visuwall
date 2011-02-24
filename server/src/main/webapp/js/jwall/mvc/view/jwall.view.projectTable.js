jwall.view.projectTable = {
		
		table : 'projectsTable',
		
		initProjects : function(projects) {
			var projectsPerLine = Math.round(Math.sqrt(projects.length));
			
			alert(projectsPerLine);
		},
		
		addProject : function(project) {
			//TODO manage adding a project to table, for the moment we reload the page
			 location.reload();
		},
		
		removeProject : function(project) {
			alert("removeProject" + project);
		},

		projectsCount : function() {
			var td = $(this.table + " td");
			alert(td);
		},
		
		_addrow : function() {
			$('projectsTable > tbody:last').append('<tr>...</tr><tr>...</tr>');
		}
		
		
};