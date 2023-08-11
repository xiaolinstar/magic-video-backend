package cn.xiaolin.message.constant;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 消息队列常量
 * @create 2023/8/6
 */
public class MessageQueueConsts {
    public static final String EXCHANGE_MEDIA_RESOURCE = "xiaolin.media";
    public static final String QUEUE_MEDIA_RESOURCE = "video.resource";


    private MessageQueueConsts() throws NoSuchMethodException {
        throw new NoSuchMethodException("常量类不允许实例化");
    }

}
