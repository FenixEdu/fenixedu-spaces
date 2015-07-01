
function uniquify(groupArray){
    var temp = {};
    for (var i = 0; i < groupArray.length; i++)
	temp[groupArray[i]] = true;
    var r = [];
    for (var k in temp)
	r.push(k);
    return r;
}

function getInfoGroup(groupString){
    var aux = $.ajax({ async : false, type : 'POST', url : Bennu.contextPath + "/api/bennu-core/users/find?query="+encodeURIComponent(groupString)+"&maxHits=1"});
    var respObjArray, respObj;
    if(aux.status == 200){
		respObjArray = JSON.parse(aux.responseText);
		if(respObjArray.users.length > 0) { 
			respObj = respObjArray.users[0]; // we only have one hit
			return {
				type : "user",
				name : respObj.name,
				expression : groupString
			};
		}
    }
    aux = $.ajax({ async : false, type : 'GET', url : Bennu.contextPath +"/api/bennu-core/groups?groupExpression="+encodeURIComponent(groupString)});
    if(aux.status == 200){
	respObj = JSON.parse(aux.responseText);
	return {
	    type : "group",
	    name : respObj.name,
	    expression : groupString
	};
    }
    return undefined;
}

function getAllGroupInfos(groupArray){
    return $.map(groupArray, function (val){
	return getInfoGroup(val);
    });
}

// the interface only supports simple expressions of the form: 'group' | 'group'
// as other forms of groups would be to hard to handle in a simple interface
// where 'group' can be a single user or a group of users

var type = {
    WORD : 0,
    OPERATOR : 1,
    PAREN : 2
};

function isOperator(theChar){
    switch (theChar){
    case  '|' : {
	return true;
    }
    default : return false;
    }
}

function isParen(theChar){
    if(theChar == '(' || theChar == ')'){
	return true;
    }
    return false;
}

function getNextTokenInternal(expressionString, iterator){
    if(isOperator(expressionString[iterator])){
	return type.OPERATOR;
    }if(isParen(expressionString[iterator])){
	return type.PAREN;
    }
    return type.WORD;
}

function getNextToken(expressionString, iterator){
    var iteratorIncrement = iterator;
    var token = "";
    while(whiteSpace(expressionString, iteratorIncrement)) iteratorIncrement++;
    if(!(iteratorIncrement < expressionString.length)) return undefined;
    
    var tokType = getNextTokenInternal(expressionString, iteratorIncrement);
    if(tokType == type.OPERATOR){
	return [expressionString[iteratorIncrement], iteratorIncrement-iterator+1];
    }
    if(tokType == type.PAREN){
	// paren outside of an expression!
	return [expressionString[iteratorIncrement], iteratorIncrement-iterator+1];
    }
    while(!whiteSpace(expressionString, iteratorIncrement) 
	  && !EOL(expressionString, iteratorIncrement) 
	  && !delimiter(expressionString, iteratorIncrement)) {
	token+=expressionString[iteratorIncrement];
	iteratorIncrement++;
    }
    return [token,iteratorIncrement-iterator];
}

function EOL(string, position){
    if( position < string.length){
	//EOL is not whitespace
	return false;
    }
    return true;
}

function skipWhiteSpace(string, position){
    while(whiteSpace(string, position)) position++;
    return position;
}

function whiteSpace(string, position){
    if(EOL(string,position)) return false;
    return /\s/.test(string[position]);
}

function delimiter(string, position){
    return /[()]/.test(string[position]);
}

function parseOP(operator){
    if(operator[0]=='|'){
    	return [operator[0], 0];
    }else{
    	return undefined;
    }
}

String.prototype.regexIndexOf = function(regex, startpos) {
    var indexOf = this.substring(startpos || 0).search(regex);
    return (indexOf >= 0) ? (indexOf + (startpos || 0)) : indexOf;
};

function parse(expression){
    token = "";
    var pos = 0;
    var groups = [];
    while(true){
	res = getNextToken(expression, pos);
	if (res == undefined){
	    break;
	}
	token = res[0];
	pos +=res[1];
	switch (token[0]){
	case 'U' : {
	    res = parseUser(expression,pos);
	    if(res == undefined) return undefined;
	    groups.push(res[0]);
	    pos+=res[1];
	    break;
	}
	case '#' : {
	    res = parseGroup(token,expression, pos);
	    if(res == undefined) return undefined;
	    groups.push(res[0]);
	    pos+=res[1];
	    break;
	}
	case '&':
	case '!':
	case '-':
	case '|': {
	    res = parseOP(token);
	    if(res == undefined) return undefined;
	    pos+=res[1];
	    break;
	}
	case '(' :
	case ')' :
	    break ;
	default: {
	    if(token.indexOf("nobody")!=-1){
		break;
	    }
	    res = parseGroup(token, expression, pos);
	    if(res == undefined) return undefined;
	    groups.push(res[0]);
	    pos+=res[1];
	    break;
	}
	}
    }
    var flatten = [].concat.apply([],groups);
    return flatten;
}

function parseString(expression, pos){
    var i= pos;
    if(expression[i]!='"'){
	return undefined;
    }
    i++;
    for(;i<expression.length;i++){
	if(expression[i]=='"'){
	    var stringExpr = expression.substring(pos,i+1);
	    return [stringExpr,i-pos+1]; //skip ' " '
	}
	if(expression[i]=='\\'){
	    i++;
	    continue;
	}
    }
    return undefined;
}

function parseGroupParams(expression, pos){
    var i = pos;
    var aux;
    if(expression[i]!='(') return undefined;
    i++;
    for(;i<expression.length;i++){
	if(expression[i]=='"'){
	    aux = parseString(expression,i);
	    if(aux == undefined) return undefined;
	    i += aux[1];
	}
	if(expression[i]=='('){
	    aux = parseGroupParams(expression,i);
	    if(aux == undefined) return undefined;
	    i += aux[1];
	}
	if(expression[i]==')'){
	    i++; // skip ')'
	    var groupExpr = expression.substring(pos,i); 
	    return [groupExpr,i-pos]; // skip ')'
	}
    }
    return undefined;
}

function parseGroup(token, expression, pos){
    var increment = pos;
    var groupExpression = token;
    var res = getNextToken(expression, pos);
    if(res !== undefined && isParen(res[0])){ // there may be no next token
	increment += res[1]-1; // set to '('
	var resGP = parseGroupParams(expression,increment);	
	if(resGP == undefined) return undefined;
	increment += resGP[1];
	groupExpression += resGP[0];
    }
    var groups = [groupExpression];
    return [groups,increment - pos];
}

function parseUser(userString, pos){
    var newPos = skipWhiteSpace(userString,pos);
    var groups = [];
    if(userString[newPos]!='(') return undefined;
    newPos++;
    var endPos = userString.indexOf(')',newPos);
    var auxString = userString.substring(newPos,endPos);
    var users = auxString.split(',');
    for(var i = 0;i<users.length;i++){
    	groups.push(users[i].trim());
    }
    //skip the closing ')'
    return [groups,endPos-pos+1];
}

function parsePersistentGroup(pgString, pos, persistentGroupString){
    var newPos = skipWhiteSpace(pgString,pos);
    var groups = [];
    if(pgString[newPos]!='(') return undefined;
    newPos++;
    var endPos = pgString.indexOf(')',newPos);
    var auxString = pgString.substring(newPos,endPos);
    groups.push(persistentGroupString+'('+auxString+')');
    return [groups,endPos-pos+1];
}

function getUserGroupsFromExpression(expression){
    var groups = parse(expression);
    if(groups === undefined) {
	return groups;    	
    }
    groups = uniquify(groups);
    return getAllGroupInfos(groups);
}

function groupEncode(groupArray){
    var expression = "";
    var groups = groupArray.filter(function(el){return el.type.indexOf("group")==0;});
    var users = groupArray.filter(function(el){return el.type.indexOf("user")==0;});
    var hasGroups = groups.length > 0;
    var hasUsers = users.length > 0;
    if(hasGroups){
	expression += groups[0].expression;	
    }
    for(var i = 1; i<groups.length;i++){
	expression += " | "+groups[i].expression;	
    }
    if(hasGroups && hasUsers){
	expression += " | U( " + users[0].expression;
    }else{
	if(hasUsers){
	    expression += "U( " + users[0].expression;
	}
    }
    for(var i = 1; i<users.length;i++){
	expression += " , "+users[i].expression;	
    }
    if(hasUsers){
	expression += " )";
    }
    if(expression.trim().length == 0){
	return "nobody";
    }
    return expression;
}

