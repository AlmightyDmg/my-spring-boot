package cn.com.dmg.myspringboot.utils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import lombok.Data;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
    public static List<School> schoolList56 = null;
    public static List<School> schoolList147 = null;

    public static void main(String[] args) throws Exception {
        //首先获得项目分组
        //Map<String, List<JSONObject>> groupMap = getGroupMap();
        //获得56所高校
        schoolList56 = get56School();
        //获得147所双一流
        schoolList147 = get147School();

        Map<String, List<Expert>> stringListMap = allocateExpert(11);

        System.out.println();
    }

    /**
     * 进行专家的分配
     * @author zhum
     * @date 2024/1/12 14:23
     * @param
     * @return void
     */
    public static Map<String, List<Expert>> allocateExpert(Integer perGroupExpertNum) throws Exception{
        //首先获得所有项目分组
        Map<String, List<Project>> groupMap = getGroupMap();
        //分配的专家 key 组名 value 专家集合
        Map<String, List<Expert>> allocateExpertMap = new HashMap<>();
        Set<String> keySet = groupMap.keySet();

        //所有专家
        List<Expert> allExpert = getAllExpert();

        //获得所有专家 根据等级分组
        Map<Integer, Stack<Expert>> expertStackGroupByGrade = getExpertStackGroupByGrade(allExpert, groupMap.size(), null);

        Integer allGroupTotalNum = 0;
        while (true) {
             //当所有选择的专家的总数达到数量时 跳出循环不再选择
             if (allGroupTotalNum == (keySet.size() * perGroupExpertNum)) {
                 break;
             }

             //对每一组进行选择
            for (String groupName : keySet) {
                //如果一个组分配的专家数达到了总数，那么该组就不再分
                if (allocateExpertMap.get(groupName) != null) {
                    List<Expert> experts = allocateExpertMap.get(groupName);
                    if (experts.size() == perGroupExpertNum) {
                        continue;
                    }
                }

                Expert expert = selectExpert(groupName, expertStackGroupByGrade);

                //判断该组项目中是否有专家所在的学校
                if (containExpertSchool(groupMap.get(groupName), expert)) {
                    expert.setDescription("注意：该项目分组中包含该专家所在的学校");
                }


                if (allocateExpertMap.containsKey(groupName)) {
                    List<Expert> experts = allocateExpertMap.get(groupName);
                    experts.add(expert);
                } else {
                    List<Expert> experts = new ArrayList<>();
                    experts.add(expert);
                    allocateExpertMap.put(groupName, experts);
                }
                allGroupTotalNum ++;
            }
        }

        return allocateExpertMap;
    }

    private static boolean containExpertSchool(List<Project> projectList, Expert expert) {
        for (Project project : projectList) {
            String schoolCode = project.getSchoolCode();
            if (Objects.equals(schoolCode, expert.getSchoolCode())) {
                return true;
            }
        }
        return false;
    }

    public static Expert selectExpert(String groupName, Map<Integer, Stack<Expert>> expertStackGroupByGrade) {
        Expert expert = null;
        //第一档
        Stack<Expert> firstStack = expertStackGroupByGrade.get(1);
        if (firstStack.size() > 0) {
           expert = myPop(firstStack, groupName);
           if (expert != null) {
               return expert;
           }
        }

        //第二档
        Stack<Expert> secondStack = expertStackGroupByGrade.get(2);
        if (secondStack.size() > 0) {
          expert = myPop(secondStack, groupName);
            if (expert != null) {
                return expert;
            }
        }

        //第三档
        Stack<Expert> thirdStack = expertStackGroupByGrade.get(3);
        if (thirdStack.size() > 0) {
            expert = myPop(thirdStack, groupName);
            if (expert != null) {
                return expert;
            }
        }

        //第四档
        Stack<Expert> fourthStack = expertStackGroupByGrade.get(4);
        if (fourthStack.size() > 0) {
            expert = myPop(fourthStack, groupName);
            if (expert != null) {
                return expert;
            }
        }

        //第五档
        Stack<Expert> fifthStack = expertStackGroupByGrade.get(5);
        if (fifthStack.size() > 0) {
            expert = myPop(fifthStack, groupName);
            if (expert != null) {
                return expert;
            }
        }

        //直接返回一个在专家邀请库中选择的
        if (expert == null) {
            expert = new Expert();
            expert.setGrade(1);
            expert.setName("邀请专家库补齐（6）");
            return expert;
        }
        return expert;
    }

    private static Expert myPop(Stack<Expert> stack, String groupName) {
        if (Objects.equals(stack.peek().groupName, groupName)) {
            int index = -1;
            for (int i = 0; i < stack.size(); i++) {
                Expert expert = stack.get(i);
                if (!Objects.equals(groupName, expert.getGroupName())) {
                    index = i;
                    break;
                }
            }
            if (index > -1) {
                return stack.remove(index);
            }
            //返回空说明 stack 不为空 ，但是里面的专家和项目都是同一组的了，需要返回null
            return null;
        }
        return stack.pop();
    }


    /**
     * 获取所有首席专家的基本信息 主要区分专家的档次
     * @author zhum
     * @date 2024/1/12 11:20
     * @param
     * @return java.util.List<cn.com.dmg.myspringboot.utils.ExcelReader.Expert>
     */
    public static List<Expert> getAllExpert() throws Exception{
        //获得项目分组
        Map<String, List<Project>> groupMap = getGroupMap();

        List<Expert> expertList = new ArrayList<>();

        Set<String> keySet = groupMap.keySet();
        for (String groupName : keySet) {
            List<Project> projectList = groupMap.get(groupName);
            for (Project project : projectList) {
                Expert expert = new Expert();
                expert.setName(project.getExpertName());
                expert.setSchoolCode(project.getSchoolCode());
                expert.setSchoolName(project.schoolName);
                expert.setTitle(project.getExpertTitle());
                expert.setGroupName(groupName);
                //设置专家档次
                setExpertGrade(expert, project);
                expertList.add(expert);
            }
        }

        return expertList;
    }


    /**
     * 分组获取专家
     * @author zhum
     * @date 2024/1/12 12:03
     * @param
     * @return java.util.Map<java.lang.String,java.util.List<cn.com.dmg.myspringboot.utils.ExcelReader.Expert>>
     */
    public static Map<String, List<Expert>> getExpertGroupByGroup() throws Exception {
        List<Expert> allExpert = getAllExpert();
        Map<String, List<Expert>> listMap = allExpert.stream().collect(Collectors.groupingBy(Expert::getGroupName));
        return listMap;
    }

    /**
     * 根据专家的档次进行分组
     * @author zhum
     * @date 2024/1/12 14:03
     * @param
     * @return java.util.Map<java.lang.Integer,java.util.List<cn.com.dmg.myspringboot.utils.ExcelReader.Expert>>
     */
    public static Map<Integer, List<Expert>> getExpertGroupByGrade(List<Expert> allExpert) throws Exception {
        Map<Integer, List<Expert>> listMap = allExpert.stream().collect(Collectors.groupingBy(Expert::getGrade));
        return listMap;
    }

    /**
     * 根据专家的档次进行分组  排除某一分组的专家
     * @author zhum
     * @date 2024/1/12 14:46
     * @param
     * @return java.util.Map<java.lang.Integer,java.util.Stack<cn.com.dmg.myspringboot.utils.ExcelReader.Expert>>
     */
    public static  Map<Integer, Stack<Expert>> getExpertStackGroupByGrade(List<Expert> allExpert, Integer groupNumber, String excludeGroupName) throws Exception {
        Map<Integer, List<Expert>> listMap = getExpertGroupByGrade(allExpert);
        Map<Integer, Stack<Expert>> stackMap = new HashMap<>();
        listMap.forEach((grade, list) -> {
            Stack<Expert> stack = new Stack();
            list.forEach(expert -> {
                if (!Objects.equals(expert.getGroupName(), excludeGroupName)) {
                    stack.push(expert);
                }
            });
            stackMap.put(grade,stack);
        });

        //当第一档的专家数量不够平均一组两人分时，需要在邀请库中进行选择
        Stack<Expert> firstStack = stackMap.get(1);
        while (firstStack.size() < (groupNumber * 2)) {
            Expert expert = new Expert();
            expert.setGrade(1);
            expert.setName("邀请专家库补齐（1）");
            firstStack.add(expert);
        }

        return stackMap;
    }

    /**
     * 设置专家的档次
     * @author zhum
     * @date 2024/1/12 11:34
     * @param expert
     * @return void
     */
    public static void setExpertGrade(Expert expert, Project project) throws Exception{
        String schoolCode = project.getSchoolCode();
        String title = project.getExpertTitle();

        //是否是正高
        boolean isZG = "正高级".equals(title);

        if (project.isZyk) {
            expert.setGrade(1);
            return;
        }

        if (is56School(schoolCode) && isZG) {
            expert.setGrade(2);
            return;
        }

        if (is147School(schoolCode) && isZG) {
            expert.setGrade(3);
            return;
        }

        if (is56School(schoolCode) && !isZG) {
            expert.setGrade(4);
            return;
        }

        if (isZG) {
            expert.setGrade(5);
            return;
        }

        expert.setGrade(6);
    }

    /**
     * 判断是否是56所种子高校
     * @author zhum
     * @date 2024/1/12 11:29
     * @param schoolCode
     * @return boolean
     */
    public static boolean is56School(String schoolCode) throws Exception{
        for (School school : schoolList56) {
            if (school.getCode().equals(schoolCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是 147 所双一流
     * @author zhum
     * @date 2024/1/12 11:31
     * @param schoolCode
     * @return boolean
     */
    public static boolean is147School(String schoolCode) throws Exception{
        for (School school : schoolList147) {
            if (school.getCode().equals(schoolCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取56所高校
     * @author zhum
     * @date 2024/1/11 19:08
     * @param
     * @return java.util.List<org.apache.poi.ss.usermodel.Row>
     */
    public static List<School> get56School() throws Exception{
        return getSchool("C:\\Users\\13117\\Desktop\\56所高校名单.xlsx");
    }

    /**
     * 获取147所双一流
     * @author zhum
     * @date 2024/1/12 11:12
     * @param
     * @return java.util.List<cn.com.dmg.myspringboot.utils.ExcelReader.School>
     */
    public static List<School> get147School() throws Exception{
        return getSchool("C:\\Users\\13117\\Desktop\\双一流147所名单.xlsx");
    }

    public static List<School> getSchool(String path) throws Exception{
        List<Row> rowList = getRowListFromExcel(path);
        List<JSONObject> rowJsonObjList = getRowJsonObjList(rowList);
        List<School> schoolList = new ArrayList<>();
        for (JSONObject jsonObject : rowJsonObjList) {
            School school = new School();
            String schoolCode = jsonObject.getStr("单位代码");
            String schoolName = jsonObject.getStr("单位名称");
            school.setCode(schoolCode);
            school.setName(schoolName);
            schoolList.add(school);
        }
        return schoolList;
    }


    /**
     * 获取项目分组
     * @author zhum
     * @date 2024/1/11 19:07
     * @param
     * @return java.util.Map<java.lang.String,java.util.List<org.apache.poi.ss.usermodel.Row>>
     */
    public static Map<String, List<Project>> getGroupMap() throws Exception{
        //分组
        Map<String, List<Project>> groupMap = new HashMap<>();

        List<Row> rowListFromExcel = getRowListFromExcel("C:\\Users\\13117\\Desktop\\项目案例-经济学.xlsx");

        List<JSONObject> rowJsonObjList = getRowJsonObjList(rowListFromExcel);
        for (JSONObject jsonObject : rowJsonObjList) {
            Project project = new Project();
            String groupName = jsonObject.getStr("组别");
            String schoolCode = jsonObject.getStr("单位代码");
            String schoolName = jsonObject.getStr("单位名称");
            String isZyk = jsonObject.getStr("是否是资源库专家");
            String title = jsonObject.getStr("首席专家职称");
            String expertName = jsonObject.getStr("首席专家姓名");

            project.setGroupName(groupName);
            project.setSchoolCode(schoolCode);
            project.setSchoolName(schoolName);
            project.setExpertTitle(title);
            project.setZyk(StrUtil.isNotEmpty(isZyk) && "是".equals(isZyk));
            project.setExpertName(expertName);

            if (groupMap.containsKey(groupName)) {
                List<Project> rows = groupMap.get(groupName);
                rows.add(project);
            } else {
                List<Project> rowList = new ArrayList<>();
                rowList.add(project);
                groupMap.put(groupName, rowList);
            }
        }
        return groupMap;
    }


    /**
     * 解析每一行 封装为json对象
     * @author zhum
     * @date 2024/1/12 10:42
     * @param rowList
     * @return java.util.List<cn.hutool.json.JSONObject>
     */
    public static List<JSONObject> getRowJsonObjList(List<Row> rowList) {

        //表头
        List<String> titleList = new ArrayList<>();
        //数据
        List<JSONObject> jsonObjects = new ArrayList<>();

        //标题
        Row titleRow = rowList.get(0);
        int titleCellNum = titleRow.getLastCellNum();
        //一行数据实体
        for (int j = 0; j < titleCellNum; j++) {
            Cell cell = titleRow.getCell(j);
            if (cell == null) {
                continue;
            }
            String title = cell.getStringCellValue();
            titleList.add(title);
        }


        //i从1 开始 避免标题行
        for (int i = 1; i < rowList.size(); i++) {
            Row row = rowList.get(i);
            //一行数据实体 根据标题的列数量进行循环 避免数组越界
            JSONObject jsonObject = new JSONObject();
            for (int j = 0; j < titleCellNum; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    //封装数据实体 放空值
                    jsonObject.putOnce(titleList.get(j), "");
                    continue;
                }

                //其他行是数据
                CellType cellType = cell.getCellType();
                String value = "";
                switch (cellType) {
                    case STRING:
                        value = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        value = cell.getNumericCellValue() + "";
                        break;
                }
                //封装数据实体
                jsonObject.putOnce(titleList.get(j), value);
            }
            jsonObjects.add(jsonObject);
        }
        return jsonObjects;
    }


    public static List<Row> getRowListFromExcel(String path) throws Exception {
        List<Row> list = new ArrayList<>();
        // 指定Excel文件路径
        FileInputStream inputStream = new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0); // 获取第一个工作表
        int lastRowNum = sheet.getLastRowNum();
        //第一行是标题
        for (int i = 0; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            list.add(row);
        }

        workbook.close();
        inputStream.close();
        return list;
    }


    @Data
    public static class Expert{
        private String name;

        private String title;

        private String schoolCode;

        private String schoolName;

        private String groupName;

        /**
         * 专家档次：
         *  1 案例库中有该专家 且 首席专家
         *  2 种子高校中的正高 且 首席专家
         *  3 双一流正高 且 首席专家
         *  4 种子高校的副高 且 首席专家
         *  5 正高级专家 且 首席专家
         *  6 其他
         */
        private Integer grade;

        private String description;
    }

    @Data
    public static class School{
        private String name;
        private String code;
    }

    @Data
    public static class Project{
        private String groupName;
        private String schoolCode;
        private String schoolName;
        private String expertName;
        //是否是资源库专家
        private boolean isZyk;
        private String expertTitle;
    }

    /**
     * @ClassName ExcelReader
     * @Description 专家 等级 和 专家 列表 数据对象
     * @author zhum
     * @date 2024/1/12 16:33
     */
    @Data
    public static class GradeExpertDto{
        private Integer grade;
        private Stack<Expert> expertStack;

    }

    /**
     * 分组-等级-专家列表 数据对象
     * 表示 分组1对应的所有可选专家，并且按照等级区分
     * @author zhum
     * @date 2024/1/12 16:35
     * @return
     */
    @Data
    public static class GroupGradeExpertDto{
        private String groupName;
        private List<GradeExpertDto> gradeExpertDtoList;
    }

}
