/* --------------------------------------------------------------------------------------------------------------
*	API:			JSMX (JavaScript MX) - Universal Ajax API for ColdFusion, PHP, .NET, or anything other language.
*	AUTHOR: 		Todd Kingham [todd@lalabird.com] with contributions by Jan Jannek [jan.jannek@Cetecom.de]
*	CREATED:		8.21.2005
*	VERSION:		2.6.1
*
*	LICENSE:		THIS IS AN OPEN SOURCE API. YOU ARE FREE TO USE THIS API IN ANY APPLICATION,
*               	TO COPY IT OR MODIFY THE FUNCTIONS FOR YOUR OWN NEEDS, AS LONG THIS HEADER INFORMATION
*              	 	REMAINS IN TACT AND YOU DON'T CHARGE ANY MONEY FOR IT. USE THIS API AT YOUR OWN
*               	RISK. NO WARRANTY IS EXPRESSED OR IMPLIED, AND NO LIABILITY ASSUMED FOR THE RESULT OF
*               	USING THIS API.
*
*               	THIS API IS LICENSED UNDER THE CREATIVE COMMONS ATTRIBUTION-SHAREALIKE LICENSE.
*               	FOR THE FULL LICENSE TEXT PLEASE VISIT: http://creativecommons.org/licenses/by-sa/2.5/
*
-----------------------------------------------------------------------------------------------------------------*/
// UNCOMMENT THE FOLLOWING LINE IF YOU WILL BE RETURNING QUERY OBJECTS. (note: you may need to point the SRC to an alternate location.
/*document.writeln('<SCRIPT TYPE="text/javascript" LANGUAGE="JavaScript" SRC="/CFIDE/scripts/wddx.js"></SCRIPT >');*/

var jsmx = new jsmxConstructor();
function jsmxConstructor(){
	this.isJSMX = true;
	this.async = true;
	this.debug = false;
	this.waitDiv = 'JSMX_loading';
	this.http = http;
	this.onWait = _popWait;
	this.onWaitEnd = _killWait;
	this.onError = _onError;
}
// perform the XMLHttpRequest();
function http(verb,url,cb,q) {
	var self = (this.isJSMX) ? this : jsmx ;
	//reference our arguments
	var qryStr = (!q) ? '' : _toQueryString(q);
	var calledOnce = false; //this is to prevent a bug in onreadystatechange... "state 1" gets called twice.
	try{//this should work for most modern browsers excluding: IE Mac
		var xhr = ( window.XMLHttpRequest ) ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP") ;
			xhr.onreadystatechange = function(){
				switch(xhr.readyState){
					case 1:
						if(!calledOnce){
							self.onWait(self.waitDiv);
							calledOnce = true;
						} 	break;
					case 2: break;
					case 3: break;
					case 4:
						self.onWaitEnd(self.waitDiv);
						if ( xhr.status == 200 ){// only if "OK"
							var success = true;
							try{
								var rObj = _parseResponse( xhr );
							}catch(e){
								self.onError(xhr,self,1);
								success = false;
							}
							if(success){ cb( rObj ); }
						}else{
							self.onError(xhr,self,2);
						}
					delete xhr.onreadystatechange; //clean this function from memory once we re done with it.
					break;
				}
			};
			xhr.open( verb , _noCache(url) , self.async );
			if(verb.toLowerCase() == 'post') { xhr.setRequestHeader("Content-Type", "multipart/form-data"); }
			xhr.send(qryStr);
	}catch(e){
		self.onError(xhr,self,3);
	}
}

/*--- BEGIN: RESPONSE PARSING FUNCTIONS ---*/
function _parseResponse($$){
	 var str = _cleanString($$.responseText);
	 return str;
}
// jan.jannek@cetecom.de, 2006-02-16, weird error: some IEs show the responseText followed by the complete response (header and body again)
function _cleanString(str){
	//Left Trim
	var rex = /\S/i;
	str = str.substring(str.search(rex),str.length);

	var i = str.indexOf("HTTP/1");
	if (i > -1) {
		str = str.substring(i, str.length);
		i = str.indexOf(String.fromCharCode(13, 10, 13, 10));
		if (i > -1) { str = str.substring(i + 2, str.length); }
	}
	return str;
}
function _parseJS(str){
	eval(str);
	var r = eval(str.split('=')[0].replace(/\s/g,''));
	return r;
}
function _parseWDDX(str){ var wddx = xmlStr2Doc(str); var data = wddx.getElementsByTagName("data"); return _parseWDDXnode(data[0].firstChild); } function xmlStr2Doc(str){ var xml; if(typeof(DOMParser) == 'undefined'){ xml=new ActiveXObject("Microsoft.XMLDOM"); xml.async="false"; xml.loadXML(str); }else{ var domParser = new DOMParser(); xml = domParser.parseFromString(str, 'application/xml'); } return xml; } function _parseWDDXnode(n){ var val; switch(n.tagName){ case 'string': val = _parseWDDXstring(n); break; case 'number': val = parseInt(n.firstChild.data); break; case 'boolean': val = n.getAttribute('value'); break; case 'dateTime': val = Date(n.firstChild.data); break; case 'array': val = _parseWDDXarray(n); break; case 'struct': val = _parseWDDXstruct(n); break; case 'recordset': val = _parseWDDXrecordset(n); break; case 'binary': val = n.firstChild.data; break; case 'char': val = _parseWDDXchar(n);; break; case 'null': val = ''; break; default: val = n.tagName; break; } return val; } function _parseWDDXstring(node){ var items = node.childNodes; var str = ''; for(var x=0;x < items.length;x++){ if(typeof(items[x].data) != 'undefined') str += items[x].data; else str += _parseWDDXnode(items[x]); } return str; } function _parseWDDXchar(node){ switch(node.getAttribute('code')){ case '0d': return '\r'; case '0c': return '\f'; case '0a': return '\n'; case '09': return '\t'; } } function _parseWDDXarray(node){ var items = node.childNodes; var arr = new Array(); for(var i=0;i < items.length;i++){ arr[i] = _parseWDDXnode(items[i]); } return arr; } function _parseWDDXstruct(node){ var items = node.childNodes; var obj = new Object(); for(var i=0;i < items.length;i++){ obj[items[i].getAttribute('name').toLowerCase()] = _parseWDDXnode(items[i].childNodes[0]); } return obj; } function _parseWDDXrecordset(node){ var qry = new Object(); var fields = node.getElementsByTagName("field"); var items; var dataType; var values; for(var x = 0; x < fields.length; x++){ items = fields[x].childNodes; values = new Array(); for(var i = 0; i < items.length; i++){ values[values.length] = _parseWDDXnode(items[i]); } qry[fields[x].getAttribute('name').toLowerCase()] = values; } return qry; }
/*--- END: RESPONSE PARSING FUNCTIONS ---*/


/*--- BEGIN: REQUEST PARAMETER FUNCTIONS ---*/
	function _toQueryString(obj){
		//determine the variable type
		if(typeof(obj) == 'string') { return obj; }
		if(typeof(obj) == 'object'){
			if(typeof obj.elements == 'undefined') {return _object2queryString(obj); }//It's an Object()!
			else{ return _form2queryString(obj); }//It's a form!
		}
	}
	function _object2queryString(obj){
		var ar = new Array();
		for(x in obj){ ar[ar.length] = x+'='+obj[x]; }
		return ar.join('&');
	}
	function _form2queryString(form){
		var obj = new Object();
		var ar = new Array();
		for(var i=0;i < form.elements.length;i++){
			try {
				elm = form.elements[i];
				nm = elm.name;
				if(nm != ''){
					switch(elm.type.split('-')[0]){
						case "select":
							for(var s=0;s < elm.options.length;s++){
								if(elm.options[s].selected){
									if(typeof(obj[nm]) == 'undefined'){ obj[nm] = new Array(); }
									obj[nm][obj[nm].length] = escape(elm.options[s].value);
								}
							}
							break;
						case "radio":
							if(elm.checked){
								if(typeof(obj[nm]) == 'undefined'){ obj[nm] = new Array(); }
								obj[nm][obj[nm].length] = escape(elm.value);
							}
							break;
						case "checkbox":
							if(elm.checked){
								if(typeof(obj[nm]) == 'undefined'){ obj[nm] = new Array(); }
								obj[nm][obj[nm].length] = escape(elm.value);
							}
							break;
						default:
							if(typeof(obj[nm]) == 'undefined'){ obj[nm] = new Array(); }
							obj[nm][obj[nm].length] = escape(elm.value);
							break;
					}
				}
			}catch(e){}
		}
		for(x in obj){ ar[ar.length] = x+'='+obj[x].join(','); }
	return ar.join('&');
	}
/*--- END: REQUEST PARAMETER FUNCTIONS ---*/

//IE likes to cache so we will fix it's wagon!
function _noCache(url){
	var qs = new Array();
	var arr = url.split('?');
	var scr = arr[0];
	if(arr[1]){ qs = arr[1].split('&'); }
	qs[qs.length]='noCache='+new Date().getTime();
return scr+'?'+qs.join('&');
}
function _popWait(id){
	proc = document.getElementById(id);
	if( proc == null ){
		var p = document.createElement("div");
		p.id = id;
		document.body.appendChild(p);
	}
}
function _killWait(id){
	proc = document.getElementById(id);
	if( proc != null ){ document.body.removeChild(proc); }
}
function _onError(obj,inst,errCode){
	var msg;
	switch(errCode){
		case 1:/*parsing error*/
			msg = (inst.debug) ? obj.responseText : 'Parsing Error: The value returned could not be evaluated.';
			break;
		case 2:/*server error*/
			msg = (inst.debug) ? obj.responseText : 'There was a problem retrieving the data:\n' + obj.status+' : '+obj.statusText;
			break;
		case 3:/*browser not equiped to handle XMLHttp*/
			msg = 'Unsupported browser detected.';
			return;/*you can remove this return to send a message to the screen*/
			break;
	}
	if(inst.debug){
		var debugWin = window.open('','error');
		debugWin.document.write(msg);
		debugWin.focus();
	}else{
		alert(msg);
	}
}
function $(id){ return document.getElementById(id); }
