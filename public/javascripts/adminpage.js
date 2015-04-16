
$(document).ready(function(){
    // the "href" attribute of .modal-trigger must specify the modal ID that wants to be triggered
    $('.modal-trigger').leanModal();
/*
    $( "input[name='datePicker']").each(function() {
        $(this).datetimepicker({
            format:'m-d-Y H:m',
            mask:true
        });
    });
*/
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


