$(document).ready(function(){
    $("#pagination").click(function(event){
        $("#chats").empty();
        event.preventDefault();
        var pages = $("#page").val();
        var sizeOfPage = $("#size").val();
        $.ajax({
            headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
            type: "GET",
            url: "/agent/chats/show?page=" + pages + "&size=" + sizeOfPage,
            dataType: 'json',
            cache: false,
            timeout:60000,
            success: function(responseJsonObject){
                var chats = responseJsonObject.content;
                var newChat;
                $.each(chats, function(index, value){
                    newChat = "<ul><li> id:" + value.id + "</li><li>creation time: " + value.creationTime
                        + "</li><li>status: ";
                    var status = value.status;
                    if (status === 1){
                        newChat = newChat + "started</li>";
                    }
                    if (status === 2){
                        newChat = newChat + "closed</li>";
                    }
                    newChat = newChat + "<li><form action=\"/agent/chats/" + value.id + "\"method=\"get\">"
                                            + "<button type=\"submit\">Detail</button></form></li></ul>";
                    $("#chats").append(newChat);
                });
            },
            error: function(e){
                console.log(e);
            }
        });
        $("#page").html(pages);
        $("#size").html(sizeOfPage);
    });

});