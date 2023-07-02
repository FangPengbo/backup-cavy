package cc.focc.cavy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestoreJobRecordVO {

    private Long id;
    private Long restoreJobId;
    private Long backupJobRecordAtomId;
    private String dataBase;
    private String tableName;
    private String log;
    private String jobState;
    private Date startTime;
    private Date endTime;

}
