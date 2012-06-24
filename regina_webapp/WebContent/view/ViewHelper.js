var ViewHelper = function() {
	var pathRoot = 'content/view/';
	
	return {
		
		LoadPage : function(page, params, divToUpdate) {
			var msg = "";
			EJS.config({
				cache : false
			});
			
			switch (page) {
				
				case 'subMenu_rete':
					var html = null;

					try {
						var myejs = new EJS({
							url : pathRoot + 'subMenu/subMenu_rete.ejs'
						});
						html = myejs.render(params);
					} catch (e) {
						if (e.description)
							e = e.description;
						alert('ex in ' + page + ' : ' + e);
					}
					if (divToUpdate != null && divToUpdate != '') {
						if ($('#' + divToUpdate)) 
							$('#' + divToUpdate).html(html);
					}
					break;
					
					
				case 'subMenu_nodi':
					var html = null;

					try {
						var myejs = new EJS({
							url : pathRoot + 'subMenu/subMenu_nodi.ejs'
						});
						html = myejs.render(params);
					} catch (e) {
						if (e.description)
							e = e.description;
						alert('ex in ' + page + ' : ' + e);
					}
					if (divToUpdate != null && divToUpdate != '') {
						if ($('#' + divToUpdate)) 
							$('#' + divToUpdate).html(html);
					}
					break;	
					
					
				case 'subMenu_software':
					var html = null;

					try {
						var myejs = new EJS({
							url : pathRoot + 'subMenu/subMenu_software.ejs'
						});
						html = myejs.render(params);
					} catch (e) {
						if (e.description)
							e = e.description;
						alert('ex in ' + page + ' : ' + e);
					}
					if (divToUpdate != null && divToUpdate != '') {
						if ($('#' + divToUpdate)) 
							$('#' + divToUpdate).html(html);
					}
					break;					
					
					
				default:
					var html = null;
					//if page not ends with '.ejs'
					if (!page.match(/.ejs$/))
						page = page + ".ejs";
					try {
						var myejs = new EJS({
							url: pathRoot + page
						});
						html = myejs.render(params, {
							callback: function(action){
								callback(action);
							}
						});
					} catch (e) {
						alert('ex in ' + page + ' : ' + e.message);
					}
					if (divToUpdate != null && divToUpdate != '') {
						if ($('#' + divToUpdate)) 
							$('#' + divToUpdate).html(html);
					}
					break;
			}
		}
	};
}();