package com.fr.swift.query.group;

import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.core.CoreService;
import com.fr.swift.structure.array.IntList;

/**
 * @author anchore
 * @date 2018/1/29
 */
public interface GroupRule<Base, Derive> extends CoreService{
    /**
     * @param index 新分组号
     * @return 新分组值
     */
    Derive getValue(int index);

    /**
     * 新值序号 -> 多个旧值序号
     *
     * @param index 新分组号
     * @return 对应的旧分组号
     */
    IntList map(int index);

    /**
     * 反向映射
     * 旧值序号 -> 新值序号
     *
     * @param originIndex 旧值序号
     * @return 新值序号
     */
    int reverseMap(int originIndex);

    /**
     * @return 新分组大小
     */
    int newSize();

    /**
     * @param dict 原始分组
     */
    void setOriginDict(DictionaryEncodedColumn<Base> dict);

    GroupType getGroupType();
}