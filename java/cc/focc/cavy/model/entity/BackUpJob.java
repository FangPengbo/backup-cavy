package cc.focc.cavy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("backup_job")
public class BackUpJob {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String jobName;
    private String jobDescribe;
    private Integer jobType;
    private String jobState;
    private String jobEnable;
    private Long dataSourceId;
    private String cronExpression;
    private Date executeTime;
    private Date lastTime;
    private Date nextTime;
    private Date createTime;
    private String jobPolicy;

}
