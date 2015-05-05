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
});
