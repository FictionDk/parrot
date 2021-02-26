# 逻辑编程

## [线程概念](BaseThreadDemo.java)

1. 创建线程
    - 继承Thread
    - 实现Runnable接口
2. 线程的基本属性
    - id/name/优先级
    - 状态: NEW/TERMINATED/RUNNABLE/BLOCKED,WAITING,TIMED_WAITING/isAlive(),run方法结束前,返回值都是true
    - daemo线程: 当整个程序都是daemo线程时,程序退出
    - sleep: 该线程让出CPU,线程阻塞;中断时抛出InterruptedException;
    - yield: 告知操作系统调度器不急着占用cpu,由调度器决定是否挂起该线程;
    - join: 让调用join的线程等待该线程结束

## synchronized

1. 用途:
    - 保护同一个对象的方法调用,确保同时只能有一个线程执行
    - 保证内存可见性,不过从性能成本考虑,用volatie更佳
2. [用法](SharedMemoryDemo.java)-incrShared(...)
    - 实例方法上: 用this对象作为锁, 保护的是this对象
    - 静态方法上: 用类对象作为锁, 保护的是类对象(确保类对象不会同时被多个线程修改)
    - 块对象: 支持自定义对象作为锁, 保护的是()内的对象,{}是同步执行的代码
3. [进阶](SharedMemoryDemo.java)
    - 可重入性,同一个锁可以调用其他需要同样锁的代码
    - 内存可见性,synchronized可以保证内存可见性,但是太重,建议使用volatie - (VisibilityThread)
    - [死锁,互相持有对方的锁,陷入相互等待造成死锁](DeadLockDemo.java)
4. [同步容器](CollectionDemo.java),对容器加上synchronized,所有的操作都变成原子操作,需要注意:
    - 复合操作(如先检查再更新)
    - 伪同步
    - 迭代,, java.util.ConcurrentModificationException
    - 并发容器,可以直接使用java专门设计的容器
5. 注意:
    - synchronized保护的是对象而非代码

## 线程协作(wait/notify)

1. 场景需求
    - [简单协作示例](WaitAndNotifyDemo.java)
    - [生产/消费者模式](ProducerAndConsumerDemo.java)
    - [同时开始.如仿真程序,要求多线程同时开始](WaitWithLatchDemo.java)
    - [等待结束, 主从协作,主线程等待所有子任务完成再结束](WaitWithLatchDemo.java)
    - [异步结果](FeatureCallDemo.java), 主从协作,异步返回Future对象,并通过该对象后续获得最终结果
    - [集合点, 如迭代计算中, 各线程在结合点交换和汇总,再进行下一次迭代](AssemblePointDemo.java)
2. 阻塞队列
    - 接口BlockingQueue,BlockingDeque;
    - 基于数组的实现: ArrayBlockingQueue;
    - 基于链表的实现: LinkedBlocking/LinkedBlockingDeque
    - 基于堆的实现: PriorityBlockingQueue

## 线程中断
1. 机制: 实例方法: isInterrupted(),interrupt(); 静态方法: interrupted(),操作当前线程
