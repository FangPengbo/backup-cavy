package cc.focc.cavy.convert;


import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConvert<T,V> {

    abstract V convert (T t);

    List<V> convert (List<T> ts){
        List<V> res = new ArrayList<>();
        for (T t : ts) {
            res.add(convert(t));
        }
        return res;
    }

}
