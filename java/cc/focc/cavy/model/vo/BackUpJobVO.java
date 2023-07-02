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
public class BackUpJobVO {

    private Long id;
    private String jobName;
    private String jobDescribe;
    private Integer jobType;
    private String jobState;
    private String jobEnable;
    private Long dataSourceId;
    private String dataSourceName;
    private String cronExpression;
    private Date executeTime;
    private Date lastTime;
    private Date nextTime;
    private Date createTime;
    private String jobPolicy;

}
