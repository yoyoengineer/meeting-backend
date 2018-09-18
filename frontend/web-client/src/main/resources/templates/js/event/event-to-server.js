/* ================================================
Topic data
================================================ */
$(function() {
    var eventId = urlParam("eventId");
    var topic = $("#topic");
    var description = $("#description");
    var speakers = $("#speakers");
    var location = $("#location");
    var time = $("#time");
    var capacity = $("#capacity");
    var nbrOfAttendees = $("#nbrOfAttendees");


    /* ================================================
  Page initializer About area
  ================================================ */

    // function populateAbout(data) {
    //
    // if(data.topic!=null && data.topic!==undefined && data.topic!=='') {
    //     topic.text(data.topic);
    //     description.text(data.description);
    //     var elements = '';
    //     $.each(data.speakers,function(index,e){
    //         if(index===0)
    //             elements=e;
    //         else
    //              elements += '; '+ e;
    //     });
    //     speakers.text(elements);
    //     location.text(data.location);
    //     var date = new Date(data.time);
    //     time.text(date.toLocaleString());
    //     capacity.text(data.capacity);
    //     nbrOfAttendees.text(data.nbrOfAttendees);
    // }
    //
    //
    // }

    // $.ajax({
    // url: 'http://localhost:8080/event/details',
    // success : populateAbout,
    // fail: populateAbout
    // });


    /* ================================================
   Event Initialize and Send Bullet Screen
   ================================================ */
    var shootBullet = $("#shootBullet");
    var allowBulletTobeShot = $("#allowBulletTobeShot");
    var bullet = $("#bullet");
    var bulletMessage = $("#message");


    // When the admin does not allow outsider to shoot bullets
    allowBulletTobeShot.on('change',function(){
        if(allowBulletTobeShot.is(':checked')){
            // allow outsiders to shoot bullet
        }

        else{
            // disallow outsiders to shoot bullets
        }

    });

    // SEND THE BULLET TO THE SERVER
    shootBullet.on('click',function(event){
        event.preventDefault();
        console.log("I am pressed once");
        var message = bulletMessage.val();
        if(message!==null && message!==undefined && message!==''){
            bulletMessage.val('');
            console.log("I am in the true thing");
            $.ajax({
                url: "/event/bullet/" + eventId,
                method: 'POST',
                data: JSON.stringify({'text':message}),
                contentType: 'application/json',
                success: function (data) {
                    console.log(data);
                },
                error: function (data) {
                    console.log(data);
                }

            });
        }

    });



    /* ================================================
      Event Initialize and Send Votes
      ================================================ */
    var votes = $("#votes");   // Posting votes area

    // AJAX REQUEST FOR INITIALIZATION
    // DEFINING AN HANDLER
    // Delegating event handler to each vote
    votes.on('click','.myVote',function(){
        console.log("I have voted");
        console.log($(this));
        var panelBody = $(this).parent().prev();
        var selctionList = panelBody.find('select');
        console.log(selctionList);
        console.log("This is the chosen value :"+selctionList.val());

        // SEND IT TO THE SERVER
    });





    /* ================================================
    Event Initialize and Send Comments
    ================================================ */
    var posts = $("#post");
    var postBtn = $("#postBtn");
    var postContainer = $("#postContainer"); // For displaying comments show user $().before();
    var showResultForCommentPost = $("#showSendPostResult"); // For showing whether a post succeeded or not

    //MAKE AN AJAX CALL HERE INITIALIZING COMMENTS
    // DEFINE AN HANDLER HERE

    // Handler for the post event
    postBtn.on('click',function(event){
        event.preventDefault();
        var myComment = posts.val();
        showResultForCommentPost.html('');
        if(myComment!==null && myComment!==undefined && myComment!==''){

            var data = {
                'eventId':eventId,
                'text':myComment
            };

            $.ajax({
                url: '/event/comment',
                contentType: 'application/json',
                method: 'POST',
                data: JSON.stringify(data),
                success: function(data){
                    console.log(data);

                    // This should be moved to the websocket part.
                    showResultForCommentPost.html('');
                    posts.val('')
                },
                error: function(error){
                    console.log(error);
                    showResultForCommentPost
                        .html('<button class="btn-rounded btn-danger" disabled>' +
                            '<i class="fa fa-times"></i>' +
                            'Failed. Try Again' +
                            ' </button>')

                }
            })
        }
    });



    /*================================================
   Event Initialize and Send FILES
   ================================================ */




    // ADD AJAX FOR QUERYING LIST OF FILES !!! ADD A DOWNLOAD FOR EVERY FILE


    /* ================================================
     Event Initialize Friends And Notifications
     ================================================ */



    function urlParam (name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
            return null;
        }
        else{
            return decodeURI(results[1]) || 0;
        }
    };
});