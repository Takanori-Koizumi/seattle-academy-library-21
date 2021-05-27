$(function(){
	
	
	
	
	
	
	
	
	
	
	//セッション機能
	$(document).ready(function(){
		
		var getval=$('.userId').val();

		if(getval!=""){
			sessionStorage.setItem('userId', getval);
		}else{
			var data = sessionStorage.getItem('userId');
			$('.userId').val(data);
		}
			
	});
	
	$(window).on("load", function() {

	// ローディング
	setTimeout(function() {
		$('.loader').fadeOut();
	},600)

	});

});