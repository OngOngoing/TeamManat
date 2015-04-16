$( "a[name='criteriaButton']").click(function() {
		console.log("hey");
        var	criteria = this.id;
        console.log(criteria);
        var value = $(this).attr('score');
        console.log(value);
        $("a[id='"+criteria+"']").each(function(){
        	$(this).addClass("btn-flat");
        })
        $("#"+criteria).val(value);
        $(this).removeClass("btn-flat").addClass("btn");
        
});
