
/**
 * 设置进度条
 * @param id
 * @param value
 */
function setProgress(id,value){
	$("#"+id).css("width",value);
	$("#"+id).html(value);
}

/**
 * 开启模态框
 * @param id
 */
function openModal(id){
	$('#'+id).on('show.bs.modal', function(){
        var $this = $(this);
        var $modal_dialog = $this.find('.modal-dialog');
        // 关键代码，如没将modal设置为 block，则$modala_dialog.height() 为零
        $this.css('display', 'block');
        $modal_dialog.css({'margin-top': Math.max(0, ($(window).height() - $modal_dialog.height()) / 2) });
   });
	$('#'+id).modal({backdrop: 'static', keyboard: false});
}
/**
 * 关闭模态框
 * @param id
 */
function closeModal(id){
	$('#'+id).modal("hide");
}

/**
	 * 请求任务进度
	 */
function queryTaskProgress(appId){
	// ajax 发送请求获取任务运行状态，如果返回运行失败或成功则关闭弹框
	$.ajax({
		type : "POST",
		url : "Monitor",
//			dataType : "json",
		async:false,// 同步执行
		data:{APPID:appId},
		success : function(data) {
//			console.info("success:"+data);
			if(data.indexOf("%")==-1){// 不包含 ，任务运行完成（失败或成功）
				clearTimeout(t);// 关闭计时器
				// 关闭弹窗进度条
				$('#myModal1').modal("hide");
				// 开启提示条模态框
			
				$('#tipId').html(data=="FINISHED"?"模型训练完成！": 
					(data=="FAILED"?"调用建模失败!":"模型训练被杀死！"));
				
				openModal("myModal2");
				console.info("closed!");
				return ;
			}
			
			setProgress("progressId", data);
			// 进度查询每次间隔1500ms
			t=setTimeout("queryTaskProgress('"+appId+"')",1500);
		},
		error: function(data){
			console.info("error"+data);
			
		}
	});
}


$(function() {

	// 请求任务进度计时器
	var t ;
	
	// 绑定建模button
	$("#train_model").click(
			function() {
				var input = $('#input_id').val();
				var train_percent = $('#train_percent').val();
				var ranks = $('#ranks').val();

				var lambda = $('#lambda').val();

				var iterations = $('#iterations').val();
				// 发送 ajax请求，调用程序运行
				// 改程序运行为线程模式，直接返回
				var ret = "null";
				$.ajax({
					type : "POST",
					url : "RunALS",
					async:false,// 同步执行
					data : {input:input,trainPercent:train_percent,ranks:ranks,lambda:lambda,iterations:iterations},
//					dataType : "json",
					success : function(data) {// data 返回appId
						console.info("success:"+data);//
						ret = data=="null"?"null":data;
					},
					error: function(data){
						console.info("error"+data);
						ret = data=="null"?"null":data;
					}
				});
				// 调用失败
				if(ret=="null") {// 弹出模态窗
					$('#tipId').html="调用建模失败！";
					openModal("myModal2");
					return ;
				}
				// 弹出窗提示程序正在运行
				setProgress("progressId", "0%");
				
				// 开启进度条模态框
				openModal("myModal1");
				
				// 定时请求任务进度
				t=setTimeout("queryTaskProgress('"+ret+"')",1000);

//				console.info("trainPercent:" + train_percent);
			});
	
	
	// 绑定查询button
	$("#checkId").click(function(){
		var userId = $('#userId').val();
		var ret =null;
		$.ajax({
			type : "POST",
			url : "Recommend",
			async:false,// 同步执行
			data : {userId:userId,flag:"check"},
//			dataType : "json",
			success : function(data) {// data 返回appId
				ret = data;
			},
			error: function(data){
				console.info("error"+data);
				ret = data=="null"?"null":data;
			}
		});
		
		ret= ret.split("::");
		
		var showResultHtml = '<br>'+
							'<p>用户评分过的电影有：'+ret[0]+'个</p>'+
							'<div class="table-responsive">' +
								'<table class="table table-striped">' +
									'<thead>'+
										'<tr>'+
											'<th>MovieId</th>'+
											'<th>电影名</th>'+
											'<th>标签</th>'+
											'<th>评分</th>'+
										'</tr>'+
									'</thead>'+
									'<tbody>'+
									ret[1] + 
									'</tbody>'+
								'</table>'+
							'</div>';
		$('#movieResultId').html(showResultHtml);
	});
	
	
	// 绑定推荐button
	$("#recommendId").click(function(){
		var userId = $('#userId').val();
		var recommendNum = $('#recommendNumId').val();
		var ret =null;
		$.ajax({
			type : "POST",
			url : "Recommend",
			async:false,// 同步执行
			data : {userId:userId,flag:"recommend",recommendNum:recommendNum},
//			dataType : "json",
			success : function(data) {// data 返回appId
				ret = data;
			},
			error: function(data){
				console.info("error"+data);
				ret = data=="null"?"null":data;
			}
		});
		
		
		var showResultHtml = '<br>'+
							'<p>数据如下：</p>'+
							'<div class="table-responsive">' +
								'<table class="table table-striped">' +
									'<thead>'+
										'<tr>'+
											'<th>MovieId</th>'+
											'<th>电影名</th>'+
											'<th>标签</th>'+
											'<th>推荐分</th>'+
										'</tr>'+
									'</thead>'+
									'<tbody>'+
									ret + 
									'</tbody>'+
								'</table>'+
							'</div>';
		$('#movieResultId').html(showResultHtml);
	});
	
});