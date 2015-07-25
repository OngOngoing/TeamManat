$(document).ready(function(){

    $('.modal-trigger').leanModal({
        dismissible: true
    });
    $('.tooltipped').tooltip({delay: 50});
    $('.slider').slider({full_width: true});

    //search user

    $( "#searchUser" ).autocomplete({
        source: function(request, response) {
            $.ajax({
                type: "POST",
                url: "../searchuser",
                dataType: "text",
                data: {
                    'search_keyword' : request.term
                },
                success: function( data ) {
                    var search = JSON.parse(data);
                    response($.map(search, function (item) {
                        var results = item.firstname + " " + item.lastname;
                        return {
                            value: item.id,
                            label: results
                        };
                    }));
                }
            });
        },
        minLength: 1,
        select: function( event, ui ) {
            $( "#searchUser" ).val( ui.item.label );
            $( "#user-id").val( ui.item.value);
            return false;
        },
        focus: function( event, ui ) {
            $( "#searchUser" ).val( ui.item.label );
            return false;
        }
    });
    $("#searchUser").focusout(function(){
        $("#show-result-ul").focusout(function(){
            $("#show-result").html("");
        });
    });

    $(".upload").upload({
        action: "../uploadimage",
        label: "Drag and drop images or click to select.<br/> Best Size (width: 600px, height: 450px)",
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
    var html = "<div class='progress' id='process-"+file.index+"'><div class='determinate' id='determinate-"+file.index+"'></div></div></li>"
    $('#process-bar').append(html);
    $('#determinate-'+file.index).css('width','0%');
}

function onFileProgress(e, file, percent) {
    $('#determinate-'+file.index).css('width',percent+'%');
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
            var history = $("#show-slides").attr("history");
            for(var i=data.length-1;i>=0;i--){
                html += "<li>";
                html += "<img src='../getimg/"+data[i].Id+"' style='background-size: auto 450px;background-position-y: 0;background-repeat: no-repeat;' >";
                html += "<div class='caption right-align'>";
                html += "<div class='row'>";
                if(data[i].imgType != 1){
                    html += "<a class='waves-effect waves-light btn blue' href='../setimg/"+$("#show-slides").attr("projectId")+"/"+data[i].Id+"?h="+history+"#upload'><i class='mdi-toggle-check-box right'></i>";
                    html += "Set Project Icon</a>";
                }else{
                    html += "<a class='waves-effect waves-light btn disabled'><i class='mdi-toggle-check-box right'></i>";
                    html += "Project Icon</a>";
                }
                html += "</div>";
                html += "<div class='row'>";
                html += "<a class='waves-effect waves-light btn red'  href='../delimg/"+$("#show-slides").attr("projectId")+"/"+data[i].Id+"?h="+history+"#upload'><i class='mdi-navigation-close right'></i>Delete</a>";
                html += "</div>";
                html += "</div></li>";
            }
            $('#show-slides').html(html);
            $('.indicators').replaceWith("");
            $('.slider').slider({
                full_width: true
            });
        }
    });
    $('#process-'+file.index).remove();
}

function onFileError(e, file, error) {
    if(error == "Unknown Status (1)"){
        Materialize.toast("<i class='mdi-alert-warning red-text small'></i><span style='padding-left: 5px'>Attachment limit.</span>", 4000);
    }else if(error == "Unknown Status (2)"){
        Materialize.toast("<i class='mdi-alert-warning red-text small'></i><span style='padding-left: 5px'>File type error.</span>", 4000);
    }else{
        Materialize.toast("<i class='mdi-alert-warning red-text small'></i><span style='padding-left: 5px'>"+error+"</span>", 4000);
    }
    $('#process-'+file.index).remove();
}
