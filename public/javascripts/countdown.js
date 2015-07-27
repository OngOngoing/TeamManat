$(document).ready(function() {
    $('#countdown').each(function () {
        $(this).countdown($('#countdown').attr("end-date"))
            .on('update.countdown', function (event) {
                var $this = $(this).html(event.strftime("<div>"
                //+ '%d<span>days</span></div><div> '
                + '%H : </div><div>'
                + '%M : </div><div>'
                + '%S</div>'));
            }).on('finish.countdown', function (event) {
                $('#count-down').html('<div class="count-down-label col s12 center">Time\'s up</div>');
            });
    });
    $('#countdown-s').countdown($('#countdown-s').attr("end-date"))
        .on('update.countdown', function (event) {
                var $this = $(this).html(event.strftime("<div>"
                    //+ '%d<span>days</span></div><div> '
                + '%H : </div><div>'
                + '%M : </div><div>'
                + '%S</div>'));
        }).on('finish.countdown', function (event) {
            $('#count-down-s').html('<div class="count-down-label-s col s12 center">Time\'s up</div>');
        });
});
