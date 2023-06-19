package cc.focc.cavy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("backup_job_record")
public class BackUpJobRecord implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long jobId;
    private String jobResult;
    private String jobMessage;
    private Date startTime;
    private Date endTime;

}
