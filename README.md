# parrot 

> java基础练习

## logic
> 书名: java编程的逻辑

### reflect: 21章- 反射

[reflect](./src/main/com/fictio/parrot/logic/reflect/README.md)

### annotation: 22章- 注解

1. 内置注解:  
 - `@Override`: 不写不改变方法重写的本质
 - `@Deprecated`: Java9多了since,forRemoval属性
 - `@SuppressWarning`

2. 框架和库的注解:  
3. 自定义注解:  
 - `@Target`: 注解的目标,使用`ElementType`枚举,目标可以有多个,用{}表示
 - `@Retention`: 注解保留到什么时候,使用`RetentionPolicy`,默认`RetentionPolicy.CLASS`
 - `@Documented`: 表示注解信息包含到生成的文档中
 - `@Inherited`: 表示该注解可以将父类的该注解信息被子类继承

### proxy: 23章- 代理

0. 代理设计模式存在的价值
    - 按需延迟加载,需要时再加载或创建;
    - 执行权限检查后,再调用实际对象;
    - 屏蔽网络的差异和复杂性;
1. 静态代理
    - 代理类写程序时固定的
2. SDK动态代理
    - 实现`InvocationHandler`接口,动态代理方法
    - 使用`java.lang.reflect.Proxy.newProxyInstance(...)`创建代理对象
3. cglib动态代理
    - 使用前引用第三方库`cglib`
    - 与SDK实现机制不同,动态创建一个类(其父类时被代理的类),该代理类重写父类所有public非final方法,改为调用Callback的相关方法
4. sdk/cglib
    - sdk是实现一组接口,然后动态创建实现类,具体实现逻辑再通过自定义的InvocationHandler引用实现类;
    - cglib是动态创建一个新类,继承该类并重新方法;

    
## thinking
> 书名: Thinking in Java

### thread: - 线程,并发


## algorithm 算法
> 使用java实现各种算法

1. sort01贪心排序算法
2. sort02冒泡排序算法
3. 贪心算法--霍夫曼编码--(平衡)二叉树
4. 搜索树BST(二叉树)&BBST(平衡二叉树)
5. 散列表,底层用数组(取模),冲突时用list
6. Huffman: Stack+Queue
7. Minimum Spaning Tree,最小支撑树,MST

    
    