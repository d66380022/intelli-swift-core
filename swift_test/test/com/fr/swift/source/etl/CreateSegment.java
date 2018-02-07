package com.fr.swift.source.etl;

import com.fr.swift.bitmap.ImmutableBitMap;
import com.fr.swift.bitmap.MutableBitMap;
import com.fr.swift.bitmap.impl.BitSetMutableBitMap;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.relation.CubeMultiRelation;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.relation.RelationIndex;
import com.fr.swift.source.SwiftMetaData;

/**
 * Created by Handsome on 2017/12/26 0026 09:24
 */
public class CreateSegment {

    public Segment getSegment() {
        return new Segment() {
            @Override
            public void flush() {

            }

            @Override
            public StoreType getStoreType() {
                return null;
            }

            @Override
            public int getRowCount() {
                return 9;
            }

            @Override
            public void putRowCount(int rowCount) {

            }

            @Override
            public RelationIndex getRelation(CubeMultiRelation f) {
                return null;
            }


            @Override
            public IResourceLocation getLocation() {
                return null;
            }

            @Override
            public Column getColumn(ColumnKey key) {
                if(key.getName().equals("column1")) {
                    return new CreateColumn().getColumn();
                } else if(key.getName().equals("column2")) {
                    return new CreateColumn().getColumn();
                }else if(key.getName().equals("column3")) {
                    return new CreateColumn2().getColumn();
                } else {
                    return new CreateColumn2().getColumn();
                }
            }

            @Override
            public ImmutableBitMap getAllShowIndex() {
                MutableBitMap bitMap = BitSetMutableBitMap.newInstance();
                for(int i = 0; i < getRowCount(); i++) {
                    bitMap.add(i);
                }
                return bitMap;
            }

            @Override
            public void putAllShowIndex(ImmutableBitMap bitMap) {

            }

            @Override
            public SwiftMetaData getMetaData() {
                return null;
            }

            @Override
            public void release() {

            }
        };
    }
}
