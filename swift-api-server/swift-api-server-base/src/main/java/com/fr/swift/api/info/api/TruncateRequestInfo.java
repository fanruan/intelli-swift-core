package com.fr.swift.api.info.api;

import com.fr.swift.api.info.RequestType;

public class TruncateRequestInfo extends TableRequestInfo {
    public TruncateRequestInfo() {
        super(RequestType.TRUNCATE_TABLE);
    }
}
