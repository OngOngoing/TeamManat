$(document).ready(function(){
    var _text = $('#user-collection').html();
    $('.modal-trigger').leanModal();
    $('#search-user').keyup(function(){
        if($('#search-user').val().length < 1){
            $('#user-collection').html(_text);
            $('.modal-trigger').leanModal();
            $(".editModal").click(function(){
                var userId = $(this).attr("userId");
                var username = $(this).attr("username");
                var password = $(this).attr("password");
                var idtype = $(this).attr("idtype");
                var projectId = $(this).attr("projectId");
                var firstname = $(this).attr("firstname");
                var lastname = $(this).attr("lastname");

                if(idtype == 3) {
                    $("#editUserModal #edit_type3").prop( 'checked', true );
                    $("#editUserModal #edit_type2").prop( 'checked', false );
                }
                if(idtype == 2) {
                    $("#editUserModal #edit_type2").prop( 'checked', true );
                    $("#editUserModal #edit_type3").prop( 'checked', false);
                }
                $("#editUserModal #username").val(username);
                $("#editUserModal #password").val(password);
                $("#editUserModal #firstname").val(firstname);
                $("#editUserModal #lastname").val(lastname);
                $("#editUserModal #projectId").val(projectId);


                $("#editUserModal #userID").val(userId);
                $("#editUserModal #userId").val(userId);
            });
        }else {
            $.ajax({
                type: "POST",
                url: "../searchuser",
                dataType: "text",
                data: {
                    'search_keyword': $(this).val()
                },
                success: function (data) {
                    var search = JSON.parse(data);
                    var html = '';
                    for(var i=search.length-1;i>=0;i--) {
                        var userid = search[i].userid;
                        var username = search[i].username;
                        var firstname = search[i].firstname;
                        var lastname = search[i].lastname;
                        var userType = search[i].userType;
                        var project = search[i].project;
                        var projectId = search[i].projectId;
                        var userIdType = search[i].userIdType;
                        html += '<li class="collection-item avatar"><div class="row"><div class="col s12">';
                        if(userIdType != 3) {
                            html += '<input type="checkbox" name="id" id="' + userid + '" value="' + userid + '">';
                            html += '<label class="check" for="' + userid + '"></label>';
                        }
                        html += '<i class="mdi-social-person circle teal lighten-1 trigger"></i>';
                        html += '<span class="title teal-text lighten-1 trigger">ID : '+userid+'</span><p>';
                        html += '<strong>Username</strong> : '+username+' <br />';
                        html += '<strong>Firstname</strong> : '+firstname+' <strong>Lastname</strong> : '+lastname+'<br />';
                        html += '<strong>User Type</strong> : '+userType+' <br />';
                        html += '<strong>Owner Project</strong> : '+project+'</p></div>';
                        html += '<a userid="'+userid+'" username="'+username+'" password="" idtype="'+userIdType+'" projectid="'+projectId+'" firstname="'+firstname+'" lastname="'+lastname+'" class="editModal secondary-content trigger modal-trigger" href="#editUserModal">'
                        html += '<i class="mdi-editor-mode-edit small"></i>';
                        html += '</a>';
                        html += '<a href="" style="position: absolute; top: 45px; right: 16px;">';
                        html += '<i class="mdi-maps-rate-review small trigger"></i>';
                        html += '</a>';
                        html += '</div>';
                        html += '</li>';
                    }
                    if(search.length == 0){
                        html = '<li class="collection-item avatar"><div class="row"><div class="col s12"><i>Not found user.</i></div></div></li>';
                    }
                    $('#user-collection').html(html);
                    $('.modal-trigger').leanModal();
                    $(".editModal").click(function(){
                        var userId = $(this).attr("userId");
                        var username = $(this).attr("username");
                        var password = $(this).attr("password");
                        var idtype = $(this).attr("idtype");
                        var projectId = $(this).attr("projectId");
                        var firstname = $(this).attr("firstname");
                        var lastname = $(this).attr("lastname");

                        if(idtype == 3) {
                            $("#editUserModal #edit_type3").prop( 'checked', true );
                            $("#editUserModal #edit_type2").prop( 'checked', false );
                        }
                        if(idtype == 2) {
                            $("#editUserModal #edit_type2").prop( 'checked', true );
                            $("#editUserModal #edit_type3").prop( 'checked', false);
                        }
                        $("#editUserModal #username").val(username);
                        $("#editUserModal #password").val(password);
                        $("#editUserModal #firstname").val(firstname);
                        $("#editUserModal #lastname").val(lastname);
                        $("#editUserModal #projectId").val(projectId);


                        $("#editUserModal #userID").val(userId);
                        $("#editUserModal #userId").val(userId);
                    });
                }
            });
        }
    });
    $( ".datePicker").each(function() {
        $(this).datetimepicker({
            format:'m-d-Y H:m'
        });
    });
    $(".materialize-textarea").focus(
        function(){
            if ($('.hiddendiv').length === 0) {
                var hiddenDiv = $('<div class="hiddendiv common"></div>'),
                    content = null;
                $('body').append(hiddenDiv);
            }
            var text_area_selector = '.materialize-textarea';
            $('.hiddendiv').css('width', $(text_area_selector).width());

            $(text_area_selector).each(function () {
                if ($(this).val().length) {
                    content = $(this).val();
                    content = content.replace(/\n/g, '<br>');
                    hiddenDiv.html(content + '<br>');
                    $(this).css('height', hiddenDiv.height());
                }
            });
        });

// File Input Path
    $('.file-field').each(function() {
        var path_input = $(this).find('input.file-path');
        $(this).find('input[type="file"]').change(function () {
            path_input.val($(this).val());
            path_input.trigger('change');
        });
    });

    $(".editModal").click(function(){
        var userId = $(this).attr("userId");
        var username = $(this).attr("username");
        var password = $(this).attr("password");
        var idtype = $(this).attr("idtype");
        var projectId = $(this).attr("projectId");
        var firstname = $(this).attr("firstname");
        var lastname = $(this).attr("lastname");

        if(idtype == 3) {
            $("#editUserModal #edit_type3").prop( 'checked', true );
            $("#editUserModal #edit_type2").prop( 'checked', false );
        }
        if(idtype == 2) {
            $("#editUserModal #edit_type2").prop( 'checked', true );
            $("#editUserModal #edit_type3").prop( 'checked', false);
        }
        $("#editUserModal #username").val(username);
        $("#editUserModal #password").val(password);
        $("#editUserModal #firstname").val(firstname);
        $("#editUserModal #lastname").val(lastname);
        $("#editUserModal #projectId").val(projectId);


        $("#editUserModal #userID").val(userId);
        $("#editUserModal #userId").val(userId);
    });
});


