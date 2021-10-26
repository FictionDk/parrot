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