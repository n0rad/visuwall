define(['jquery',
        'require',
        'text!Visuwall/Theme/Global/WallForm2/WallFormTemplate.html',
        'text!Visuwall/Theme/Global/WallForm2/SoftwareInfo.html',
        'Ajsl/view',
        'Visuwall/Service/pluginService',
        'Visuwall/Service/wallService',
        
        'css!Visuwall/Theme/Global/WallForm2/wallForm.css',
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm.css',
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm-softTab.css',
        'css!Visuwall/Theme/Global/WallForm2/visuwallForm-projectTab.css'],
function($, require, WallFormTemplate, SoftwareInfoTemplate, view, pluginService, wallService) {
	'use strict';

	var softwareInfoTpl = _.template(SoftwareInfoTemplate);

	function WallFormView(context) {
		this.formDiv = context;
		this.softTabsCount = 1;		
	};
	
	WallFormView.prototype = {
		displayForm : function(data) {
			$('#over').show();
			this.formDiv.slideDown();
			this.formDiv.html(WallFormTemplate);
			view.rebuildFormRec(this.formDiv, data, this);
			$('#projectTab', this.formDiv).tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
			$('#projectTab li', this.formDiv).removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
			$( ".projectsAccordion", this.formDiv).accordion();
			$("#radio", this.formDiv).buttonset();
			$( "#format", this.formDiv).buttonset();
			$( "#allProjects", this.formDiv).button();
			$( "#slider-range" ).slider({
				range: true,
				min: 0,
				max: 500,
				values: [ 75, 300 ],
				slide: function( event, ui ) {
					$( "#amount" ).val( "$" + ui.values[ 0 ] + " - $" + ui.values[ 1 ] );
				}
			});
			$( "#amount" ).val( "$" + $( "#slider-range" ).slider( "values", 0 ) +
				" - $" + $( "#slider-range" ).slider( "values", 1 ) );

			var el = $( "#slider-range a" )[0];
			$(el).addClass('warning');
			
			
			$("#projectTabNav", this.formDiv).sortable({connectWith: ".softwareElementList"});
			$(".softwareElementList", this.formDiv).sortable({connectWith: "#projectTabNav" });
			$('.projectList, .viewList', this.formDiv).sortable({
				connectWith: "#projectTabNav",
				helper: "clone",
				appendTo: "body",
				});
			
//			$( "#sortable1, #sortable2" ).sortable({
//				connectWith: ".connectedSortable",
//				helper: "clone", appendTo: "body",
//			}).disableSelection();
		},
		hideForm : function() {
			this.formDiv.slideUp();
			var v = $('LABEL:regex(id,softwareAccesses.*\.urlcheck)');
			v.mouseout();

			$.history.queryBuilder().removeController(isCreate ? 'wall/create2' : 'wall/edit2').load();
			
			// TODO unregister live events
		},
		discoverSoftware : function(urlObj, loginObj, passObj) {
			var val = urlObj.val();
			if (val[val.length - 1] == '/') {
				urlObj.val(val.slice(0, -1));
				val = urlObj.val();
			}

			var softTabs = $('#softTabs');
			var tabIdFull = $('DIV[id^="tabs-"]', softTabs).has(urlObj)
					.attr('id');
			var hostname = getHostname(urlObj.val());
			if (!hostname) {
				hostname = urlObj.val();
			}
			if (!hostname) {
				hostname = 'New';
			}
			$('UL LI A[href="#' + tabIdFull + '"]', softTabs).html(
					hostname);

			// //////////

			var classes = [ 'failureCheck', 'successCheck',
					'loadingCheck', 'warningCheck' ];
			var domObj = $('#' + urlObj.attr('id').replace(".", "\\.") + "check", urlObj.parent());
			var tabContent = urlObj.parent();

			var id = urlObj.attr('id');
			var preId = id.substring(0, id.lastIndexOf('.') + 1);

			var name = urlObj.attr('name');
			var preName = name.substring(0, name.lastIndexOf('.') + 1);

			if (!urlObj.val().trim()) {
				domObj.switchClasses(classes, '', 1);
				return;
			}

			domObj.switchClasses(classes, 'loadingCheck', 1);

			pluginService.manageable(urlObj.val(), loginObj.val(), passObj.val(), function(softwareInfo) {
				// success
				if (softwareInfo.softwareId.warnings) {
					domObj.switchClasses(classes, 'warningCheck', 1);

					var infoContent = softwareInfoTpl({
							"pluginInfo.name" : softwareInfo.pluginInfo.name,
							"pluginInfo.version" : softwareInfo.pluginInfo.version,
							"softwareId.name" : softwareInfo.softwareId.name,
							"softwareId.version" : softwareInfo.softwareId.version,
							"softwareId.warnings" : softwareInfo.softwareId.warnings});

					domObj.qtip({
						content : infoContent,
						position : {corner : {tooltip : 'topLeft', target : 'bottomRight'}},
						style : {border : {width : 5, radius : 2},
							padding : 10, textAlign : 'center', tip : true, name : 'cream'}
					});
					domObj.mouseover();

				} else {
					// success
					domObj.switchClasses(classes, 'successCheck', 1);
				}

				// display build part
				if ($.inArray('build', softwareInfo.pluginInfo.capabilities) != -1) {
					$('FIELDSET.buildField', tabContent).show();
				} else {
					$('FIELDSET.buildField', tabContent).hide();
				}

				// display properties
				var propertyDiv = $(".properties", tabContent);
				propertyDiv.empty();
				for ( var propertyName in softwareInfo.pluginInfo.properties) {
					var propertyValue = softwareInfo.pluginInfo.properties[propertyName];
					var str = '<div style="float:left">';
					str += '<label for="' + preId + 'properties-' + propertyName + '">' + propertyName.ucFirst() + '</label>';
					str += '<input id="' + preId + 'properties-' + propertyName + '" class="ui-widget-content ui-corner-all" name="'
							+ preName + 'properties[' + propertyName + ']" value="' + propertyValue + '" />';
					str += '</div>';
					propertyDiv.append(str);
				}

				// project Names
				var projectNamesFormElem = $('SELECT:regex(id,softwareAccesses.*\.projectNames)', tabContent);
				var oldVal = projectNamesFormElem.val();
				if (oldVal == null) {
					oldVal = $(projectNamesFormElem)
							.data('newVal');
				}
				projectNamesFormElem.empty();
				for ( var key in softwareInfo.projectNames) {
					var projectName = softwareInfo.projectNames[key];
					projectNamesFormElem.append($("<option></option>").attr("value", key).text(projectName));
				}
				projectNamesFormElem.val(oldVal);

				// views
				var projectViewsFormElem = $('SELECT:regex(id,softwareAccesses.*\.viewNames)', tabContent);
				var oldVal = projectViewsFormElem.val();
				if (oldVal == null) {
					oldVal = $(projectViewsFormElem).data('newVal');
				}
				projectViewsFormElem.empty();
				if (softwareInfo.viewNames) {
					for ( var i = 0; i < softwareInfo.viewNames.length; i++) {
						var viewName = softwareInfo.viewNames[i];
						projectViewsFormElem.append($("<option></option>").attr("value", viewName).text(viewName));
					}
				}
				projectViewsFormElem.val(oldVal);

			}, function() {
				// fail
				domObj.switchClasses(classes, 'failureCheck', 1);
			});
		},
		submitForm : function() {
			$("#modal .success").empty();
			$("#modal .failure").empty();
			var wallName = $('#wallForm INPUT#name').val().trim();
			if (!wallName) {
				$("#modal .failure").html("Wall name is mandatory");
				return false;
			}
			$("#wallForm .loader").empty().html(
					'<img src="res/img/ajax-loader.gif" />');
			wallService.create(this, function() { // success
				$("#wallForm .loader").empty();
				$("#modal .success").html("Success");
				setTimeout(function() {
					wallService.wall(function(wallNameList) {
						require(['Visuwall/Theme/Default/View/navigationView' ], function(navigationView) {
								navigationView.replaceWallList(wallNameList);
								$.history.queryBuilder().forceController('wall',wallName).load();
						});
					});
					$("#modal").dialog('close');
				}, 1000);
			}, function(msg) { // failure
				$("#wallForm .loader").empty();
				$("#modal .failure").html(msg);
			});
		},
		addFormSoftwareAccesses : function(id) {
			if ($('#tabs-' + id).length) {
				return;
			}

			if (id == undefined) {
				id = $this.softTabsCount;
				$this.softTabsCount++;
			} else {
				// be sure that next tab id is not in used
				if ($this.softTabsCount <= id) {
					$this.softTabsCount = id + 1;
				}
			}
			var tabsElement = $('#softTabs', $this.context);
			tabsElement.tabs('add', '#tabs-' + id, "New");
		}
	};
	return WallFormView;
});