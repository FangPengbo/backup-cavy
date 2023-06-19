package cc.focc.cavy.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class JobPolicy {

    List<String> backUpDataBase;
    List<String> excludeDataBase;

}
