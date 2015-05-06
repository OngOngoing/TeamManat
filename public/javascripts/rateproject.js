$("a[name='criteriaButton']").click(function () {
    var criteria = this.id;
    var value = $(this).attr('score');
    $("a[id='" + criteria + "']").each(function () {
        if($(this).attr('score') != 0){
            $(this).addClass("btn-flat");
        }else{
            $(this).removeClass("btn-flat").addClass("btn").addClass("red");
        }
    })
    $(this).removeClass("btn-flat").addClass("btn");
    $("#" + criteria).val(value);

});

$("a[name='criteriaButtonRemove']").click(function () {
    var criteria = this.id;
    var value = $(this).attr('score');
    $("a[id='" + criteria + "']").each(function () {
        $(this).addClass("btn-flat");
    });
    $(this).removeClass("btn").removeClass("red").addClass("disabled");
    $("#" + criteria).val(value);
});


$(function() {
    var $textarea = $('.projectDescription').attr("data"),
        convert = new Markdown.getSanitizingConverter().makeHtml;
    $('.projectDescription').html(convert($textarea));
});

$("a[name='criteriaButtonRemove']").each(function(){
    var criteria = this.id;
    var score = $("#" + criteria).attr('value');
    if(score == 0){
        $(this).addClass("btn-flat").removeClass("red").addClass("disabled");
    }
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
