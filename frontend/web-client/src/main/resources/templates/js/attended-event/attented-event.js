// // Grab the template script
// var theTemplateScript = $("#attended-conference").html();
//
//
// // compile the template
// var theTemplate = Handlebars.compile(theTemplateScript);
//
// // Define our data object

$(function(){


    var myName = $(".myname");

    function generateFriendListItem(data){
        return $('<li class="member"><a href="/?user='+data.username+'"><img src="profile/picture/'+data.photo+'" alt="img"><b>'+data.name+'</b>'+data.town+'</a></li>')
    }
    $.ajax({
        url: "profile/myname",
        method: 'GET',
        contentType: 'application/json',
        success: function(data){
            myName.text(data.name);
        },
        error: function(data){
            console.log(data);
        }

    });

    var friendList = $("#friendList");
    $("#friendRefresh").on('click',function(event){
        event.preventDefault();
        myFriendListFunction();
        friendList.empty();
    });


    myFriendListFunction();
    function myFriendListFunction(){
        $.ajax({
            url: "profile/myfriends",
            method: 'GET',
            contentType: 'application/json',
            success: function(data){

                for(var i=0; i<data.length; i++) {
                    var friend = generateFriendListItem(data[i]);
                    friendList.append(friend);
                }
            },
            error: function(data){
                console.log(data);
            }

        });
    }

    $.ajax({
        url: "profile/conferences/attended",
        method: 'GET',
        contentType: 'application/json',
        success: function (data) {
            console.log(data);
    //         var context = {context: data}
    //         var theCompiledHtml = theTemplate(context);
    // // Add the compiled html to the page
    //         $('#body').prepend(theCompiledHtml);
            var attendBody = $("#attendedBody");

            for (var i = 0; i < data.length; i++) {
                attendBody.append("<div class='col-md-4'> <div class='panel panel-default'>" +
                    "<div class='panel-body status'>" +
                    "<ul class='panel-tools'>" +
                    "<li>"+data[i].likes +"<i class='fa fa-thumbs-o-up'></i> 赞</li>" +
                    "<li><a class='icon expand-tool'><i class='fa fa-expand'></i></a></li>" +
                    "</ul>" +
                    "<div class='who clearfix'>" +
                    "<span class='name'><b>"+data[i].topic+"</b></span>" +
                    "<span class='from'><b>"+ new Date(data[i].time).toLocaleDateString()+"</b> </span>" +
                    "<span class='fa fa-map-marker'>"+data[i].town+"</span><br>" +
                    " </div>" +
                    "<div class='text'>"+data[i].description+"</div>" +
                    "<ul class='links'>" +
                    "<li><a href='/event?eventId="+data[i].id+"'><i class='fa fa-plus'></i> 更多</a></li>" +
                    "</ul> </div> </div></div>")
            }
        },
        error: function (data) {
            console.log(data);
        }
    })


});


