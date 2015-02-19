define(['jquery', //
        'log', //
        'jqueryui' //
        ], function($, log) {
	"use strict";

	var get_gravatar = function(email, size) {
	    var MD5=function(s){function L(k,d){return(k<<d)|(k>>>(32-d))}function K(G,k){var I,d,F,H,x;F=(G&2147483648);H=(k&2147483648);I=(G&1073741824);d=(k&1073741824);x=(G&1073741823)+(k&1073741823);if(I&d){return(x^2147483648^F^H)}if(I|d){if(x&1073741824){return(x^3221225472^F^H)}else{return(x^1073741824^F^H)}}else{return(x^F^H)}}function r(d,F,k){return(d&F)|((~d)&k)}function q(d,F,k){return(d&k)|(F&(~k))}function p(d,F,k){return(d^F^k)}function n(d,F,k){return(F^(d|(~k)))}function u(G,F,aa,Z,k,H,I){G=K(G,K(K(r(F,aa,Z),k),I));return K(L(G,H),F)}function f(G,F,aa,Z,k,H,I){G=K(G,K(K(q(F,aa,Z),k),I));return K(L(G,H),F)}function D(G,F,aa,Z,k,H,I){G=K(G,K(K(p(F,aa,Z),k),I));return K(L(G,H),F)}function t(G,F,aa,Z,k,H,I){G=K(G,K(K(n(F,aa,Z),k),I));return K(L(G,H),F)}function e(G){var Z;var F=G.length;var x=F+8;var k=(x-(x%64))/64;var I=(k+1)*16;var aa=Array(I-1);var d=0;var H=0;while(H<F){Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=(aa[Z]|(G.charCodeAt(H)<<d));H++}Z=(H-(H%4))/4;d=(H%4)*8;aa[Z]=aa[Z]|(128<<d);aa[I-2]=F<<3;aa[I-1]=F>>>29;return aa}function B(x){var k="",F="",G,d;for(d=0;d<=3;d++){G=(x>>>(d*8))&255;F="0"+G.toString(16);k=k+F.substr(F.length-2,2)}return k}function J(k){k=k.replace(/rn/g,"n");var d="";for(var F=0;F<k.length;F++){var x=k.charCodeAt(F);if(x<128){d+=String.fromCharCode(x)}else{if((x>127)&&(x<2048)){d+=String.fromCharCode((x>>6)|192);d+=String.fromCharCode((x&63)|128)}else{d+=String.fromCharCode((x>>12)|224);d+=String.fromCharCode(((x>>6)&63)|128);d+=String.fromCharCode((x&63)|128)}}}return d}var C=Array();var P,h,E,v,g,Y,X,W,V;var S=7,Q=12,N=17,M=22;var A=5,z=9,y=14,w=20;var o=4,m=11,l=16,j=23;var U=6,T=10,R=15,O=21;s=J(s);C=e(s);Y=1732584193;X=4023233417;W=2562383102;V=271733878;for(P=0;P<C.length;P+=16){h=Y;E=X;v=W;g=V;Y=u(Y,X,W,V,C[P+0],S,3614090360);V=u(V,Y,X,W,C[P+1],Q,3905402710);W=u(W,V,Y,X,C[P+2],N,606105819);X=u(X,W,V,Y,C[P+3],M,3250441966);Y=u(Y,X,W,V,C[P+4],S,4118548399);V=u(V,Y,X,W,C[P+5],Q,1200080426);W=u(W,V,Y,X,C[P+6],N,2821735955);X=u(X,W,V,Y,C[P+7],M,4249261313);Y=u(Y,X,W,V,C[P+8],S,1770035416);V=u(V,Y,X,W,C[P+9],Q,2336552879);W=u(W,V,Y,X,C[P+10],N,4294925233);X=u(X,W,V,Y,C[P+11],M,2304563134);Y=u(Y,X,W,V,C[P+12],S,1804603682);V=u(V,Y,X,W,C[P+13],Q,4254626195);W=u(W,V,Y,X,C[P+14],N,2792965006);X=u(X,W,V,Y,C[P+15],M,1236535329);Y=f(Y,X,W,V,C[P+1],A,4129170786);V=f(V,Y,X,W,C[P+6],z,3225465664);W=f(W,V,Y,X,C[P+11],y,643717713);X=f(X,W,V,Y,C[P+0],w,3921069994);Y=f(Y,X,W,V,C[P+5],A,3593408605);V=f(V,Y,X,W,C[P+10],z,38016083);W=f(W,V,Y,X,C[P+15],y,3634488961);X=f(X,W,V,Y,C[P+4],w,3889429448);Y=f(Y,X,W,V,C[P+9],A,568446438);V=f(V,Y,X,W,C[P+14],z,3275163606);W=f(W,V,Y,X,C[P+3],y,4107603335);X=f(X,W,V,Y,C[P+8],w,1163531501);Y=f(Y,X,W,V,C[P+13],A,2850285829);V=f(V,Y,X,W,C[P+2],z,4243563512);W=f(W,V,Y,X,C[P+7],y,1735328473);X=f(X,W,V,Y,C[P+12],w,2368359562);Y=D(Y,X,W,V,C[P+5],o,4294588738);V=D(V,Y,X,W,C[P+8],m,2272392833);W=D(W,V,Y,X,C[P+11],l,1839030562);X=D(X,W,V,Y,C[P+14],j,4259657740);Y=D(Y,X,W,V,C[P+1],o,2763975236);V=D(V,Y,X,W,C[P+4],m,1272893353);W=D(W,V,Y,X,C[P+7],l,4139469664);X=D(X,W,V,Y,C[P+10],j,3200236656);Y=D(Y,X,W,V,C[P+13],o,681279174);V=D(V,Y,X,W,C[P+0],m,3936430074);W=D(W,V,Y,X,C[P+3],l,3572445317);X=D(X,W,V,Y,C[P+6],j,76029189);Y=D(Y,X,W,V,C[P+9],o,3654602809);V=D(V,Y,X,W,C[P+12],m,3873151461);W=D(W,V,Y,X,C[P+15],l,530742520);X=D(X,W,V,Y,C[P+2],j,3299628645);Y=t(Y,X,W,V,C[P+0],U,4096336452);V=t(V,Y,X,W,C[P+7],T,1126891415);W=t(W,V,Y,X,C[P+14],R,2878612391);X=t(X,W,V,Y,C[P+5],O,4237533241);Y=t(Y,X,W,V,C[P+12],U,1700485571);V=t(V,Y,X,W,C[P+3],T,2399980690);W=t(W,V,Y,X,C[P+10],R,4293915773);X=t(X,W,V,Y,C[P+1],O,2240044497);Y=t(Y,X,W,V,C[P+8],U,1873313359);V=t(V,Y,X,W,C[P+15],T,4264355552);W=t(W,V,Y,X,C[P+6],R,2734768916);X=t(X,W,V,Y,C[P+13],O,1309151649);Y=t(Y,X,W,V,C[P+4],U,4149444226);V=t(V,Y,X,W,C[P+11],T,3174756917);W=t(W,V,Y,X,C[P+2],R,718787259);X=t(X,W,V,Y,C[P+9],O,3951481745);Y=K(Y,h);X=K(X,E);W=K(W,v);V=K(V,g)}var i=B(Y)+B(X)+B(W)+B(V);return i.toLowerCase()};
	    var size = size || 80;
	    return 'http://www.gravatar.com/avatar/' + MD5(email) + '?d=retro&s=' + size;
	}


	var bodyBackClass = 'visuwallBack';

	var buildVisualDuration = function(duration) {
		var inst = {
			options : {
				compact : true,
				format : 'dhms'
			},
			_periods : [ 0, 0, 0, 0, 0, 0, 0 ],
			_show : [ '!', '!', '!', '!', '!', '!', '!' ]
		};

		inst._now = new Date();
		inst._until = new Date(inst._now.getTime() + duration);
		inst._show = jQuery.countdown._determineShow(inst);
		var res = jQuery.countdown
				._calculatePeriods(inst, inst._show, 0, inst._now);
		var html = jQuery.countdown._generateHTML(inst);
		var good = strip(html);
		if (inst._periods[0] == 0 && inst._periods[1] == 0 && inst._periods[2] == 0
				&& inst._periods[3] == 0 && inst._periods[4] == 0
				&& inst._periods[5] == 0) {
			good += 's';
		}
		return good;
	};


	var wallView = new function() {
			var $this = this;
			this.table;

			this.statusClasses = [ 'failure-state', 'success-state', 'unstable-state',
					'aborted-state', 'new-state', 'notbuilt-state', 'unknown-state' ];

			$(function() {
				$this.table = $('ul#projectsTable').sortable().disableSelection();
			});

			this.setLastUpdate = function(projectId, date) {
				var lastUpdate = $this._getElement(projectId, 'SPAN.lastUpdate');
				lastUpdate.html(date);
			};

			this.getLastUpdate = function(projectId, callback) {
				var lastUpdate = $this._getElement(projectId, 'SPAN.lastUpdate');
				callback(lastUpdate.html());
			};

			this.isProject = function(projectId, callback) {
				var isproject = $this._getElement(projectId, '').length > 0;
				callback(isproject);
			};

			this.getProjectIds = function(callback) {
				var res = [];
				var projects = $('LI.project', $this.table);
				for ( var i = 0; i < projects.length; i++) {
					res[i] = projects[i].id;
				}
				callback(res);
			};

			this.addProject = function(projectId, name) {
				$('BODY').removeClass(bodyBackClass);
				log.info('add project to display : ' + projectId);

				var newCss = $this._runResize($('LI.project', $this.table).length + 1);

				var projectLI = $this._buildProjectTD(projectId, name);
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
				var rows = Math.ceil((NumberOfProjects) / projectsByRow);

				var newCss = {
					width : (99 / projectsByRow) + '%',
					height : 97 / rows + '%',
					margin : (1 / (rows * 2)) + '% ' + (1 / (projectsByRow * 2)) + '%'
				};
				currentProjects.stop(true, false);
				currentProjects.css({
					opacity : '1',
					display : 'inline-block'
				});

				currentProjects.animate(newCss, 1500, function() {
//					$('.projectName', currentProjects).textfill();
				});

//				currentProjects.animate(newCss, {
//					step : function(now, fx) {
//						$('.projectName', fx.elem).textfill();
////						var data = fx.elem.id + ' ' + fx.prop + ': ' + now;
////						$('body').append('<div>' + data + '</div>');
//					}
//				});

				// .textfill({ maxFontPixels: 70 })

				return newCss;
			};

			this.removeProject = function(projectId) {
				$this._runResize($('LI.project', $this.table).length - 1);
				$this._getElement(projectId).fadeOut("slow").remove();

				if ($('LI.project', $this.table).length == 0) {
					$('BODY').addClass(bodyBackClass);
				}
			};

			this.removeAllProjects = function() {
				$('LI.project', $this.table).remove();
				$('BODY').addClass(bodyBackClass);
			};

			this.setCountdown = function(projectId, finishDate) {
				$this._hideQuality(projectId);
				$this._hideCommiters(projectId);
				var countdownElement = $this._getElement(projectId, 'p.timeleft');
				countdownElement.countdown({
					until : finishDate,
					compact : true,
					format : 'dHMS',
					onExpiry : function() {
						countdownElement.html('N/A');
					}
				});
				$this._showCountdown(projectId);
			};

			this.updateBuildTime = function(projectId, duration) {
				var good = buildVisualDuration(duration);
				$this._getElement(projectId, 'span.duration').html(' ~ ' + good);
			};

			this.updateQuality = function(projectId, quality) {
				if (Object.size(quality) == 0) {
					$this._hideQuality(projectId);
					return;
				}
				var qualityLi = '';
				for ( var i = 0; i < quality.length; i++) {
					var name = quality[i].value.name;
					var value = quality[i].value.formattedValue;
					qualityLi += '<li>' + name + ': ' + value + '</li>';
				}
				$this._getElement(projectId, 'UL.quality').append($(qualityLi))
						.marquee({
							yScroll : 'bottom'
						});
				$this._showQuality(projectId);
			};

			this.showBuilding = function(projectId) {
				$this._getElement(projectId).blink({
					fadeDownSpeed : 2000,
					fadeUpSpeed : 2000,
					blinkCount : -1,
					fadeToOpacity : 0.5
				});
			};
			this.stopBuilding = function(projectId) {
				$this._getElement(projectId).stopBlink();
				$this._hideCountDown(projectId);
			};

			this.updateCommiters = function(projectId, commiters, state) {
				var nameDiv = $this._getElement(projectId, '.projectName');
				var statusNeedCommiters = false;
				if (state == 'FAILURE' || state == 'UNSTABLE') {
					statusNeedCommiters = true;
				}
				if (commiters.length == 0 || !statusNeedCommiters) {
					$this._hideCommiters(projectId);
					return;
				}
				var commiterString = '';
				for ( var i = 0; i < commiters.length; i++) {
					var commiter = commiters[i];

                    var gravatarId;
                    if (commiter.email != null && commiter.email != "") {
                        gravatarId = commiter.email;
                    } else if (commiter.username != null && commiter.username != "") {
                        // username as a fallback should give us unique 8-bit faces
                            // even for people without their TeamCity and Git accounts
                                // hooked up.
                                    gravatarId = commiter.username;
                    } else {
                        gravatarId = "";
                    }

					commiterString += '<li><img src="'
							+ get_gravatar(commiter.email == null ? "" : commiter.email, 250)
							+ '" style="height:100%" /></li>';
				}
				$this._getElement(projectId, 'ul.commiters').html($(commiterString))
						.marquee({
							yScroll : 'bottom'
						});
			};

			this.displaySuccess = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'success-state', 3000);

				$this._hideCommiters(projectId);
			};

			this.displayFailure = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'failure-state', 3000);
				$this._getElement(projectId).prependTo($this.table);

				$this._hideQuality(projectId);
				$this._showCommiters(projectId);
			};
			this.displayUnstable = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'unstable-state', 3000);
				$this._getElement(projectId).prependTo($this.table);

				$this._showCommiters(projectId);
			};

			this.displayNew = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'new-state', 3000);
			};

			this.displayAborted = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'aborted-state', 3000);
				$this._getElement(projectId).prependTo($this.table);
			};

			this.displayNotbuilt = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'notbuilt-state', 3000);
				$this._getElement(projectId).prependTo($this.table);
			};

			this.displayUnknown = function(projectId) {
				$this._getElement(projectId, 'TABLE').switchClasses(
						$this.statusClasses, 'unknown-state', 3000);
				$this._getElement(projectId).prependTo($this.table);
			};

			this.updateUT = function(projectId, fail, success, skip) {
				$this._updateTest(projectId, fail, success, skip, 'u');
			};

			this.updateIT = function(projectId, fail, success, skip) {
				$this._updateTest(projectId, fail, success, skip, 'i');
			};

			this.updateUTCoverage = function(projectId, coverage) {
				if (coverage == 0) {
					coverage = 100;
				}
				$this._updateCoverage(projectId, coverage, 'u');
			};

			this.updateITCoverage = function(projectId, coverage) {
				if (coverage == 0) {
					coverage = 100;
				}
				$this._updateCoverage(projectId, coverage, 'i');
			};

			this.updateUTDiff = function(projectId, failDiff, successDiff, skipDiff) {
				$this._updateTestDiff(projectId, failDiff, successDiff, skipDiff, 'u');
			};

			this.updateAgo = function(projectId, finishBuild) {
				var abbr = $this._getElement(projectId, 'abbr.timeago');
				if (finishBuild == 0) {
					abbr.html('never');
					return;
				}
				abbr.attr("title", ISODateString(new Date(finishBuild)));
				abbr.data("timeago", null).timeago();
			};

			// ///////////////////////////////////////////////

			this._updateTestDiff = function(projectId, failDiff, successDiff, skipDiff,
					type) {
				if (successDiff) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.success SPAN.diff').html(
							this._getBracketed(this._getSignedInt(successDiff)))
							.fadeIn("slow");
				} else {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.success SPAN.diff').hide();
				}

				if (failDiff) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.failure SPAN.diff').html(
							this._getBracketed(this._getSignedInt(failDiff))).fadeIn(
							"slow");
				} else {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.failure SPAN.diff').hide();
				}

				if (skipDiff) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.ignore SPAN.diff').html(
							this._getBracketed(this._getSignedInt(skipDiff))).fadeIn(
							"slow");
				} else {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.ignore SPAN.diff').hide();
				}
			};

			this._getBracketed = function(value) {
				if (value == null) {
					return;
				}
				return '(' + value + ')';
			};

			this._getSignedInt = function(value) {
				if (value > 0) {
					return '+' + value;
				} else if (value < 0) {
					return '' + value;
				} else {
					return null;
					// return '±0';
				}
			};

			this._updateCoverage = function(projectId, coverage, type) {
				var displayCoverage = coverage;
				if (coverage == undefined || coverage == 0) {
					displayCoverage = 100;
				}
				$this._getElement(projectId, 'TABLE.' + type + 'Test').animate({
					width : displayCoverage + "%"
				}, 3000);
			};

			this._updateTest = function(projectId, fail, success, skip, type) {
				if (fail == 0 && success == 0 && skip == 0) {
					$this['_hide' + type + 'T'](projectId);
					return;
				}
				var allTest = fail + success + skip;
				var failBar = (fail * 100) / allTest;
				var successBar = (success * 100) / allTest;
				var skipBar = (skip * 100) / allTest;
				if (success != 0) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.success SPAN.num').html(success);
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.success')
							.animate({
								width : successBar + "%"
							}, 2000).fadeIn("slow");
				} else {
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.success')
							.hide();
				}
				if (fail != 0) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.failure SPAN.num').html(fail);
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.failure')
							.animate({
								width : failBar + "%"
							}, 2000).fadeIn("slow");
				} else {
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.failure')
							.hide();
				}
				if (skip != 0) {
					$this._getElement(projectId,
							'TABLE.' + type + 'Test TD.ignore SPAN.num').html(skip);
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.ignore')
							.animate({
								width : skipBar + "%"
							}, 2000).fadeIn("slow");
				} else {
					$this._getElement(projectId, 'TABLE.' + type + 'Test TD.ignore')
							.hide();
				}
				$this['_show' + type + 'T'](projectId);
			};

			this._getElement = function(projectId, suffix) {
				var request = 'LI[id="' + projectId + '"]';
				if (suffix != undefined) {
					request += ' ' + suffix;
				}
				return $(request, $this.table);
			};

			this._buildProjectTD = function(projectId, projectName) {
				var projectTD = $('<li style="display:none" id="' + projectId
						+ '" class="project"></li>');
				var projectInnerTable = $('<table class="innerTable"><tbody></tbody></table>');
				projectTD.append(projectInnerTable);

				projectInnerTable
						.append($('<tr height="50%"><td class="projectName"><span>' + projectName + '</span>'
								+ ' <div class="inlineInfo"><abbr class="timeago" title=""></abbr> <span class="duration"></span><div><span class="lastUpdate"></span></td></tr>'));
				projectInnerTable
						.append($('<tr style="display:none; height: 80%" class="commitersTR"><td><ul class="commiters marquee" style="height: 100%"></ul></tr></td>'));
				//projectInnerTable
				//		.append($('<tr style="display:none" class="qualityTR"><td><ul class="quality marquee"></ul></tr></td>'));
				projectInnerTable
						.append($('<tr style="display:none;" class= "projectBuild timeleftTR"><td><p class="timeleft"></p></tr></td>'));
				//projectInnerTable
				//		.append($('<tr style="display:none" class="iTestTR"><td class="iTestTD"><table class="iTest"><tr><td class="failure"><span class="num"></span><span class="diff"></span></td><td class="ignore"><span class="num"></span><span class="diff"></span></td><td class="success"><span class="num"></span><span class="diff"></span></td></tr></table></tr></td>'));
				//projectInnerTable
				//		.append($('<tr style="display:none" class="uTestTR"><td class="uTestTD"><table class="uTest"><tr><td class="failure"><span class="num"></span><span class="diff"></span></td><td class="ignore"><span class="num"></span><span class="diff"></span></td><td class="success"><span class="num"></span><span class="diff"></span></td></tr></table></tr></td>'));
				return projectTD;
			};

			this._hideCountDown = function(projectId) {
				$this._getElement(projectId, 'DIV.inlineInfo').show();
				$this._getElement(projectId, 'TR.timeleftTR').hide();
			};
			this._hideQuality = function(projectId) {
				$this._getElement(projectId, 'TR.qualityTR').hide();
			};
			this._showQuality = function(projectId) {
				$this._getElement(projectId, 'TR.qualityTR').show();
			};
			this._hideCommiters = function(projectId) {
				$this._getElement(projectId, "TR.commitersTR").hide();
			};
			this._showCommiters = function(projectId) {
				$this._getElement(projectId, "TR.commitersTR").show();
			};
			this._hideiT = function(projectId) {
				$this._getElement(projectId, "TR.iTestTR").hide();
			};
			this._hideuT = function(projectId) {
				$this._getElement(projectId, "TR.uTestTR").hide();
			};
			this._showiT = function(projectId) {
				$this._getElement(projectId, "TR.iTestTR").show();
			};
			this._showuT = function(projectId) {
				$this._getElement(projectId, "TR.uTestTR").show();
			};
			this._showCountdown = function(projectId) {
				$this._getElement(projectId, 'DIV.inlineInfo').hide();
				$this._getElement(projectId, "TR.timeleftTR").show();
			};

		};

	return wallView;
});