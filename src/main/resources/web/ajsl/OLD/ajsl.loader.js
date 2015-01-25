
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