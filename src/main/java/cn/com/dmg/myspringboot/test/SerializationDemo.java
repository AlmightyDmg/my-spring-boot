package cn.com.dmg.myspringboot.test;

import java.io.*;

public class SerializationDemo{
public static void main(String args[]){

   //Object serialization
   try{
        MyClass object1=new MyClass("Hello",-7,2.7e10);
        System.out.println("object1:"+object1);
        FileOutputStream fos=new FileOutputStream("serial.txt");
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(object1);
        oos.flush();
        oos.close();
    } catch(Exception e) {
        System.out.println("Exception during serialization:"+e);
        System.exit(0);
    }

   //Object deserialization
   try{
        MyClass object2;
        FileInputStream fis=new FileInputStream("serial.txt");
        ObjectInputStream ois=new ObjectInputStream(fis);
        object2=(MyClass)ois.readObject();
        ois.close();
        System.out.println("object2:"+object2);
    } catch(Exception e) {
        System.out.println("Exception during deserialization:"+e);
        System.exit(0);
    }
   }
}


@SuppressWarnings("serial")
class MyClass implements Serializable{

//private static final long serialVersionUID = 7279921812756335413L;

//private static final long serialVersionUID = 1L;
    String s;
    int i;
    double d;
    public MyClass(String s,int i,double d){
        this.s=s;
        this.i=i;
        this.d=d;
    }
    @Override
    public String toString(){
        return "s="+s+";i="+i+";d="+d;
    }
}
