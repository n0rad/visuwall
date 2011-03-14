jwall.mvc.view.Projects = {
	table : null,

	statusClasses : [ 'failure', 'success', 'unstable', 'aborted', 'new' ],
	
	init : function() {
		this.table = $('ul#projectsTable').sortable().disableSelection();
	},
	
	addProject : function(name, description) {
		LOG.info('add project to display : ' + name);
		
		var newCss = this._runResize($('LI.project', this.table).length + 1);
		
		var projectLI = this._buildProjectTD(name, description);
		projectLI.css(newCss);
		this.table.append(projectLI);
		projectLI.fadeIn("slow");
	},
	
	/**
	 * return newCss to apply to a new Project
	 */
	_runResize : function(NumberOfProjects) {
		var currentProjects = $('LI.project', this.table);
		
		var projectsByRow = Math.round(Math.sqrt(NumberOfProjects));
		var rows =  Math.round((NumberOfProjects) / projectsByRow);
		
		var newCss = {width : (99 / projectsByRow) + '%', height : 97 / rows + '%', margin : (1 / (rows * 2)) + '% ' + (1 / (projectsByRow * 2)) + '%'};
		currentProjects.stop(true, false);
		currentProjects.css({opacity: '1', display : 'inline-block'});
		currentProjects.animate(newCss);
		return newCss;
	},

	removeProject : function(projectName) {
		this._runResize($('LI.project', this.table).length - 1);
		this._getElement(projectName).fadeOut("slow").remove();		
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
	
	updateBuildTime : function(projectName, duration) {
		var good = buildVisualDuration(duration);
		this._getElement(projectName, 'span.duration').replaceWith(' ~ ' + good);
	},
	
	updateQuality : function(projectName, quality) {
		if (Object.size(quality) == 0) {
			this._hideQuality(projectName);
			return;
		}
		var qualityLi = '';
		for (var i = 0; i < quality.length; i++) {
			   var name = quality[i].value.name;
			   var value = quality[i].value.formattedValue;
			   qualityLi += '<li>' + name + ': ' + value + '</li>';
		}
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
	
	displayNew : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'new', 3000);
	},
	
	displayAborted : function(projectName) {
		this._getElement(projectName, '.projectName').switchClasses(
				this.statusClasses, 'aborted', 3000);
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

	updateUTDiff : function(projectName, failDiff, successDiff, skipDiff) {
		this._updateTestDiff(projectName, failDiff, successDiff, skipDiff, 'u');
	},
	
	updateAgo : function(projectName, finishBuild) {
		var abbr = this._getElement(projectName, 'abbr.timeago');
		if (finishBuild == 0) {
			abbr.append('never');
			return;
		}
		abbr.attr("title", ISODateString(new Date(finishBuild)));
		abbr.data("timeago", null).timeago();
	},

	// ///////////////////////////////////////////////
	
	_updateTestDiff : function(projectName, failDiff, successDiff, skipDiff, type) {
		this._getElement(projectName, 'TABLE.' + type + 'Test TD.success SPAN.diff').html(successDiff);
		this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure SPAN.diff').html(failDiff);
		this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore SPAN.diff').html(skipDiff);
	},
	
	_updateCoverage : function(projectName, coverage, type) {
		var displayCoverage = coverage;
		if (coverage == 0) {
			displayCoverage = 100;
		}
		this._getElement(projectName, 'TABLE.' + type + 'Test').animate({width : displayCoverage + "%"}, 3000);
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
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.success SPAN.num').html(success);
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').animate({width: successBar + "%"}, 2000).fadeIn("slow");
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').hide();
		}
		if (fail != 0) {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure SPAN.num').html(fail);
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').animate({width: failBar + "%"}, 2000).fadeIn("slow");
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').hide();
		}
		if (skip != 0) {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore SPAN.num').html(skip);
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').animate({width: skipBar + "%"}, 2000).fadeIn("slow");
		} else {
			this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').hide();
		}
		this['_show' + type + 'T'](projectName);
	},

	_getElement : function(projectName, suffix) {
		var request = 'LI#' + projectName;
		if (suffix != undefined) {
			request += ' ' + suffix;
		}
		return $(request, this.table);
	},

	_buildProjectTD : function(projectName, description) {
		var visualName = projectName;
		if (description && description != '') {
			visualName = description;
		}

		var projectTD = $('<li style="display:none" id="' + projectName
				+ '" class="project"></li>');
		var projectInnerTable = $('<table class="innerTable"><tbody></tbody></table>');
		projectTD.append(projectInnerTable);
		
		projectInnerTable.append($('<tr><td class="projectName">' + visualName
				+ ' <div class="inlineInfo"><abbr class="timeago" title=""></abbr> <span class="duration"></span><div></td></tr>'));
		projectInnerTable.append($('<tr style="display:none" class="commitersTR"><td><ul class="commiters marquee"></ul></tr></td>'));
		projectInnerTable.append($('<tr style="display:none" class="qualityTR"><td><ul class="quality marquee"></ul></tr></td>'));
		projectInnerTable.append($('<tr style="display:none" class= "timeleftTR"><td><p class="timeleft"></p></tr></td>'));
		projectInnerTable.append($('<tr style="display:none" class="iTestTR"><td class="iTestTD"><table class="iTest"><tr><td class="failure"><span class="num"></span><span class="diff"></span></td><td class="ignore"><span class="num"></span><span class="diff"></span></td><td class="success"><span class="num"></span><span class="diff"></span></td></tr></table></tr></td>'));
		projectInnerTable.append($('<tr style="display:none" class="uTestTR"><td class="uTestTD"><table class="uTest"><tr><td class="failure"><span class="num"></span><span class="diff"></span></td><td class="ignore"><span class="num"></span><span class="diff"></span></td><td class="success"><span class="num"></span><span class="diff"></span></td></tr></table></tr></td>'));
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