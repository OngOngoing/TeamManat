$("#s1").click(
	function(){
		$("#score").val(1);
		$("#s1").removeClass("btn-flat").addClass("btn");
		$("#s2").addClass("btn-flat");
		$("#s3").addClass("btn-flat");
		$("#s4").addClass("btn-flat");
		$("#s5").addClass("btn-flat");
});

$("#s2").click(
	function(){
		$("#score").val(2);
		$("#s2").removeClass("btn-flat").addClass("btn");
		$("#s1").addClass("btn-flat");
		$("#s3").addClass("btn-flat");
		$("#s4").addClass("btn-flat");
		$("#s5").addClass("btn-flat");
});

$("#s3").click(
	function(){
		$("#score").val(3);
		$("#s3").removeClass("btn-flat").addClass("btn");
		$("#s1").addClass("btn-flat");
		$("#s2").addClass("btn-flat");
		$("#s4").addClass("btn-flat");
		$("#s5").addClass("btn-flat");
});

$("#s4").click(
	function(){
		$("#score").val(4);
		$("#s4").removeClass("btn-flat").addClass("btn");
		$("#s1").addClass("btn-flat");
		$("#s2").addClass("btn-flat");
		$("#s3").addClass("btn-flat");
		$("#s5").addClass("btn-flat");
});

$("#s5").click(
	function(){
		$("#score").val(5);
		$("#s5").removeClass("btn-flat").addClass("btn");
		$("#s1").addClass("btn-flat");
		$("#s2").addClass("btn-flat");
		$("#s3").addClass("btn-flat");
		$("#s4").addClass("btn-flat");
		
});

$('.linkable').click(function(){
    $('html, body').animate({
        scrollTop: $( $(this).attr("link-to") ).offset().top-36
    }, 500);
    return false;
});


$(document).ready(function() {
    $('select').material_select();
});
