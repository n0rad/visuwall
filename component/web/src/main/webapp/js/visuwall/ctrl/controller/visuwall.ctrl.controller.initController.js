/*
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

visuwall.ctrl.controller.initController = new function() {
	var $this = this;
	
	this.__inject__ = ['wallFormView', 'navigationView', 'navigationEvent'];
	
	this.run = function(initData) {
		//TODO manage events in DI
		$this.navigationEvent.__getObject__(function(bean) {
			ajsl.event.register(bean, $('DIV#navigationContainer'));
		});
		$this.navigationView.replaceWallList(initData.wallNames);
		if (initData.wallNames.length == 0) {
			$.history.queryBuilder().addController('wall/create').load();
		} else {
			$('#wallSelector #wallSelect').val(initData.wallNames[0]).change();
//			$.history.queryBuilder().addController('wall/' + initData.wallNames[0]).load();			
		}
	};
};