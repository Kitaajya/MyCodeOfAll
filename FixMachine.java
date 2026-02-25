import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Set;

abstract class People{
    protected String name;
    protected int id;
    protected double wage;
    public String getName () {return name;}
    public int getId () {return id;}
    public double getWage () {return wage;}
    public void setName (String name){this.name = name;}
    public void setId (int id){this.id = id;}
    public void setWage ( double wage){
        this.wage = wage;
    }
    public People(){
            this.name="姓名";
            this.wage=3000;
            this.id=20260000;}
    abstract public void add(boolean isAdd,int quantity);
    @Override
    public String toString(){
            return"我的姓名："+this.name+"\n我的工号："+this.id+"\n我的工资："+this.wage;
        }
}
class Employee extends People{
    public Employee(){
        super();
        this.setName(name);
        this.setId(id);
        this.setWage(wage);
    }
    @Override
    public void add(boolean isAdd,int quantity){
        Hashtable<Integer,String>table=new Hashtable<>();       //table(id,name)
        try{
            Scanner scanner=new Scanner(System.in);
            System.out.print("是否要添加人数：");
            isAdd=scanner.nextBoolean();
            scanner.nextLine();
            System.out.println();
            if(isAdd==true){
                System.out.print("请输入你要添加的人数：");
                quantity=scanner.nextInt();
                if(quantity<=0)throw new IllegalArgumentException("请输入大于零的数字！");
                else{
                    int BaseId =20260000;
                    for(int i=0;i<quantity;i++){
                        System.out.print("请输入姓名：");
                        name=scanner.next();
                        scanner.nextLine();
                        System.out.println();
                        int id=i+BaseId;
                        table.put(id,name);
                        Set<Integer>idInteger= table.keySet();
                        String nameString= table.values().toString();

                        System.out.println("最终添加结果：");
                        System.out.println("工号："+idInteger+"\t姓名："+nameString);
                    }

                }
            }else{
                System.out.println("即将退出程序……");
                try{Thread.sleep(3000);}catch (InterruptedException e){System.out.println("线程中断！");}
                System.exit(0);
            }
        } catch (InputMismatchException e) {
            System.out.println("请输入合规字！");
        }
    }
}



public class FixMachine{
    public static void main(String[] args){
        Employee e=new Employee();
        e.add(true,2);
    }
}
