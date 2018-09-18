$(function(){
    var logout = $("#logout");
    logout.on('click',function(event){
        event.preventDefault();

        $.ajax({
            url: '/logout',
            method: 'POST',
            success: function(data){
                window.location.replace('/')
            }
        })
    })
})