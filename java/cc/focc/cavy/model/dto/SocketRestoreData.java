package cc.focc.cavy.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SocketRestoreData {

    private Long dataSouceId;
    private List<SocketRestoreAtomData> atom;

}
