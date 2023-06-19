package cc.focc.cavy.enums;

import cc.focc.cavy.model.common.BaseErrorInfoInterface;
public enum ExceptionEnum implements BaseErrorInfoInterface {

    // 数据操作错误定义
    SUCCESS("0", "成功!"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误!");


    /**
     * 错误码
     */
    private final String resultCode;

    /**
     * 错误描述
     */
    private final String resultMsg;

    ExceptionEnum(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
