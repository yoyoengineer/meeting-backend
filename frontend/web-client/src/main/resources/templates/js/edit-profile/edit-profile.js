/* ================================================
First Name update
================================================ */
    $("#firstNameSave").on('click', function (event) {
        event.preventDefault();
        console.log("Operating on the first name button");
        var showResult = $(this).next();
        showResult.html('');
        var firstName = $('#firstName').val();
        console.log(firstName);
        if(firstName!=='') {
            $.ajax({
                method: 'POST',
                url: '/profile/update',
                contentType: 'application/json',
                data: JSON.stringify({'fieldName':'name','fieldValue':firstName}),
                success: function(){
                    showResult
                        .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

                },
                error: function(data){
                    console.log(data);
                    showResult
                        .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
                }
            })
        }
    });

/* ================================================
Last Name update
================================================ */
$("#lastNameSave").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on the last name button");
    var showResult = $(this).next();
    showResult.html('');
    var lastName = $('#lastName').val();
    console.log("lastName: " + lastName);
    if(lastName!=='') {
        $.ajax({
            method: 'POST',
            url: 'http://httpbin.org/get',
            contentType: 'application/json',
            data: JSON.stringify({'lastName':lastName}),
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
First Name update
================================================ */
$("#emailSave").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on email button");
    var showResult = $(this).next();
    showResult.html('');
    var email = $('#email').val();
    console.log("email: " + email);
    var e_Pat = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

    if(!e_Pat.test(email)){
      $('#email').val('').attr('placeholder','Must be a valid email!');
        showResult
            .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
    }
     else if(email!=='') {
        $.ajax({
            method: 'POST',
            url: 'http://httpbin.org/post',
            contentType: 'application/json',
            data: JSON.stringify({'email':email}),
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
Birthday Date
================================================ */
$("#birthDateSave").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on birth date button");
    var showResult = $(this).next();
    showResult.html('');
    var birthDate = $('#birthDate').val();
    console.log("birthDate: " + birthDate);
    if(birthDate!=='') {
        $.ajax({
            method: 'POST',
            url: '/profile/update',
            contentType: 'application/json',
            data: JSON.stringify({'fieldName':'birthday','fieldValue':birthDate}),
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
Home Town update
================================================ */
$("#homeTownSave").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on hometown button");
    var showResult = $(this).next();
    showResult.html('');
    var homeTown = $('#homeTown').val();
    var parts = homeTown.split("/");
    if(parts.length > 1)
        homeTown = parts[1]
    homeTown = homeTown.substring(0,homeTown.length-1);
    console.log("homeTown: " + homeTown);
    if (homeTown !== '') {
        $.ajax({
            method: 'POST',
            url: '/profile/update',
            contentType: 'application/json',
            data: JSON.stringify({'fieldName':'homeTown','fieldValue':homeTown}),
            success: function () {
                showResult
                    .html('<button class="btn-rounded btn-success" disabled><i class="fa fa-check"></i></button>');

            },
            error: function () {
                showResult
                    .html('<button class="btn-rounded btn-danger" disabled><i class="fa fa-times"></i></button>')
            }
        })
    }

});

/* ================================================
Current Town update
================================================ */
$("#currentTownSave").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on current town button");
    var showResult = $(this).next();
    showResult.html('');
    var currentTown = $('#currentTown').val();
    var parts = currentTown.split("/");
    if(parts.length > 1)
        currentTown = parts[1];
    currentTown = currentTown.substring(0,currentTown.length-1);

    console.log("currentTown: " + currentTown);
    if(currentTown!=='') {
        $.ajax({
            method: 'POST',
            url: '/profile/update',
            contentType: 'application/json',
            data: JSON.stringify({'fieldName':'currentTown','fieldValue':currentTown}),
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
Password update
================================================ */
$("#savePwd").on('click', function (event) {
    event.preventDefault();
    console.log("Operating on password button");
    var pwd=$("#password");
    var pwdConfig = $("#confirm_password");
    var pwdValue = pwd.val();
    var pwdConfigValue=pwdConfig.val();
    var showResult = $("#showResult");
    showResult.html('');

    if(pwdValue==='' && pwdConfigValue===''){

    }
    else if(pwdValue!==pwdConfigValue){
       pwd.val('');
       pwdConfig.val('');
       pwd.attr('placeholder','Passwords should match');
    }
    else if(pwdValue.length < 6){
        pwd.val('');
        pwdConfig.val('');
        pwd.attr('placeholder','Password must contain at least six characters');
    }
    else {
        $.ajax({
            method: 'get',
            url: 'http://httpbin.org/get',
            contentType: 'application/json',
            data: JSON.stringify({'password':password}),
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

