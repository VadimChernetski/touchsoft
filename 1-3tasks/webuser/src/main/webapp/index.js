var sendMsg = document.querySelector("#send");
var messageInput = document.querySelector('#message');
var registration = document.querySelector('#registration');
var registrationInput = document.querySelector('#name');
var registrationForm = document.querySelector('#username-page');
var chatForm = document.querySelector('#chat-page');
var messageArea = document.querySelector('#messageArea');
var leave = document.querySelector('#leave');
var userRole = document.getElementsByName('role');
var userName = null;
var socket = null;
var userColor = null;
var companionColor = null;
var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];
 window.onload = function(){

    userColor = getColor();

    companionColor = getAnotherColor();

    socket = new WebSocket("ws://localhost:8080/webuser/chat");

    socket.onopen = function(event) {
    }

    socket.onclose = function(event){
                var msg = new Object();
                msg.name = userName;
                msg.context = "/exit";
                socket.send(JSON.stringify(msg));
    }

    socket.onerror = function(event) {
    }

    socket.onmessage = function(event) {
        var msg = JSON.parse(event.data);
        var senderName = msg.name;
        var context = msg.context;
        var color;
        var name;
        if (senderName === userName) {
            name = "you";
            color = userColor;
        } else {
            name = senderName;
            color = companionColor;
        }
        var msgPresentation = document.createElement('li');
        msgPresentation.classList.add('chat-message');
        if(name !== "server"){
        var avatar = document.createElement('i');
        var userFirstLetter = document.createTextNode(name.charAt(0));
        avatar.appendChild(userFirstLetter);
        avatar.style['background-color'] = color;
        msgPresentation.appendChild(avatar);
        var nameElement = document.createElement('span');
        nameElement.appendChild(document.createTextNode(name));
        msgPresentation.appendChild(nameElement);
        }
        var contextElement = document.createElement('p');
        contextElement.appendChild(document.createTextNode(context));
        msgPresentation.appendChild(contextElement);
        messageArea.appendChild(msgPresentation);
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    registration.onclick = function(event) {
    	var role;
    	var selectRole = false;
    	for(var i = 0; i < 2; i++){
    		if(userRole[i].checked){
    			role = userRole[i].value;
    			selectRole = true;
    		}
    	}
        userName = registrationInput.value.trim();
    	if(selectRole && userName !== ""){
            var msg = new Object();
            msg.context = userName;
            msg.name = "/register " + role + " ";
            socket.send(JSON.stringify(msg));
            registrationForm.classList.add('hidden');
            chatForm.classList.remove('hidden');
            if(role === "agent"){
            leave.classList.add('hidden');
            var msgPresentation = document.createElement('li');
            var contextElement = document.createElement('p');
            contextElement.appendChild(document.createTextNode("Сообщения не будут отправлены, пока клиент не присоеденится"));
            msgPresentation.appendChild(contextElement);
            messageArea.appendChild(msgPresentation);
            messageArea.scrollTop = messageArea.scrollHeight;
            }
            event.preventDefault();
        } else {
        	alert("You didn't enter name or choose role");
        }
    }

    leave.onclick = function(event){
        event.preventDefault();
        var msg = new Object();
        msg.name = userName;
        msg.context = "/leave";
        socket.send(JSON.stringify(msg));
        alert("You disconnected from agent.");
    }

    sendMsg.onclick = function(event) {
        event.preventDefault();
        var context = messageInput.value.trim();
        if(context !== ""){
            var msg = new Object();
            msg.name = userName;
            msg.context = context;
            socket.send(JSON.stringify(msg));
        }
        messageInput.value = "";
        if(context === "/exit"){
            registrationForm.classList.remove('hidden');
            chatForm.classList.add('hidden');
        }
    }
 }

window.onbeforeunload = function() {
    socket.onclose();
}

 function getColor() {
    var index = Math.round(Math.random() * colors.length);
    return colors[index];
 }

 function getAnotherColor(){
        var index;
        var tempColor;
        while(true){
            index = Math.round(Math.random() * colors.length);
            tempColor = colors[index];
            if(userColor !== tempColor){
                break;
            }
        }
        return tempColor;
 }