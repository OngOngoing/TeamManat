$("a[name='criteriaButton']").click(function () {
    var criteria = this.id;
    var value = $(this).attr('score');
    $("a[id='" + criteria + "']").each(function () {
        if($(this).attr('score') != 0){
            $(this).addClass("btn-flat");
        }
    })
    $("#" + criteria).val(value);
    $(this).removeClass("btn-flat").addClass("btn");

});
$(function() {
    var $textarea = $('.projectDescription').attr("data"),
        convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').html(convert($textarea));
});

$(document).ready(function(){
    $('.collapsible').collapsible({
      accordion : false // A setting that changes the collapsible behavior to expandable instead of the default accordion style
    });
    $('.slider').slider({
        full_width: true
    });
    $('.materialboxed').materialbox();
    $('.dropdown-button').dropdown({
        inDuration: 0,
        belowOrigin: true
    });
    $('#inbox').click(function(){
        $('.pointer').css('left', $(this).position().left - $('#inbox_message').position().left + 15 );
    });
    $('#scrollbar-message').slimscroll({
        color: '#000',
        size: '10px',
        width: '100%',
        height: 'auto',
        maxHeight: '250px'
    });
    $('li[id^="inbox-id-"]').click(function(){
        var that = $(this);
        $.ajax({
            type: "get",
            url: "../inbox/" + $(this).attr("inbox-id"),
            dataType: "text",
            success: function (msg) {
                $("#icon-inbox-" + that.attr("inbox-id")).removeClass();
                if(msg == "READ") {
                    if($('#i-inbox').attr('data-badge') == 1){
                        $('#i-inbox').removeAttr('data-badge');
                    }else{
                        $('#i-inbox').attr('data-badge', $('#i-inbox').attr('data-badge') - 1);
                    }
                    $("#icon-inbox-" + that.attr("inbox-id")).attr('class', 'mdi-content-drafts left');
                    that.attr('data-tooltip', 'Make as unread');
                }else{
                    if($('#i-inbox').attr('data-badge') == undefined){
                        $('#i-inbox').attr('data-badge', 1);
                    }else{
                        $('#i-inbox').attr('data-badge', $('#inbox').attr('data-badge') + 1);
                    }
                    $("#icon-inbox-" + that.attr("inbox-id")).attr('class', 'mdi-content-markunread left');
                    that.attr('data-tooltip', 'Make as read');
                }
            }
        });
    });
    $('.tooltipped').tooltip({delay: 50});
});
