$(document).ready(function(){
    $(".button-collapse").sideNav();
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
                        $('#i-inbox').attr('data-badge', parseInt($('#i-inbox').attr('data-badge')) - 1);
                    }
                    $("#icon-inbox-" + that.attr("inbox-id")).attr('class', 'mdi-content-drafts left');
                    that.attr('data-tooltip', 'Make as unread');
                }else{
                    if($('#i-inbox').attr('data-badge') == undefined){
                        $('#i-inbox').attr('data-badge', 1);
                    }else{
                        $('#i-inbox').attr('data-badge', parseInt($('#i-inbox').attr('data-badge')) + 1);
                    }
                    $("#icon-inbox-" + that.attr("inbox-id")).attr('class', 'mdi-content-markunread left');
                    that.attr('data-tooltip', 'Make as read');
                }
            }
        });
    });
    $('.tooltipped').tooltip({delay: 50});
    var prevScroll = 0,
        curDir = 'down',
        prevDir = 'up';

    //$(window).scroll(function(){
    //    if ($(this).scrollTop() >= prevScroll) {
    //        if($(this).scrollTop() >= 45 && $('#mobile-demo').position().left != 0) {
    //            curDir = 'down';
    //            if (curDir != prevDir) {
    //                $('#head').addClass('scroll');
    //                $('#head-2').addClass('scroll');
    //                prevDir = curDir;
    //            }
    //        }
    //    } else {
    //        curDir = 'up';
    //        if (curDir != prevDir) {
    //            $('#head').removeClass('scroll');
    //            $('#head-2').removeClass('scroll');
    //            prevDir = curDir;
    //        }
    //    }
    //    if($(window).scrollTop() + $(window).height() >= $(document).height()- 5) {
    //        $('#head').removeClass('scroll');
    //        $('#head-2').removeClass('scroll');
    //    }
    //    prevScroll = $(this).scrollTop();
    //});
    $('.linkable').click(function(){
        prevDir = 'down';
        $('html, body').animate({
            scrollTop: $( $(this).attr("link-to") ).offset().top-105
        }, 500);
        return false;
    });
});