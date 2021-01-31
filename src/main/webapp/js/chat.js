layui.use(['form'], function(){
	var layer = layui.layer;
	var form = layui.form;
	
	// 绑定退出按钮单击事件
	$("#logoutBtn").click(function() {
		layer.open({
			type:1,
			title:'退出',
			content: '<div style="text-align:center;line-height:100px;color:black;">确定要退出吗?</div>',
			btn: ['确定', '取消'],
			area: ['300px', '200px'],
			yes: function(index, layero){
				// 退出
				logout();
		  }
		});
	})
});

var name = null;
var id = null;

$(function (){
	$.ajax({
		url:"chat/getUserInfo.do",
		type:"get",
		dataType:"json",
		success:function (data) {
			name = data.name;
			id = data.id;
			$("#loginUser").text(name);
			console.log("name = "+name);
			console.log("id = "+id);
		}
	})

	//按钮绑定事件，执行发送消息
	$("#submit").click(function () {
		
		var textArea = document.getElementById('sendMsgText');
		var message = textArea.value;
		/*
        {
            "isSysMsg":false,
            "name":name,
            "id":id,
            "message":message,
            "dateTime":connectWebSocket()
        }
         */
		var sendMessage = "{'isSysMsg':false,'name':'"+name+"','id':'"+id+"','message':'"+message+"','dateTime':'"+getCurrentDateTime()+"'}"
		console.log(sendMessage)
		sendWebSocket(sendMessage);
		textArea.value='';
	})
	connectWebSocket();
})


/**
 * 退出，发送ajax，注销服务器session
 */
function logout() {
	// 关闭WebSocket
	closeWebSocket();
	// 注销session
	$.ajax({
		url:"user/logout.do",
		type:"get",
		async:false,
		success:function () {
			window.location.href="login.html";
		}
	})
	console.log('已退出');
}