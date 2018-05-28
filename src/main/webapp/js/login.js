
jQuery(document).ready(function() {
    $('.login-form input[type="password"]').on('focus', function() {
    	$(this).removeClass('input-error');
    });
    
    $('.login-form').on('submit', function(e) {    	
    	$(this).find('input[type="password"]').each(function(){
    		if( $(this).val() == "" || $(this).val().length < 40) {
    			e.preventDefault();
    			$(this).addClass('input-error');
    		}else {
    			$(this).removeClass('input-error');
    			$("#login-ok").attr("disabled","true");
    			$("#login-ok").html("登录中...");
    			$.ajax({
    				type: "post",
    				dataType: 'json',
    				data:{password: $(this).val()},  
    				url: '/wallet/doLogin',
    				async:false,
    				success:function(res){
    					if(res && res.status && res.status == 1){
    						if(location.href.indexOf("returnUrl") > 0){
    							window.location.href = location.href.substring(location.href.indexOf("returnUrl=") + "returnUrl=".length);
    						}else{
    							window.location.href = "/wallet";
    						}
    					}
    				},
    				error:function(res){
    					alert("登录失败！");
    					$("#login-ok").removeAttr("disabled");
    					$("#login-ok").html("确定");
    				}
    			});
    		}
    	});
    	return false;
    });    
    $('.register-form input[type="text"], .register-form input[type="password"]').on('focus', function() {
    	$(this).removeClass('input-error');
    });
    $('.register-form input[type="checkbox"]').on('click', function() {
    	$(this).next().removeClass('input-error');
    });
    
    $('.register-form').on('submit', function(e) {  
    	
    	var canSubmit = true;
    	$(this).find('input[type="checkbox"]').each(function(){
    		if( !$(this).is(':checked')){
    			$(this).next().addClass('input-error');
    			e.preventDefault();
    			canSubmit = false;
    		}
    	});  
    	if(!canSubmit){
    		return false;
    	}
    	$(this).find('input[type="text"]').each(function(){
    		if( $(this).val() == "" || $(this).val().length < 35) {
    			e.preventDefault();
    			$(this).addClass('input-error');
    			canSubmit = false;
    		}
    	});
    	$("#register-ok").html("登录中...");
    	$("#register-ok").attr("disabled","true");
    	$.ajax({
			type: "post",
			dataType: 'json',
			data:{password: $("#form-password").val(),address: $("#form-address").val()},  
			url: '/wallet/doLogin',
			async:false,
			success:function(res){
				setTimeout(function(){}, 1000);
				location.href = "/wallet";
			},
			error:function(res){
				alert("创建失败！");
				$("#register-ok").removeAttr("disabled");
				$("#register-ok").html("确定");
			}
		});
    	return false;
    });  
    $('.register-form').on('reset', function(e) { 
    	register();
    	return false;
    });
});
function login(){
	$("#register_row").hide();
	$("#login_row").show();
}
function register(){
	$("#register_row").show();
	$("#login_row").hide();
	$.ajax({
		type: "get",
		dataType: 'json',
		url: '/wallet/generate',
		async:false,
		success:function(res){
			if(res && res.address){
				$("#form-address").val(res.address);
				$("#form-password").val(res.password);
			}
		}
	});
}