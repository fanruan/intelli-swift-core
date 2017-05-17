/**
 * Created by Kary on 2017/5/16.
 */
public class BuilderDataFactory {
    public static class GROUP {
        public static class NODIM_NOEXPAND {
            public static final String DATA = GROUP_DATA_NODIM_NOEXPAND;
            public static final String VIEWMAP = GROUP_VIEWMAP_NODIM_NOEXPAND;
        }

        public static class NOTAR_NOEXPAND {
            public static final String DATA = GROUP_DATA_NOTAR_DATA_NOEXPAND;
            public static final String VIEWMAP = GROUP_VIEWMAP_NOTAR_DATA_NOEXPAND;
        }

        public static class NORMAL_NOEXPAND {
            public static final String DATA = GROUP_DATA_NORMAL_DIMENSION_DATA_NOEXPAND;
            public static final String VIEWMAP = GROUP_VIEWMAP_NORMAL_NOEXPAND;
        }
    }

    public static class CROSS {
        public static class NORMAL_NOEXPAND {
            public static final String DATA = CROSS_DATA_NORMAL_NOEXPAND;
            public static final String VIEWMAP = CROSS_VIEWMAP_NORMAL_NOEXPAND;
        }

        public static class NODIM_NOEXPAND {
            public static final String DATA = CROSS_DATA_NODIM_NOEXPAND;
            public static final String VIEWMAP = CROSS_VIEWMAP_NODIM_NOEXPAND;
        }

        public static class NOTAR_DATA_NOEXPAND {
            public static final String DATA = CROSS_DATA_NOTAR_DATA_NOEXPAND;
            public static final String VIEWMAP = CROSS_VIEWMAP_NOTAR_DATA_NOEXPAND;
        }

    }

    private static String GROUP_DATA_NODIM_NOEXPAND = "{\"s\":[668,2.7787643E8],\"x\":1}";
    private static String GROUP_VIEWMAP_NODIM_NOEXPAND = "{\"30000\":[{\"text\":\"合同信息记录数\",\"used\":true,\"type\":0,\"dId\":\"2765fd5c0ddbd7b5\"},{\"text\":\"合同金额\",\"used\":true,\"type\":32,\"dId\":\"07b67ff8ec9f5080\"}]}";


    private static String GROUP_DATA_NOTAR_DATA_NOEXPAND = "{\"s\":[668,2.7787643E8],\"c\":[{\"s\":[664,2.7151643E8],\"x\":1,\"n\":\"0-20\"},{\"s\":[2,2860000],\"x\":1,\"n\":\"20-40\"},{\"s\":[1,2000000],\"x\":1,\"n\":\"40-60\"},{\"s\":[1,1500000],\"x\":1,\"n\":\"80-100\"}],\"x\":5}";
    private static String GROUP_VIEWMAP_NOTAR_DATA_NOEXPAND = "{\"30000\":[{\"text\":\"合同信息记录数\",\"used\":true,\"type\":0,\"dId\":\"2765fd5c0ddbd7b5\"},{\"text\":\"合同金额\",\"used\":true,\"type\":32,\"dId\":\"07b67ff8ec9f5080\"}],\"10000\":[{\"text\":\"购买数量\",\"used\":true,\"type\":32,\"dId\":\"4c816a1f60acd519\"},{\"text\":\"合同类型\",\"used\":true,\"type\":16,\"dId\":\"72972919b873bb67\"}]}";


    private static String GROUP_DATA_NORMAL_DIMENSION_DATA_NOEXPAND = "{\"s\":[],\"c\":[{\"s\":[],\"x\":1,\"n\":\"0-20\"},{\"s\":[],\"x\":1,\"n\":\"20-40\"},{\"s\":[],\"x\":1,\"n\":\"40-60\"},{\"s\":[],\"x\":1,\"n\":\"80-100\"}],\"x\":5}";
    private static String GROUP_VIEWMAP_NORMAL_NOEXPAND = "{\"10000\":[{\"text\":\"购买数量\",\"used\":true,\"type\":32,\"dId\":\"4c816a1f60acd519\"},{\"text\":\"合同类型\",\"used\":true,\"type\":16,\"dId\":\"72972919b873bb67\"}]}";


    private static String CROSS_VIEWMAP_NORMAL_NOEXPAND ="{\"20000\":[{\"text\":\"合同类型\",\"used\":true,\"type\":16,\"dId\":\"d3eb48946cdb758e\"},{\"text\":\"合同付款类型\",\"used\":true,\"type\":16,\"dId\":\"40bc66afbab88fe7\"}],\"30000\":[{\"text\":\"合同金额\",\"used\":true,\"type\":32,\"dId\":\"a7722519aca59a21\"},{\"text\":\"合同信息记录数\",\"used\":true,\"type\":0,\"dId\":\"0ba749bc4a026440\"}],\"10000\":[{\"text\":\"购买数量\",\"used\":true,\"type\":32,\"dId\":\"a92ec02bbf714d9e\"},{\"text\":\"购买的产品\",\"used\":true,\"type\":32,\"dId\":\"f701ea1fe4a8761a\"}]}";

    private static String CROSS_DATA_NORMAL_NOEXPAND="{\"t\":{\"c\":[{\"c\":[{\"n\":\"a7722519aca59a21\"},{\"n\":\"0ba749bc4a026440\"}],\"n\":\"购买合同\"},{\"c\":[{\"n\":\"a7722519aca59a21\"},{\"n\":\"0ba749bc4a026440\"}],\"n\":\"服务协议\"},{\"c\":[{\"n\":\"a7722519aca59a21\"},{\"n\":\"0ba749bc4a026440\"}],\"n\":\"长期协议订单\"},{\"c\":[{\"n\":\"a7722519aca59a21\"},{\"n\":\"0ba749bc4a026440\"}],\"n\":\"长期协议\"}]},\"l\":{\"s\":{\"s\":[2.7787643E8,668],\"c\":[{\"s\":[2.2194621E8,482]},{\"s\":[1.16658E7,27]},{\"s\":[2.477942E7,110]},{\"s\":[1.9485E7,49]}]},\"c\":[{\"s\":{\"s\":[2.7151643E8,664],\"c\":[{\"s\":[2.1814621E8,480]},{\"s\":[1.16658E7,27]},{\"s\":[2.221942E7,108]},{\"s\":[1.9485E7,49]}]},\"n\":\"0-20\"},{\"s\":{\"s\":[2860000,2],\"c\":[{\"s\":[1800000,1]},{\"s\":[null,null]},{\"s\":[1060000,1]},{\"s\":[null,null]}]},\"n\":\"20-40\"},{\"s\":{\"s\":[2000000,1],\"c\":[{\"s\":[2000000,1]},{\"s\":[null,null]},{\"s\":[null,null]},{\"s\":[null,null]}]},\"n\":\"40-60\"},{\"s\":{\"s\":[1500000,1],\"c\":[{\"s\":[null,null]},{\"s\":[null,null]},{\"s\":[1500000,1]},{\"s\":[null,null]}]},\"n\":\"80-100\"}]}}";

    private static String CROSS_DATA_NODIM_NOEXPAND="{\"s\":[2.7787643E8,668],\"c\":[{\"s\":[2.2194621E8,482],\"x\":1,\"n\":\"购买合同\"},{\"s\":[1.16658E7,27],\"x\":1,\"n\":\"服务协议\"},{\"s\":[2.477942E7,110],\"x\":1,\"n\":\"长期协议订单\"},{\"s\":[1.9485E7,49],\"x\":1,\"n\":\"长期协议\"}],\"x\":5}";
    private static String CROSS_VIEWMAP_NODIM_NOEXPAND="{\"20000\":[{\"text\":\"合同类型\",\"used\":true,\"type\":16,\"dId\":\"d3eb48946cdb758e\"},{\"text\":\"合同付款类型\",\"used\":true,\"type\":16,\"dId\":\"40bc66afbab88fe7\"}],\"30000\":[{\"text\":\"合同金额\",\"used\":true,\"type\":32,\"dId\":\"a7722519aca59a21\"},{\"text\":\"合同信息记录数\",\"used\":true,\"type\":0,\"dId\":\"0ba749bc4a026440\"}]}";

    private static String CROSS_DATA_NOTAR_DATA_NOEXPAND="{\"t\":{\"c\":[{\"n\":\"购买合同\"},{\"n\":\"服务协议\"},{\"n\":\"长期协议订单\"},{\"n\":\"长期协议\"}]},\"l\":{\"s\":{\"s\":[\"--\"],\"c\":[{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]}]},\"c\":[{\"s\":{\"s\":[\"--\"],\"c\":[{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]}]},\"n\":\"0-20\"},{\"s\":{\"s\":[\"--\"],\"c\":[{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]}]},\"n\":\"20-40\"},{\"s\":{\"s\":[\"--\"],\"c\":[{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]}]},\"n\":\"40-60\"},{\"s\":{\"s\":[\"--\"],\"c\":[{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]},{\"s\":[\"--\"]}]},\"n\":\"80-100\"}]}}";
    private static String CROSS_VIEWMAP_NOTAR_DATA_NOEXPAND="{\"20000\":[{\"text\":\"合同类型\",\"used\":true,\"type\":16,\"dId\":\"d3eb48946cdb758e\"},{\"text\":\"合同付款类型\",\"used\":true,\"type\":16,\"dId\":\"40bc66afbab88fe7\"}],\"10000\":[{\"text\":\"购买数量\",\"used\":true,\"type\":32,\"dId\":\"85524f82cc763345\"},{\"text\":\"购买的产品\",\"used\":true,\"type\":32,\"dId\":\"52689f5fcc53e74d\"}]}";
}
