package com.fr.swift.service;

import com.fr.swift.segment.SegmentKey;

import java.util.Set;

/**
 * @author anchore
 * @date 2019/1/22
 */
public interface UploadService extends SwiftService {

    void upload(Set<SegmentKey> segKeys) throws Exception;

    Set<String> download(Set<SegmentKey> segKeys, boolean replace) throws Exception;

    void uploadAllShow(Set<SegmentKey> segKeys) throws Exception;

    void downloadAllShow(Set<SegmentKey> segKeys) throws Exception;
}