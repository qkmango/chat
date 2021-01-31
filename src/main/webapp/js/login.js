layui.use('form', function(){
	var form = layui.form;
	 
	form.verify({
		loginAct: function(value, item){ //value：表单的值、item：表单的DOM对象
			if(!new RegExp("^[a-zA-Z0-9]+$").test(value)||
				value.length > 10 || value.length <5) {
				return '[5-10]位字母或数字组合';
			}
		}
	});
	
	//校验完毕执行
	form.on('submit(formsubmit)', function(data){
		login();
	    return false;
	});
});


function login() {
	$.ajax({
		url:"user/login.do",
		data:{
			loginAct:$.trim($("#loginAct").val()),
			loginPwd:$.trim($("#loginPwd").val())
		},
		type:"post",
		dataType:"json",
		success:function (data) {
			if (data.success) {
				console.log("登陆成功");
				console.log(data.user.name);
				console.log(data.user.id);
				window.location.href = "/chat/chat.html";
			} else{
				alert("登陆失败,请重试!");
			}
		}
	})
}
