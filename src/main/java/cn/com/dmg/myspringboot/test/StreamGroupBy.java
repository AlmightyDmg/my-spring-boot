package cn.com.dmg.myspringboot.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamGroupBy {
    public static void main(String[] args){
        List<User> list = getUserList();
        Map<String,List<User>> userGroupMap = list.stream().collect(Collectors.groupingBy(User::getType));
        System.out.println(userGroupMap);
    }


    public static List<User> getUserList(){
        User user1 = new User(1,"张三","小学");
        User user2 = new User(2,"李四","小学");
        User user3 = new User(3,"王五","初中");
        User user4 = new User(4,"马六","高中");

        List<User> list = new ArrayList<User>();
        list.add(user1);
        list.add(user2);
        list.add(user3);
        list.add(user4);

        return list;
    }
}



class User{
    private Integer id;
    private String type;
    private String name;

    public User(){}
    public User(Integer id,String name,String type){
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public Integer getId(){
        return id;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
