$(function () {
    var convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.tooltipped').tooltip({delay: 50});
    $('.projectDescription').each(function () {
        $(this).html(convert($(this).attr("data")));
    });
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
    $('#countdown').each(function(){
      $(this).countdown($('#countdown').attr("end-date"))
          .on('update.countdown', function (event) {
              var $this = $(this).html(event.strftime("<div>"
              + '%d<span>days</span></div><div> '
              + '%H<span>hrs</span></div><div>'
              + '%M<span>min</span></div><div> '
              + '%S<span>sec</span></div><div>'));
          }).on('finish.countdown', function (event) {
              $('#count-down').html('<div class="count-down-label col s12 red-text center">Time\'s up<i class="mdi-device-access-alarm"></i></div>');
          });
    });
    $('#countdown-s').countdown($('#countdown-s').attr("end-date"))
        .on('update.countdown', function (event) {
            var $this = $(this).html(event.strftime("<div>"
            + '%d<span>days</span></div><div> '
            + '%H<span>hrs</span></div><div>'
            + '%M<span>min</span></div><div> '
            + '%S<span>sec</span></div><div>'));
        }).on('finish.countdown', function (event) {
            $('#count-down-s').html('<div class="count-down-label col s12 red-text center">Time\'s up<i class="mdi-device-access-alarm"></i></div>');
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
});
