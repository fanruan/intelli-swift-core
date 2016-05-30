/**
 *
 */
package com.fr.bi.cal.generate;

/**
 * @author Daniel
 *         *
 */
//public class SingleTableTask extends AllTask {
//
//    private BusinessTable table;
//
//    public SingleTableTask(BusinessTable table, long userId) {
//        super(userId);
//        this.table = table;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        SingleTableTask other = (SingleTableTask) obj;
//        if (table == null) {
//            if (other.table != null) {
//                return false;
//            }
//        } else if (!ComparatorUtils.equals(table, other.table)) {
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 将Java对象转换成JSON对象
//     *
//     * @return json对象
//     * @throws Exception
//     */
//    @Override
//    public JSONObject createJSON() throws Exception {
//        JSONObject jo = super.createJSON();
//        jo.put("table", table.createJSON());
//        return jo;
//    }
//
//    @Override
//    protected Map<Integer, Set<CubeTableSource>> getGenerateTables() {
//        Map<Integer, Set<CubeTableSource>> generateTable = new HashMap<Integer, Set<CubeTableSource>>();
//        try {
//            BICollectionUtils.mergeSetValueMap(generateTable, BICubeConfigureCenter.getDataSourceManager().getTableSource(table).createGenerateTablesMap());
//        } catch (BIKeyAbsentException e) {
//            e.printStackTrace();
//        }
//        return generateTable;
//    }
//
//    @Override
//    protected boolean checkCubeVersion(TableCubeFile cube) {
//        return false;
//    }
//
//
//    @Override
//    public CubeTaskType getTaskType() {
//        return CubeTaskType.SINGLE;
//    }
//
//
//}
