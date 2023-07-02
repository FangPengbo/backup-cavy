package cc.focc.cavy.model.dto;

import lombok.Data;

@Data
public class SocketRestoreAtomData {

    private Integer key;
    private Long id;
    private String dataBase;
    private String table;
    private Integer percentage;
    private Integer result;
    private String log;

}
