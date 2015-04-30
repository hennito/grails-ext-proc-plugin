//= require jquery

var extproc = {
	removeMe: function(meDom) {
		var meA = $(meDom);
		
		var meLi = meA.parent();
		var ul=meLi.parent();
		var idType = ul.attr("id").indexOf("Map") > -1 ? "Map" : "List";
		var sectionName = ul.attr("id").substr(0,ul.attr("id").length-idType.length);

		meLi.remove();
		var lis = ul.find("li");
		for (var idx = 0; idx < lis.length; idx++) {
			var inputs = $(lis[idx]).find("input");
			if (inputs.length > 1) {
				$(inputs[0]).attr("id",sectionName +".key["+idx+"]");
				$(inputs[0]).attr("name",sectionName +".key["+idx+"]");
				$(inputs[1]).attr("id",sectionName +".value["+idx+"]");
				$(inputs[1]).attr("name",sectionName +".value["+idx+"]");

			}
			else {
				$(inputs[0]).attr("id",sectionName +"["+idx+"]");
				$(inputs[0]).attr("name",sectionName +"["+idx+"]");
			}
		}
		return false;
	},

	addStrPairToList: function(type) {
		var self = this;
		var ul = $('#' + type + "Map");
		if (ul.length > 0) {
			var idx = $(ul).find("li").length;		
			var newLi = "<li><input type='text' name='"+type+".key[" + (idx) + "]' id='"+type+"[" + (idx) + "]' value='' />"+
			"<input type='text' name='"+type+".value[" + (idx) + "]' id='"+type+"[" + (idx) + "]' value='' />" +
			' <a class="int-link" onclick="return self.removeMe(this)" href="#">x</a>' + 
			"</li>" ;
			ul.append(newLi);
		}
		return false;
	},

	addStrToList: function (type) {
		var ul = $('#' + type + "List");
		if (ul.length > 0) {
			var idx = $(ul).find("li").length;		
			var newLi = "<li><input type='text' name='"+type+"[" + (idx) + "]' id='"+type+"[" + (idx) + "]' value='' />"+
			' <a class="int-link" onclick="return extproc.removeMe(this)" href="#">x</a>' + 
			"</li>" ;
			ul.append(newLi);
		}
		return false;
	},

	onReturnDirChange: function(elem) {
		var checkbox = $(elem);
		var check = checkbox.attr("checked") 
		if (check) {
			$('.returnDir').show();
		}
		else {
			$('.returnDir').hide();		
		}
	},

 	onWorkDirChange: function(elem) {
		var select = $(elem);
		if (select.length > 0) {
			var selected = select.val();
			if (selected == 'CUSTOM') {
				$('#workDir').val('');
				$('#workDir').focus();
				$('#workDir').one("blur",function() { if ($(this).val() == '') {select.val('NONE'); onWorkDirChange(select);} })
				$('.workDir').show();
			}
			else if (selected == 'NEW') {
				$('#workDir').val('_CREATE_NEW_');
				$('.workDir').show();			
			}
			else if (selected == 'NONE') {
				// disable all workdir related stuff
				$('#workDir').val('_NO_WORKDIR_');
				$('.workDir').hide();
			}
			else {
				// text modifed
				if (selected == '') {
					$('#wDir').val('NONE');
					$('.workDir').hide();
				}
				else {
					$('#wDir').val('CUSTOM');
					$('.workDir').show();
				}
			}
		}
	}	
}
