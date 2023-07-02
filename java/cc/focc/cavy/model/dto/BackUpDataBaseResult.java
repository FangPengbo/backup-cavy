package cc.focc.cavy.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class BackUpDataBaseResult {

    Boolean result;
    String log;
    String dataBase;
    String filePath;
    Long fileSize;
    List<String> tables;
    Date startTime;
    Date endTime;

}
