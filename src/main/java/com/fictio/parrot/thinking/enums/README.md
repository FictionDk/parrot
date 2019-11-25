## 枚举类型

### 基本enums特性(`EnumClass,Spiciness`)

[EnumClass](./EnumClass.java) 
[Spiciness](./Spiciness.java)

1. `ordinal()`返回一个int,即每个enum实例声明时的次序;
2. 可以使用`==`比较实例;
3. Enum类实现了`Comparable`接口,具有`compareTo()`方法;
4. `name()`方法等同于`toString()`;
5. `Enum.valuesOf(...)`为静态方法,返回相应的enum实例;
6. 使用静态导入(`static import`)可以将enum实例导入当前命名空间

### [添加/覆盖enum方法,OzWatch](./OzWatch.java)

1. 为枚举添加自定义字段
2. 覆盖枚举原有方法

### [Siwch语句中的enum](./TrafficLight.java)

1. 当switch中缺少defalut时,编译器的`warning`情况

### [通过反射获取values()方法,检查valueOf(...)方法](./Reflection.java)

1. values()方法是由编译器添加的`static`方法;
2. valueOf(...)新增重载方法,一个参数

### 使用接口组织枚举(`RandomTest`,`EnumImpl`)

1. [接口组织枚举](./EnumImpl.java)
2. ["枚举的枚举"](./RandomTest.java)

### 枚举相关集合(`EnumSets`,`EnumMaps`)

[EnumSets](./EnumSets.java)  
[EnumMaps](./EnumMaps.java)  

1. EnumSet 使用 long 作为bit向量,提高处理速度
2. EnumSet.of(...) 方法 在5个参数内通过重载提高速度,超过5个使用可变参函数(数组)
3. enum实例定义的次序决定其在EnumMap和EnumSet中的次序

### 枚举常量相关方法(`ConstantSpecificMethod`)

[ConstantSpecificMethod](./ConstantSpecificMethod.java)

### enum职责链(`PostOffic`)

[PostOffice](./PostOffice.java)

### enum状态机

使用enum的状态机[VendingMachine](./VendingMachine.java)  
Add Money -> Item selection -> Get change
                |
             Abord transaction -> Get change(return)
                |
          <- Insufficient money for item

### 多路分发

1. [使用接口实现多路分发](./multiple/RoShamBo1.java)
2. [使用Enum实现多路分发1](./multiple/RoShamBo2.java)
3. [使用Enum常量相关方法实现多路分发2](./multiple/RoShamBo3.java)
4. [使用双参数增加分发优化常量方法实现的多路分发](./multiple/RoShamBo4.java)
5. [使用EnumMap实现的多路分发](./multiple/RoShamBo5.java)
6. [使用二维数组实现的多路分发](./multiple/RoShamBo6.java)

注: ==表驱动式编码==, 分析各种表的表现形式,找出最合适的一种



