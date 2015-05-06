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
});



var startColor = '#FC5B3F';
var endColor = '#6FD57F';

window.onload = function onLoad() {
    var circle = new ProgressBar.Circle('#progress-circle', {
        strokeWidth: 3,
        trailWidth: 1,
        text: {
            value: '0'
        },
        color: startColor,
        duration: 1500,
        easing: 'easeInOut',
        step: function(state, bar) {
            bar.setText((bar.value() * 100).toFixed(0) + '%');
            $('.progressbar-text').css('color',state.color);
            bar.path.setAttribute('stroke',state.color);
            bar.path.setAttribute('stroke-width', state.width);
        }

    });

    circle.animate($('#progress-circle').attr('value'), {
        from: {color: startColor, width: 0.5},
        to: {color: endColor, width: 3}
    });
};