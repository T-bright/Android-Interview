
在说int和Integer的区别之前，先要弄清楚两个问题：</br>
一、java基本数据类型及其封装类</br>
二、java自动拆箱和自动装箱</br>

### 一、java基本数据类型及其封装类
数据类型	|字节大小|	封装类
 ---- |  ---- | ---
byte	|8位|	Byte
short|	16位|	Short
int	|32位|	Interger
long	|64位	|Long
float	|32位	|Float
double	|64位	|Double
boolean	|1位|	Boolean

java数据类型有基本数据类型和引用数据类型，为了方便将基本数据类型当作对象处理，java引入了基本数据类型相对应的封装类，如int封装类是Integer。

### 二、java自动拆箱和自动装箱
- 1、自动装箱
自动装箱其实就是将基本数据类型转换为引用数据类型（对象）

-  2、自动拆箱
自动拆箱其实就是将引用数据类型转化为基本数据类型

代码如下
```
    public static void main(String[] args) {
        Integer a = 1;//这里就用到了自动装箱；等同于Integer a = new Integer(1);

        int b = a - 1;//对象不能直接进行计算，所以这里有自动拆箱的操作,将a对象转换成基本数据类型，然后-1
        System.out.println(a);
        System.out.println(b);
    }
```
打印结果
```
1
0
```


### 三、int和Interger的区别
从上面我们就能看出int和Interger的区别：
- int是基本数据类型，Integer是引用数据类型；
- int默认值是0，Integer默认值是null；
- int类型直接存储数值，Integer需要实例化对象，指向对象的地址。

说到这，是不是认为就这么完事了，其实它们之间还有一些细节方面的区别：如下

```
    public static void main(String[] args) {
        Integer a = new Integer(1);
        Integer b = new Integer(1);

        int c = 1;
        int d = 1;

        Integer e = 1;
        Integer f = 1;
        
        Integer g = 130;
        Integer h = 130;
        
        Integer i = new Integer(130);
        int j = 130;
    }
```
- 1：a == b 吗？  废话，肯定不相等。两个new出来的对象地址不一样。
- 2：c == d 吗？ 这个也是废话，都是基本数据类型的值肯定相等。
- 3：现在的关键问题是 ***e == f*** 吗？ ***g == h*** 吗？
        答案是：***e == f; g != h***。为什么会出现这种情况?因为ava在进行编译时 Integer g = 130会被编译成 ***Integer.valueOf(130)*** ，这个可以通过反编译class文件看到。而通过Integer源码可以得出，***Integer.valueOf()*** 方法会在数值-128~127之间会对Integer进行缓存，不会再重新new一个，所以 e==f  ；当数值二大于127或者小于-128的时候则会重新new一个，所以g != h 。
        Integer的valueOf方法如下
    

```
    public static Integer valueOf(int i) {
         //IntegerCache.low == -128  ;IntegerCache.high == 127
         //当数值大于-128小于127时，进行缓存；否则重新new一个。
        if (i >= IntegerCache.low && i <= IntegerCache.high)
            return IntegerCache.cache[i + (-IntegerCache.low)];
        return new Integer(i);
    }
```
- 4：c == e  吗，   i == j 吗？
  答案都是相等的。因为封装类和基本数据类型进行比较的时候，java会自动拆箱，然后比较数值是 否相等。

**综上，我们可以得出几个结论：**</br>
1、都是封装类，都是new出来的，肯定不相等。因为对象的内存地址不一样。</br>
2、都是封装类，都不是new出来的，如果值在-128~127之间，那就相等，否则不相等。</br>
3、如果是封装类和基本类型进行比较，只要数值相等那就相等，否则就不相等。因为封装类和基本数据类型进行比较的时候会有一个自动拆箱操作。</br>
4、都是基本数据类型，如果数值相等，那就相等；否则不相等。




