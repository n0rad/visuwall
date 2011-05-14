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

visuwall.theme.def.event.navigationEvent = new function() {
	var $this = this;
	
	this.__inject__ = ['navigationController', 'wallController'];
	
	this.context;
	
	this.toggleFlag = 'show';
	
	$(function() {
		$this.context = 'DIV#navigationContainer';
	});
	
	this['|init'] = function() {
		$this['|mouseleave']();
	};
	
	this['|mouseenter'] = function() {
		if ($this.toggleFlag == 'wait') {
			$this.toggleFlag = 'show';
		} else if ($this.toggleFlag == 'hide') {
			$this.toggleFlag = 'show';
			$("#navigation").slideToggle("fast");			
		}
	};
	
	this['|mouseleave'] = function() {
		$this.toggleFlag = 'wait';
		window.setTimeout(function() {
			$("#navigation").each(function() {
				if ($this.toggleFlag != 'wait') {
					return;
				}
				$(this).slideToggle("fast", function() {
					$this.toggleFlag = 'hide';
				});
			});
		}, 1000);
	};

	this['#fontSizeSlider|init'] = function() {
		var slideFunc =  function(e, ui) {
			$('#projectsTable').css({
				fontSize : ui.value + 'em'
			});
		};
		
		$(this).slider({
			min : 0.5,
			max : 1.2,
			step : 0.01,
			slide : slideFunc,
			change : slideFunc
		});
		$(this).slider( "option", "value", 0.6 );
	};
	
	this['#wallSelector #wallSelect|init'] = function() {
		setInterval($this.navigationController.updateWallList, 15000);
	};

	this['#wallSelector #wallSelect|change'] = function() {
		var wallCtrlUrl = 'wall/' + $('#wallSelector #wallSelect').val();
		$.history.queryBuilder().addController(wallCtrlUrl).load();
	};

	this['#wallSelector #edit|click'] = function() {
		var editCtrlUrl = 'wall/edit/' + $('#wallSelector #wallSelect').val();
		$.history.queryBuilder().addController(editCtrlUrl).load();
	};

	this['#wallSelector #add|click'] = function() {
		$.history.queryBuilder().addController('wall/create').load();
	};
	
	this['#refresh|click'] = function() {
		$this.wallController.updateStatus();
	};

};