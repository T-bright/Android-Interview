### 一、介绍下三者
- 1、"== "运算符只比较两个变量的值是否相等。
如果是变量基本数据类型，那可以直接用"== "比较是否相等，而且只能用"== "比较，基本数据类型不存在equals比较。
如果变量指向引用数据类型（对象），那比较的是变量所保存的两个引用数据类型的内存地址是否相等。如果要比较两个对象的
内容是否相等，"== "是做不到的。

- 2、equals是Object里的一个方法，所以java程序的每一个对象都会包含一个equals方法，Object中默认的equals方法就是"== ",
也就是判断的是两个对象的地址是否相等。但是equals和"=="不同的地方就是equals可以被重写，可以根据自己业务去定制equals判断
两个对象是否相等。如：
String s1 = new String("Hello World");
String s2 = new String("Hello World");
s1 == s2      ---> 结果false，因为比较的是s1和s2在堆内存中的地址
s1.equals(s2) ---> 结果true，String类重写了equals方法，比较的是两个对象的值是否相等，而不是两个对象在堆内存中那个的地址

- 3、hashcode也是Object中的一个方法，也是用来比较两个对象是否相等的，但是hashcode和equals是由区别的，默认的hashcode是返回该
对象在内存中地址转换成的一个int值，而equals返回的是一个布尔值。

### 二、下面重点说下equals和hashcode的关系
首先先说一下官方给出的hashcode常规协定：
协定一：如果根据 equals 方法，两个对象是相等的，那么在两个对象中的每个对象上调用 hashCode 方法都必须生成相同的整数结果，也就是equa相等，hashCode也必须相等。
协定二：如果两个对象的hashCode相等，equals并不一定相等。
这里只说这两个协定，还有别的就不说了，根据上面两个协定我们可以得出下面几个结论：
```
1、如果两个对象equals，Java运行时环境会认为他们的hashcode一定相等。
2、如果两个对象不equals，他们的hashcode有可能相等。
3、如果两个对象hashcode相等，他们不一定equals。
4、如果两个对象hashcode不相等，他们一定不equals。
```

根据上面的结论，所以如果我们重写了equals方法，而不去重写hashcode方法，很有可能出现equals相等，hashcode不相等不满足第一个结论。

所以Java中建议重写了equals方法同时也要重写hashcode方法，当然只是建议。如果你重写equals没有重写hashcode的类没有放到集合中是没有问题的，

但是你的对象想放进散列存储的集合中（比如：HashSet,LinkedHashSet）或者想作为散列Map（例如：HashMap,LinkedHashMap等等）的Key时，在
重写equals()方法的同时，必须重写hashCode()方法。
代码示例如下:
```
public class DemoTest {
    public static void main(String[] args) {
        HashMap map = new HashMap();
        map.put(new A(), new Object());
        map.put(new B(), new Object());
        System.out.println(map.size());
    }
}
public class A {

    public boolean equals(Object obj) {
        System.out.println("A 判断equals true");
        return true;
    }
    public int hashCode() {
        System.out.println("A hashcode = 2");
        return 2;
    }
}
public class B {
    public boolean equals(Object obj) {
        System.out.println("B 判断equals false");
        return false;
    }
    public int hashCode() {
        System.out.println("B hashcode = 1");
        return 1;
    }
}
```
输出结果：
A hashcode = 2
B hashcode = 1
2


现在修改下B类的代码
```
public class B {
    public boolean equals(Object obj) {
        System.out.println("B 判断equals false");
        return false;
    }
    public int hashCode() {
        System.out.println("B hashcode = 2");
        return 2;
    }
}
```
输出结果：
A hashcode = 2
B hashcode = 2
B 判断equals false
2

通过示例可以清楚的看到，将对象放到map中的时候，首先会判断hashcode，如果hashcode不相等，那么两个对象肯定不相等，就不用判断equals了。如果hashcode相等，就继续判断equals，如果equals为false，表示不相等，添加到map中去。所以如果重写equals方法的对象放到了map中时，必须重写hashcode，其他情况也建议重写。

`
hashCode()方法存在的主要目的就是提高效率。如果两个不相等对象，只要判断hashcode这一步就行了，没必要再去判断里面的内容。
`
