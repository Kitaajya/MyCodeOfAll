
class HunterBehaviour {

    public boolean isStrike; // 是否击中求生者
    public double distance = 1.0; // 求生者与屠夫之间的距离
    public final double strikeRange = 4.5; // 屠夫的攻击范围

    public double survivorTime;
    public double hunterTime;
    public double totalTime;

    // 计时方法，返回经过的时间（秒）
    public double timeCalculator(int seconds) {
        System.out.println("===游戏开始，计时开始===");
        long startTime = System.currentTimeMillis();                        //开始时间
        try {
            for (int i = 0; i < seconds; i++) {
                Thread.sleep(1000);
                System.out.printf("计时：%d秒\r", i + 1);
            }
        } catch (InterruptedException e) {
            System.out.println("线程被中断：" + e.getMessage());
        }
        long endTime = System.currentTimeMillis();                              //结束时间
        return (endTime - startTime) / 1000.0;
    }

    public HunterBehaviour(boolean isStrike, double survivorTime, double hunterTime, double totalTime) {
        this.isStrike = isStrike;
        this.hunterTime = hunterTime;
        this.survivorTime = survivorTime;
        this.totalTime = totalTime;
    }

    // 攻击判定
    public void justifyStrike() {
        if (distance < 0) {
            System.out.println("距离不能为负数！");
            return;
        }
        if (distance <= strikeRange) {
            System.out.println(isStrike ? "命中目标！" : "未命中目标！");
        } else {
            System.out.println("目标超出攻击范围！");
        }
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "求生者是否交互：" + isStrike
                + "\n求生者与监管者之间的距离：" + distance
                + "\n监管者的攻击范围：" + strikeRange;
    }
}

class SurvivorBehaviour extends HunterBehaviour {

    public boolean survivorInteraction; // 求生者交互

    public SurvivorBehaviour(boolean isStrike, boolean survivorInteraction, double survivorTime,
            double hunterTime, double totalTime) {
        super(isStrike, survivorTime, hunterTime, totalTime);
        this.survivorInteraction = survivorInteraction;
    }

    // 恐惧震慑判定
    @Override
    public void justifyStrike() {
        super.justifyStrike();
        if (survivorInteraction && distance <= strikeRange && distance >= 0) {
            if (Math.abs(hunterTime - survivorTime) < 0.1) { // 允许微小时间误差
                System.out.println(isStrike ? "恐惧震慑！" : "攻击落空，未触发恐惧震慑！");
            } else {
                System.out.println("未触发恐惧震慑！");
            }
        } else {
            System.out.println("未触发恐惧震慑！");
        }
    }
}

public class FearShockSimulation {

    public static void main(String[] args) {
        HunterBehaviour hunter = new HunterBehaviour(true, 5, 5, 10);
        hunter.timeCalculator(5); // 计时5秒

        SurvivorBehaviour survivor = new SurvivorBehaviour(true, true, 5, 5, 10);
        survivor.setDistance(3); // 设置距离为3（在攻击范围内）

        System.out.println("\n" + hunter);
        System.out.println(survivor);
        survivor.justifyStrike(); // 触发攻击判定和恐惧震慑检查
    }
}
