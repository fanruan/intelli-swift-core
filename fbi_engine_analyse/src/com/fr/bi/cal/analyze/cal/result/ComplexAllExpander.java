<<<<<<< HEAD:fbi_engine_analyse/src/com/fr/bi/cal/analyze/cal/result/ComplexAllExpander.java
package com.fr.bi.cal.analyze.cal.result;

/**
 * Created by 小灰灰 on 2014/12/24.
 */
public class ComplexAllExpander extends ComplexExpander {

    @Override
    public NodeExpander getXExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;

    }

    @Override
    public NodeExpander getYExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;
    }
=======
package com.fr.bi.cal.analyze.cal.result;

import java.io.Serializable;

/**
 * Created by 小灰灰 on 2014/12/24.
 */
public class ComplexAllExpalder extends ComplexExpander implements Serializable{

    private static final long serialVersionUID = -8433093486752591553L;

    @Override
    public NodeExpander getXExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;

    }

    @Override
    public NodeExpander getYExpander(int regionIndex) {
        return NodeExpander.ALL_EXPANDER;
    }
>>>>>>> 67b55d486e769f445942f15883303ca839ffd092:fbi_engine_analyse/src/com/fr/bi/cal/analyze/cal/result/ComplexAllExpalder.java
}