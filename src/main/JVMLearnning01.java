package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20.
*java堆溢出实例
 * 原理：java的堆是用来存放对象实例的，所以我们只要做到以下三点就可以使堆溢出：
 * 1、限制堆的大小，不可扩展
 * 2、不断新建对象
 * 3、保持对象存活不被回收
 * 对应的，我们需要：
 * 1、改变JVM的启动参数，将堆的最小值和最大值设成一样，这样就可以避免堆自动扩展（其实不一样也可以）
 * 2、不断产生对象
 * 3、使用一个List来保存对象，保持对象存活
 *
 * JVM配置参数： -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 *
 */

public class JVMLearnning01 {
    static class OOMObject{

    }
    public static void main(String[] args){
        JVMLearnning01 hello = new JVMLearnning01();
        List<OOMObject> list = new ArrayList<>();
        while(true){
            list.add(new OOMObject());
        }
    }
}
