var sendMsg = document.querySelector("#send");
var messageInput = document.querySelector('#message');
var registration = document.querySelector('#registration');
var registrationInput = document.querySelector('#name');
var registrationForm = document.querySelector('#username-page');
var chatForm = document.querySelector('#chat-page');
var messageArea = document.querySelector('#messageArea');
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
        var avatar = document.createElement('i');
        var userFirstLetter = document.createTextNode(name.charAt(0));
        avatar.appendChild(userFirstLetter);
        avatar.style['background-color'] = color;
        msgPresentation.appendChild(avatar);
        var nameElement = document.createElement('span');
        nameElement.appendChild(document.createTextNode(name));
        msgPresentation.appendChild(nameElement);
        var contextElement = document.createElement('p');
        contextElement.appendChild(document.createTextNode(context));
        msgPresentation.appendChild(contextElement);
        messageArea.appendChild(msgPresentation);
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    registration.onclick = function(event) {
        var input = registrationInput.value.trim();
        if (input.match("(client)|(agent) [A-z0-9]+")){
            var inputData = input.split(" ");
            userName = inputData[1];
            var msg = new Object();
            msg.context = userName;
            msg.name = "/register " + inputData[0];
            socket.send(JSON.stringify(msg));
            registrationForm.classList.add('hidden');
            chatForm.classList.remove('hidden');
            if(inputData[0] === "agent"){
            var msgPresentation = document.createElement('li');
            var contextElement = document.createElement('p');
            contextElement.appendChild(document.createTextNode("Сообщения не будут отправлены, пока клиент не присоеденится"));
            msgPresentation.appendChild(contextElement);
            messageArea.appendChild(msgPresentation);
            messageArea.scrollTop = messageArea.scrollHeight;
            }
            event.preventDefault();
        } else {
            alert("incorrect data");
        }
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