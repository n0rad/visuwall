visuwall.mvc.view.Loader = {

	_loader : null,

	_list : null,

	init : function() {
		this._loader = $('#loader');
		this._list = $('UL', this._loader);
	},

	add : function(id, desc, cancel) {
		var li = $('<li id="loaderli' + id + '">' + desc + '</li>');
		var delb = $('<input type="button" value="CANCEL">');
		delb.bind('click', cancel);
		li.append(delb);
		this._list.append(li);
	},

	del : function(id) {
		var li = $('li.#loaderli' + id, this._list);
		li.remove();
	},

	show : function() {
		this._loader.show();
	},

	hide : function() {
		this._loader.hide();
	}
};
