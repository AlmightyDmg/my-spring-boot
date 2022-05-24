package cn.com.dmg.myspringboot.test;

import cn.hutool.crypto.digest.MD5;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName FileSyncAlgorithm
 * @Description 文件增量同步算法
 * @author zhum
 * @date 2022/5/24 16:22
 */
public class FileSyncAlgorithm {
    public static void main(String[] args) throws Exception {

        //String originalStr = "taouiissomahuiissoman123";
        String originalStr = "我爱天宇威视科技股份有限公司";
        String targetStr = "1我爱天宇5威6视7科8技股份有限公司";
        int step = 4;
        System.out.println(execute(originalStr, targetStr, step));
    }

    /**
     * 执行算法 返回true算法执行成功
     * @author zhum
     * @date 2022/5/24 16:25
     * @param originalStr
     * @param targetStr
     * @param step
     * @return boolean
     */
    public static boolean execute(String originalStr,String targetStr,int step){
        //分组之后剩余的部分
        StringBuilder extra = new StringBuilder();

        //1.按照一定的字节大小对字符串进行分块 算md5 和 adler32  （这里先只算md5）
        List<String> md5List = makeBolck(originalStr, step, extra);


        //2.通过和目标字符串进行对比，返回 目标字符串在原字符串中存在的部分的位置信息
        List<PositionInfo> positionInfos = compareStr(md5List,targetStr,step);


        //3.通过位置信息 与 原字符串相比较 将 变化的部分 转换为字符串 进行传递
        List resultList = translateChange(md5List,positionInfos,originalStr,step,extra.toString());


        //4.翻译位置信息 和 字符串 得出最后的文本结果  如果最后的文本结果 与 原来的文本 originalStr 相同  则说明算法没有问题
        String s = translateStr(resultList, targetStr);
        System.out.println(s);
        return s.equals(originalStr);
    }

    /**
     * 通过位置信息 与 原字符串相比较 将 变化的部分 转换为字符串 进行传递
     * @author zhum
     * @date 2022/5/24 15:57
     * @param md5List
     * @param positionInfos
     * @param step
     * @param extra
     * @return java.util.List
     */
    private static List translateChange(List<String> md5List, List<PositionInfo> positionInfos,String originalStr,int step,String extra) {
        List resultList = new ArrayList(md5List);
        //记录所有添加过位置信息的索引
        Set<Integer> indexSet = new HashSet<>();
        //进行转换  将有位置匹配的项换成位置信息  没有匹配的换成相对应的字符串
        for (PositionInfo positionInfo : positionInfos) {
            //向相应的索引位置添加位置信息
            List<Integer> originalIndexArr = positionInfo.getOriginalIndexArr();
            for (Integer index : originalIndexArr) {
                //添加过的不需要添加
                Object obj = resultList.get(index);
                if(obj != null && obj instanceof PositionInfo){
                    continue;
                }
                resultList.set(index,positionInfo);
                indexSet.add(index);
            }
        }

        //将没有添加位置信息的部分转换为相应的字符串
        for (int i = 0; i < md5List.size(); i++) {
            //没有添加位置信息 需要转换为相应的字符串
            if(!indexSet.contains(i)){
                //根据规则去原始字符串中截取相应字符串
                int subFirstIndex = i * step;
                int subLastIndex =  (i+1) * step;
                String substring = originalStr.substring(subFirstIndex, subLastIndex);
                //放入list中
                resultList.set(i,substring);
            }
        }

        //最后在末尾添加上 extra 字符串
        resultList.add(extra);
        return resultList;
    }

    /**
     * 对字符串进行分块 算出md5 和 adler32
     * @author zhum
     * @date 2022/5/24 15:46
     * @param originalStr
     * @param step
     * @param extra
     * @return void
     */
    private static List<String> makeBolck(String originalStr, int step, StringBuilder extra) {
        //四个字节一组，每一组算出md5值
        int firstIndex = 0;
        int lastIndex = firstIndex + step;
        //存放md5
        List<String> md5List = new ArrayList<>();
        while (lastIndex <= originalStr.length() - 1){
            //求出md5值
            String md5 = MD5.create().digestHex(originalStr.substring(firstIndex, lastIndex));
            md5List.add(md5);
            firstIndex += step;
            lastIndex += step;
        }

        if(firstIndex == 0){
            extra.append(originalStr);
        } else {
            if(originalStr.length() % firstIndex > 0){
                //最后的值
                extra.append(originalStr.substring(firstIndex));
            }
        }


        return md5List;
    }


    /**
     * 原字符串的 md5 和 adler 与 目标字符串的进行对比 返回目标字符串与原字符串中相同的 位置信息
     * @author zhum
     * @date 2022/5/24 15:51
     * @param md5s
     * @param targetStr
     * @param step
     * @return java.util.ArrayList<cn.com.dmg.myspringboot.test.PositionInfo>
     */
    public static List<PositionInfo> compareStr(List<String> md5s, String targetStr, int step){

        /*
            每四个字节为一组进行拆分，查找是否有一样的
            如果有 则记录 位置，如果没有，则起点向后移动一位 继续遍历  直到最后 有 或者 没有
            @author zhum
            @date 2022/5/24 10:07
         */
        //起点
        int startIndex = 0;
        //第一个点
        int firstIndex = startIndex;
        //最后一个点
        int lastIndex = firstIndex + step;

        //记录位置 相同的不进行添加
        List<PositionInfo> positionInfos = new ArrayList<>();


        while (lastIndex <= targetStr.length() - 1){
            //求出md5值
            String substring = targetStr.substring(firstIndex, lastIndex);
            //System.out.println("待比较的值为：" + substring);
            String bMd5 = MD5.create().digestHex(substring);
            if(md5s.contains(bMd5)){
                //System.out.println("匹配上了，md5值为" + bMd5 + "，起止位置为（不包含最后一位）：" + firstIndex + "," + lastIndex);
                //记录位置信息
                List<Integer> indexList = findIndex(md5s, bMd5);
                PositionInfo positionInfo = new PositionInfo();
                positionInfo.setOriginalIndexArr(indexList);
                positionInfo.setFirstIndex(firstIndex);
                positionInfo.setLastIndex(lastIndex);
                positionInfos.add(positionInfo);

                //匹配上了则跳出本次循环  在当前匹配的起点向后移动四位
//                firstIndex += step;
//                lastIndex = firstIndex + step;
//                continue;
            }
            //没有匹配上则 比对下一个块
            firstIndex += step;
            lastIndex += step;

            //如果本轮全部匹配完成  则起点向后移动一位 重新开始进行匹配
            if(lastIndex > targetStr.length() - 1){
                //向后移动一位
                startIndex++;
                firstIndex = startIndex;
                lastIndex = firstIndex + step;
            }
        }

        return positionInfos;
    }

    private static List<Integer> findIndex(List<String> md5s,String md5){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < md5s.size(); i++) {
            if(md5s.get(i).equals(md5)){
                list.add(i);
            }
        }
        return list;
    }

    /**
     * 根据转换完成的位置等信息 和 目标文字  转换为原始的文字
     * @author zhum
     * @date 2022/5/24 15:26
     * @param list
     * @param targetStr
     * @return java.lang.String
     */
    private static String translateStr(List list,String targetStr){
        StringBuilder stringBuilder = new StringBuilder();
        //遍历 list  如果类型为 Position Info 则去截取字符串 否则直接追加
        for (Object obj : list) {
            if(obj instanceof PositionInfo){
                int firstIndex = ((PositionInfo) obj).getFirstIndex();
                int lastIndex = ((PositionInfo) obj).getLastIndex();
                String substring = targetStr.substring(firstIndex, lastIndex);
                stringBuilder.append(substring);
                continue;
            }

            stringBuilder.append(obj.toString());
        }

        return stringBuilder.toString();

    }
}
