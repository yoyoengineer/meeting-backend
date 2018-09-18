$(function(){

    // Get all the button involved in the vote functionality
    var eventId = urlParam("eventId");
    var question = $("#question"); // the question textarea
    var options= $("#options"); // options to be chosen from for the vote
    var submission = $("#submission"); // To show the next and remove button
    var askBtn = $("#ask");
    var votes = $("#votes");   // Posting votes area
    var resetBtn = $("#reset");  // Cancel the current vote
    var showResult = $("#showResult");
    var nbreOfFiles =  $("#nbrOfFiles");  // To display the number of files



    console.log("I am loginnin in");

    // The variable that will make sure that the option button is clicked one and only one time
    var again = true;

    // Hide both the
    submission.hide();
    askBtn.hide();

    // The button for allowing the user to enter options.
    $("#optionsBtn").on('click',function(event){
        event.preventDefault();
        if(question.val()!=null && question.val()!==undefined && question.val()!==''){
            if(again) {
                addInput();
                again = false;
                submission.show();
                askBtn.show();

            }
        }
    });

    // clear everything
    resetBtn.on('click',function(event){
        event.preventDefault();
        question.val('');
        options.html('');
        submission.hide();
        showResult.html('');
        askBtn.hide();
        again=true;
    });

    // add an option to the answer options
    $("#next").on('click',function(event){
        event.preventDefault();
        var currentOption = options.children(":last").children(":last");
        console.log(currentOption);
        console.log(options.first());
        console.log("I am about to add a next one");
        if(currentOption.val()!=null && currentOption.val()!==undefined && currentOption.val()!==''){
            currentOption.prop('disabled',true).addClass('color5');
            console.log("It is not empty");
            addInput();
        }
    });

    function addInput(){
        console.log("I am adding a input");
        var div = $('<div/>');
        div.html('<input type="text" placeholder="输入选项" class="input-sm">');
        options.append(div);
    }

    // remove an option from the answer options
    $("#remove").on('click',function(event){
        event.preventDefault();

        var currentOptionDiv = options.children(":last");
        var previousOptionDiv = currentOptionDiv.prev(":first");
        console.log("CurrentOptionDiv length is" + currentOptionDiv.length);
        if (currentOptionDiv.length!==0) {
            console.log("I am removing: ", currentOptionDiv);
            currentOptionDiv.remove();

            console.log("Previous div length is" + previousOptionDiv.length);
            if (previousOptionDiv.length !== 0) {
                previousOptionDiv.children(":first").prop('disabled', false)
                    .removeClass('color5').focus();
            }
            else {
                submission.hide();
                again=true;
                askBtn.hide();
            }
        }

    });

    // Send vote question to the server
    askBtn.on('click',function(event){
        event.preventDefault();
        showResult.html('');
        var opts=[];

        options.find('div').each(function(){
            var element = $(this).children(":first").val();
             if(element!=='') {
                 opts.push(element);
             }
        });


        var data = {
            'question':question.val(),
            'options':opts,
            'eventId':eventId
        };
        if(opts.length!==0) {
            $.ajax({
                url: '/event/vote/create',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(data),
                success: function (data) {
                    console.log(data);
                    resetBtn.trigger('click');
                },
                error: function (data) {
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
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




});