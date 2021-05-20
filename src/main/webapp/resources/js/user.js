$(function(){
	
	//セッション機能
	$(document).ready(function(){
    	var getval=$('#userId').val();

		if(getval!=""){
			sessionStorage.setItem('userId', getval);
		}		
		var data = sessionStorage.getItem('userId');
		$('#userId').val(data);
		$('#rent_userId').val(data);
		$('#return_userId').val(data);
		
	});


});