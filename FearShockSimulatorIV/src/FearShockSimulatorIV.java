/**设计思路>>
 * 项目：第四次模拟恐惧震慑
 * 项目名称：FearShockSimulatorIV.java
 * 作者：亚当斯·柯南·道尔（Kitaajya）
 * 时间：20260227
 * 改动：<相较于第三次的恐惧震慑模拟，本次改动将设计大量线程池，阻塞队列，佣兵恐惧震慑后延迟伤害不会立刻倒地，
 * 先知劳神未翻板结束而受到恐惧震慑，拒绝立刻倒地，而是翻板结束后倒地。>
 * 声明：以上延迟均由ScheduleThreadPool线程池和对应的第七个函数参数拒绝策略执行
 * 个人网址：https://github.com/Kitaajya
 * **/
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.*;

// 抽象求生者类
abstract class Survivor {
    public String name;        // 姓名
    public double healthValue; // 血量（1.0=满血，0.5=半血，0.0=倒地）
    public boolean isShocked;  // 是否被恐惧震慑

    public Survivor() {
        this.name = "求生者";
        this.healthValue = 1.0;
        this.isShocked = false;
    }

    abstract public void beStruck() throws InterruptedException; // 受击函数
    abstract public void checkHealth();                         // 检测血量
    abstract public void checkShock();                          // 检测是否发生震慑
}
class Mercenary extends Survivor {
    private ScheduledExecutorService mercenaryExecutor;
    public Mercenary() {
        super();
        this.name = "佣兵";
        this.healthValue = 1.0;
        initThreadPool();
    }
    // 初始化线程池：核心线程1，最大线程4，非核心线程存活12秒
    private void initThreadPool() {
        //阻塞队列（存储延迟任务，容量5）
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
        //线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            private int threadCount = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "佣兵延迟任务线程-" + (++threadCount));
                thread.setDaemon(true); // 守护线程，程序退出时自动销毁
                return thread;
            }
        };

        //拒绝策略,佣兵受击任务被拒绝时，打印提示并延迟1秒重试
        RejectedExecutionHandler rejectPolicy = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                System.out.println("拒绝策略触发，佣兵延迟倒地任务提交失败，1秒后重试");
                try {
                    // 等待1秒后尝试重新提交
                    Thread.sleep(1000);
                    if (!executor.isShutdown()) {
                        executor.submit(r);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("重试提交任务失败：" + e.getMessage());
                }
            }
        };

        //构造ThreadPoolExecutor（ScheduledThreadPoolExecutor需用其指定构造）
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1,                  // 核心线程数
                4,                  // 最大线程数
                12,                 // 非核心线程存活时间
                TimeUnit.SECONDS,   // 时间单位
                workQueue,          // 阻塞队列
                threadFactory,      // 线程工厂
                rejectPolicy        // 拒绝策略
        );
        //转换为ScheduledExecutorService，实现延迟任务
        this.mercenaryExecutor = Executors.unconfigurableScheduledExecutorService(
                new ScheduledThreadPoolExecutor(1, threadFactory, rejectPolicy)
        );
    }

    //受击逻辑：被震慑后延迟2秒倒地
    @Override
    public void beStruck() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("===== 佣兵受击模拟 =====");
        while (true) {
            System.out.print("是否被监管者命中（true/false）：");
            boolean isStruck;
            try {
                isStruck = scanner.nextBoolean();
            } catch (InputMismatchException e) {
                System.out.println("输入错误！请输入true或false");
                scanner.next();
                continue;
            }
            if (isStruck) {
                System.out.print("是否触发恐惧震慑（true/false）：");
                boolean isShock = scanner.nextBoolean();
                this.isShocked = isShock;
                if (isShock) {
                    //恐惧震慑延迟2秒扣血倒地
                    System.out.println("恐惧震慑佣兵不会立刻倒地，2秒后倒地！");
                    mercenaryExecutor.schedule(()->{
                        this.healthValue = 0.0;
                        System.out.println("佣兵血量清零，倒地！");
                        checkHealth(); // 检测最终血量
                    }, 2, TimeUnit.SECONDS);
                } else {
                    //普通受击：直接扣血
                    this.healthValue -= 0.5;
                    System.out.println("佣兵血量减少0.5，当前血量：" + this.healthValue);
                    checkHealth();
                }
            } else {
                System.out.println("未受到攻击，当前血量：" + this.healthValue);
            }
            //检测是否倒地，退出循环
            if (this.healthValue <= 0.0) {
                System.out.println("模拟结束：佣兵已倒地！");
                break;
            }
            System.out.print("是否继续模拟（输入exit退出，其他继续）：");
            String input = scanner.next();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("模拟结束！");
                break;
            }
        }
        // 关闭线程池
        mercenaryExecutor.shutdown();
        scanner.close();
    }
    // 检测血量
    @Override
    public void checkHealth() {
        if (healthValue <= 0.0) {
            System.out.println("【血量检测】佣兵血量≤0，已倒地！");
        } else if (healthValue == 0.5) {
            System.out.println("【血量检测】佣兵半血，可继续行动！");
        } else {
            System.out.println("【血量检测】佣兵满血，状态良好！");
        }
    }
    // 检测是否发生震慑
    @Override
    public void checkShock() {
        if (isShocked) {
            System.out.println("【震慑检测】触发恐惧震慑，已标记！");
        } else {
            System.out.println("【震慑检测】未触发恐惧震慑！");
        }
    }
}

public class FearShockSimulatorIV {
    public static void main(String[] args) {
        try {
            // 创建佣兵对象，执行受击模拟
            Mercenary mercenary = new Mercenary();
            mercenary.beStruck();
        } catch (InterruptedException e) {
            System.out.println("模拟过程被中断：" + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}