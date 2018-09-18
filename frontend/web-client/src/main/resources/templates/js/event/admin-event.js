$(function(){


    var eventId = urlParam("eventId");
    var topic = $("#topic");
    var description = $("#description");
    var speakers = $("#speakers");
    var location = $("#location");
    var time = $("#time");
    var capacity = $("#capacity");
    var nbrOfAttendees = $("#nbrOfAttendees");
    var announcement = $("#announcements");
    var postContainer = $("#postContainer");


    var myName = $(".myname");

    getNbrOfAttendees();
    function getNbrOfAttendees() {
        $.ajax({
            url: "/event/attendees/count/" + eventId,
            method: 'GET',
            contentType: 'application/json',
            success: function (data) {
                nbrOfAttendees.text(data);
            },
            error: function (data) {
                console.log(data);
            }

        });
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
        url: "event/get/"+eventId,
        method: 'GET',
        contentType: 'application/json',
        success: function(data){
            console.log(data);
            topic.text(data.topic);
            announcement.text("主题： "+data.topic);
            capacity.text(data.capacity);
            description.text(data.description);
            location.text(data.location.address+" ("+data.specificAddress+")");
            var speakersArray = [];
            if(data.meSpeaker){
                speakersArray.push(myName.text());
            }
            speakersArray.push(data.otherSpeakers);
            speakers.text(speakersArray.join());
            time.text(new Date(data.time).toLocaleDateString());

        },
        error: function(data){
            console.log(data);
        }

    });


    $.ajax({
        url: "event/comments/"+eventId,
        method: 'GET',
        contentType: 'application/json',
        success: function(data){
                    console.log(data);
            for(var i=0; i<data.length; i++) {
                postContainer.append(generateComment(data[i].name,data[i].text,data[i].username));
            }
        },
        error: function(data){
            console.log(data);
        }

    });


    $.ajax({
        url: "event/files/"+eventId,
        method: 'GET',
        contentType: 'application/json',
        success: function(data){
            console.log(data);
            for(var i=0; i<data.length; i++) {
                generateFileRow(data[i]);
            }
        },
        error: function(data){
            console.log(data);
        }

    });



    function urlParam (name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
            return null;
        }
        else{
            return decodeURI(results[1]) || 0;
        }
    };

    function generateFriendListItem(data){
        return $('<li class="member"><a href="/?user='+data.username+'"><img src="profile/picture/'+data.photo+'" alt="img"><b>'+data.name+'</b>'+data.town+'</a></li>')
    }



    function generateComment(name, comment,username){
        var nameContainer = $("<span/>").addClass('name').text(name);
        var text = $("<p/>").text(comment);
        var commentContainer = $("<li/>").text(comment);
        var image = $("<img src='/profile/mypicture/"+username+"'>");
        commentContainer.prepend(nameContainer);
        commentContainer.prepend(image);
        return commentContainer;
    }

    var fileList = $("#fileList");
    function generateFileRow(file) {
        var row = $("<tr/>");
        var nameFile = $("<td/>").text(file.fileName);
        nameFile.prepend("<i class='fa fa-file'></i>");
        var fileExtension = $("<td/>").text(getFileExtension1(file.fileName));
        var downloadLink = $("<td/>").html('<a href="/event/file/download/'+eventId+'/'+file.fileId+'"><i class="fa fa-download"></i></a>');
        row.append(nameFile).append(fileExtension).append(downloadLink);
        fileList.append(row);
    }

    function getFileExtension1(filename) {
        return (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename)[0] : undefined;
    }
});