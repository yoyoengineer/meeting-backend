
/*jslint unparam: true, regexp: true */
/*global window, $ */
$(function () {

    // File details placeholder
    var filePlaceHolder = $("#files");

    // Delete Placeholder
    var cancelPlaceHolder = $('#cancelbTn');

    // Bar process
    var barProcess = $('#progress');

    // Create a upload button.
    var uploadButton = $('<button/>')
        .addClass('btn btn-primary')
        .prop('disabled', true)
        .text('Processing...')
        .on('click', function () {
            var $this = $(this),
                data = $this.data();
            data.submit().done(function () {
                $this.remove();
                barProcess.attr("class","progress-bar progress-bar-success");
                cancelPlaceHolder.html('');

            });
        });

    // Create cancel button
    var cancelButton = $('<button/>')
        .text('取消')
        .addClass('btn btn-primary')
        .on('click',function(event){
            $('#files').html('');
            barProcess.css('width', '0%');
            $(this).remove();

        });


    // Defining Callback methods
    function add(e,data){
        barProcess.css('width', '0%');
        console.log('I am changing the class now');
        var node = $('<p/>')
            .append($('<span/>').text(data.files[0].name))
            .append('<br>')
            .append(uploadButton.clone(true).data(data));

        data.context = node;
        filePlaceHolder.html(node);
        cancelPlaceHolder.html(cancelButton.clone(true));
    }

    function  processAllways(e, data) {

        var  file = data.files[0];
        if (file.error) {
            data.context
                .append('<br>')
                .append($('<span class="text-danger"/>').text(file.error));
        }
        else  if (file.preview) {
            data.context
                .prepend('<br>')
                .prepend(file.preview);
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
        console.log(e);
        console.log(data);
        $.each(data.files, function (index) {
            var error = $('<span class="text-danger"/>').text('File upload failed. Try again later');
            filePlaceHolder.append(error);
            filePlaceHolder.find('button').remove();
            barProcess.attr('class','progress progress-bar-danger')
            cancelPlaceHolder.find('button').prop('disabled',false);
        });
    }

    // Initialize the uploader.
    $('#profile').fileupload({
        url:'profile/picture/upload',
        dataType: 'json',
        autoUpload: false,
        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
        maxFileSize: (100 * 1024 * 1024),
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true})

        .on('fileuploadadd',  function (e,data){

            barProcess.css('width', '0%');
            barProcess.attr('class','progress-bar');
            var node = $('<p/>')
                .append($('<span/>').text('Profile picture -->> '+data.files[0].name))
                .append('<br>')
                .append(uploadButton.clone(true).data(data));

            data.context = node;
            filePlaceHolder.html(node);
            cancelPlaceHolder.html(cancelButton.clone(true));
        })

        .on('fileuploadprocessalways',processAllways)

        .on('fileuploadprogressall',progressAll)

        .on('fileuploadfail',processFail)

        .prop('disabled', !$.support.fileInput)

        .parent().addClass($.support.fileInput ? undefined : 'disabled');


    $('#background').fileupload({
        url:'http://httpbin.org/get',
        dataType: 'json',
        autoUpload: false,
        acceptFileTypes: /(\.|\/)(gif|jpe?g|png)$/i,
        maxFileSize: 10 * 1024 * 1024,
        previewMaxWidth: 100,
        previewMaxHeight: 100,
        previewCrop: true})

        .on('fileuploadadd',  function (e,data){
            barProcess.css('width', '0%');
            barProcess.attr('class','progress-bar');
            var node = $('<p/>')
                .append($('<span/>').text('Background picture -->> '+data.files[0].name))
                .append('<br>')
                .append(uploadButton.clone(true).data(data));
            data.context = node;
            filePlaceHolder.html(node);
            cancelPlaceHolder.html(cancelButton.clone(true));
        })

        .on('fileuploadprocessalways',processAllways)

        .on('fileuploadprogressall',progressAll)

        .on('fileuploadfail',processFail)

        .prop('disabled', !$.support.fileInput)

        .parent().addClass($.support.fileInput ? undefined : 'disabled');
})