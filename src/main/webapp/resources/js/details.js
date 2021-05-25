$(function(){
	
	//お気に入りボタン表示
	$(document).ready(function(){
    	var getval=$('#favoriteCheck').val();

		if(getval==0){
			$('#fav').addClass('unlike-btn');
			$('#fav').removeClass('like-btn');
		}else{
			$('#fav').addClass('like-btn');
			$('#fav').removeClass('unlike-btn');
		}
			
	});


});