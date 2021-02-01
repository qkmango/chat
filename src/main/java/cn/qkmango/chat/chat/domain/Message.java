package cn.qkmango.chat.chat.domain;

/**
 * @version 1.0
 * <p>Message，消息对象</p>
 * <p>服务与客户端，发送一个消息就是一个Message对象</p>
 * @className Message
 * @author: Mango
 * @date: 2021-01-30 16:18
 */
public class Message {

    private String message;
    private String dataTime;
    private String userId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", dataTime='" + dataTime + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
