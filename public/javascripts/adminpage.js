
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
});