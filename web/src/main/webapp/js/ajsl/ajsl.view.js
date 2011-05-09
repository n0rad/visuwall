ajsl.view = new function() {

	this.formFields = "input, checkbox, select, textarea";

	this.rebuildFormRec = function(form, data, root, rootMethod) {
		if (root == undefined) {
			root = '';
		}
		if (rootMethod == undefined) {
			rootMethod = 'addForm';
		}

		for ( var formElem in data) {
			if (data[formElem] instanceof Array) {
				var assertMethodName = rootMethod + formElem.ucfirst();
				for ( var i = 0; i < data[formElem].length; i++) {
					if (assertMethodName in $this.editFormMng) {
						$this.editFormMng[assertMethodName](i);
					}
					rebuildFormRec(form, data[formElem][i], root + formElem
							+ '[' + i + '].', assertMethodName);
				}
			} else {
				// .trigger('change');
				$('[name="' + root + formElem + '"]', form).val(data[formElem])
						.blur();
			}
		}
	};

	this.resetFormValues = function(element) {
		element.find($this.formFields).val('');
	};

	this.incrementFormIndexes = function(element) {
		// increment name
		element.find($this.formFields).each(function() {
			this.name = this.name.replace(/\[(\d+)\]/, function(str, p1) {
				return '[' + (parseInt(p1, 10) + 1) + ']';
			});

			this.id = this.id.replace(/(\d+)\./, function(str, p1) {
				return (parseInt(p1, 10) + 1) + '.';
			});
		});

		// increment name
		element.find('label').each(function() {
			this.htmlFor = this.htmlFor.replace(/(\d+)\./, function(str, p1) {
				return (parseInt(p1, 10) + 1) + '.';
			});
		});
	};
	
	// $(':input','#myform')
	// .not(':button, :submit, :reset, :hidden')
	// .val('')
	// .removeAttr('checked')
	// .removeAttr('selected');

	// (function($)
	// {
	// $.fn.defaultValue = function( options )
	// {
	// var options = $.extend(
	// {
	// defVal: 'Search'
	// },
	// options
	// );
	// return this.each( function()
	// {
	// $( this ).blur( function()
	// {
	// if ( $( this ).val() === '' )
	// $( this ).val( options.defVal );
	// }
	// ).focus( function()
	// {
	// if ( $( this ).val() === options.defVal )
	// $( this ).val( '' );
	// }
	// );
	// }
	// );
	// }
	// }
	// )(jQuery);
	//		

	// $.fn.clearForm = function() {
	// return this.each(function() {
	// var type = this.type, tag = this.tagName.toLowerCase();
	// if (tag == 'form')
	// return $(':input',this).clearForm();
	// if (type == 'text' || type == 'password' || tag == 'textarea')
	// this.value = '';
	// else if (type == 'checkbox' || type == 'radio')
	// this.checked = false;
	// else if (tag == 'select')
	// this.selectedIndex = -1;
	// });
	// };

};