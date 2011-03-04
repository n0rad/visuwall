jwall.mvc.view.Projects = {
	table : null,

	statusClasses : [ "failure", "success", "unstable"],
	
	unitStats : {},

	init : function() {
		$(window).resize(this._windowResize);
		this.table = $('table#projectsTable');
	},
	
	addProjects : function(projects) {
		var projectsByRow = Math.round(Math.sqrt(projects.length));
		var projectTR;
		for (var i = 0; i < projects.length; i++) {
			if (i % projectsByRow == 0) {
				projectTR = $('<tr></tr>');
				$('#projectsTable').append(projectTR);
			}
		var width = 100 / projectsByRow;
			var projectTD = this._buildProjectTD(projects[i]);
			projectTR.append(projectTD);
			projectTD.fadeIn("slow");
		}
	},
	
	addProject : function(project) {
		LOG.info('add project to display : ' + project.name);

		var projectTD = this._buildProjectTD(project);

		var projectTR = $('<tr></tr>');
		projectTR.append(projectTD);
		this.table.append(projectTR);
		projectTD.fadeIn("slow");		
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
	
	updateCompliance : function(projectName, compliance) {
		this._getElement(projectName, 'p.compliance').html('rules : ' + compliance + '%').show();
	},
	hideCompliance : function(projectName) {
		this._getElement(projectName, 'p.compliance').hide().html('');
	},
	
	showBuilding : function(projectName) {
		this._getElement(projectName).blink({fadeDownSpeed : 2000, fadeUpSpeed : 2000, blinkCount : -1, fadeToOpacity : 0.3});
	},
	stopBuilding : function(projectName) {
		this._getElement(projectName).stopBlink();
		this._getElement(projectName, 'p.timeleft').hide().html('');
	},
	
	updateCommiters : function(projectName, commiters) {
		var displayCommiter = this._buildCommiters(commiters);
		this._getElement(projectName, 'ul.commiters').html(displayCommiter).marquee({yScroll: "bottom"}).show();
	},
	
	displaySuccess : function(projectName) {
		this._getElement(projectName, 'ul.commiters').hide().html('');
		this._getElement(projectName, 'div.projectName').switchClasses(this.statusClasses, 'success', 3000);	
	},
	
	displayFailure : function(projectName) {
		this._getElement(projectName, 'div.projectName').switchClasses(this.statusClasses, 'failure', 3000);			
		this.hideCompliance(projectName, compliance);
	},
	
	displayUnstable : function(projectName) {
		this._getElement(projectName, 'div.projectName').switchClasses(this.statusClasses, 'unstable', 3000);	
	},
	
	displayUT : function(projectName, fail, success, skip, coverage) {
		this.unitStats[projectName] = [fail, success, skip, coverage];
		
		this._realDisplayUT(projectName);
	},

	// ///////////////////////////////////////////////

	
	_realDisplayUT : function(projectName) {

		var fail = this.unitStats[projectName][0];
		var success = this.unitStats[projectName][1];
		var skip = this.unitStats[projectName][2];
		var coverage =  this.unitStats[projectName][3];
		
		if (coverage == 0) {
			coverage = 100;
		}

		var coverageTestNum = fail + success;
		var failBar = (fail * coverage) / coverageTestNum;
		var successBar = (success * coverage) / coverageTestNum;

		jwall.mvc.view.Stats.testStatus(this._getElement(projectName, ".unitTest")[0], [[failBar], [0], [successBar]], this._getElement(projectName)[0].clientWidth);		

	},
	
	_getElement : function(projectName, suffix) {
		var request = 'TD#' + projectName;
		if (suffix != undefined) {
			request += ' ' + suffix;
		}
		return $(request, this.table);
	},
	
	_windowResize : function() {
		// remove all units stats
		$("DIV.unitTest", this.table).each(function(i , val) {
			$(this).html('');
		});

		// TODO windowResize is called from window so this is window :/
		var $this = jwall.mvc.view.Projects;
		// redraw unit stats
		$("TD", this.table).each(function(i , val) {
			var projectName = $(this).attr('id');
			$this._realDisplayUT(projectName);
		});
		$("TD", this.table).each(function(i , val) {
			var projectName = $(this).attr('id');
			$this._realDisplayUT(projectName);
		});
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
		
		
		var projectTD = $('<td style="display:none" id="' + project.name + '" class="project"><div id="content"></div></td>');
		projectTD.append($('<div class="projectName ' + status + '">' + visualName + '<span id="when"></span></div>'));
		projectTD.append($('<ul class="commiters marquee"></ul>'));
		projectTD.append($('<p class="compliance"></p>'));
		projectTD.append($('<p class="timeleft"></p>'));
		projectTD.append($('<div class="unitTest"></div>'));
		projectTD.append($('<div class="iTest"></div>'));
		
		return projectTD;
		//jwall.mvc.view.Stats.testStatus([[20.2], [10.5], [40.4]], $(".iTest", projectTD)[0]);
		
		
		
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
	
	_buildCommiters : function(commiters) {
		var commiterString = "";
		for (var i = 0; i < commiters.length; i++) {
			var commiter =  commiters[i];
			commiterString += "<li>";
			commiterString += commiter; 
			commiterString += "</li>";
		}
		return $(commiterString);
	}
};

$(function (){ 
	jwall.mvc.view.Projects.init();
});