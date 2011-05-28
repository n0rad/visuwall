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

ajsl.view = new function() {
	var $this = this;
	
	this.formFields = "input, checkbox, select, textarea";

	this.rebuildFormRec = function(form, data, formManager, root, rootMethod) {
		if (root == undefined) {
			root = '';
		}
		if (rootMethod == undefined) {
			rootMethod = 'addForm';
		}
	
		for (var formElem in data) {
			if (data[formElem] instanceof Array) {
				var assertMethodName = rootMethod + formElem.ucfirst();
				for ( var i = 0; i < data[formElem].length; i++) {
					if (assertMethodName in formManager) {
						formManager[assertMethodName](i);
					}
					$this.rebuildFormRec(form, data[formElem][i], formManager, root + formElem
							+ '[' + i + '].', assertMethodName);
				}
			} else {
				// .trigger('change');
				$('[name="' + root + formElem + '"]', form).val(data[formElem])
						.blur().change();
			}
		}
	};

	this.resetFormValues = function(element) {
		element.find($this.formFields).val('').blur().change();
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

		// increment labels
		element.find('label').each(function() {
			this.htmlFor = this.htmlFor.replace(/(\d+)\./, function(str, p1) {
				return (parseInt(p1, 10) + 1) + '.';
			});
			this.id = this.id.replace(/(\d+)\./, function(str, p1) {
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