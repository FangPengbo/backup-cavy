package cc.focc.cavy.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BackUpJobRecordAtomDTO {

    private Long id;
    private String jobResult;
    private String jobMessage;
    private String dataBase;
    private Date jobStartTime;
    private Date jobEndTime;
    private String result;
    private String log;
    private String filePath;
    private Long fileSize;
    private String tables;
    private Date startTime;
    private Date endTime;

}
