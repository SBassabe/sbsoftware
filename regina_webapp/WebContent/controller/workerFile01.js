self.addEventListener('message', function(e) {
  
  
  /*
  switch (data.cmd) {
    case 'start':
      self.postMessage('WORKER STARTED: ' + canvasMgr.daysLoaded);
      break;
    case 'stop':
      self.postMessage('WORKER STOPPED: ' + data.msg +
                       '. (buttons will no longer work)');
      self.close(); // Terminates the worker.
      break;
    default:
      self.postMessage('Unknown command: ' + data.msg);
  };
  */
  /*
	console.log("addEventListener called ...");
	var buildId = data.currFloor;
	var dt = data.dt;
	
	var method = 'addEventListener.FloorOccupancy';
    try {
    	
    	var params = {
                buildId: buildId,
                dt: dt
            };
        
        $.ajax({
            url: "FloorOccupancy",
            dataType: "json",
            timeout: 10000,
            type: 'POST',
            async: false,
            data: params,
            //context: document.body,
            success: function(transport){
            	self.postMessage(transport);
            },
            error: function(jqXHR, textStatus, errorThrown){
                var errDesc = "error on method:"+method+", textStatus:"+textStatus+", errorThrown:"+errorThrown;
                //canvasMgr.showError(errDesc);
                console.log(" error => " + errDesc );
            }
        });
        
    } catch (e) {
    	//canvasMgr.showError(" on method:" + method);
        console.log(e);
        return;
    }
    */
    
    function load(url, build, dt, callback) {
    	var xhr;
     
    	if(typeof XMLHttpRequest !== 'undefined') xhr = new XMLHttpRequest();
    	else {
    		var versions = ["MSXML2.XmlHttp.5.0", 
    			 	"MSXML2.XmlHttp.4.0",
    			 	"MSXML2.XmlHttp.3.0", 
    			 	"MSXML2.XmlHttp.2.0",
    			 	"Microsoft.XmlHttp"]
     
    		for(var i = 0, len = versions.length; i < len; i++) {
    		try {
    			xhr = new ActiveXObject(versions[i]);
    			console.log("created with -> version " + i);
    			break;
    		}
    			catch(e){}
    		} // end for
    	}
    		
    	xhr.onreadystatechange = ensureReadiness;
    		
    	function ensureReadiness() {
    		if(xhr.readyState < 4) {
    			return;
    		}
    			
    		if(xhr.status !== 200) {
    			return;
    		}
     
    		// all is well	
    		if(xhr.readyState === 4) {
    			callback(xhr);
    		}			
    	}
    		
    	//var obj = {buildId: build, dt: dt};
    	//var send = JSON.stringify(obj);
    	//console.log("send => " + send);
    	//var url2=url+'?buildId='+build+'&dt='dt;
    	xhr.open('GET', url, true);
    	xhr.send();
    	return xhr.ResponseText;
    };
    
    
    	
    //and here is how you use it to load a json file with ajax
    var par = '../FloorOccupancy?buildId='+e.data.buildId+'&dt='+e.data.dt;
    var ret =  load(par, e.data.buildId, e.data.dt, function(xhr) {	
    	var result = xhr.responseText;
    	console.log('done call');
    	return result;
    });
    
    self.postMessage('ret => ' + ret) ;
    
}, false);