
$('.linkable').click(function(){
    $('html, body').animate({
        scrollTop: $( $(this).attr("link-to") ).offset().top-36
    }, 500);
    return false;
});


$(document).ready(function() {
  $('select').material_select();
});
