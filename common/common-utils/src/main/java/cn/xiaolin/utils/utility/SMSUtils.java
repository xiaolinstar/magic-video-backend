package cn.xiaolin.utils.utility;

import cn.xiaolin.utils.exception.GlobalException;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云短信服务
 * 在使用该服务前，请在服务器环境变量中配置访问密钥
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 短信发送工具类
 * @create 2023/9/1
 */

@Slf4j
public class SMSUtils {

    private SMSUtils() {}

    public static Client createClient(String accessKeyId, String accessKeySecret, String endpoint)
            throws Exception {
        Config config = new Config()
                        // 必填，您的 AccessKey ID
                        .setAccessKeyId(accessKeyId)
                        // 必填，您的 AccessKey Secret
                        .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = endpoint;
        return new Client(config);
    }

    /**
     * 发送短信
     * @param signName 签名
     * @param templateCode 短信模版
     * @param phoneNumber 手机号
     * @param param 参数
     */
    public static void senMessage(String signName, String templateCode, String phoneNumber, String param) throws Exception {

        String smsAccessKeyId = System.getenv("SMS_ACCESS_KEY_ID");
        String smsAccessSecret = System.getenv("SMS_ACCESS_SECRET");

        Client client = createClient(smsAccessKeyId, smsAccessSecret, "dysmsapi.aliyuncs.com");

        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumber);
        request.setSignName(signName);
        request.setTemplateCode(templateCode);
        request.setTemplateParam("{\"code\":\"" + param + "\"}");
        try {
            SendSmsResponse response = client.sendSms(request);
            String res = JacksonUtils.toJson(response.getBody());
            log.info("aliyun send message: " + res);
        } catch (TeaException e) {
            throw new GlobalException(e.getMessage());
        }
    }
}
