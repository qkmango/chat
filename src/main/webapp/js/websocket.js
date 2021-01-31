var websocket = null;

/**
 * 测试浏览器是否支持websocket
 */
function checkWebSocket(){
    if ("WebSocket" in window) {
        console.log("checkWebSocket =>> 浏览器支持 WebSocket!");
    }
    else {
        console.log("checkWebSocket =>> 浏览器不支持 WebSocket!");
    }
}

/**
 * 连接  WebSocket
 */
function connectWebSocket(){
    console.log("connectWebSocket =>>");
    if (websocket != null) {
        return;
    }

    // 打开一个 web socket
    websocket = new WebSocket("ws://localhost:80/chat/websocket");
    websocket.onopen = function() {
        // Web Socket 已连接上，使用 send() 方法发送数据
        console.log("connectWebSocket.websocket.onopen =>> 已连接");
    };
    websocket.onmessage = function(evt) {
        // var received_msg = evt.data;
        receiveMessage(evt.data);
    };
    websocket.onclose = function() {
        console.log("connectWebSocket.websocket.onclose =>> 已关闭");
    };
}

/**
 * 向WebSocket服务端发送消息
 */
function sendWebSocket(sendMessage){
    if (websocket){
        if (websocket.readyState == websocket.OPEN) {
            websocket.send(sendMessage);
        } else {
            // addMessage("WebSocket 未连接...");
        }
    }else {
        // addMessage("WebSocket 未创建...");
    }
}

// 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
window.onbeforeunload = function() {
    closeWebSocket();
}

/**
 * 关闭WebSocket连接
 */
function closeWebSocket() {
    websocket.close();
}

/**
 * 将消息显示在网页上
 * @param innerHTML
 */
function receiveMessage(responseData) {
    /*
        {
            "isSysMsg":false,
            "name":name,
            "id":id,
            "message":message,
            "dateTime":2020-01-30 10:10:10
        }
     */

    var jsonObj = eval('(' + responseData + ')');

    //如果是系统消息
    if (jsonObj.isSysMsg) {
        onlineInfo(jsonObj);
    } else {
        addMessage(jsonObj)
    }

}


/**
 * 添加消息，聊天记录
 * @param jsonObj
 */
function addMessage(jsonObj) {

    //是非为我发送的消息
    var isMyMessage = id==jsonObj.id?'message-me':'';

    console.log(id)
    console.log(jsonObj.id)
    console.log(isMyMessage)

    var html ='';

    html+='<li class="message-item '+isMyMessage+'">'
    html+='<div class="message-info">'
    html+='<span class="message-name">'+jsonObj.name+'</span>'
    html+='<span class="message-time">'+jsonObj.dateTime+'</span></div>'
    html+='<div class="message-msg"><span>'+jsonObj.message+'</span></div></li>'

    $("#message").append(html);
}

/**
 * 加载在线人数情况
 */
function onlineInfo(jsonObj) {

    /*
    {
        "isSysMsg":true,
        "onlineCount":20,
        "onlineList"['zs','ls','ww']
    }
     */

    var html = '';

    $.each(jsonObj.onlineList,function (i,n) {
        html += '<tr><td>'+n+'</td></tr>';
    })

    $("#onlineinfo-list").html(html);
    $("#onlineinfo-count").text(jsonObj.onlineCount+"人在线");

}