$(document).ready(function(){
    setInterval(function(){
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
                },
                type: "get",
                dataType: 'json',
                url: "/agent/chat/send",
                cache: false,
                timeout:12000,
                success: function(responseJsonObject){
                    $.each(responseJsonObject,function(index, value){
                        var message = "<li>" + value.name + " (" + value.creationTime + ") " + value.context + "</li>";
                        $("#chat").append(message)
                    });
                },
                error: function(e){
                    console.log(e);
                }
        });
    }, 2000);
    $("#send").click(function(event){
        event.preventDefault();
        var message = new Object();
        message.context = $("#message").val();
        $("#message").val("");
        $("#chat").append("<ul>you: " + message.context + "</li>");
        $.ajax({
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            type: "post",
            url: "/agent/chat/post",
            data: JSON.stringify(message),
            dataType: 'json',
            cache: false,
            timeout: 60000,
            success: function(result){

            },
            error: function(e){
            }
        });
    });
});