/* ================================================
Map configurations
================================================ */
var $map0div = $('#map0');
$map0div.hide();
$('#suggestId').on('focus',function () {
    $map0div.fadeIn(300);
});
$('#suggestId').on('blur',function () {
    $map0div.hide();
});
var map = new BMap.Map("allmap", {mapType: BMAP_NORMAL_MAP});
var yesbinaryLocation = new Object();
yesbinaryLocation = initLocation(map);
var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
    {
        "input": "suggestId"
        , "location": map
    });
ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
    var searchResultPanel = G("searchResultPanel");
    mouseOnSelector(e,searchResultPanel);
});
ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
    var searchResultPanel = G("searchResultPanel");
    yesbinaryLocation = clickOnSelectorItem(e, searchResultPanel,map);
});
map.addEventListener("click", function(e){
    yesbinaryLocation = mapOnClick(e,G('suggestId'));
});

/* ================================================
Processing data
================================================ */

var invitation = $("#invitation");

$.ajax({
    url: "/profile/myfriends",
    method: 'GET',
    contentType: 'application/json',
    success: function(data){
        console.log(data);
       for(var i=0; i< data.length; i++){
           invitation.prepend("<option value='"+data[i].username+"'>"+data[i].name+" ("+data[i].town+")</option>")
       }
    },
    error: function(data){
        console.log(data);
    }

});

$("form").submit(function(event){

    event.preventDefault();

    var topic = $("#topic").val();
    var meSpeaker = $("#meSpeaker").is(":checked");
    var otherSpeaker = $("#otherSpeaker").val().split(",");
    var description = $("#description").val();
    var time = $("#time").val();
    var location = $("#location").val();
    var capacity = $("#capacity").val();
    var invitees = invitation.val();
    var showResult = $("#showResult");
    var specificAddress = $("#specificAddress").val();
    var eventPublicState = $("#eventPublicState").val();
    showResult.html('');

    console.log("time before conversion");
    // alert("before converting time is: " + time);
    time = Date.parse(time);
    // alert("time is : "+time);

    var isValid = true;

    if(!meSpeaker && otherSpeaker ==null){

        $("#beforeAlert").prepend('<div class="alert alert-info alert-dismissable text-c">\n' +
            '      <a class="panel-close close" id="alert" data-dismiss="alert">×</a>\n' +
            '      <i class="fa fa-coffee"></i>\n' +
            '      <strong>A speaker has to be specified</strong>.\n' +
            '      </div>');
        isValid = false;
    }


    yesbinaryLocation.town = yesbinaryLocation.town.substring(0,yesbinaryLocation.town.length-1);

    if(isValid){
        if(otherSpeaker==null)
            otherSpeaker =[];

        if(invitees==null)
            invitees=[];

        eventPublicState = eventPublicState!=="off";

        var data = {
            'topic':topic,
            'meSpeaker':meSpeaker,
            'otherSpeakers':otherSpeaker,
            'description':description,
            'time':time,
            'location':yesbinaryLocation,
            'capacity':capacity,
            'invitees':invitees,
            'eventPublicState': eventPublicState,
            'specificAddress':specificAddress
        };


        console.log('yesbinaryLocation:',yesbinaryLocation);
        console.log(data);
        $.ajax({
            url: '/event/create',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: function(data){
                console.log(data);
                window.location.replace("/");
            },
            error: function(data){
                showResult
                    .html('<button class="btn-rounded btn-danger" disabled>' +
                        '<i class="fa fa-times">Something went wrong! Try Again Later...</i>' +
                        '</button>');
                console.log("There was a error");
                console.log(data);
            }
        })
    }

});
