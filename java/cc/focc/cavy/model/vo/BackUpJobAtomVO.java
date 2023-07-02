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
public class BackUpJobAtomVO {

    private Long id;
    private String result;
    private String dataBase;
    private String log;
    private String filePath;
    private Long fileSize;
    private String tables;
    private Date startTime;
    private Date endTime;

}
