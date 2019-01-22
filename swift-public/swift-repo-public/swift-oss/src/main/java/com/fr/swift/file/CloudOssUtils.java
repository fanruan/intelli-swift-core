package com.fr.swift.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.comm.ResponseMessage;
import com.aliyun.oss.model.CopyObjectResult;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.PutObjectResult;
import com.fr.swift.util.IoUtil;
import com.fr.swift.util.Strings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-20
 */
public class CloudOssUtils {
    public static boolean upload(OssClientPool pool, String bucketName, String objectName, InputStream is) throws Exception {
        OSS oss = pool.borrowObject();
        try {
            PutObjectResult result = oss.putObject(bucketName, objectName, is);
            ResponseMessage response = result.getResponse();
            if (null != response) {
                return response.isSuccessful();
            }
            return true;
        } finally {
            pool.returnObject(oss);
            IoUtil.close(is);
        }
    }

    public static InputStream getObjectStream(OssClientPool pool, String bucketName, String objectName) throws Exception {
        OSS oss = pool.borrowObject();
        OSSObject object = oss.getObject(bucketName, objectName);
        try {
            return object.getObjectContent();
        } finally {
            pool.returnObject(oss);
        }
    }

    public static boolean upload(OssClientPool pool, String objectName, InputStream is) throws Exception {
        return upload(pool, pool.getConfig().getBucketName(), objectName, is);
    }

    public static InputStream getObjectStream(OssClientPool pool, String objectName) throws Exception {
        return getObjectStream(pool, pool.getConfig().getBucketName(), objectName);
    }

    public static boolean exists(OssClientPool pool, String bucketName, String objectName) throws Exception {
        OSS oss = pool.borrowObject();
        try {
            return oss.doesObjectExist(bucketName, objectName);
        } finally {
            pool.returnObject(oss);
        }
    }

    public static boolean exists(OssClientPool pool, String objectName) throws Exception {
        return exists(pool, pool.getConfig().getBucketName(), objectName);
    }

    public static void delete(OssClientPool pool, String bucketName, String objectName) throws Exception {
        OSS oss = pool.borrowObject();
        try {
            oss.deleteObject(bucketName, objectName);
        } finally {
            pool.returnObject(oss);
        }
    }

    public static void delete(OssClientPool pool, String objectName) throws Exception {
        delete(pool, pool.getConfig().getBucketName(), objectName);
    }

    public static boolean copy(OssClientPool pool, String srcBucket, String srcObjectName, String destBucket, String destObject) throws Exception {
        OSS oss = pool.borrowObject();
        try {
            CopyObjectResult result = oss.copyObject(srcBucket, srcObjectName, destBucket, destObject);
            return result.getResponse().isSuccessful();
        } finally {
            pool.returnObject(oss);
        }
    }

    public static boolean copy(OssClientPool pool, String srcObject, String destObject) throws Exception {
        String bucketName = pool.getConfig().getBucketName();
        return copy(pool, bucketName, srcObject, bucketName, destObject);
    }

    public static List<String> listNames(OssClientPool pool, String bucketName, String path) throws Exception {
        OSS oss = pool.borrowObject();
        List<String> names = new ArrayList<String>();
        try {
            ListObjectsRequest request = new ListObjectsRequest(bucketName);
            request.setDelimiter("/");
            request.setPrefix(path);
            ObjectListing list = oss.listObjects(request);
            for (OSSObjectSummary objectSummary : list.getObjectSummaries()) {
                names.add(objectSummary.getKey());
            }
            for (String commonPrefix : list.getCommonPrefixes()) {
                names.add(Strings.trimSeparator(commonPrefix + "/", "/"));
            }
        } finally {
            pool.returnObject(oss);
        }
        return names;
    }

    public static List<String> listNames(OssClientPool pool, String path) throws Exception {
        return listNames(pool, pool.getConfig().getBucketName(), path);
    }
}
