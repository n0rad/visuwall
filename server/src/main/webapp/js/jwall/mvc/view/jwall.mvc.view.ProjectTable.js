jwall.mvc.view.ProjectTable = {
		
		table : $('table#projectsTable'),
		
		initProjects : function(projects) {
			var projectsPerLine = Math.round(Math.sqrt(projects.length));
			
			LOG.info("Reading projects");
			var projectTR;
			for (var i = 0; i < projects.length; i++) {
				if (i % projectsPerLine == 0) {
					projectTR = $('<tr></tr>');
					$('#projectsTable').append(projectTR);
				}
				var projectTD = jwall.mvc.view.Project.buildProject(projects[i], projectsPerLine);
				projectTR.append(projectTD);
			}
		},
		
		updateStatus : function(projectsStatus) {
			LOG.debug("Update projects status");
			$("td", this.table).each(function() {
				for (var i = 0; i < projectsStatus.length; i++) {
					if (this.id == projectsStatus[i].name) {
						jwall.mvc.view.Project.updateStatus($(this), projectsStatus[i]);
					}
				}
			});
			
			//TODO find new projects
			//TODO find removed projects
			
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