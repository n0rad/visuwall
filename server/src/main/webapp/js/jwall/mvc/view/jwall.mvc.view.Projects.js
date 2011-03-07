jwall.mvc.view.Projects = {
	table : null,

	statusClasses : [ "failure", "success", "unstable" ],

	init : function() {
		this.table = $('table#projectsTable');
	},

	addProjects : function(projects) {
		var projectsByRow = Math.round(Math.sqrt(projects.length));
		var projectTR;
		for ( var i = 0; i < projects.length; i++) {
			if (i % projectsByRow == 0) {
				projectTR = $('<tr></tr>');
				$('#projectsTable').append(projectTR);
			}
			var width = 100 / projectsByRow;
			var projectTD = this._buildProjectTD(projects[i].name,
					projects[i].description);
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
		// TODO manage adding a project to table, for the moment we reload the
		// page
		location.reload();
	},

	showCountdown : function(projectName, finishDate) {
		this.hideCompliance(projectName);
		this.hideCommiters(projectName);
		var countdownElement = this._getElement(projectName, 'p.timeleft');
		countdownElement.countdown({
			until : finishDate,
			compact : true,
			format : 'dHMS',
			onExpiry : function() {
				countdownElement.html('N/A');
			}
		}).show();
	},
	stopCountDown : function(projectName) {

	},

	updateCompliance : function(projectName, compliance) {
		this._getElement(projectName, '.compliance').html(
				'rules : ' + compliance + '%').show();
	},
	hideCompliance : function(projectName) {
		this._getElement(projectName, 'TR.complianceTR').hide();
	},

	showBuilding : function(projectName) {
		this._getElement(projectName).blink({
			fadeDownSpeed : 2000,
			fadeUpSpeed : 2000,
			blinkCount : -1,
			fadeToOpacity : 0.5
		});
	},
	stopBuilding : function(projectName) {
		this._getElement(projectName).stopBlink();
		this._getElement(projectName, '.timeleft').hide().html('');
	},

	updateCommiters : function(projectName, commiters) {
		var displayCommiter = this._buildCommiters(commiters);
		this._getElement(projectName, 'ul.commiters').html(displayCommiter)
				.marquee({
					yScroll : "bottom"
				}).show();
	},
	
	hideCommiters : function(projectName) {
		this._getElement(projectName, "TR.commitersTR").hide();
	},

	displaySuccess : function(projectName) {
		this
		this._getElement(projectName, 'ul.commiters').hide().html('');
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'success', 3000);
	},

	displayFailure : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'failure', 3000);
		this.hideCompliance(projectName);
	},

	displayUnstable : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'unstable', 3000);
	},

	displayUT : function(projectName, fail, success, skip) {
		var allTest = fail + success + skip;

		var failBar = (fail * 100) / allTest;
		var successBar = (success * 100) / allTest;
		var skipBar = (skip * 100) / allTest;

		if (success != 0) {
			this._getElement(projectName, 'TABLE.uTest TD.success').width(successBar + "%").html(success);
		} else {
			this._getElement(projectName, 'TABLE.uTest TD.success').hide().html('');
		}
		if (fail != 0) {
			this._getElement(projectName, 'TABLE.uTest TD.failure').width(failBar + "%").html(fail);
		} else {
			this._getElement(projectName, 'TABLE.uTest TD.failure').hide().html('');
		}
		if (skip != 0) {
			this._getElement(projectName, 'TABLE.uTest TD.ignore').width(skipBar + "%").html(skip);
		} else {
			this._getElement(projectName, 'TABLE.uTest TD.ignore').hide().html('');
		}
	},
	
	displayIT : function(projectName, fail, success, skip) {
		var allTest = fail + success + skip;

		var failBar = (fail * 100) / allTest;
		var successBar = (success * 100) / allTest;
		var skipBar = (skip * 100) / allTest;

		if (success != 0) {
			this._getElement(projectName, 'TABLE.iTest TD.success').width(successBar + "%").html(success);
		} else {
			this._getElement(projectName, 'TABLE.iTest TD.success').hide().html('');
		}
		if (fail != 0) {
			this._getElement(projectName, 'TABLE.iTest TD.failure').width(failBar + "%").html(fail);
		} else {
			this._getElement(projectName, 'TABLE.iTest TD.failure').hide().html('');
		}
		if (skip != 0) {
			this._getElement(projectName, 'TABLE.iTest TD.ignore').width(skipBar + "%").html(skip);
		} else {
			this._getElement(projectName, 'TABLE.iTest TD.ignore').hide().html('');
		}
	},

	setUTCoverage : function(projectName, coverage) {
		var displayCoverage = coverage;
		if (coverage == 0) {
			displayCoverage = 100;
		}
		this._getElement(projectName, 'TABLE.uTest').width(
				displayCoverage + "%");
	},

	setITCoverage : function(projectName, coverage) {
		var displayCoverage = coverage;
		if (coverage == 0) {
			displayCoverage = 100;
		}
		this._getElement(projectName, 'TABLE.iTest').width(
				displayCoverage + "%");
	},

	updateAgo : function(projectName, finishBuild) {
		var abbr = this._getElement(projectName, 'abbr.timeago');
		abbr.attr("title", ISODateString(new Date(finishBuild)));
		abbr.data("timeago", null).timeago();
	},

	// ///////////////////////////////////////////////

	_getElement : function(projectName, suffix) {
		var request = 'TD#' + projectName;
		if (suffix != undefined) {
			request += ' ' + suffix;
		}
		return $(request, this.table);
	},

	_buildProjectTD : function(projectName, description) {
		var visualName = projectName;
		if (description != '') {
			visualName = description;
		}

		var projectTD = $('<td style="display:none" id="' + projectName
				+ '" class="project"></td>');
		var projectInnerTable = $('<table class="innerTable"><tbody></tbody></table>');
		projectTD.append(projectInnerTable);
		
		projectInnerTable.append($('<tr><td class="projectName">' + visualName
				+ ' <abbr class="timeago" title=""></abbr></td></tr>'));
		projectInnerTable.append($('<tr class="commitersTR"><td><ul class="commiters marquee"></ul></tr></td>'));
		projectInnerTable.append($('<tr class="complianceTR"><td><p class="compliance"></p></tr></td>'));
		projectInnerTable.append($('<tr class= "timeleftTR"><td><p class="timeleft"></p></tr></td>'));
		projectInnerTable.append($('<tr class="iTestTR"><td class="iTestTD"><table class="iTest"><tr><td class="failure"></td><td class="ignore"></td><td class="success"></td></tr></table></tr></td>'));
		projectInnerTable.append($('<tr class="uTestTR"><td class="uTestTD"><table class="uTest"><tr><td class="failure"></td><td class="ignore"></td><td class="success"></td></tr></table></tr></td>'));
		return projectTD;
	},

	_buildCommiters : function(commiters) {
		var commiterString = "";
		for ( var i = 0; i < commiters.length; i++) {
			var commiter = commiters[i];
			commiterString += "<li>";
			commiterString += commiter;
			commiterString += "</li>";
		}
		return $(commiterString);
	}
};

$(function() {
	jwall.mvc.view.Projects.init();
});