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

visuwall.theme.def.view.loader = new function() {
	var $this = this;
	
	
	tt = function() {
		// init loader
		ajsl.loader.init(visuwall.theme.def.view.loader);

		// loader test
		$("#loader1").bind(
				'click',
				function() {
					if ($("#loader1").hasClass('selected')) {
						$("#loader1").removeClass('selected');
						ajsl.loader.del('loader1');
					} else {
						$("#loader1").addClass('selected');
						ajsl.loader.add('loader1', 'description of loader1',
								function() {
									LOG.info('cancel loader1');
								});
					}
				});
		$("#loader2").bind(
				'click',
				function() {
					if ($("#loader2").hasClass('selected')) {
						$("#loader2").removeClass('selected');
						ajsl.loader.del('loader2');
					} else {
						$("#loader2").addClass('selected');
						ajsl.loader.add('loader2', 'description of loader2',
								function() {
									LOG.info('cancel loader2');
								});
					}
				});

		
	}
	
	this._loader;

	this._list;

	$(function() {
		$this._loader = $('#loader');
		$this._list = $('UL', $this._loader);
	});

	this.add = function(id, desc, cancel) {
		var li = $('<li id="loaderli' + id + '">' + desc + '</li>');
		var delb = $('<input type="button" value="CANCEL">');
		delb.bind('click', cancel);
		li.append(delb);
		$this._list.append(li);
	};

	this.del = function(id) {
		var li = $('li.#loaderli' + id, $this._list);
		li.remove();
	};

	this.show = function() {
		$this._loader.show();
	};

	this.hide = function() {
		$this._loader.hide();
	};
};
