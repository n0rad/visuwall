jwall.mvc.view.Projects = {
	table : null,

	statusClasses : [ "failure", "success", "unstable"],

	init : function() {
		this.table = $('table#projectsTable');
	},
	
	addProject : function(project) {
		LOG.info('add project to display : ' + project.name);

		var projectTD = this._buildProjectTD(project);

		var projectTR = $('<tr></tr>');
		projectTR.append(projectTD);
		this.table.append(projectTR);
		projectTD.fadeIn("slow");
		
		//var projectsPerLine = Math.round(Math.sqrt(projects.length));
//		var projectTR;
//		for (var i = 0; i < projects.length; i++) {
//			if (i % projectsPerLine == 0) {
//				projectTR = $('<tr></tr>');
//				$('#projectsTable').append(projectTR);
//			}
//		var width = 100 / projectsByRow;
//			var projectTD = jwall.mvc.view.Project.buildProject(projects[i], projectsPerLine);
//			projectTR.append(projectTD);
//		}
	},
	
	removeProject : function(project) {
		alert("removeProject" + project);
		//TODO manage adding a project to table, for the moment we reload the page
		 location.reload();
	},
	
	showCountdown : function(projectName, finishDate) {
		var countdownElement = this._getElement(projectName,'p.timeleft');
		countdownElement.countdown({until: finishDate, compact: true, format: 'dHMS', onExpiry : function() {
			countdownElement.html('N/A');
		}}).show();
	},
	stopCountDown : function(projectName) {
		
	},
	
	
	showBuilding : function(projectName) {
		this._getElement(projectName).blink({fadeDownSpeed : 2000, fadeUpSpeed : 2000, blinkCount : -1, fadeToOpacity : 0.3});
	},
	stopBuilding : function(projectName) {
		this._getElement(projectName).stopBlink();
		this._getElement(projectName, 'p.timeleft').hide().html('');
	},
	
	updateCommiters : function(projectName, commiters) {
		var commiters = this._buildCommiters(project);
		this._getElement(projectName, 'p.commiters').html(commiters).show();
	},
	
	displaySuccess : function(projectName) {
		this._getElement(projectName, 'p.commiters').hide().html('');
		this._getElement(projectName).switchClasses(this.statusClasses, 'success', 3000);	
	},
	
	displayFailure : function(projectName) {
		this._getElement(projectName).switchClasses(this.statusClasses, 'failure', 3000);			
	},
	
	displayUnstable : function(projectName) {
		this._getElement(projectName).switchClasses(this.statusClasses, 'unstable', 3000);	
	},
	

	// ///////////////////////////////////////////////
	
	_getElement : function(projectName, suffix) {
		var request = 'TD#' + projectName;
		if (suffix != undefined) {
			request += ' ' + suffix;
		}
		return $(request, this.table);
	},

	_buildProjectTD : function(project) {
		var visualName = project.name;
		if (project.description != '') {
			visualName = project.description;
		}
		
		var status = 'failure';
		if (project.hudsonProject.lastBuild.successful) {
			status = 'success';
		}
		
		var projectTD = $('<td style="display:none" id="' + project.name + '" class="project ' + status + '"><div id="content"></div></td>');
		projectTD.append($('<div class="projectName">' + visualName + '<span id="when"></span></div>'));
		projectTD.append($('<p class="commiters"></p>'));
		projectTD.append($('<p class="timeleft"></p>'));
		projectTD.append($('<div class="unitTest"></div>'));
		projectTD.append($('<div class="iTest"></div>'));
		return projectTD;
		
		// jwall.mvc.view.Stats.TestStatus([[30.2], [20.5], [10.4]],
		// $(".unitTest", projectTD)[0]);
		// jwall.mvc.view.Stats.TestStatus([[20.2], [10.5], [40.4]],
		// $(".iTest", projectTD)[0]);
		
		
		// $(".commiters", projectTD).marquee({});
		
//var metrics = $('<ul class="projectMetrics"></ul>');
//metrics.append($('<li>coverage : ' + project.coverage + '</li>'));
//metrics.append($('<li>rulesCompliance : ' + project.rulesCompliance +
//'</li>'));
//metrics.append($('<li>failCount : ' +
//project.hudsonProject.lastBuild.testResult.failCount + '</li>'));
//metrics.append($('<li>skipCount : ' +
//project.hudsonProject.lastBuild.testResult.skipCount + '</li>'));
//metrics.append($('<li>totalCount : ' +
//project.hudsonProject.lastBuild.testResult.totalCount + '</li>'));
//metrics.append($('<li>passCount : ' +
//project.hudsonProject.lastBuild.testResult.passCount + '</li>'));
//metrics.append($('<li>integrationTestCount : ' +
//project.hudsonProject.lastBuild.testResult.integrationTestCount + '</li>'));
//metrics.sortable();
//projectTD.append(metrics);
	},
	
	_buildCommiters : function(project) {
		var commiterString = "";
		for (var i = 0; i < project.hudsonProject.lastBuild.commiters.length; i++) {
			var commiter =  project.hudsonProject.lastBuild.commiters[i];
			if (i > 0) {
				commiterString += ", ";
			}
			commiterString += commiter; 
		}
		return commiterString;
	}
};

$(function (){ 
	jwall.mvc.view.Projects.init();
});