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

ajsl.loader = {
		
		loading : {},
		
		view : null,

		init : function(view) {
			this.view = view;
			this._checkDisplay();
		},

		add : function(id, info, remove) {
			LOG.debug('Register loader', id, info);
			if (this.loading[id] != undefined) {
				LOG.warn('Loader is already registered', id);
				this.del(id);
			}
			this.loading[id] = {};
			this.view.add(id, info, function() {
					remove(id);
					ajsl.loader.del(id);
					ajsl.loader._checkDisplay();					
				});
			this._checkDisplay();
		},

		del : function(id) {
			LOG.debug('Remove loader', id);
			if (this.loading[id] == undefined) {
				LOG.warn('Loader does not exists', id);
			}
			delete this.loading[id];
			this.view.del(id);
			this._checkDisplay();
		},

		_checkDisplay : function() {
			if (isObjectEmpty(this.loading)) {
				this.view.hide();
			} else {
				this.view.show();
			}
		}
};