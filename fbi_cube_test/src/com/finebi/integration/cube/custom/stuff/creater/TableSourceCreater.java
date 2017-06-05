package com.finebi.integration.cube.custom.stuff.creater;

import com.finebi.integration.cube.custom.it.CustomDBTableSource;
import com.finebi.integration.cube.custom.it.CustomETLTableSource;
import com.fr.bi.conf.data.source.SingleOperatorETLTableSource;
import com.fr.bi.conf.data.source.operator.create.TableJoinOperator;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucifer on 2017-6-1.
 *
 * @author Lucifer
 * @since Advanced FineBI Analysis 1.0
 */
public class TableSourceCreater {

    public static CubeTableSource getTableSourceA() {
        return new CustomDBTableSource("BIdemo", "A");
    }

    public static CubeTableSource getTableSourceB() {
        return new CustomDBTableSource("BIdemo", "B");
    }

    public static CubeTableSource getTableSourceC() {
        return new CustomDBTableSource("BIdemo", "C");
    }

    public static CubeTableSource getTableSourceD() {
        return new CustomDBTableSource("BIdemo", "D");
    }

    public static CubeTableSource getTableSourceAB() {
        List<CubeTableSource> parentAB = new ArrayList<CubeTableSource>();
        parentAB.add(getTableSourceA());
        parentAB.add(getTableSourceB());
        TableJoinOperator tableJoinOperatorAB = new TableJoinOperator();
        CustomETLTableSource tableSourceAB = new CustomETLTableSource(tableJoinOperatorAB, parentAB);
        return tableSourceAB;
    }

//    public static CubeTableSource getTableSourceBC() {
//        List<CubeTableSource> parentBC = new ArrayList<CubeTableSource>();
//        parentBC.add(getTableSourceB());
//        parentBC.add(getTableSourceC());
//        TableJoinOperator tableJoinOperatorBC = new TableJoinOperator();
//        CustomETLTableSource tableSourceBC = new CustomETLTableSource(tableJoinOperatorBC, parentBC);
//        return tableSourceBC;
//    }

    public static CubeTableSource getTableSourceCD() {
        List<CubeTableSource> parentCD = new ArrayList<CubeTableSource>();
        parentCD.add(getTableSourceC());
        parentCD.add(getTableSourceD());
        TableJoinOperator tableJoinOperatorCD = new TableJoinOperator();
        CustomETLTableSource tableSourceCD = new CustomETLTableSource(tableJoinOperatorCD, parentCD);
        return tableSourceCD;
    }

    public static CubeTableSource getTableSourceAB_CD() {
        List<CubeTableSource> parentAB_CD = new ArrayList<CubeTableSource>();
        parentAB_CD.add(getTableSourceAB());
        parentAB_CD.add(getTableSourceCD());
        TableJoinOperator tableJoinOperatorAB_CD = new TableJoinOperator();
        CustomETLTableSource tableSourceAB_CD = new CustomETLTableSource(tableJoinOperatorAB_CD, parentAB_CD);
        return tableSourceAB_CD;
    }


    public static CubeTableSource getSOTableSourceAB() {
        List<CubeTableSource> parentAB = new ArrayList<CubeTableSource>();
        parentAB.add(getTableSourceA());
        parentAB.add(getTableSourceB());
        TableJoinOperator tableJoinOperatorAB = new TableJoinOperator();
        SingleOperatorETLTableSource tableSourceAB = new SingleOperatorETLTableSource(parentAB, tableJoinOperatorAB);
        return tableSourceAB;
    }

    public static CubeTableSource getSOTableSourceCD() {
        List<CubeTableSource> parentCD = new ArrayList<CubeTableSource>();
        parentCD.add(getTableSourceC());
        parentCD.add(getTableSourceD());
        TableJoinOperator tableJoinOperatorCD = new TableJoinOperator();
        SingleOperatorETLTableSource tableSourceCD = new SingleOperatorETLTableSource(parentCD, tableJoinOperatorCD);
        return tableSourceCD;
    }

    public static CubeTableSource getSOTableSourceAB_CD() {
        List<CubeTableSource> parentAB_CD = new ArrayList<CubeTableSource>();
        parentAB_CD.add(getTableSourceAB());
        parentAB_CD.add(getTableSourceCD());
        TableJoinOperator tableJoinOperatorAB_CD = new TableJoinOperator();
        SingleOperatorETLTableSource tableSourceAB_CD = new SingleOperatorETLTableSource(parentAB_CD, tableJoinOperatorAB_CD);
        return tableSourceAB_CD;
    }
}
