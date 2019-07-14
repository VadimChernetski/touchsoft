$(document).ready(function(){

    poll();

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
            url: "/client/chat/post",
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

    function poll(){
            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                    },
                    type: "get",
                    dataType: 'json',
                    url: "/client/chat/send",
                    cache: false,
                    success: function(responseJsonObject){
                        $.each(responseJsonObject,function(index, value){
                            var message = "<li>" + value.name + " (" + value.creationTime + ") " + value.context + "</li>";
                            $("#chat").append(message)
                        });
                        poll();
                    },
                    error: function(e){
                        poll();
                    }
            });
        };