$(function(){

/* ================================================
Modify Topic
================================================ */
    $("#topicSave").on("click",function(event){
        event.preventDefault();
        console.log("Operating on the new topic button");
        var showResult = $(this).next();
        showResult.html('');
        var newTopic = $('#newTopic').val();
        console.log("newTopic: " + newTopic);
        if(newTopic!=='') {
            $.ajax({
                method: 'POST',
                url: 'http://httpbin.org/post',
                data: JSON.stringify({'newTopic':newTopic}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(){
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    });

    /* ================================================
  Modify Description
  ================================================ */
    $("#descriptionSave").on("click",function(event){
        event.preventDefault();
        console.log("Operating on the new description button");
        var showResult = $(this).next();
        showResult.html('');
        var newDescription = $('#newDescription').val();
        console.log("newDescription: " + newDescription);
        if(newDescription!=='') {
            $.ajax({
                method: 'POST',
                url: 'http://httpbin.org/post',
                data: JSON.stringify({'newDescription':newDescription}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(){
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    })


    /* ================================================
    Modify Speakers
    ================================================ */

    $("#speakersModify").on("click",function(){
        $("#newSpeakers").val($("#speakers").text());
    });

    $("#speakersSave").on("click",function(event){
        event.preventDefault();
        console.log("Operating on the new speakers topic button");
        var showResult = $(this).next();
        showResult.html('');
        var newSpeakers = $('#newSpeakers').val();
        console.log("newSpeakers: " + newSpeakers);
    if(newSpeakers!=='') {
            $.ajax({
                method: 'POST',
                url: 'http://httpbin.org/post',
                data: JSON.stringify({'newSpeakers':newSpeakers}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(){
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    });


    /* ================================================
    Modify Time
    ================================================ */
    $("#timeSave").on('click', function (event) {
        event.preventDefault();
        console.log("Operating on new time button");
        var showResult = $(this).next();
        showResult.html('');
        var newTime = $('#newTime').val();
        newTime = Date.parse(newTime);
        console.log("newTime: " + newTime);
        if(newTime!=='') {
            $.ajax({
                method: 'POST',
                url: 'http://httpbin.org/post',
                data: JSON.stringify({'newTime':newTime}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(){
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    });


    /* ================================================
    Modify Capacity
    ================================================ */
    $("#capacitySave").on("click",function(event){
        event.preventDefault();
        console.log("Operating on the new capacity button");
        var showResult = $(this).next();
        showResult.html('');
        var newCapacity = $('#newCapacity').val();
        console.log("newCapacity: " + newCapacity);
        if(newCapacity!=='') {
            $.ajax({
                method: 'POST',
                url: 'http://httpbin.org/post',
                data: JSON.stringify({'newCapacity':newCapacity}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(){
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    })
});

