// this is the code which will be injected into a given page...

(function() {

	// just place a div at top right
	if(document.getElementById('Scales-Article-Div') == null) {
		var div = document.createElement('div');
		div.id = 'Scales-Article-Div';
		div.style.position = 'fixed';
		div.style.maxHeight = '400px';
		div.style.width = '30%';
		div.style.minWidth = '300px';
		div.style.top = 0;
		div.style.right = 0;
		div.textContent = 'Scales!';
		div.style.zIndex = 9999999;
		div.style.backgroundColor = "FFFDD0";
		div.style.padding = "20px;"
		document.body.appendChild(div);
		}

})();