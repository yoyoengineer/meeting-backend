// var map = new BMap.Map("allmap", {mapType: BMAP_NORMAL_MAP});
// var map1 = new BMap.Map("allmap1", {mapType: BMAP_NORMAL_MAP})
// var yesbinaryLocation = new Object();
// initLocation(map);
// initLocation(map1);
function initLocation(map)
{
    var yesbinaryLocation = new Object();
    //设置中心点坐标
    var point = new BMap.Point(120, 30);
    //设置地图类型及最小最大级别

    var marker = new BMap.Marker(point); //将点转化成标注点
    map.addOverlay(marker);  //将标注点添加到地图上

    //设置地图级别（1-18）
    map.centerAndZoom(point, 12);
    var geolocation = new BMap.Geolocation();
    geolocation.getCurrentPosition(function(r){
        if(this.getStatus() == BMAP_STATUS_SUCCESS){
            var mk = new BMap.Marker(r.point);
            map.addOverlay(mk);
            map.panTo(r.point);
            // alert('您的位置：'+r.point.lng+','+r.point.lat);
            yesbinaryLocation.latitude = r.point.lat;
            yesbinaryLocation.longitude = r.point.lng;
        }
        else {
            alert('failed'+this.getStatus());
        }
    },{enableHighAccuracy: true})
    //开启滚轮缩放地图
    map.enableScrollWheelZoom();
    return yesbinaryLocation;
}

function G(id) {
    return document.getElementById(id);
}

// var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
//     {
//         "input": "suggestId"
//         , "location": map
//     });

// var ac1 = new BMap.Autocomplete(    //建立一个自动完成的对象
//     {
//         "input": "suggestId1"
//         , "location": map1
//     });

// ac.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
//     var searchResultPanel = G("searchResultPanel");
//     mouseOnSelector(e,searchResultPanel);
// });

// ac1.addEventListener("onhighlight", function (e) {  //鼠标放在下拉列表上的事件
//     var searchResultPanel = G("searchResultPanel1");
//     mouseOnSelector(e,searchResultPanel);
// });

function mouseOnSelector(e,searchResultPanel) {
    var str = "";
    var _value = e.fromitem.value;
    var value = "";
    if (e.fromitem.index > -1) {
        value = _value.province + _value.city + _value.district + _value.street + _value.business;
    }
    str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;

    value = "";
    if (e.toitem.index > -1) {
        _value = e.toitem.value;
        value = _value.province + _value.city + _value.district + _value.street + _value.business;
    }
    str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
    searchResultPanel.innerHTML = str;
}

// ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
//     var searchResultPanel = G("searchResultPanel");
//     clickOnSelectorItem(e, searchResultPanel,map);
// });

// ac1.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
//     var searchResultPanel = G("searchResultPanel1");
//     clickOnSelectorItem(e, searchResultPanel,map1);
// });

function clickOnSelectorItem(e, searchResultPanel,map) {
    var _value = e.item.value;
    var myValue = _value.province + _value.city + _value.district + _value.street + _value.business;
    console.log("myValue:",myValue);
    // yesbinaryLocation.Address = myValue;
    searchResultPanel.innerHTML = "onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;

    return setPlace(myValue,map,_value.city);
}

function setPlace(myValue,map,town) {
    var yesbinaryLocation = new Object();
    yesbinaryLocation.address = myValue;
    yesbinaryLocation.town = town;
    // console.log("value0:",yesbinaryLocation);
    map.clearOverlays();    //清除地图上所有覆盖物
    function myFun() {
        var pp = local.getResults().getPoi(0).point;    //获取第一个智能搜索的结果
        yesbinaryLocation.latitude = pp.lat;
        yesbinaryLocation.longitude = pp.lng;
        map.centerAndZoom(pp, 18);
        map.addOverlay(new BMap.Marker(pp));    //添加标注
    }
// console.log("value1:",yesbinaryLocation);
    var local = new BMap.LocalSearch(map, { //智能搜索
        onSearchComplete: myFun
    });
    local.search(myValue);
    return yesbinaryLocation;
}
var geoc = new BMap.Geocoder();
// map.addEventListener("click", function(e){
//     mapOnClick(e,G('suggestId'));
// });

// map1.addEventListener("click", function(e){
//     mapOnClick(e,G('suggestId1'));
// });

function mapOnClick(e,searchInput) {
    var yesbinaryLocation = new Object();
    var pt = e.point;
    yesbinaryLocation.latitude = pt.lat;
    yesbinaryLocation.longitude = pt.lng;
    geoc.getLocation(pt, function(rs){
        var addComp = rs.addressComponents;
        console.log("lng:" + pt.lng + "lat:" + pt.lat);
        searchInput.value=addComp.province + " " + addComp.city + " " + addComp.district + " " + addComp.street + " " + addComp.streetNumber;
        yesbinaryLocation.address = searchInput.value;
        yesbinaryLocation.town = addComp.city;
        console.log("value:",yesbinaryLocation);
        // console.log(addComp.province + ", " + addComp.city + ", " + addComp.district + ", " + addComp.street + ", " + addComp.streetNumber);
    });
    return yesbinaryLocation;
}