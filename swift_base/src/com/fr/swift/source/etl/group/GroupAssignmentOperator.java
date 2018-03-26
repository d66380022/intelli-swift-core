package com.fr.swift.source.etl.group;

import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.*;
import com.fr.swift.source.core.CoreField;
import com.fr.swift.source.etl.AbstractOperator;
import com.fr.swift.source.etl.OperatorType;
import com.fr.swift.source.ColumnTypeConstants.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Handsome on 2018/2/22 0022 11:30
 */
public class GroupAssignmentOperator extends AbstractOperator {
    @CoreField
    private String columnName;
    @CoreField
    private ColumnType columnType;
    @CoreField
    private String otherName;
    @CoreField
    private ColumnKey columnKey;
    @CoreField
    private List<SingleGroup> group;

    public GroupAssignmentOperator(String columnName, ColumnType columnType, String otherName, ColumnKey columnKey, List<SingleGroup> group) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.otherName = otherName;
        this.columnKey = columnKey;
        this.group = group;
    }

    @Override
    public List<SwiftMetaDataColumn> getColumns(SwiftMetaData[] metaDatas) {
        List<SwiftMetaDataColumn> columnList = new ArrayList<SwiftMetaDataColumn>();
        columnList.add(new MetaDataColumn(this.columnName,
                this.columnName, ColumnTypeUtils.columnTypeToSqlType(this.columnType), fetchObjectCore().getValue()));
        return columnList;
    }

    @Override
    public OperatorType getOperatorType() {
        return OperatorType.GROUP_STRING;
    }

    @Override
    public List<String> getNewAddedName() {
        List<String> addColumnNames = new ArrayList<String>();
        addColumnNames.add(columnName);
        return addColumnNames;
    }

    public String getOtherName() {
        return otherName;
    }

    public ColumnKey getColumnKey() {
        return columnKey;
    }

    public List<SingleGroup> getGroup() {
        return group;
    }
}
