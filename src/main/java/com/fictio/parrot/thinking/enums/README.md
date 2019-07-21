## 枚举类型

### 基本enums特性(`EnumClass,Spichiness`)

1. `ordinal()`返回一个int,即每个enum实例声明时的次序;
2. 可以使用`==`比较实例;
3. Enum类实现了`Comparable`接口,具有`compareTo()`方法;
4. `name()`方法等同于`toString()`;
5. `Enum.valuesOf(...)`为静态方法,返回相应的enum实例;
6. 使用静态导入(`static import`)可以将enum实例导入当前命名空间

### 添加/覆盖enum方法(`OzWatch`)

1. 为枚举添加自定义字段
2. 覆盖枚举原有方法

### Siwch语句中的enum(`TrafficLight`)

1. 当switch中缺少defalut时,编译器的`warning`情况
