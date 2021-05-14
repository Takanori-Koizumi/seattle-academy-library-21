$(function(){
	
	
	//ボタンの活性化、非活性化
	
	if($('#lendingStatus_label').text()=="貸出中"){
				
		$("#return").hover(function() {
			$(this).addClass('cursor_pointer');
			$(this).addClass('opacity_hover');
			
  		},function(){	
			$(this).removeClass('cursor_pointer');
			$(this).removeClass('opacity_hover');
  		});
		
		$('#rent').addClass('opacity_inValid');
		$('#return').removeClass('opacity_inValid');
		$('#rent').prop('disabled',true);	
		$('#return').prop('disabled',false);		
	}
	
	if($('#lendingStatus_label').text()=="貸出可"){
		
		$("#rent").hover(function() {
			$(this).addClass('cursor_pointer');
			$(this).addClass('opacity_hover');
  		},function(){	
			$(this).removeClass('cursor_pointer');
			$(this).removeClass('opacity_hover');
  		});

		$('#return').addClass('opacity_inValid');
		$('#rent').removeClass('opacity_inValid');
		$('#return').prop('disabled',true);
		$('#rent').prop('disabled',false);		
	}
	
	
	
	
	$('#bulkDelete').click(function(){
  		if(!confirm('本当に削除しますか？')){
       		/* キャンセルの時の処理 */
       		return false;
  		 }else{

       		/*　OKの時の処理 */
       		return location.href;
 		}
	});
	

});