package com.wdw.study.service;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CuratorServiceTest {

    @Resource(name="curatorFramework")
    private CuratorFramework client;

    // 共享信号量，多个信号量
    @Test
    public void testInterProcessSemaphoreV22() throws Exception {
        // 创建一个信号量, Curator 以公平锁的方式进行实现
        final InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, "/lock", 3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取锁对象
                try {
                    String threadName = Thread.currentThread().getName();
                    // 获取2个许可
                    Collection<Lease> acquire = semaphore.acquire(2);
                    System.out.println(threadName + "获取信号量>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(2 * 1000);
                    semaphore.returnAll(acquire);
                    System.out.println(threadName + "释放信号量>>>>>>>>>>>>>>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取锁对象
                try {
                    String threadName = Thread.currentThread().getName();
                    // 获取一个许可
                    Lease lease = semaphore.acquire();
                    System.out.println(threadName + "获取信号量>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(2 * 1000);
                    semaphore.returnLease(lease);
                    System.out.println(threadName + "释放信号量>>>>>>>>>>>>>>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
    }

    // 共享信号量
    @Test
    public void testInterProcessSemaphoreV2() throws Exception {
        // 创建一个信号量, Curator 以公平锁的方式进行实现
        final InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, "/lock", 1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取锁对象
                try {
                    String threadName = Thread.currentThread().getName();
                    // 获取一个许可
                    Lease lease = semaphore.acquire();
                    System.out.println(threadName + "获取信号量>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(2 * 1000);
                    semaphore.returnLease(lease);
                    System.out.println(threadName + "释放信号量>>>>>>>>>>>>>>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取锁对象
                try {
                    String threadName = Thread.currentThread().getName();
                    // 获取一个许可
                    Lease lease = semaphore.acquire();
                    System.out.println(threadName + "获取信号量>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(2 * 1000);
                    semaphore.returnLease(lease);
                    System.out.println(threadName + "释放信号量>>>>>>>>>>>>>>>>>>>>>");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
    }

    // 多重共享锁
    @Test
    public void testInterProcessMultiLock() throws Exception {
        // 可重入锁
        final InterProcessLock interProcessLock1 = new InterProcessMutex(client, "/lock");
        // 不可重入锁
        final InterProcessLock interProcessLock2 = new InterProcessSemaphoreMutex(client, "/lock");
        // 创建多重锁对象
        final InterProcessLock lock = new InterProcessMultiLock(Arrays.asList(interProcessLock1, interProcessLock2));

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    // 获取参数集合中的所有锁
                    lock.acquire();

                    // 因为存在一个不可重入锁, 所以整个 InterProcessMultiLock 不可重入
                    System.out.println(threadName + "----->" + lock.acquire(2, TimeUnit.SECONDS));
                    // interProcessLock1 是可重入锁, 所以可以继续获取锁
                    System.out.println(threadName + "----->" + interProcessLock1.acquire(2, TimeUnit.SECONDS));
                    // interProcessLock2 是不可重入锁, 所以获取锁失败
                    System.out.println(threadName + "----->" + interProcessLock2.acquire(2, TimeUnit.SECONDS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
    }

    // 分布式读写锁
    @Test
    public void testReadWriteLock() throws Exception {
        // 创建共享可重入读写锁
        final InterProcessReadWriteLock locl1 = new InterProcessReadWriteLock(client, "/lock");
        final InterProcessReadWriteLock lock2 = new InterProcessReadWriteLock(client, "/lock");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    locl1.writeLock().acquire(); // 获取锁对象
                    System.out.println(threadName + "获取写锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    locl1.readLock().acquire(); // 获取读锁，锁降级
                    System.out.println(threadName + "获取读锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    locl1.readLock().release();
                    System.out.println(threadName + "释放读锁<<<<<<<<<<<<<<<<<<<<<");
                    locl1.writeLock().release();
                    System.out.println(threadName + "释放写锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    lock2.writeLock().acquire(); // 获取锁对象
                    System.out.println(threadName + "获取写锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    lock2.readLock().acquire(); // 获取读锁，锁降级
                    System.out.println(threadName + "获取读锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    lock2.readLock().release();
                    System.out.println(threadName + "释放读锁<<<<<<<<<<<<<<<<<<<<<");
                    lock2.writeLock().release();
                    System.out.println(threadName + "释放写锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
    }

    // 分布式可重入排它锁
    @Test
    public void testInterProcessMutex() throws Exception {
        // 分布式可重入排它锁
        final InterProcessLock lock = new InterProcessMutex(client, "/lock");
        final InterProcessLock lock2 = new InterProcessMutex(client, "/lock");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    lock.acquire(); // 获取锁对象
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    lock.acquire(); // 测试锁重入
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    lock.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                    lock.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    lock.acquire(); // 获取锁对象
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    lock.acquire(); // 测试锁重入
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(1 * 1000);
                    lock.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                    lock.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
//        顺序不一定，但是同一个线程可以多次获取，获取几次就必须释放几次，其他线程才能获取到锁
    }


    // 分布式不可重入排它锁
    @Test
    void testInterProcessSemaphoreMutex() throws Exception {
        // 分布式不可重入排它锁
        final InterProcessLock lock = new InterProcessSemaphoreMutex(client, "/lock");
        final InterProcessLock lock2 = new InterProcessSemaphoreMutex(client, "/lock");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();
                    lock.acquire(); // 获取锁对象
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    // 测试锁重入
                    Thread.sleep(2 * 1000);
                    lock.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 获取锁对象
                try {
                    String threadName = Thread.currentThread().getName();
                    lock2.acquire();
                    System.out.println(threadName + "获取锁>>>>>>>>>>>>>>>>>>>>>");
                    Thread.sleep(2 * 1000);
                    lock2.release();
                    System.out.println(threadName + "释放锁<<<<<<<<<<<<<<<<<<<<<");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
        }
//        顺序不一定，但是必须是获取后再释放其他线程才能获取到锁
    }

}