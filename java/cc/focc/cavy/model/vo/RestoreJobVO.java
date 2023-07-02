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
public class RestoreJobVO {

    private Long id;
    private Long dataSourceId;
    private String dataSourceName;
    private String jobState;
    private String log;
    private Date startTime;
    private Date endTime;

}
