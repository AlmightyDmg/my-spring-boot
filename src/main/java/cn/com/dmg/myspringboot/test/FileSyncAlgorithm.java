package cn.com.dmg.myspringboot.test;

import cn.hutool.crypto.digest.MD5;

import java.nio.file.Files;
import java.nio.file.Paths;
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

        //读取原字符串
        byte[] originalBytes = Files.readAllBytes(Paths.get("C:\\Users\\zhum\\Desktop\\from.txt"));
        String originalStr = new String(originalBytes, "utf-8");

        //读取目标字符串
        byte[] targetBytes = Files.readAllBytes(Paths.get("C:\\Users\\zhum\\Desktop\\target.txt"));
        String targetStr = new String(targetBytes, "utf-8");
        int length = targetStr.length();

        int step = 80;

        long start = System.currentTimeMillis();
        System.out.println(execute(originalStr, targetStr, step,false));
        long end = System.currentTimeMillis();

        System.out.println("执行整个算法用时：" + (end - start) + "毫秒，目标字符串的长度为：" + length + "，分块的长度为：" + step);


    }

    /**
     * 执行算法 返回true算法执行成功
     * @author zhum
     * @date 2022/5/24 16:25
     * @param originalStr
     * @param targetStr
     * @param step
     * @param isTranslate 是否执行第四步翻译操作
     * @return boolean
     */
    public static boolean execute(String originalStr,String targetStr,int step,boolean isTranslate){
        //分组之后剩余的部分
        StringBuilder extra = new StringBuilder();

        //1.按照一定的字节大小对字符串进行分块 算md5 和 adler32  （这里先只算md5）
        List<String> md5List = makeBolck(originalStr, step, extra);


        //2.通过和目标字符串进行对比，返回 目标字符串在原字符串中存在的部分的位置信息
        List<int[]> positionInfos = compareStr(md5List,targetStr,step);


        //3.通过位置信息 与 原字符串相比较 将 变化的部分 转换为字符串 进行传递
        List resultList = translateChange(md5List,positionInfos,originalStr,step,extra.toString(),true);

        if(!isTranslate){
            return true;
        }

        //4.翻译位置信息 和 字符串 得出最后的文本结果  如果最后的文本结果 与 原来的文本 originalStr 相同  则说明算法没有问题
        String s = translateStr(resultList, targetStr);
        //System.out.println(s);
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
     * @param isMergeResult 是否对位置信息进行合并
     * @return java.util.List
     */
    private static List translateChange(List<String> md5List, List<int[]> positionInfos,String originalStr,int step,String extra,boolean isMergeResult) {
        List resultList = new ArrayList(md5List);
        //记录所有添加过位置信息的索引
        Set<Integer> indexSet = new HashSet<>();
        //进行转换  将有位置匹配的项换成位置信息  没有匹配的换成相对应的字符串
        for (int[] positionInfo : positionInfos) {
            //向相应的索引位置添加位置信息
            int originalIndex = positionInfo[0];
            //添加过的不需要添加
            Object obj = resultList.get(originalIndex);
            if(obj != null && obj instanceof int[]){
                continue;
            }
            resultList.set(originalIndex,positionInfo);
            indexSet.add(originalIndex);
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

        //对list中的块进行合并
        if(isMergeResult){
            List list = mergePosition(resultList);
            return list;
        }
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
     *
     * 为了使传递过程中数据量减少，位置信息采用int数组的方式记录  数组一共三位
     * 第一位：在原字符串中 块所在的索引
     * 第二位：在目标字符串中 块所在的起始位置
     * 第三位：在目标字符串中 块所在的终止位置
     *
     * @author zhum
     * @date 2022/5/24 15:51
     * @param md5s
     * @param targetStr
     * @param step
     * @return
     */
    public static List<int[]> compareStr(List<String> md5s, String targetStr, int step){

        /*
            每step长度为一组进行拆分，查找是否有一样的
            如果有 则记录 位置
            比对完成该块之后 第一个点 firstIndex 和 最后一个点 lastIndex 都向后移动一位 继续进行比较
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
        List<int[]> positionInfos = new ArrayList<>();


        while (lastIndex <= targetStr.length() - 1){
            //求出md5值
            String substring = targetStr.substring(firstIndex, lastIndex);
            String bMd5 = MD5.create().digestHex(substring);
            if(md5s.contains(bMd5)){
                //记录位置信息
                List<Integer> indexList = findIndex(md5s, bMd5);
                for (Integer integer : indexList) {
                    int[] arr = new int[3];
                    arr[0] = integer;
                    arr[1] = firstIndex;
                    arr[2] = lastIndex;
                    positionInfos.add(arr);
                }
            }
            //没有匹配上则 比对下一个块
            firstIndex ++;
            lastIndex ++;
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
            if(obj instanceof int[]){
                int firstIndex = ((int[]) obj)[1];
                int lastIndex = ((int[]) obj)[2];
                String substring = targetStr.substring(firstIndex, lastIndex);
                stringBuilder.append(substring);
                continue;
            }
            stringBuilder.append(obj.toString());
        }

        return stringBuilder.toString();

    }

    /**
     * 将结果中能够进行合并的位置信息进行合并
     * @author zhum
     * @date 2022/5/26 13:57
     * @param originalPositions
     * @return java.util.List
     */
    private static List mergePosition(List originalPositions){
        List newResult = new ArrayList();
        int index = 0;
        while (index < originalPositions.size()){
            /*
                1.如果当前元素为String类型的，则直接放入 newResult
                2.如果当前元素为int[]类型，则向后一直寻找，直到找到String类型的，然后将前面的部分进行合并
                @author zhum
                @date 2022/5/26 11:20
             */
            Object currentObj = originalPositions.get(index);
            if(currentObj instanceof String){
                newResult.add(currentObj);
                index ++;
                continue;
            }

            if(currentObj instanceof int[] && index < originalPositions.size() - 1){
                Object nextObj = originalPositions.get(++index);
                while (nextObj instanceof int[] && index < originalPositions.size()){
                    nextObj = originalPositions.get(++index);
                }
                //合并 firstIndex = currentObj[1]  lastIndex = nextObj的上一个元素[2]
                int[] arr= new int[3];
                arr[0] = ((int[])currentObj)[0];
                arr[1] = ((int[])currentObj)[1];
                arr[2] = ((int[])originalPositions.get(index - 1))[2];
                newResult.add(arr);
            }
        }
        return newResult;
    }
}
