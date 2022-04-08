package cn.com.dmg.myspringboot.test;

import cn.hutool.core.date.DateUtil;
import cn.hutool.system.OsInfo;
import cn.hutool.system.SystemUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.*;

public class MyTest {

    public static void main(String[] args) throws Exception {

        System.out.println(DateUtil.format(new Date(), "YYMMdd"));
        System.out.println(DateUtil.format(new Date(), "YYYYMMdd"));

    }



    class User{
        private String name;
        private Integer age;

    }

    public static void a(Object o){
        System.out.println(o.getClass());

    }

    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();
    }






    //CyclicBarrier和线程池共同使用造成死锁
    public static void cycAndThreadPoolDeadLock(){
        //最大线程数=CPU运行核数
        int maxPoolSize = Runtime.getRuntime().availableProcessors();
        //核心线程数=maxPoolSize/2
        int corePoolSize = maxPoolSize / 2;
        //等待队列线程数=maxPoolSize-corePoolSize
        int waitQueueSize = maxPoolSize - corePoolSize;
        ExecutorService threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                2L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(waitQueueSize),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );


        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                list.add(j);
            }
            lists.add(list);
        }


        CyclicBarrier cyclicBarrier = new CyclicBarrier(lists.size(),()->{
            System.out.println("this is cyclicBarrier");
        });

        lists.forEach(list->{
            threadPool.submit(()->{
                System.out.println("list的长度为：" + list);
                for (int i = 0; i < list.size(); i++) {
                    System.out.println(Thread.currentThread().getName() + "：" + i);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        });

        threadPool.shutdown();
    }


    public static void test1(){
        // 创建出一个数组
        List<String> strList = Arrays.asList("YangHang", "AnXiaoHei", "LiuPengFei");

        strList.forEach(System.out::println);

        strList.forEach((x)->{
            System.out.println(x);
        });
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remainder = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            if(value.size() == 0) continue;
            result.add(value);
        }
        return result;
    }


}
