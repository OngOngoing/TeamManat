$(function() {
    var convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').each(function(){
        $(this).html(convert($(this).attr("data")));
    });

    $('#countdown').countdown({
        date: $('#countdown').attr("end-date"),
        render: function(data) {
            $(this.el).html("<div>" + this.leadingZeros(data.days, 3) + " <span>days</span></div><div>" + this.leadingZeros(data.hours, 2) + " <span>hrs</span></div><div>" + this.leadingZeros(data.min, 2) + " <span>min</span></div><div>" + this.leadingZeros(data.sec, 2) + " <span>sec</span></div>");
        },
        onEnd: function() {
            $('#count-down').html('<div class="count-down-label col s12 red-text center">Time\'s up<i class="mdi-device-access-alarm"></i></div>');
        }
    });
    $('#countdown-s').countdown({
        date: $('#countdown-s').attr("end-date"),
        render: function(data) {
            $(this.el).html("<div>" + this.leadingZeros(data.days, 3) + " <span>days</span></div><div>" + this.leadingZeros(data.hours, 2) + " <span>hrs</span></div><div>" + this.leadingZeros(data.min, 2) + " <span>min</span></div><div>" + this.leadingZeros(data.sec, 2) + " <span>sec</span></div>");
        },
        onEnd: function() {
            $('#count-down').html('<div class="count-down-label col s12 red-text center">Time\'s up<i class="mdi-device-access-alarm"></i></div>');
        }
    });
});

