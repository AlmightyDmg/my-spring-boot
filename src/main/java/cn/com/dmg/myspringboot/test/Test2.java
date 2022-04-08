package cn.com.dmg.myspringboot.test;

public class Test2 {

    public static void main(String[] args) throws Exception {

        float[] floats = new float[5];

        floats[0] = 78.8f;
        floats[1] = 67.8f;
        floats[2] = 88.8f;
        floats[3] = 72.8f;
        floats[4] = 99.8f;

        //冒泡排序 从小到大排序
        for (int i = 0; i < floats.length - 1; i++) {
            for (int j = 0; j < floats.length - 1 - i; j++) {
                if (floats[j] > floats[j + 1]) {
                    float temp = floats[j];
                    floats[j] = floats[j + 1];
                    floats[j + 1] = temp;
                }
            }
        }

        //总成绩
        float sum = 0;
        for (float aFloat : floats) {
            sum += aFloat;
        }
        System.out.println("5个学生的总成绩为：" + sum);

        //平均成绩
        float average = sum / 5;
        System.out.println("5个学生的平均成绩为：" + average);

        //最大成绩 最小成绩
        float min = floats[0];
        float max = floats[4];
        System.out.println("最大成绩为" + max + "，最小成绩为：" + min);


    }
}
