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

	updateCountdown : function(projectName, finishDate) {
		this._hideQuality(projectName);
		this._hideCommiters(projectName);
		var countdownElement = this._getElement(projectName, 'p.timeleft');
		countdownElement.countdown({
			until : finishDate,
			compact : true,
			format : 'dHMS',
			onExpiry : function() {
				countdownElement.html('N/A');
			}
		});
		this._showCountdown(projectName);
	},

	updateQuality : function(projectName, quality) {
		if (Object.size(quality) == 0) {
			this._hideQuality(projectName);
			return;
		}
		var qualityLi = "";
		for (var i = 0; i < quality.length; i++) {
			   var name = quality[i].value.name;
			   var value = quality[i].value.formattedValue;
			   qualityLi += '<li>' + name + ' : ' + value + '</li>';
		}
		
		//		for (var key in quality) {
//			   var obj = quality[key];
//			   qualityLi += '<li>' + key + ' : ' + quality[key] + '</li>';
//			}
		this._getElement(projectName, 'UL.quality').append($(qualityLi)).marquee({
			yScroll : 'bottom'
		});
		this._showQuality(projectName);
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
		this._hideCountDown(projectName);
	},

	updateCommiters : function(projectName, commiters) {
		if (commiters.length == 0) {
			this._hideCommiters(projectName);
			return;
		}
		var commiterString = '';
		for ( var i = 0; i < commiters.length; i++) {
			var commiter = commiters[i];
			commiterString += '<li>' + commiter + '</li>';
		}
		this._getElement(projectName, 'ul.commiters').html($(commiterString))
				.marquee({
					yScroll : 'bottom'
				});
		this._showCommiters(projectName);
	},

	displaySuccess : function(projectName) {
		this._hideCommiters(projectName);
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'success', 3000);
	},

	displayFailure : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'failure', 3000);
		this._hideQuality(projectName);
	},
	displayUnstable : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'unstable', 3000);
	},
	
	updateUT : function(projectName, fail, success, skip) {
		this._updateTest(projectName, fail, success, skip, 'u');
	},

	updateIT : function(projectName, fail, success, skip) {
		this._updateTest(projectName, fail, success, skip, 'i');
	},

	updateUTCoverage : function(projectName, coverage) {
		this._updateCoverage(projectName, coverage, 'u');
	},

	updateITCoverage : function(projectName, coverage) {
		this._updateCoverage(projectName, coverage, 'i');
	},

	updateAgo : function(projectName, finishBuild) {
		var abbr = this._getElement(projectName, 'abbr.timeago');
		abbr.attr("title", ISODateString(new Date(finishBuild)));
		abbr.data("timeago", null).timeago();
	},

	// ///////////////////////////////////////////////
	
	_updateCoverage : function(projectName, coverage, type) {
		var displayCoverage = coverage;
		if (coverage == 0) {
			displayCoverage = 100;
		}
		this._getElement(projectName, 'TABLE.' + type + 'Test').width(
				displayCoverage + "%");
	},
	
	_updateTest : function(projectName, fail, success, skip, type) {
		if (fail == 0 && success == 0 && skip == 0) {
			this['_hide' + type + 'T'](projectName);
			return;
		}	
		var allTest = fail + success + skip;
		var failBar = (fail * 100) / allTest;
		var successBar = (success * 100) / allTest;
		var skipBar = (skip * 100) / allTest;
		if (success != 0) {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').width(successBar + "%").html(success).show();
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').hide().html('');
		}
		if (fail != 0) {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').width(failBar + "%").html(fail).show();
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').hide().html('');
		}
		if (skip != 0) {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').width(skipBar + "%").html(skip).show();
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').hide().html('');
		}
		this['_show' + type + 'T'](projectName);
	},

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
		projectInnerTable.append($('<tr class="qualityTR"><td><ul class="quality marquee"></ul></tr></td>'));
		projectInnerTable.append($('<tr class= "timeleftTR"><td><p class="timeleft"></p></tr></td>'));
		projectInnerTable.append($('<tr class="iTestTR"><td class="iTestTD"><table class="iTest"><tr><td class="failure"></td><td class="ignore"></td><td class="success"></td></tr></table></tr></td>'));
		projectInnerTable.append($('<tr class="uTestTR"><td class="uTestTD"><table class="uTest"><tr><td class="failure"></td><td class="ignore"></td><td class="success"></td></tr></table></tr></td>'));
		return projectTD;
	},
	
	_hideCountDown : function(projectName) {
		this._getElement(projectName, 'TR.timeleftTR').hide();
	},
	_hideQuality : function(projectName) {
		this._getElement(projectName, 'TR.qualityTR').hide();
	},
	_showQuality : function(projectName) {
		this._getElement(projectName, 'TR.qualityTR').show();
	},
	_hideCommiters : function(projectName) {
		this._getElement(projectName, "TR.commitersTR").hide();
	},
	_showCommiters : function(projectName) {
		this._getElement(projectName, "TR.commitersTR").show();
	},
	_hideiT : function(projectName) {
		this._getElement(projectName, "TR.iTestTR").hide();
	},
	_hideuT : function(projectName) {
		this._getElement(projectName, "TR.uTestTR").hide();
	},
	_showiT : function(projectName) {
		this._getElement(projectName, "TR.iTestTR").show();
	},
	_showuT : function(projectName) {
		this._getElement(projectName, "TR.uTestTR").show();
	},
	_showCountdown : function(projectName) {
		this._getElement(projectName, "TR.timeleftTR").show();		
	},
	_hideCountdown : function(projectName) {
		this._getElement(projectName, "TR.timeleftTR").hide();		
	}
};

$(function() {
	jwall.mvc.view.Projects.init();
});