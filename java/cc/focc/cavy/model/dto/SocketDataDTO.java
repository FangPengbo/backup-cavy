package cc.focc.cavy.model.dto;

import lombok.Data;

@Data
public class SocketDataDTO {

    private String msgId;
    private Object data;
    private Integer code;

}
