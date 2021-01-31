layui.use('layer', function(){
	var layer = layui.layer;
});

layui.use('form', function(){
	var form = layui.form;
	 
	form.verify({
		loginAct: function(value, item){ //value：表单的值、item：表单的DOM对象
			if(!new RegExp("^[a-zA-Z0-9]+$").test(value)||
				value.length > 10 || value.length <5) {
				return '[5-10]位字母或数字组合';
			}
			if (!queryLoginAct()) {
				return '已存在'
			}
		},
		loginPwd: function(value, item){
			if(!new RegExp("^[a-zA-Z0-9]+$").test(value)||
				value.length > 10 || value.length <5) {
				return '[5-10]位字母或数字组合';
			}
		},
		checkPwd:function(value, item) {
			var loginPwd = $("#loginPwd").val();
			if(value!=loginPwd) {
				return '两次密码不一样';
			}
		}
	});
	
	//校验完毕执行
	form.on('submit(formsubmit)', function(data){
		register();
	    return false;
	});
	
});


/**
 * 注册函数
 */
function register() {
	$.ajax({
		url:"user/register.do",
		data:{
			"loginAct":$.trim($("#loginAct").val()),
			"loginPwd":$.trim($("#loginPwd").val()),
			"name":$.trim($("#name").val())
		},
		type:"post",
		dataType:"json",
		success:function (data) {
			if (data.success) {
				console.log("注册成功");
				// 注册成功后，弹出layui的弹出层进行提示
				alertTip('恭喜','注册成功',function(){
					console.log("callback fun")
					// 回调函数，定向到登陆页面
					window.location.href = "/chat/login.html";
				});
			} else{
				console.log("注册失败");
				alertTip('抱歉','注册失败，请重试！',function(){});
			}
		}
	})
}


/**
 * 查询账户是否可用
 */
function queryLoginAct() {
	var flag = false;
	$.ajax({
		url:"user/queryLoginAct.do",
		data:{
			"loginAct":$.trim($("#loginAct").val())
		},
		type:"post",
		dataType:"json",
		async:false,
		success:function (data) {
			flag = data.success;
		}
	})
	//返回一定要写外面，写在success中仅仅是success的匿名函数的返回值，不是此queryLoginAct函数的返回值！！
	return flag;
}


	
/**
 * 对layui弹出层的封装
 * 
 * @param {Object} title 	标题
 * @param {Object} msg	 	内容
 * @param {Object} callback	回调函数
 */
function alertTip(title,msg,callback) {
	layer.open({
		title:title,
		type:1, 
		content: '<div style="text-align:center;line-height:100px;color:black;">'+msg+'</div>',
		area: ['300px', '200px'],
		btn:"确定",
		time:3000,	//3秒自动关闭
		yes:callback,	//点击按钮回调函数
		cancel:callback,//关闭X的回调函数
		end:callback	//提示结束的回调函数
	});
}