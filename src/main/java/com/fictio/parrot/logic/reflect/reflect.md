### reflect: 21章- 反射

1. 用途
常用于各种框架,库和系统
 - springmvc,jersey,用于处理web请求,将用户请求参数和内容转为java对象
 - spring,guice,用于对象管理容器的实现
 - Tomcat,利用类加载器实现不同应用之间的隔离
 - Aop,依赖反射实现

[Testing](./Testing.java)  
[SimpleMapperDemo](./SimpleMapperDemo.java)

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
 
```java
class demo {
    String [] strArr = new String[10];
    int [][] twoDimArr = new int[3][2];
    int [] oneDimArr = new int[10];
    Class<? extends String[]> strArrCls = strArr.getClass();
    Class<? extends int [][]> twoDimArrCls = twoDimArr.getClass();
    Class<? extends int []> oneDimArrCls = oneDimArr.getClass();
}
```

Class的静态方法`forName`,根据类名获取对象  
Class对象方法:  

```java
class demo {
    public String getName();
    public String getSimpleName();
    // 返回友好的名称
    public String getCanonicalName();
    public Pakcage getPackage();
    
    //返回所有public字段,包括父类
    public Field[] getFields();
    //返回本类中所有字段,包括非public,不包括父类
    public Field[] getDeclaredFields();
    public Field getField(String name);
    
    public Method[] getMethods();
    public Method[] getDeclaredMethods();
    public Method getMethod(String name,Class<?>... parameterTypes);
    
    public Constructor<?>[] getConstructors();
    public Constructor<?>[] getDeclaredConstructors();
    public Constructor<T> getConstructor(Class<?>... parameterTypes);
    public Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes);
}
```

3. 静态或实例变量: Field,通过Class对象获取Fields

```java
class demo {
    public String getName();
    public String getType();
    public boolean isAccessible();
    public void setAccessible(boolean isAccess);
    //如果是静态变量,obj可以为null
    public void set(Object obj, Object value);
}
```

4. 静态方法或实例方法: Method,通过Class获取Method

```java
class demo {
    public String getName();
    public void setAccessible(boolean flag);
    // 如果是静态,obj可以为null,args也可以为null
    public Object invoke(Object obj, Object... args);
}
```

5. 构造方法创建对象: 

```java
class demo{
    Object[] initArgs;
    // Class对象直接调用方法,相当于无参构造
    Object obj = Class.newInstance();
    Object obj = Constructor.newInstance(initArgs);
}
```