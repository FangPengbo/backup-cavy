package cc.focc.cavy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("restore_job_record")
public class RestoreJobRecord {

    @TableId(type = IdType.AUTO)
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
