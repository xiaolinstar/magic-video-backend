package cn.xiaolin.message.constant;

/**
 * 消息队列常量
 *
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @create 2023/8/6
 */
public class MessageQueueConsts {

    /** 转码任务交换机（fanout） */
    public static final String EXCHANGE_MEDIA_RESOURCE = "xiaolin.media";

    /** 转码任务队列 */
    public static final String QUEUE_MEDIA_RESOURCE = "video.resource";

    /** 死信交换机：转码失败的消息投递到这里，避免原队列无限重投 */
    public static final String EXCHANGE_MEDIA_RESOURCE_DLQ = "xiaolin.media.dlx";

    /** 死信队列 */
    public static final String QUEUE_MEDIA_RESOURCE_DLQ = "video.resource.dlq";

    private MessageQueueConsts() throws NoSuchMethodException {
        throw new NoSuchMethodException("常量类不允许实例化");
    }

}
