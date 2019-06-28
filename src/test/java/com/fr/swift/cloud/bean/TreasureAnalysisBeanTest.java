package com.fr.swift.cloud.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import java.io.IOException;

/**
 * Create by lifan on 2019-06-27 15:16
 */
public class TreasureAnalysisBeanTest {

    String expect = "{\"clientId\":\"\",\"clientAppId\":\"a024a11a-a160-4197-bafe-f941669b1064\",\"yearMonth\":\"201904\",\"version\":\"1.0\",\"timestamp\":1561626871232,\"type\":\"inner\",\"customerId\":\"60944748-59cf-4fbb-ad51-b987e92f0521\"}";
    String beanStr = "{\"topic\":\"__fine_intelli_treasure_upload__\",\"key\":\"operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip\",\"url\":\"http://fine-intelli.oss-cn-shanghai.aliyuncs.com/operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip?OSSAccessKeyId=LTAIohHwEqoUUPnN&Expires=1562223807&Signature=UukhN4QVKhikW2FPDxKtWz0%2Bfxg%3D\",\"clientId\":\"\",\"customerId\":\"60944748-59cf-4fbb-ad51-b987e92f0521\",\"clientAppId\":\"a024a11a-a160-4197-bafe-f941669b1064\",\"yearMonth\":\"201904\",\"version\":\"1.0\",\"timestamp\":1561619007273,\"type\":\"inner\"}";
    private ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {

        TreasureBean treasureBean = jsonMapper.readValue(beanStr, TreasureBean.class);
        TreasureAnalysisBean treasureAnalysisBean = new TreasureAnalysisBean(treasureBean.getClientId(), treasureBean.getClientAppId()
                , treasureBean.getYearMonth(), treasureBean.getVersion(), treasureBean.getType(), treasureBean.getCustomerId());

        TreasureAnalysisBean expectBean = jsonMapper.readValue(expect, TreasureAnalysisBean.class);
        Whitebox.setInternalState(expectBean, "timestamp", 0L);
        Whitebox.setInternalState(treasureAnalysisBean, "timestamp", 0L);
        Assert.assertEquals(expectBean.toString(), treasureAnalysisBean.toString());
    }
}
