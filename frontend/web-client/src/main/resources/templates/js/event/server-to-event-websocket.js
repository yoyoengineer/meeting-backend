'use strict';

$(function() {
    var eventId = urlParam("eventId");
    var screen = $("#screen");  // used as the frame of the canvas, providing background and size
    var canvasEl = $("#canvas");  // used for drawing bullets
    var bulletOn = $("#bullet");  // For turn the bullet screen on or off
     // used for announcement on the screen

    var shootBullet = $("#shootBullet");
    var allowBulletTobeShot = $("#allowBulletTobeShot");
    // var bullet = $("#bullet");
    var votes = $("#votes");
    var nbrOfAttendees = $("#nbrOfAttendees");


// Resize the canvas to the size of the screen
    var newWidth = screen.width();
    var newHeight = screen.height();
    canvasEl.attr('width', newWidth);
    canvasEl.attr('height', newHeight);
    var arr = ['#FF9800', '#5D4037', '#FF5252', '#512DA8', '#512DA8', '#388E3C', '#FF5722'];
    var _createClass = function () {
        function defineProperties(target, props) {
            for (var i = 0; i < props.length; i++) {
                var descriptor = props[i];
                descriptor.enumerable = descriptor.enumerable || false;
                descriptor.configurable = true;
                if ("value" in descriptor) descriptor.writable = true;
                Object.defineProperty(target, descriptor.key, descriptor);
            }
        }

        return function (Constructor, protoProps, staticProps) {
            if (protoProps) defineProperties(Constructor.prototype, protoProps);
            if (staticProps) defineProperties(Constructor, staticProps);
            return Constructor;
        };
    }();

    function _classCallCheck(instance, Constructor) {
        if (!(instance instanceof Constructor)) {
            throw new TypeError("Cannot call a class as a function");
        }
    }

    var Barrage = function () {
        function Barrage(canvas) {
            _classCallCheck(this, Barrage);

            this.canvas = document.getElementById(canvas);
            var rect = this.canvas.getBoundingClientRect();
            this.w = rect.right - rect.left;
            this.h = rect.bottom - rect.top;
            this.ctx = this.canvas.getContext('2d');

            this.ctx.font = '20px Microsoft YaHei';
            this.barrageList = [];
        }

        //添加弹幕列表


        _createClass(Barrage, [{
            key: 'shoot',
            value: function shoot(value) {

                // For the binding the width of the drawing to the canvas
                canvasEl.attr('width', screen.width());
                canvasEl.attr('height', screen.height());
                this.ctx.font = '20px Microsoft YaHei';

                ///////

                var top = this.getTop();
                var color = this.getColor();
                var offset = this.getOffset();
                var width = Math.ceil(this.ctx.measureText(value).width);

                var barrage = {
                    value: value,
                    top: top,
                    left: this.w,
                    color: color,
                    offset: offset,
                    width: width
                };
                this.barrageList.push(barrage);
            }

            //开始绘制

        }, {
            key: 'draw',
            value: function draw() {
                if (this.barrageList.length) {
                    this.w = screen.width();
                    this.h = screen.height();

                    this.ctx.clearRect(0, 0, this.w, this.h);
                    for (var i = 0; i < this.barrageList.length; i++) {
                        var b = this.barrageList[i];
                        if (b.left + b.width <= 0) {
                            this.barrageList.splice(i, 1);
                            i--;
                            continue;
                        }
                        b.left -= b.offset;
                        this.drawText(b);
                    }
                }
                requestAnimationFrame(this.draw.bind(this));
            }

            //绘制文字

        }, {
            key: 'drawText',
            value: function drawText(barrage) {
                this.ctx.fillStyle = barrage.color;
                this.ctx.fillText(barrage.value, barrage.left, barrage.top);
            }

            //获取随机颜色

        }, {
            key: 'getColor',
            value: function getColor() {
                // console.log("There is a new color!");
                var index = Math.floor(Math.random() * arr.length);
                return arr[index];
            }

            //获取随机top

        }, {
            key: 'getTop',
            value: function getTop() {
                //canvas绘制文字x,y坐标是按文字左下角计算，预留30px
                return Math.floor(Math.random() * (this.h - 30)) + 30;
            }

            //获取偏移量

        }, {
            key: 'getOffset',
            value: function getOffset() {
                return +(Math.random() * 3).toFixed(1) + 1;
            }
        }]);

        return Barrage;
    }();


// This is where the screen is filled with the bullets to be used for websockets
    var barrage = new Barrage('canvas');
    barrage.draw();

    // $("#shootBullet").on('click', function (event) {
    //     console.log("clicked on bullet send");
    //     event.preventDefault();
    //     if (bulletOn.is(":checked")) {
    //         var t = $("#message").val();
    //         if (t !== null && t !== undefined && t !== '')
    //             barrage.shoot(t);
    //     }
    // });

    function urlParam (name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null){
            return null;
        }
        else{
            return decodeURI(results[1]) || 0;
        }
    };


    var stompClient = null;
    var socket = null;

    function connect() {
        socket = new SockJS('/stompwebsocket');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, stompSuccess, stompFailure);
    }

    function stompSuccess(frame) {

        stompClient.subscribe('/user/queue/event/'+eventId,showMessage);
        stompClient.subscribe("/topic/event/"+eventId,showMessage);
    }

    function showMessage(message){
        console.log(message);
        var receivedMessage = JSON.parse(message.body);
        var content = JSON.parse(receivedMessage.content);

        switch(receivedMessage.type){
            case 'COMMENT':
                postContainer.prepend(generateComment(content.name,content.text,content.username));
                break;
            case 'VOTE':
                getVoteResult(content.id);
                break;
            case 'FILE':
                generateFileRow(content);
                break;
            case 'ANNOUNCEMENT':
                break;
            case 'NEW_VOTE':
                getVoteResult(content.bulletinId);
                break;
            case 'LOTTERY':
                getLotteryResult(eventId);
                break;
            case 'LOTTERY_WIN':
                getLotteryResult(eventId);
                console.log(content);
                if (bulletOn.is(":checked")) {
                        barrage.shoot(content.name+"获得了"+content.prize);
                }
                break;
            case 'BULLET':
                if (bulletOn.is(":checked")) {
                    if (content.text !== null && content.text !== undefined && content.text !== '')
                        barrage.shoot(content.text);
                }
                break;
            case 'ATTENDANCE':
                getNbrOfAttendees();
                break;

        }


    }

    function stompFailure(error) {
        console.log("Reconnecting ");
        setTimeout(connect, 10000);
    }

    function disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        window.location.href = "/chat";
    }

    connect();


    //////////////////////////////////////////////////////
    ////////////////// VOTE PART
    //////////////////////////////////////////////


    var votes = $("#votes");   // Posting votes area
    /** To be removed and be used in server-to-event-websocket.js **/
    var announcements = $("#announcements");


    // Delegating event handler to each vote
    votes.on('click','.myVote',function(){
        console.log("I have voted");
        console.log($(this));
        var panelBody = $(this).parent().prev();
        var selctionList = panelBody.find('select');
        console.log(selctionList);
        console.log("This is the chosen value :"+selctionList.val());
    });





    function generatePanel(opts,question){

        var optionList = $("<select/>").addClass('col-lg-8 input-sm');
        for(var i=0;i<opts.length;i++){
            var opt = $("<option/>").text(opts[i]);
            optionList.prepend(opt);

        }

        var panel = $("<div/>").addClass('panel panel-default');
        var panelTitle = $("<div/>").addClass("panel-title").text(question);
        var formGroup = $("<div/>").addClass("form-group").html(optionList);
        var form = $("<form/>").html(formGroup);
        var panelBody = $("<div/>").addClass("panel-body text-center").html(form);
        var panelFooter = $("<div/>").addClass("panel-footer text-right").html("<button class='btn myVote'><i class='fa fa-hand-o-up'></i>投票</button>");

        panel.prepend(panelFooter);
        panel.prepend(panelBody);
        panel.prepend(panelTitle);

        // For display announcement only



        return panel ;

    }


    /////////////////////////
    ////////// FOR COMMENTS
    ///////////////
    var postContainer = $("#postContainer"); // For displaying comments show user $().before();

    function generateComment(name, comment,username){
        var nameContainer = $("<span/>").addClass('name').text(name);
        var text = $("<p/>").text(comment);
        var commentContainer = $("<li/>").text(comment);
        var image = $("<img src='/profile/mypicture/"+username+"'>");
        commentContainer.prepend(nameContainer);
        commentContainer.prepend(image);
        return commentContainer;
    }




    /////////////////////////////////////////
    //////////////// This is for the file part
    /////////////////////////////////////
    var fileList = $("#fileList"); // fileList placeholder

    function generateFileRow(file) {
        var row = $("<tr/>");
        var nameFile = $("<td/>").text(file.fileName);
        nameFile.prepend("<i class='fa fa-file'></i>");
        var fileExtension = $("<td/>").text(getFileExtension1(file.fileName));
        var downloadLink = $("<td/>").html('<a href="/event/file/download/'+eventId+'/'+file.fileId+'"><i class="fa fa-download"></i></a>');
        row.append(nameFile).append(fileExtension).append(downloadLink);
        fileList.prepend(row);
    }

    function getFileExtension1(filename) {
        return (/[.]/.exec(filename)) ? /[^.]+$/.exec(filename)[0] : undefined;
    }



    function showVoteResult(bulletin) {
        votes.text('');
        votes.append("<h5>" + bulletin.question + "</h5>");
        var results = bulletin.results;
        var total = getTotal(results);
        for (var key in results) {
            // skip loop if the property is from prototype
            if (!results.hasOwnProperty(key)) continue;
            var widthZ = 0;
            var points = results[key];

            if(total>0){
                widthZ = Math.floor((points * 100 )/total);
                console.log("widthz "+widthZ);
            }
            console.log(key + " = " + results[key]);
            votes.append("<strong>" + key + "</strong><span class='pull-right'>" + points + "</span> " +
                "<div class='progress active' > " +
                "<div class='progress-bar' style ='width:"+widthZ+"%;'></div> </div>")
            // var div1 = $("<div class='progress progress-bar-success'></div>");
            // var div2 = $("<div class='progress-bar' ></div>").css(
            //     'width',
            //     0 + '%'
            // );
            // div1.append(div2);
            // votes.append(div2);

        }
    }

    function getTotal(results){
        var total = 0;
        for (var key in results) {
            // skip loop if the property is from prototype
            if (!results.hasOwnProperty(key)) continue;

            total +=results[key];
        }

        return total;
    }

    function getVoteResult(bulletinId){
        $.ajax({
            url: '/event/bulletin/'+bulletinId,
            success: function (data) {
                console.log(data);
                showVoteResult(data);
            },
            error: function (data) {
                console.log(data)
            }
        })

    }

    var lottery = $("#lottery");
    function showLotteryResult(players){


        var tableBody = $("<tbody/>");

        for(var i=0; i < players.length; i++){
            tableBody.append("<tr>" +
                                "<th scope='row'>"+(i+1)+"</th>" +
                                "<td>"+players[i].name+"</td>" +
                                "<td>"+players[i].prize+"</td>" +
                            "</tr>")
        }

        var table = $("<table class=\"table\">" +
            "  <thead>" +
            "    <tr>" +
            "      <th scope='col'>#</th>" +
            "      <th scope='col'>名字</th>" +
            "      <th scope='col'>奖项</th>" +
            "    </tr>" +
            "  </thead>").append(tableBody);

        lottery.html(table);
    }

    function getLotteryResult(eventId){
        $.ajax({
            url: '/event/lottery/'+eventId,
            success: function (data) {
                console.log(data);
                showLotteryResult(data);
            },
            error: function (data) {
                console.log(data)
            }
        })
    }

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

    // ADD AN ANNOUNCER FOR EVENTS ON THE SCREEN
    // ADD A VOTE UPDATE SUBSCRIBER
    // ADD AN ABOUT EVENT MODIFICATION SUBSCRIBER
    // ADD A COMMENT UPDATE SUBSCRIBER
    // ADD A FILE LIST UPDATE SUBSCRIBER
    // ADD A SCREEN BULLET SUBSCRIBER
    // ADD NOTIFICATIONS
});




