$("a[name='criteriaButton']").click(function () {
    var criteria = this.id;
    var value = $(this).attr('score');
    $("a[id='" + criteria + "']").each(function () {
        $(this).addClass("btn-flat");
    })
    $("#" + criteria).val(value);
    $(this).removeClass("btn-flat").addClass("btn");

});
$(function() {
    var $textarea = $('.projectDescription').attr("data"),
        convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').html(convert($textarea));
});