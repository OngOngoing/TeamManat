$(function() {
    var convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').each(function(){
        $(this).html(convert($(this).attr("data")));
    });
});

