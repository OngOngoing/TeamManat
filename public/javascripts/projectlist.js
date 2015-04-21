$(function() {
    var convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').each(function () {
        $(this).html(convert($(this).attr("data")));
    });
    $('#countdown').countdown($('#countdown').attr("end-date"), function (event) {
        var $this = $(this).html(event.strftime("<div>"
        + '%d<span>days</span></div><div> '
        + '%H<span>hrs</span></div><div>'
        + '%M<span>min</span></div><div> '
        + '%S<span>sec</span></div><div>'));
    });
});

