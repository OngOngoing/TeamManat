$(function () {
    var convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').each(function () {
        $(this).html(convert($(this).attr("data")));
    });
    $('.dropdown-button').dropdown({
            inDuration: 0,
            gutter: -250,
            belowOrigin: true
        }
    );
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
            console.log("time up");
            $('#count-down-s').html('<div class="count-down-label col s12 red-text center">Time\'s up<i class="mdi-device-access-alarm"></i></div>');
        });
});

