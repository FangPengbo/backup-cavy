package cc.focc.cavy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("backup_job_record_atom")
public class BackUpJobRecordAtom implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long recordId;
    private String dataBase;
    private String result;
    private String log;
    private String filePath;
    private Long fileSize;
    private String tables;
    private Date startTime;
    private Date endTime;

}
