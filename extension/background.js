// this is the background code...

// listen for our browerAction to be clicked
chrome.browserAction.onClicked.addListener(function (tab) {
	// for the current tab, inject the "inject.js" file & execute it
	lookUpPage(tab);
});

function getword() {
  //testing.
console.log("Scales launched");  
}

function injectDiv(tab) {
	//prevent doubles.
	chrome.tabs.executeScript(tab.ib, {
		file: 'inject.js'
	});
}

function getData(searchString) {
	$.ajax({
		type: "POST",
		url: 'http://localhost/scales/',
		data: "url=" + searchString,
		success: function(data) {
			//console.log(\""+data+"\");
			chrome.tabs.executeScript({
				code: "document.getElementById('Scales-Article-Div').innerHTML = \""+data+"\";"
			});
		}
	});
}

function lookUpPage(tab) {
	//call java program with tab.url.
	injectDiv(tab);
	getData(tab.title);
	
	// put java responseList into the div.
}

function lookUpSelection(info, tab) {
	//call java program with info.
	injectDiv(tab);
	getData(info);
}

chrome.contextMenus.create({
      title: 'Scales',
      type: 'normal',
      contexts: ['page'],
	  onclick: lookUpPage
    },getword);

chrome.contextMenus.create({
    title: "Put %s on the Scales", 
    contexts:["selection"], 
    onclick: function(info, tab) {
        lookUpSelection(info.selectionText,tab);
    }
});