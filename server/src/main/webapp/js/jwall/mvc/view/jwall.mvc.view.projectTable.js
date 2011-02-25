jwall.mvc.view.projectTable = {
		
		table : 'projectsTable',
		
		initProjects : function(projects) {
			var projectsPerLine = Math.round(Math.sqrt(projects.length));
			
			LOG.info("Reading projects");
			var projectTR;
			for (var i = 0; i < projects.length; i++) {
				if (i % projectsPerLine == 0) {
					projectTR = $('<tr></tr>');
					$('#projectsTable').append(projectTR);
				}
				var projectTD = jwall.mvc.view.project.buildProject(projects[i]);
				projectTR.append(projectTD);
			}
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
		}
		
};