$(function(){
   var username = "";
   var myName = $(".myname");
   var myFriendName = $(".myfriendname");
   var aboutMe = $(".aboutMe");
   var school = $(".myCompany");
   var myCurrentTown = $(".myCurrentTown");
   var myEmail = $(".myEmail");
   var myPicture = $(".myPicture");
   var myFriendPicture = $(".myFriendPicture");
   var myPictureURL = "profile/mypicture";
   var myFriendPictureURL = myPictureURL;
   var newFriend =  $("#newFriend");

    var friendList = $("#friendList");
    $("#friendRefresh").on('click',function(event){
        event.preventDefault();
        myFriendListFunction();
        friendList.empty();
    });

    console.log("Loading the profile.js");

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
    var url = "/profile/user";

    if(user!==undefined && user!== null){
       url+="/"+user;
       myFriendPictureURL+="/"+user;
    }

    myPicture.attr("src","/profile/mypicture");
    myFriendPicture.attr("src",myFriendPictureURL);



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

    $.ajax({
        url: url,
        method: 'GET',
        contentType: 'application/json',
        success: function(data){
            console.log(data);
            myFriendName.text(data.name);
            myCurrentTown.text(data.currentTown);
            aboutMe.text(data.aboutMe);
            school.text(data.schoolName);
            if(data.email!==undefined)
               myEmail.text(data.email);
            username = data.username;

            if(data.areFriend===false){
                newFriend.html("<i class=\"fa fa-plus\"></i>添加好友").attr("class","btn btn-success")
                    .on('click',function(event){
                        event.preventDefault();
                        var newFrienUrl ="profile/befriend/"+username;
                        $.ajax({
                            url: newFrienUrl,
                            method: 'POST'
                        })
                    })
            }
        },
        error: function(data){
           console.log(data);
        }

    });


   myFriendListFunction();


    function generateFriendListItem(data){
        return $('<li class="member"><a href="/?user='+data.username+'"><img src="profile/picture/'+data.photo+'" alt="img"><b>'+data.name+'</b>'+data.town+'</a></li>')
    }

    console.log("Finished loading the profile");
});