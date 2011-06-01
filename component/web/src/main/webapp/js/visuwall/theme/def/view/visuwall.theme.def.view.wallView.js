/*
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

visuwall.theme.def.view.wallView = new function() {
	var $this = this;
	this.table;

	this.statusClasses = [ 'failure-state', 'success-state', 'unstable-state', 'aborted-state', 'new-state', 'notbuilt-state', 'unknown-state' ];
	
	$(function() {
		$this.table = $('ul#projectsTable').sortable().disableSelection();
	});

//	// TODO V2
//	$('ul#projectsTable li:last-child').live('click', function() {
//	    $(this).prependTo('ul#projectsTable');
//	});

	
	this.addProject = function(name, description) {
		LOG.info('add project to display : ' + name);
		
		var newCss = $this._runResize($('LI.project', $this.table).length + 1);
		
		var projectLI = $this._buildProjectTD(name, description);
		projectLI.css(newCss);
		$this.table.append(projectLI);
		projectLI.fadeIn("slow");
	};
	
	/**
	 * return newCss to apply to a new Project
	 */
	this._runResize = function(NumberOfProjects) {
		var currentProjects = $('LI.project', $this.table);
		
		var projectsByRow = Math.ceil(Math.sqrt(NumberOfProjects));
		var rows =  Math.ceil((NumberOfProjects) / projectsByRow);
		
		var newCss = {width : (99 / projectsByRow) + '%', height : 97 / rows + '%', margin : (1 / (rows * 2)) + '% ' + (1 / (projectsByRow * 2)) + '%'};
		currentProjects.stop(true, false);
		currentProjects.css({opacity: '1', display : 'inline-block'});
		currentProjects.animate(newCss);
		return newCss;
	};

	this.removeProject = function(projectName) {
		$this._runResize($('LI.project', $this.table).length - 1);
		$this._getElement(projectName).fadeOut("slow").remove();		
	};

	this.updateCountdown = function(projectName, finishDate) {
		$this._hideQuality(projectName);
		$this._hideCommiters(projectName);
		var countdownElement = $this._getElement(projectName, 'p.timeleft');
		countdownElement.countdown({
			until : finishDate,
			compact : true,
			format : 'dHMS',
			onExpiry : function() {
				countdownElement.html('N/A');
			}
		});
		$this._showCountdown(projectName);
	};
	
	this.updateBuildTime = function(projectName, duration) {
		var good = buildVisualDuration(duration);
		$this._getElement(projectName, 'span.duration').replaceWith(' ~ ' + good);
	};
	
	this.updateQuality = function(projectName, quality) {
		if (Object.size(quality) == 0) {
			$this._hideQuality(projectName);
			return;
		}
		var qualityLi = '';
		for (var i = 0; i < quality.length; i++) {
			   var name = quality[i].value.name;
			   var value = quality[i].value.formattedValue;
			   qualityLi += '<li>' + name + ': ' + value + '</li>';
		}
		$this._getElement(projectName, 'UL.quality').append($(qualityLi)).marquee({
			yScroll : 'bottom'
		});
		$this._showQuality(projectName);
	};

	this.showBuilding = function(projectName) {
		$this._getElement(projectName).blink({
			fadeDownSpeed : 2000,
			fadeUpSpeed : 2000,
			blinkCount : -1,
			fadeToOpacity : 0.5
		});
	};
	this.stopBuilding = function(projectName) {
		$this._getElement(projectName).stopBlink();
		$this._hideCountDown(projectName);
	};

	this.updateCommiters = function(projectName, commiters) {
		if (commiters.length == 0) {
			$this._hideCommiters(projectName);
			return;
		}
		var commiterString = '';
		for ( var i = 0; i < commiters.length; i++) {
			var commiter = commiters[i];
			commiterString += '<li>' + commiter + '</li>';
		}
		$this._getElement(projectName, 'ul.commiters').html($(commiterString))
				.marquee({
					yScroll : 'bottom'
				});
		$this._showCommiters(projectName);
	};

	this.displaySuccess = function(projectName) {
		$this._hideCommiters(projectName);
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'success-state', 3000);
	};

	this.displayFailure = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'failure-state', 3000);
		$this._hideQuality(projectName);
	};
	this.displayUnstable = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'unstable-state', 3000);
	};
	
	this.displayNew = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'new-state', 3000);
	};
	
	this.displayAborted = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'aborted-state', 3000);
	};
	
	this.displayNotBuilt = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'notbuilt-state', 3000);
	};
	
	this.displayUnknown = function(projectName) {
		$this._getElement(projectName, '.projectName').switchClasses(
				$this.statusClasses, 'unknown-state', 3000);
	};
	
	this.updateUT = function(projectName, fail, success, skip) {
		$this._updateTest(projectName, fail, success, skip, 'u');
	};

	this.updateIT = function(projectName, fail, success, skip) {
		$this._updateTest(projectName, fail, success, skip, 'i');
	};

	this.updateUTCoverage = function(projectName, coverage) {
		$this._updateCoverage(projectName, coverage, 'u');
	};

	this.updateITCoverage = function(projectName, coverage) {
		$this._updateCoverage(projectName, coverage, 'i');
	};

	this.updateUTDiff = function(projectName, failDiff, successDiff, skipDiff) {
		$this._updateTestDiff(projectName, failDiff, successDiff, skipDiff, 'u');
	};
	
	this.updateAgo = function(projectName, finishBuild) {
		var abbr = $this._getElement(projectName, 'abbr.timeago');
		if (finishBuild == 0) {
			abbr.html('never');
			return;
		}
		abbr.attr("title", ISODateString(new Date(finishBuild)));
		abbr.data("timeago", null).timeago();
	};
	
	// ///////////////////////////////////////////////
	
	this._updateTestDiff = function(projectName, failDiff, successDiff, skipDiff, type) {
		if (successDiff) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.success SPAN.diff').html(this._getBracketed(this._getSignedInt(successDiff))).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.success SPAN.diff').hide();
		}
		
		if (failDiff) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure SPAN.diff').html(this._getBracketed(this._getSignedInt(failDiff))).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure SPAN.diff').hide();			
		}
		
		if (skipDiff) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore SPAN.diff').html(this._getBracketed(this._getSignedInt(skipDiff))).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore SPAN.diff').hide();			
		}
	};
	
	this._getBracketed = function (value) {
		if (value == null) {
			return;
		}
		return '(' + value + ')';
	};
	
	this._getSignedInt = function (value) {
		if (value > 0) {
			return '+' + value;
		} else if (value < 0) {
			return '' + value;
		} else {
			return null;
			//return '±0';
		}
	};
	
	this._updateCoverage = function(projectName, coverage, type) {
		var displayCoverage = coverage;
		if (coverage == undefined || coverage == 0) {
			displayCoverage = 100;
		}
		$this._getElement(projectName, 'TABLE.' + type + 'Test').animate({width : displayCoverage + "%"}, 3000);
	};
	
	this._updateTest = function(projectName, fail, success, skip, type) {
		if (fail == 0 && success == 0 && skip == 0) {
			$this['_hide' + type + 'T'](projectName);
			return;
		}	
		var allTest = fail + success + skip;
		var failBar = (fail * 100) / allTest;
		var successBar = (success * 100) / allTest;
		var skipBar = (skip * 100) / allTest;
		if (success != 0) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.success SPAN.num').html(success);
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').animate({width: successBar + "%"}, 2000).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.success').hide();
		}
		if (fail != 0) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure SPAN.num').html(fail);
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').animate({width: failBar + "%"}, 2000).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.failure').hide();
		}
		if (skip != 0) {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore SPAN.num').html(skip);
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').animate({width: skipBar + "%"}, 2000).fadeIn("slow");
		} else {
			$this._getElement(projectName, 'TABLE.' + type + 'Test TD.ignore').hide();
		}
		$this['_show' + type + 'T'](projectName);
	};

	this._getElement = function(projectName, suffix) {
		var request = 'LI[id="' + projectName + '"]';
		if (suffix != undefined) {
			request += ' ' + suffix;
		}
		return $(request, $this.table);
	};

	this._buildProjectTD = function(projectName, description) {
		var visualName = projectName;
//		if (description && description != '') {
//			visualName = description;
//		}

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
	};
	
	this._hideCountDown = function(projectName) {
		$this._getElement(projectName, 'TR.timeleftTR').hide();
	};
	this._hideQuality = function(projectName) {
		$this._getElement(projectName, 'TR.qualityTR').hide();
	};
	this._showQuality = function(projectName) {
		$this._getElement(projectName, 'TR.qualityTR').show();
	};
	this._hideCommiters = function(projectName) {
		$this._getElement(projectName, "TR.commitersTR").hide();
	};
	this._showCommiters = function(projectName) {
		$this._getElement(projectName, "TR.commitersTR").show();
	};
	this._hideiT = function(projectName) {
		$this._getElement(projectName, "TR.iTestTR").hide();
	};
	this._hideuT = function(projectName) {
		$this._getElement(projectName, "TR.uTestTR").hide();
	};
	this._showiT = function(projectName) {
		$this._getElement(projectName, "TR.iTestTR").show();
	};
	this._showuT = function(projectName) {
		$this._getElement(projectName, "TR.uTestTR").show();
	};
	this._showCountdown = function(projectName) {
		$this._getElement(projectName, "TR.timeleftTR").show();		
	};
	this._hideCountdown = function(projectName) {
		$this._getElement(projectName, "TR.timeleftTR").hide();		
	};
};
