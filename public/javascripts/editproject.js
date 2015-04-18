$(document).ready(function(){
    $('.modal-trigger').leanModal({
        dismissible: true
    });
    $('.tooltipped').tooltip({delay: 50});
    var minlength = 1;
    $('.slider').slider({full_width: true});
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
    $("#searchUser").focusout(function(){
        $("#show-result-ul").focusout(function(){
            $("#show-result").html("");
        });
    });

    $(".upload").upload({
        action: "../uploadimage",
        label: "Drag and drop images or click to select. MaxSize : 500Kb. Max 10 pictures",
        postData: {
            'projectId' : $(".upload").attr("proId")
        },
        maxSize: 500000
    }).on("start.upload", onStart)
        .on("complete.upload", onComplete)
        .on("filestart.upload", onFileStart)
        .on("fileprogress.upload", onFileProgress)
        .on("filecomplete.upload", onFileComplete)
        .on("fileerror.upload", onFileError);
});

function onStart(e, files) {
}

function onComplete(e) {
}

function onFileStart(e, file) {
    var html = "<div class='progress' id='process-"+file.index+"'><div class='determinate-"+file.index+"'></div></div></li>"
    $('#process-bar').append(html);
    $('#determinate-'+file.index).attr("width",0);
}

function onFileProgress(e, file, percent) {
    $('#determinate-'+file.index).attr("width",percent);
}

function onFileComplete(e, file, response) {
    Materialize.toast("<i class='mdi-file-cloud-upload green-text small'></i><span style='padding-left: 5px'>upload successful.</span>", 4000);
    $.ajax({
        type: "get",
        url: "../getimgs/" + $("#show-slides").attr("projectId"),
        dataType: "text",
        success: function (msg) {
            var data = JSON.parse(msg);
            var html = "";
            for(var i=data.length-1;i>=0;i--){
                html += "<li>";
                html += "<img src='../getimg/"+data[i].Id+"'>";
                html += "<div class='caption right-align'>";
                if(data[i].imgType != 1){
                    html += "<div class='row'>";
                    html += "<a class='waves-effect waves-light btn blue'><i class='mdi-toggle-check-box right'></i>";
                    html += "Default</a>";
                    html += "</div>";
                }
                html += "<div class='row'>";
                html += "<a class='waves-effect waves-light btn red'><i class='mdi-navigation-close right'></i>Delete</a>";
                html += "</div>";
                html += "</div></li>";
            }
            $('#show-slides').html(html);
            $('.indicators').replaceWith("");
            $('.slider').slider({full_width: true});
        }
    });
    $('#process-'+file.index).remove();
}

function onFileError(e, file, error) {
    if(error == "Unknown Status (1)"){
        Materialize.toast("<i class='mdi-alert-warning red-text small'></i><span style='padding-left: 5px'>Attachment limit.</span>", 4000);
    }else{
        Materialize.toast("<i class='mdi-alert-warning red-text small'></i><span style='padding-left: 5px'>"+error+"</span>", 4000);
    }
    $('#process-'+file.index).remove();
}
