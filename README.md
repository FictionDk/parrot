# parrot 

> java基础练习

## logic
> 书名: java编程的逻辑

### reflect: 21章- 反射

1. 用途
常用于各种框架,库和系统
 - springmvc,jersey,用于处理web请求,将用户请求参数和内容转为java对象
 - spring,guice,用于对象管理容器的实现
 - Tomcat,利用类加载器实现不同应用之间的隔离
 - Aop,依赖反射实现

2. 入口: `Class`
所有已加载的类在内存中都有一份类信息,类信息对应的类就是`java.lang.Class`  
Object有一个方法可以获取Class  
`public final native Class<?> getClass()`  
已知类名的情况下:  
`Class<Date> cls = Date.class;`  
接口也有Class对象   
基本类型没`getClass`方法,但有Class对象:  
`Class<Integer> intCls = int.class`  
Void作为特殊返回类型,同样有Class对象:  
`Class<Void> voidCls = void.class`  
数组的Class对象: 
 
```
String [] strArr = new String[10];
int [][] twoDimArr = new int[3][2];
int [] oneDimArr = new int[10];
Class<? extends String[]> strArrCls = strArr.getClass();
Class<? extends int [][]> twoDimArrCls = twoDimArr.getClass();
Class<? extends int []> oneDimArrCls = oneDimArr.getClass();
```

Class的静态方法`forName`,根据类名获取对象  
Class对象方法:  

```
public String getName();
public String getSimpleName();
// 返回友好的名称
public String getCanonicalName();
public Pakcage getPackage();

//返回所有public字段,包括父类
public Field[] getFields();
//返回本类中所有字段,包括非public,不包括父类
public Field[] getDeclaredFields();
publci Field getField(String name);

public Method[] getMethods();
public Method[] getDeclaredMethods();
public Method getMethod(String name,Class<?>... parameterTypes);

public Constructor<?>[] getConstructors();
public Constructor<?>[] getDeclaredConstructors();
public Constructor<T> getConstructor(Class<?>... parameterTypes);
public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes);

```

3. 静态或实例变量: Field,通过Class对象获取Fields

```
public String getName();
public String getType();
public boolean isAccessible();
public void setAccessible(boolean isAccess);
//如果是静态变量,obj可以为null
public void set(Object obj,Object value);
```

4. 静态方法或实例方法: Method,通过Class获取Method

```
public String getName();
public void setAccessible(boolean flag);
// 如果是静态,obj可以为null,args也可以为null
public Object invoke(Object obj, Object... args);
```

5. 构造方法创建对象: 

```
// Class对象直接调用方法,相当于无参构造
Class.newInstance();
Constructor.newInstance(Object... initargs);
```

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
