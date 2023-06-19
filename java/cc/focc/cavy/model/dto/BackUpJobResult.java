package cc.focc.cavy.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class BackUpJobResult {

    Boolean result;
    String message;
    List<BackUpDataBaseResult> backUpDataBaseResults;
    Date startTime;
    Date endTime;

}
