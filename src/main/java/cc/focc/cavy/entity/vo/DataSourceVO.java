package cc.focc.cavy.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataSourceVO {

    private Long id;
    private String sourceName;
    private String sourceType;
    private String sourceHost;
    private Integer sourcePort;
    private String sourceUser;
    private String sourcePwd;

}
