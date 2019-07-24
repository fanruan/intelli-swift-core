package com.fr.swift.cloud.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Create by lifan on 2019-06-27 15:16
 */
public class TreasureBeanTest {
    String a = "{\"topic\":\"__fine_intelli_treasure_upload__\",\"key\":\"operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip\",\"url\":\"http://fine-intelli.oss-cn-shanghai.aliyuncs.com/operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip?OSSAccessKeyId=LTAIohHwEqoUUPnN&Expires=1562223807&Signature=UukhN4QVKhikW2FPDxKtWz0%2Bfxg%3D\",\"clientId\":\"\",\"customerId\":\"60944748-59cf-4fbb-ad51-b987e92f0521\",\"clientAppId\":\"a024a11a-a160-4197-bafe-f941669b1064\",\"yearMonth\":\"201904\",\"version\":\"1.0\",\"timestamp\":1561619007273,\"type\":\"inner\"}";

    private ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    public void test() throws IOException {

        TreasureBean treasureBean1 = jsonMapper.readValue(a, TreasureBean.class);
        Assert.assertEquals("__fine_intelli_treasure_upload__", treasureBean1.getTopic());
        Assert.assertEquals("operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip", treasureBean1.getKey());
        Assert.assertEquals("http://fine-intelli.oss-cn-shanghai.aliyuncs.com/operation/analyze/60944748-59cf-4fbb-ad51-b987e92f0521/a024a11a-a160-4197-bafe-f941669b1064/treas201904.zip?OSSAccessKeyId=LTAIohHwEqoUUPnN&Expires=1562223807&Signature=UukhN4QVKhikW2FPDxKtWz0%2Bfxg%3D", treasureBean1.getUrl());
        Assert.assertEquals("", treasureBean1.getClientId());
        Assert.assertEquals("a024a11a-a160-4197-bafe-f941669b1064", treasureBean1.getClientAppId());
        Assert.assertEquals("201904", treasureBean1.getYearMonth());
        Assert.assertEquals("1.0", treasureBean1.getVersion());
        Assert.assertEquals(1561619007273l, treasureBean1.getTimestamp());
        Assert.assertEquals("inner", treasureBean1.getType());
        Assert.assertEquals("60944748-59cf-4fbb-ad51-b987e92f0521", treasureBean1.getCustomerId());

        String bean = jsonMapper.writeValueAsString(treasureBean1);
        TreasureBean treasureBean2 = jsonMapper.readValue(bean, TreasureBean.class);

        Assert.assertEquals(treasureBean1.toString(), treasureBean2.toString());

    }
}
