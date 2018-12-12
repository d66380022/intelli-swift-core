package com.fr.swift.result.qrs;

import com.fr.swift.result.SwiftResultSet;
import com.fr.swift.source.SwiftMetaData;

/**
 * Created by lyon on 2018/11/28.
 */
public interface QueryResultSetConverter {

    SwiftResultSet convert(QueryResultSet resultSet, SwiftMetaData metaData);
}
