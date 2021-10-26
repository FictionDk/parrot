# 逻辑编程-16章 并发

## 原子变量和CAS

1. 基本原子变量
    - AtomicBoolean
    - AtomicInteger
    - AtomicLong
    - AtomicReference
    - AtomicLongArray
    - AtomicReferenceArray
2. CAS -- `final boolean compareAndSet(int exp, int update);`
    - 乐观锁, 冲突检查失败时重启,不阻塞,不用线程切换开销
    - 原子变量操作简单, 易用cas;复杂结构可使用并发包内已实现的并发非阻塞容器
3. 实现锁
