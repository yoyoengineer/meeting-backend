// To be used for by other pages apart from event.html and admin-event.html
$(function(){


    /* ================================================
        Notifications initializer and handlers
    ================================================ */

    var notifications = $("#notifications");
    var notificationRefresh = $("#notificationRefresh");

    notificationRefresh.on('click',function(event){
        event.preventDefault();
        console.log("Refreshing Notification Layer");

        // CLEAN THE LIST AND THEN MAKE THE AJAX CALL
    });

    // START AJAX REQUEST PLACEHOLDER
    var arr = [{name: 'arnold', username: '22222'},
        {name: 'vigo',username: '33333'},
        {name: 'lydia',username: '1111111'},
        {name: 'gracia',username: '44444444'}];

    for(var i=0; i<arr.length; i++) {
        var newNotifications = generateNotificationPanel(arr[i]);
        notifications.append(newNotifications);
    }
    // END AJAX REQUEST PLACEHOLDER

    function generateNotificationPanel(data){

        // user/{username} You can create this to obtain the profile of a given user.
        // data should be an object containing the name and the username.
       return  $(' <div class="notificationBody kode-alert alert1">\n' +
            '          <h4>好友请求</h4>\n' +
            '          <p><b>'+data.name+'</b> 请求成为您的好友</p>\n' +
            '           <input type="hidden" value="'+data.username+'">' +
            '          <div class="text-c">\n' +
            '            <button class="acceptFriend btn btn-primary">接受</button>\n' +
            '            <button class="denyFriend btn btn-danger">拒绝</button>\n' +
            '          </div>\n' +
            '        </div>');
    }

    // Set an handler for each accept friend request
    notifications.on('click','.acceptFriend',function(event){
        event.preventDefault();
        var username = $(this).parent().prev().val();
        console.log("The  accept friend request's username is ",username);

    });


    // Set an handler for the click action for every deny friend request
    notifications.on('click','.denyFriend',function(event){
        event.preventDefault();
        var username = $(this).parent().prev().val();
        console.log("The denied friend request's username is ", username );
    });


    /* ================================================
      Friend List initializer and handlers
  ================================================ */

    var friendList = $("#friendList");
    var friendRefresh = $("#friendRefresh");

    friendRefresh.on('click',function(event){
        event.preventDefault();
        console.log("Refreshing the friend list");
        // CLEAN THE LIST AND MAKE THE AJAX CALL
    });
    var arrofFriends = [
        {photo:"img/profileimg.png", name: 'Allice Mingham', town: 'Los Angeles', username:'1111111'},
        {photo:"img/profileimg2.png", name: 'Throwing', town: 'Las Vegas', username: '2222222'},
        {photo: "img/profileimg3.png", name: 'Fred Stonefield', town: 'New York', username: '33333333'},
        {photo: "img/profileimg4.png", name: 'Chris M. Johnson', town: 'California',username: '4444444'},
        {photo: "img/profileimg5.png", name: 'Allice Mingham', town: 'Los Angeles', username: '5555555'},
        {photo: "img/profileimg6.png", name: "James Throwing", town: 'Las Vegas', username: '6666666'}
    ];

    for( i=0; i< arrofFriends.length; i++) {
        var friend = generateFriendListItem(arrofFriends[i])
        friendList.append(friend);
    }

   function generateFriendListItem(data){
       return $('<li class="member"><a href="user/'+data.username+'"><img src="'+data.photo+'" alt="img"><b>'+data.name+'</b>'+data.town+'</a></li>')
   }


    /* ================================================
        Friend List initializer and handlers
    ================================================ */
    var logout = $("#logout");
    logout.on('click',function(event){
        event.preventDefault();

        console.log("I am logging out");
        // ADD AJAX FOR LOGGING OUT
    })

});