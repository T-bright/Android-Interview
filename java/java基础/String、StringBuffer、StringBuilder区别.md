一般说这三个类的区别，主要是都是说运行速度和线程安全这两个方面。

# 一、运行速度
进行字符串操作的时候，这三个类的运行速度由快到慢分别是StringBuilder>StringBuffer>String。

很多时候，我们进行代码优化的时候，如果涉及到很多字符串操作的都会使用StringBuilder或者StringBuffer,那是因为String为字符串常量，String对象一旦创建
就不可以在更改了。而StringBuilder和StringBuffer是字符串变量，是可以更改的。
```
String str = "a";
System.out.println(str);
str=str+"b";
System.out.println(str);
```
运行上面那段代码，给人的感觉好像就是str被修改了。其实并不然，开始，JVM创建一个String的对象str，并将"a"赋值给它。然后又创建一个新的String对象str，
之后吧原来的str对象的值和"b"拼接起来赋值给新的对象str，原来的str对象被GC回收，虽然这里对象名相同，但是实际上str已经是两个不同的对象了。所以，Java中对String对象进行的操作实际上是一个不断创建新的对象并且将旧的对象回收的一个过程，所以执行速度很慢。

而StringBuilder和StringBuffer是直接对该对象进行进行修改，没有重新创建对象和回收对象的过程，所以速度回快很多。

另外还要注意的是：
```
String str1 = "a";
tring str2 = "b";
String str = str1 + str2;
```
上面这段代码和下面这段代码是有本质的区别的,虽然结果都是一样的。
上面那段代码的执行过程就不说了，下面这段代码其实和String str = "ab";这种操作是完全一样的，速度很快。
所以少量的字符串操作可以使用下面这种形式的代码。
```
String str = "a" + "b";
```
# 二、线程安全
翻看StringBuilder和StringBuffer源码，发现StringBuffer大部分的方法都有synchronized关键字，这可以保证StringBuffer是线程安全的，
而StringBuilder是没有synchronized的线程不安全。

最后总结：
String：少量字符串的操作，使用+号直接进行拼接。</br>
StringBuilder：非多线程字符串的操作。</br>
StringBuffer：多线程字符串的操作。</br>

