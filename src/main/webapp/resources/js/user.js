$(function(){
	
	//セッション機能
	$(document).ready(function(){

		var data = sessionStorage.getItem('userId');
		$('.userId').val(data);
		
	});
	
	$(window).on("load", function() {

	// ローディング
	setTimeout(function() {
		$('.loader').fadeOut();
	},600)

	});

});