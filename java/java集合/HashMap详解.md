在之前先介绍下几个参数的含义：
```
//hahsmap底层数据结构就是数组+链表+红黑树
transient Node<K,V>[] table;

//默认的初始容量（initailCapacity），即数组长度，默认值为16
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

//极限容量，超过这个容量将不再扩容
static final int MAXIMUM_CAPACITY = 1 << 30;

//负载因子（loadFactor），默认0.75
static final float DEFAULT_LOAD_FACTOR = 0.75f;


//阈值。threshold = capacity * loadFactor；阈值就是hashmap的数组长度乘以负载因子。当put数据时，数组里的元素个数大于threshold就会进行数组扩容
int threshold;
```

## 一、HashMap核心数据结构
HashMap是基于哈希表的Map接口的非同步实现，提供key-value形式的键值对映射操作，key和value都可以为null。hashmap不能保证添加的元素顺序。
```
    transient Node<K,V>[] table;

    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
        ...
     }
```
上面是部分源码，可以看到数组中的每一项都是一个链表。当添加数据发生hash碰撞时，那么数组table当前index的元素的最后一个Node.next就会指向新添加的数据。如下图（盗图）

![](https://github.com/T-bright/PictureSave/blob/master/other/00000010.png?raw=true)

在jdk1.7以后，当链表的长度大于8的时候，就会将链表转换成红黑树

```
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
        ...
        }
```
## 二、HashMap如何put数据和get数据
源码如下：

```
    public V put(K key, V value) {
        //1、计算关于keyhashcode值
        return putVal(hash(key), key, value, false, true);
    }


    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        
        //2、如果table为空，调用resize()进行初始化
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        
        //3、如果没有发生hash碰撞，就直接添加到table数组里。这里很关键，计算数组的下标 (n - 1) & hash
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            //4、如果发生了hash碰撞，key的hash值相同，同时key的地址或者key的equals也相同，则替换旧值
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            //5、如果是红黑树结构，则调用TreeNode的插入方法
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    //6、如果是链表结构，遍历整个链表，将数据插入到链表的结尾
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        //7、如果链表的长度大于8，就将链表转换成红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    //8、遍历链表的工程中，如果有节点与插入元素的hashcode和内容相同，则覆盖，同时结束链表遍历
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            //9、发生了hash碰撞时，新的value会覆盖旧的value。这个就是获取旧的value并返回
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        //10、如果table里面元素个数大于阈值，调用resize()进行扩容
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
    
    
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

put数据的大致步骤如下：
- 1、计算关于keyhashcode值。这里不是key.hashCode(),而是与key.hashCode()高16位做异或运算。这里有个问题面试会问到，hashmap中的hash函数是怎么实现     的？
- 2、如果table为空，调用resize()进行初始化
- 3、如果没有发生hash碰撞，就直接添加到table数组里。这里很关键，计算数组的下标 (n - 1) & hash。这里会有一个面试经常被问到的问题，hashmap是如何通   过key确定元素在数组table的位置（也就是数组的角标index）  
- 4、如果发生了hash碰撞，key的hash值相同，同时key的地址或者key的equals也相同，则替换旧值
- 5、如果是红黑树结构，则调用TreeNode的插入方法。
- 6、如果是链表结构，遍历整个链表，将数据插入到链表的结尾
- 7、如果链表的长度大于8，就将链表转换成红黑树。这里有两个个经常被问到的为题，为什么要将链表转换成红黑树？为什么是在链表长度大于8的时候转换，而不是    在长度大于4或者长度大于16的时候转换？
- 8、遍历链表的工程中，如果有节点与插入元素的hashcode和内容相同，则覆盖，同时结束链表遍历
- 9、发生了hash碰撞时，新的value会覆盖旧的value。这个就是获取旧的value并返回
- 10、如果table里面元素个数大于阈值，调用resize()进行扩容。这里也有个问题，就是hashmap是如何进行扩容的？

大致的步骤如上：同时里面也有一些面试经常会被问到的问题。下面一一详解。

#### 1、hashmap中的hash函数是怎么实现的？
jdk1.8的hash哈数源码

```
    static final int hash(Object key) {
        //其实h=key.hashCode()
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

源码中返回的并不是key.hashCode(),而是与key.hashCode()高16位做异或运算。为什么要这么设计呢？

最终的目的就是为了减少key的hash冲突。

我们上面put数据时提到了，计算key在table数组中位置即角标时采用的是 **(n - 1) & hash** 。其中 **n** 表示table数组的长度，默认是16；**hash** 就是hash函数返回的值。

下面举个实例说明，如果直接拿 **key.hashCode() & (n -1)** 来计算key在table中位置角标，那么发生碰撞的几率将会大大增加。

![](https://github.com/T-bright/PictureSave/blob/master/other/00000011.png?raw=true)

上图可以看到两个不同的key1和key2，如果直接用他们的hashCode和(n-1)求 与，那么得到的结果（也就是十进制的5）是一样的，大家可以想象一下，只要put时候key的二进制最后4位相同，都会发生hash的碰撞。这与设计hashmap的初衷不相符。hashmap的初衷就是尽可能的减少hash碰撞，让每一个put进来的元素均匀的分布在table数组里。

那用key.hashCode()与key.hashCode()高16位做异或运算，就能减少hash碰撞吗？答案是的！

![](https://github.com/T-bright/PictureSave/blob/master/other/00000012.png?raw=true)

如上图，可以看到，经过异或运算之后，得出的结果是不一样的（直接用key.hashCode() & (n-1) 得到的都是5，这个5也就是就是table数组的角标。经过处理之后得到的结果分别是13、10），这样就尽可能的在put数据时，减少了hash碰撞，让put进来的元素尽可能的均匀分布在table数组里。

#### 2、hashmap如何确定key在table中的位置？计算key在table中index位置时，为什么用(n - 1) & hash
如何确定key在table中位置，其实只要只要拿hash()%n,就能得到结果。但是谷歌工程师为什么不这么做呢？因为取余（%）的效率没有位运算的高，所以源码中采用了与运算(其实hash()%n = (n - 1) & hash())。但是使用位运算，有个前提，那就是n（数组table的长度）必须是2的指数幂。

所以这里也有个经常被问到的问题，那就是hashmap的容量为什么是2的指数幂？下面进行详解。

#### 3、HashMap的初始容量为什么是2的指数幂，如果是14或者15会怎么样？
当table数组长度等于14时，即 n = 14; n-1 = 13.计算结果如下图

![](https://github.com/T-bright/PictureSave/blob/master/other/00000013.png?raw=true)

通过上图可以看到，当hashmap的容量为你n = 14 的时候，在hashmap添加数据时，总有一些位置永远不会插进数据，这不仅造成数组空间的浪费，而且会加大hash的碰撞，导致数组中链表的长度加长，降低hashmap的性能。

其实只要table数组的长度n不是2的指数次幂的偶数都会遇到这种情况。

当table数组的长度等于15时，即n = 15； n - 1 = 14。因为 14 时 偶数，二进制最后一位时0，所以无论与什么数做与运算得到的结果都是偶数。这样hashmap的table数组的奇数角标位置就永远无法插进数据，不仅浪费一半数组空间，还大大加大了hash的碰撞。

当table数组的长度等于2的指数次幂时，会均匀的分布在数组的每一个位置。

下面附上table数组长度 n = 15和 n = 16的两幅计算角标图
![](https://github.com/T-bright/PictureSave/blob/master/other/00000014.png?raw=true)
![](https://github.com/T-bright/PictureSave/blob/master/other/00000015.png?raw=true)

所以为什么hashmap的数组容量为什么是2的指数次幂，是因为hashmap在put数据时，计算key在table数组中的位置时，采用了与运算(n - 1) & hash，所以为了能够使得key尽可能均匀的分布在table数组的每一个位置，table数组长度 n 必须是 2 的指数次幂。

所以这就是为什么HashMap的容量必须是2 的指数次幂。

#### 3、hashmap为什么在链表长度大于8的时候，将链表转换成红黑树？为什么是在链表长度大于8的时候转换，而不是在长度大于4或者长度大于16的时候转换？
咱们先看下官方的说明文档。

```
Because TreeNodes are about twice the size of regular nodes, we
use them only when bins contain enough nodes to warrant use
(see TREEIFY_THRESHOLD). And when they become too small (due to
removal or resizing) they are converted back to plain bins.  In
usages with well-distributed user hashCodes, tree bins are
rarely used.  Ideally, under random hashCodes, the frequency of
nodes in bins follows a Poisson distribution
(http://en.wikipedia.org/wiki/Poisson_distribution) with a
parameter of about 0.5 on average for the default resizing
threshold of 0.75, although with a large variance because of
resizing granularity.Ignoring variance, the expected
occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
factorial(k)). The first values are:

0:    0.60653066
1:    0.30326533
2:    0.07581633
3:    0.01263606
4:    0.00157952
5:    0.00015795
6:    0.00001316
7:    0.00000094
8:    0.00000006
more: less than 1 in ten million

```

这句话的大致含义是：因为TreeNode的大小约为常规节点的两倍，所以所以只有在链表长度达到一定长度的时候，才会去使用他们，而这个长度就是TREEIFY_THRESHOLD=8的时候，链表转换为红黑树。当红黑树变得太小时，也就是UNTREEIFY_THRESHOLD=6，红黑树转换成链表。
理想情况下，在随机hashCodes下，箱中的节点遵循泊松分布（这是统计学知识，这里就不详说了）。并且给出了当加载因子为0.75，链表长度0~8分别出现的概率。链表长度为8的概率为0.00000006，基本可以认为不会发生的。所以正常理想情况下，使用到红黑树的概率也很低。

但是，既然这么设计了，应该是防止非理想情况下，而引起的性能下降。链表的查询速度比红黑树的查询速度慢。

#### 4、hashmap是如何进行扩容的
hashmap发生扩容的时机就是当   **table中的元素个数 > table.length * loadFactor** 就是触发扩容。也就是数组中的元素个数要大于数组的长度与加载因子的积。

## 三、加载因子为什么是0.75
之所以加载因子取0.75，是因为在时间和空间复杂度取了个折中。比如当加载因子等于0.5的时候，如果数组的长度为16，那么当数组中的元素个数大于16*0.5的时候就会进行数组扩容，这个时候发生hash碰撞的几率可定比加载因子等于0.75时的低。为什么呢？当加载因子等于0.75时，当数组元素个数大于12，才会扩容；而当加载因子等于0.5时，当数组元素个数大于8才会进行扩容；所以加载因子为0.75时，要多存放4个，才会进行数组扩容，但是谁也不能保证多存放的4个元素，就不会与之前的元素发生hash碰撞。为什么不将加载因子设置为1，同理可证。因为加载因子越大，发生hash碰撞的几率越大。

所以，如果想要查询更快一点，空间要求不大，可以将加载因子设置小一点，这样，减少了hash碰撞，就相当于桶中的元素少了，元素都均匀的分布在table数组里，查询速度快。




