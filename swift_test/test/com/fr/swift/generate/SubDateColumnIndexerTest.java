package com.fr.swift.generate;

import com.fr.swift.generate.history.index.ColumnIndexer;
import com.fr.swift.generate.history.index.SubDateColumnIndexer;
import com.fr.swift.generate.history.transport.TableTransporter;
import com.fr.swift.manager.LocalSegmentProvider;
import com.fr.swift.query.group.GroupType;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.segment.column.impl.DateDerivers;
import com.fr.swift.segment.column.impl.SubDateColumn;
import com.fr.swift.source.DataSource;
import com.fr.swift.source.db.QueryDBSource;
import com.fr.swift.util.function.Function;

import java.util.List;

/**
 * @author anchore
 * @date 2018/3/24
 */
public class SubDateColumnIndexerTest extends BaseTest {
    private String columnName = "注册时间";
    private DataSource dataSource = new QueryDBSource("select " + columnName + " from DEMO_CONTRACT", getClass().getName());

    @Override
    public void setUp() throws Exception {
        super.setUp();
        setConfDb();
        new TableTransporter(dataSource).work();
        new ColumnIndexer(dataSource, new ColumnKey(columnName)).work();
    }

    public void testIndexSubDate() {
        GroupType[] types = {
                GroupType.YEAR, GroupType.QUARTER, GroupType.MONTH,
                GroupType.WEEK, GroupType.WEEK_OF_YEAR, GroupType.DAY,
                GroupType.HOUR, GroupType.MINUTE, GroupType.SECOND,
                GroupType.Y_M_D_H_M_S, GroupType.Y_M_D_H_M, GroupType.Y_M_D_H,
                GroupType.Y_M_D, GroupType.Y_M, GroupType.Y_Q, GroupType.Y_W, GroupType.Y_D
        };

        for (GroupType type : types) {
            indexSubDate(type);
        }
    }

    private void indexSubDate(GroupType type) {
        new SubDateColumnIndexer(dataSource, new ColumnKey(columnName), type).work();

        List<Segment> segments = LocalSegmentProvider.getInstance().getSegment(dataSource.getSourceKey());
        assertTrue(!segments.isEmpty());

        Column origin = segments.get(0).getColumn(new ColumnKey(columnName));
        Column derive = new SubDateColumn(origin, type);

        DictionaryEncodedColumn originDict = origin.getDictionaryEncodedColumn();
        DictionaryEncodedColumn deriveDict = derive.getDictionaryEncodedColumn();

        Function dateDeriver = DateDerivers.newDeriver(type);
        for (int i = 1; i < originDict.size(); i++) {
            Object deriveVal = dateDeriver.apply(originDict.getValue(i));
            assertTrue(deriveDict.getIndex(deriveVal) != -1);
        }
    }
}