$(document).ready(function(){
    $("#pagination").click(function(event){
        $("#listOfUsers").empty();
        event.preventDefault();
        var pages = $("#page").val();
        var sizeOfPage = $("#size").val();
        var status = $("input[name='status']:checked").val();
        $.ajax({
            headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
            },
            type: "GET",
            url: "/agent/clients/show?page=" + pages + "&size=" + sizeOfPage + "&status=" + status,
            dataType: 'json',
            cache: false,
            timeout:60000,
            success: function(responseJsonObject){
                var users = responseJsonObject.content;
                var newUser;
                $.each(users, function(index, value){
                    newUser = "<ul><li> id:" + value.id + "</li><li>name: " + value.name
                        + "</li><li>role(s): ";
                    var roles = value.roles;
                    $.each(roles, function(index, role){
                        newUser = newUser + role;
                    });
                    newUser = newUser + "</li><li>status: ";
                    var status = value.status;
                    if (status === 0){
                        newUser = newUser + "offline</li>";
                    }
                    if (status === 1){
                        newUser = newUser + "online</li>";
                    }
                    if (status === 2){
                        newUser = newUser + "in chat</li>";
                    }
                    newUser = newUser + "<li><form action=\"/agent/all/" + value.id + "\"method=\"get\">"
                                            + "<button type=\"submit\">Detail</button></form></li></ul>";
                    $("#listOfUsers").append(newUser);
                });
            },
            error: function(e){
                console.log(e);
            }
        });
        $("#page").html(pages);
        $("#size").html(sizeOfPage);
    });
    $("#freeclients").click(function(event){
        event.preventDefault();
        $.ajax({
                    headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                    },
                    type: "GET",
                    url: "/agent/clients/free",
                    dataType: 'json',
                    cache: false,
                    timeout:60000,
                    success: function(responseJsonObject){
                    var free = "Clients in queue " + responseJsonObject;
                        $("#free").append(free);
                    },
                    error:function(e){
                    console.log(e);
                    }
    });
});
});