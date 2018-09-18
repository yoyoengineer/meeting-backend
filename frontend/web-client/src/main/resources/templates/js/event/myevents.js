$(function(){

    var myEvents = $("#myEvents");

    var urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
            return null;
        }
        else{
            return decodeURI(results[1]) || 0;
        }
    };
    var user= urlParam("user");

    console.log(user);
    var url = "/profile/conferences";

    if(user!==undefined && user!== null){
        url+="/user/"+user;
    }

    else
        url+="/mine";

    $.ajax({
        url: url,
        method: 'GET',
        contentType: 'application/json',
        success: function (data) {
            console.log(data);
            for (var i = 0; i < data.length; i++) {
                console.log("eventId is",data[i].eventId);
                myEvents.append("  <div class='panel panel-default'> " +
                    "<div class='panel-body status'>" +
                    " <ul class='panel-tools'> " +
                    "<li>" + data[i].likes + " <i class='fa fa-thumbs-o-up'></i> 赞</li> " +
                    "<li><a class='icon expand-tool'><i class='fa fa-expand'></i></a></li> " +
                    "</ul> " +
                    "<div class='who clearfix'> " +
                    "<span class='name'><b>" + data[i].topic + "</b></span> " +
                    "<span class='from'><b>" + new Date(data[i].time).toLocaleDateString() + "</b></span> " +
                    "<span class='fa fa-map-marker'> " + data[i].town + "</span><br> " +
                    // "<span class='fa fa-group color-up'>会议人数：2(30 %)</span> " +
                    "</div> " +
                    "<div class='text'>" + data[i].description + "</div> " +
                    "<ul class='links'> " +
                    "<li><a href='/event?eventId=" + data[i].id + "'><i class='fa fa-plus'></i> 更多</a></li> " +
                    "</ul> " +
                    "</div> " +
                    "</div>")
            }
        },
        error: function (data) {
            console.log(data);
        }
    })



});