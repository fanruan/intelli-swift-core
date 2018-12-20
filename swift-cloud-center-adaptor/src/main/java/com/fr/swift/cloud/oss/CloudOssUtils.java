package com.fr.swift.cloud.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import com.fr.swift.util.IoUtil;

import java.io.InputStream;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CloudOssUtils {
    public static boolean upload(String bucketName, String objectName, InputStream is) throws Exception {
        OSS oss = OssClientPool.getInstance().borrowObject();
        try {
            PutObjectResult result = oss.putObject(bucketName, objectName, is);
            return result.getResponse().isSuccessful();
        } finally {
            OssClientPool.getInstance().returnObject(oss);
            IoUtil.close(is);
        }
    }

    public static InputStream getObjectStream(String bucketName, String objectName) throws Exception {
        OSS oss = OssClientPool.getInstance().borrowObject();
        OSSObject object = oss.getObject(bucketName, objectName);
        try {
            return object.getObjectContent();
        } finally {
            OssClientPool.getInstance().returnObject(oss);
        }
    }

    public static boolean upload(String objectName, InputStream is) throws Exception {
        return upload(CloudOssProperty.getInstance().getBucketName(), objectName, is);
    }

    public static InputStream getObjectStream(String objectName) throws Exception {
        return getObjectStream(CloudOssProperty.getInstance().getBucketName(), objectName);
    }

    public static boolean exists(String bucketName, String objectName) throws Exception {
        OSS oss = OssClientPool.getInstance().borrowObject();
        try {
            return oss.doesObjectExist(bucketName, objectName);
        } finally {
            OssClientPool.getInstance().returnObject(oss);
        }
    }

    public static boolean exists(String objectName) throws Exception {
        return exists(CloudOssProperty.getInstance().getBucketName(), objectName);
    }

    public static void delete(String bucketName, String objectName) throws Exception {
        OSS oss = OssClientPool.getInstance().borrowObject();
        try {
            oss.deleteObject(bucketName, objectName);
        } finally {
            OssClientPool.getInstance().returnObject(oss);
        }
    }

    public static void delete(String objectName) throws Exception {
        delete(CloudOssProperty.getInstance().getBucketName(), objectName);
    }

    public static boolean copy(String srcBucket, String srcObjectName, String destBucket, String destObject) throws Exception {
        OSS oss = OssClientPool.getInstance().borrowObject();
        try {
            CopyObjectResult result = oss.copyObject(srcBucket, srcObjectName, destBucket, destObject);
            return result.getResponse().isSuccessful();
        } finally {
            OssClientPool.getInstance().returnObject(oss);
        }
    }

    public static boolean copy(String srcObject, String destObject) throws Exception {
        String bucketName = CloudOssProperty.getInstance().getBucketName();
        return copy(bucketName, srcObject, bucketName, destObject);
    }
}
