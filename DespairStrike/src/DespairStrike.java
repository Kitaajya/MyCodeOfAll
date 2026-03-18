import javax.swing.*;
import java.awt.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//搏命一击模拟
class Strike{
    private boolean isStruck;        //监管者是否攻击
    private String[] whoRescue;        //谁去救援
    private boolean isToken;        //是否携带搏命
    public boolean isStruck() {
        return isStruck;
    }
    public void setStruck(boolean struck) {
        isStruck = struck;
    }
    public String[] getWhoRescue() {
        return whoRescue;
    }
    public void setWhoRescue(String whoRescue) {
       this.whoRescue= new String[]{whoRescue};
    }
    public boolean isToken() {
        return isToken;
    }
    public void setToken(boolean token) {
        isToken = token;
    }

    //派遣谁去救援
    public String despatch(String[] whoRescue){
        return"派遣谁去救援";
    }
    public String rescue(String[] whoRescue, boolean isToken){
      return "携带了化险为夷！||未携带化险为夷！";
    }
    public String toString(){
        return "救援者："+despatch(whoRescue)+"\n是否携带了化险为夷："+isToken;
    }
    public Strike(){
        boolean isStruck=true;
        String[] whoRescue= new String[]{"救援者"};
        boolean isToken=true;
    }
}
//模拟
class Simulator extends Strike{
    final String picture="src/img.png";
    ImageIcon image;

    Scanner scan=new Scanner(System.in);
    public Simulator(){
        super();
        initImage();
    }
    JFrame frame=new JFrame();
    // ======================加载并显示图片======================
    void initImage() {
        // 这是系统绝对能读取的写法
        ImageIcon tmp = new ImageIcon(new ImageIcon(picture).getImage());

        // 缩放
        Image scaled = tmp.getImage().getScaledInstance(500, 300, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaled);

        // 窗口
        frame = new JFrame("搏命一击特效");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel label = new JLabel(image);
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);

        // 测试：加载成功就打印
        if (image.getImageLoadStatus() == MediaTracker.COMPLETE) {
            System.out.println("图片加载成功！");
        } else {
            System.out.println("图片没找到！");
        }
    }
    //***********************************************************************
    double healthValue=1;//健康血量默认1
    @Override
    public String despatch(String[] whoRescue){
        System.out.println("请派出xxx去救援：（0,1,2,3）");
        int i;
        Scanner scanner=new Scanner(System.in);
        i=scanner.nextInt();
        if(i<0||i>3)throw new IllegalArgumentException("请输入0|1|2|3的数！");
        whoRescue=new String[4];
        whoRescue[0]="记者";
        whoRescue[1]="医生";
        whoRescue[2]="小说家";
        whoRescue[3]="幸运儿";
        return whoRescue[i];
    }
    void print(){
        System.out.println("搏命一击！");
    }
    @Override
    public String rescue(String[] whoRescue, boolean isToken){
        //假设救援者未受到任何攻击,救下人后开始计时，20秒内受击者不会到底，过后结算伤害
        String pivot=despatch(whoRescue);

        //救援者是pivot
        System.out.println(pivot+"是否携带了化险为夷：");isToken=scan.nextBoolean();
        // boolean isStruck=true;
        isStruck();
        boolean isStruck = true;
        if(!isToken){
            System.out.println("是否被救后再次受击？");
            isStruck=scan.nextBoolean();
            if(isStruck) {
                System.out.println("倒地！");
                return "倒地！";
            }
           return pivot+"未携带化险为夷！";
        }
        else {
            count();
            frame.setVisible(true);
            if(isStruck){
                return pivot + "携带了化险为夷！";
            }
            return "携带了化险为夷！";
        }
    }
    final long invincibleStateLastTime =20;
    //化险为夷期间显示时间
    void showtimeFunction(){
        ScheduledThreadPoolExecutor duringTime=new ScheduledThreadPoolExecutor(1);//用于计时
        int[] showtime={0};
        Runnable taskOnShowTime=()->{
            System.out.println("化险为夷进行"+showtime[0]+"秒");
            showtime[0]++;
            if(showtime[0]>invincibleStateLastTime)duringTime.shutdown();
        };
        duringTime.scheduleAtFixedRate(taskOnShowTime,0,1,TimeUnit.SECONDS);
    }
    //计时20秒
    void count(){
        new Thread(this::showtimeFunction).start();
        new Thread(this::justify ).start();
    }
    void justify() {
        try{
            healthValue=0.5;
            System.out.println("无敌状态持续20秒！");
            long startTime=System.currentTimeMillis();
            boolean s;
            System.out.println("是否受击？？？？");
            while(true){
                Scanner scanner =new Scanner(System.in);
                s=scanner.nextBoolean();
                if(s){
                    long happenStruckTime=System.currentTimeMillis();
                    long practicalDeltaTime=happenStruckTime-startTime;
                    initImage();
                    print();
                    System.out.println(practicalDeltaTime/1000);
                    if(practicalDeltaTime>20) break;
                }
            }
        } catch (InputMismatchException e) {
            System.out.println("请输入正确文本！"+e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("线程中断！"+e.getMessage());
        }

    }

    public static void self(){
        Simulator s=new Simulator();
        s.rescue(new String[]{"幸运儿"},true);
    }
}

public class DespairStrike{
    public static void main(String[] a){
        Simulator.self();
    }
}