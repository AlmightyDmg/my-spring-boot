package cn.com.dmg.myspringboot.test;

import lombok.Data;

import java.util.List;

@Data
class PositionInfo{
    /**在原字符串中 块所在的索引 可能包换多个索引*/
    private List<Integer> originalIndexArr;
    /**在目标字符串中 块所在的起始位置*/
    private int firstIndex;
    private int lastIndex;
}
