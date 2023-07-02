package cc.focc.cavy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BackUpJobRecordAtomVO {

    private Long id;
    private String jobResult;
    private String jobMessage;
    private Date jobStartTime;
    private Date jobEndTime;
    private List<BackUpJobAtomVO> atomVOList;

}
