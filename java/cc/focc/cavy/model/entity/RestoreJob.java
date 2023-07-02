package cc.focc.cavy.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("restore_job")
public class RestoreJob {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long dataSourceId;
    private String jobState;
    private String log;
    private Date startTime;
    private Date endTime;

}
