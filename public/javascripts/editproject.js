$(document).ready(function(){
    $('.tooltipped').tooltip({delay: 50});
    var minlength = 1;

    $("#searchUser").keyup(function () {
        var that = this,
            value = $(this).val();

        if (value.length >= minlength ) {
            $.ajax({
                type: "POST",
                url: "../searchuser",
                data: {
                    'search_keyword' : value
                },
                dataType: "text",
                success: function(msg){
                    if (value==$(that).val()) {
                        var search = JSON.parse(msg);
                        var htmlTxt = "";
                        htmlTxt += "<ul id='show-result-ul' class='dropdown-content select-dropdown active' style='display: block; opacity: 1;'>";
                        for (var i = 0; i < search.length; i++) {
                            htmlTxt += "<li><a href='#' id='found-name' class='found-name' userid='"+search[i].id+"'>"+search[i].firstname+" "+search[i].lastname+"</a></li>";
                        }
                        htmlTxt += "</ul>";
                        $("#show-result").html(htmlTxt);
                        $(".found-name").each(function() {
                            $(this).click(function(){
                                $("#searchUser").val($(this).attr("userid"));
                                $("#show-result").html("");
                            });
                        });
                    }
                }
            });
        }else{
            $("#show-result").html("");
        }
    });
    $('#project_description').redactor({
        imageUpload: '../uploadimage',
    });
    $("#searchUser").focusout(function(){
        $("#show-result-ul").focusout(function(){
            $("#show-result").html("");
        });
    });
});