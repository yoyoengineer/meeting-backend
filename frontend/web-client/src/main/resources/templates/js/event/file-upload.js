
/*jslint unparam: true, regexp: true */
/*global window, $ */
$(function () {

    var eventId = urlParam("eventId");

    // File details placeholder
    var filePlaceHolder = $("#files");

    // Delete Placeholder
    var cancelPlaceHolder = $('#cancelbTn');

    // Bar process
    var barProcess = $('#progress');

    var fileName='';

    // Create a upload button.
    var uploadButton = $('<button/>')
        .addClass('btn btn-primary')
        .prop('disabled', true)
        .text('运行中...')
        .on('click', function (event) {
            event.preventDefault();
            var $this = $(this),
                data = $this.data();
            data.submit().done(function () {
                $this.remove();
                barProcess.attr("class","progress-bar progress-bar-success");
                cancelPlaceHolder.html('');


                /** To be removed and be used in server-to-event-websocket.js **/
                // var fileList = $("#fileList");
                // var row = $("<tr/>");
                // var nameFile = $("<td/>").text(fileName);
                // nameFile.prepend("<i class='fa fa-file'></i>");
                // var fileExtension = $("<td/>").text("File Extension");
                // var downloadLink = $("<td/>").html('<a href="#"><i class="fa fa-download"></i></a>');
                // row.append(nameFile).append(fileExtension).append(downloadLink);
                // fileList.prepend(row);

            });
        });

    // Create cancel button
    var cancelButton = $('<button/>')
        .text('取消')
        .addClass('btn btn-primary')
        .on('click',function(event){
            event.preventDefault();
            $('#files').html('');
            barProcess.css('width', '0%');
            $(this).remove();

        });


 //   Defining Callback methods
    function  processAllways(e, data) {

        var  file = data.files[0];
        if (file.error) {
            data.context
                .append('<br>')
                .append($('<span class="text-danger"/>').text(file.error));
        }

        data.context.find('button')
            .text('上传')
            .prop('disabled', !!data.files.error);


    }

    function progressAll(e, data) {
        console.log("Processing your upload");
        var progress = parseInt(data.loaded / data.total * 100, 10);
        barProcess.css(
            'width',
            progress + '%'
        );
        console.log("Progress is: ",progress);

        // Disable cancel when the file is being sent to the server.
        filePlaceHolder.find('button').prop('disabled',true);
        cancelPlaceHolder.find('button')
            .prop('disabled', true);
    }

    function processFail(e, data) {
        $.each(data.files, function (index) {
            var error = $('<span class="text-danger"/>').text('上传文件失败，请稍后再试！');
            filePlaceHolder.append(error);
            filePlaceHolder.find('button').remove();
            barProcess.attr('class','progress progress-bar-danger')
            cancelPlaceHolder.find('button').prop('disabled',false);
        });
    }

  //  Initialize the uploader.
    $('#newFile').fileupload({
        url:'/event/file/'+eventId,
        dataType: 'json',
        autoUpload: false,
        acceptFileTypes: /^[^.]+$|\.(?!(js|exe)$)([^.]+$)/,
        maxFileSize: (100 * 1024 * 1024),
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true})

        .on('fileuploadadd',  function (e,data){

            console.log("Adding a file");
            barProcess.css('width', '0%');
            barProcess.attr('class','progress-bar');
            var node = $('<p/>')
                .append($('<span/>').text(data.files[0].name))
                .append('<br>')
                .append(uploadButton.clone(true).data(data));

            // Just for trying to be deleted later
            fileName = data.files[0].name;

            data.context = node;
            filePlaceHolder.html(node);
            cancelPlaceHolder.html(cancelButton.clone(true));
        })

        .on('fileuploadprocessalways',processAllways)

        .on('fileuploadprogressall',progressAll)

        .on('fileuploadfail',processFail)

        .prop('disabled', !$.support.fileInput)

        .parent().addClass($.support.fileInput ? undefined : 'disabled');

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