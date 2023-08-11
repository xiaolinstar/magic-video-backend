package cn.xiaolin.utils.enums;

import lombok.Getter;

/**
 * @author xingxiaolin xlxing@bupt.edu.cn
 * @Description 统一回复枚举类，定义状态码和消息
 * @create 2023/7/23
 */

@Getter
public enum ResponseCode {
    SUCCESS(200,  "操作成功"),
    ERROR(500, "服务器错误"),
    INVALID_REQUEST(400, "无效的请求"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源未找到"),
    BAD_REQUEST(422, "请求参数错误"),
    DUPLICATE_RESOURCE(405, "资源已存在");


    private final Integer code;
    private final String msg;

    ResponseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
