import java.io.IO;
import java.sql.*;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Scanner;

interface DeleteDataBaseSomeInformation{
    void delete();
}

interface Add {
    void add();
}

class PeopleReception {
    private String pack    ;
    private int    quantity;
    private String name    ;
    private String userName;
    private String task    ;
    private int id;

    public PeopleReception(String pack, int quantity, String name, String userName, String task,int id) {
        this.pack = pack;
        this.quantity = quantity;
        this.name = name;
        this.userName = userName;
        this.task = task;
        this.id=id;
    }

    public String getPack() {
        return pack;
    }
    public void setPack(String pack) {
        this.pack = pack;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getTask() {
        return task;
    }
    public void setTask(String task) {
        this.task = task;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Override
    public String toString(){
        return
                "甲方："+this.name+"\n"+
                "包裹名称："+this.pack+"\n"+
                "包裹数量："+this.quantity+"\n"+
                "甲方任务："+this.task+"\n"+
                "用户名称："+this.userName+"\n"+
                "员工工号："+this.id;
    }
}
class Operator extends PeopleReception implements Add,DeleteDataBaseSomeInformation{
    public Operator(String pack, int quantity, String employeeName, String userName, String task,int id) {
        super(pack, quantity, employeeName, userName, task,id);
    }
    public final String userNameOnDataBase="root";
    public final String passwordOnDatabase ="123456";
    public final String url="jdbc:mysql://localhost:3306/PackageSystem?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    Connection connection;
    Statement statement;
    ResultSet resultSet=null;
    String sql="select*from employee";

    public void connectDatabase() {
        try{
            connection=DriverManager.getConnection(url,userNameOnDataBase, passwordOnDatabase);
            statement= connection.createStatement();
            resultSet=statement.executeQuery(sql);
            HashMap<String,String> map=new HashMap<>();
            while(resultSet.next()){
                String pack=resultSet.getString("pack");
                String quantity=resultSet.getString("quantity");
                String name=resultSet.getString("name");
                String userName=resultSet.getString("userName");
                String task=resultSet.getString("task");
                int id= (resultSet.getInt("id"));
                map.put(name,userName);
                IO.println("员工工号："+id +"\n\t"+
                        "甲方："+name+"\n\t"+
                        "包裹名称："+pack+"\n\t"+
                        "包裹数量："+quantity+"\n\t"+
                        "甲方任务："+task+"\n\t"+
                        "用户名称："+userName+"\n\t"
                );
                IO.println("查找甲方与用户：");
                String fingUserName=map.get(name);
                IO.println(fingUserName);
            }} catch(SQLException e) {
            IO.println("连接错误!");
        }finally {
            Runnable taskOfResultSet=()->{
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        IO.println("关闭连接错误！");
                    }
            };
            Runnable taskOfStatement=()->{
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        IO.println("关闭连接错误！");
                    }
            };
            Runnable taskOfConnection=()->{
                try {
                    connection.close();
                } catch (SQLException e) {
                    IO.println("关闭连接错误！");
                }
            };
                HashMap<Integer,Runnable>closeMap=new HashMap<>();
                closeMap.put(1,taskOfResultSet);
                closeMap.put(2,taskOfStatement);
                closeMap.put(3,taskOfConnection);
        }
    }
    Scanner scanner =new Scanner(System.in);

    @Override
     public void add(){
        try{
            connection=DriverManager.getConnection(url,userNameOnDataBase, passwordOnDatabase);
            statement= connection.createStatement();
            IO.print("请添加用户：");
            String addUser=scanner.next().trim();
            scanner.nextLine();
            String addOperation ="" + "Insert into employee(userName)values(' " +addUser+ "');";
            statement.executeUpdate(addOperation);
            String select="select*from employee";
            resultSet=statement.executeQuery(select);
            IO.println("最新用户："+addUser);
        }catch(SQLException e){
            IO.println("MySQL使用错误！"+e.getMessage());
        }finally{
            try{
                if(resultSet!=null) resultSet.close();
                if(statement!=null) statement.close();
                if(connection!=null) connection.close();
            }catch(SQLException e){
                IO.println("MySQL关闭出现异常！");
            }
            scanner.close();
        }
    }

    public void delete() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入你要删除的人名：");
        String deleteUserName = scanner.next();

        String deleteOperation = "DELETE FROM employee WHERE name = ?";
        /**新建连接**/
        try (Connection conn = DriverManager.getConnection(url, userNameOnDataBase, passwordOnDatabase);
             PreparedStatement pstmt = conn.prepareStatement(deleteOperation)) {
            pstmt.setString(1, deleteUserName);
            pstmt.executeUpdate();
            System.out.println("删除成功！");
        } catch (SQLException e) {
            System.out.println("删除失败：" + e.getMessage());
        }
    }
    public static void operate(){
        Scanner scanner1=new Scanner(System.in);
        try{
            Operator o=new Operator("包裹",21,"姓名","用户","任务",202600001);
            IO.println("本系统具有增加用户和封禁用户的功能，请选择：");
            IO.println("-----------A,增加用户-----------");
            IO.println("-----------B,封禁用户-----------");
            IO.println("-----------C,显示表格-----------");
            IO.println("-----------E,退出系统-----------");
            while(true){
                char choice=scanner1.next().charAt(0);
                switch(choice){
                    case'A'->o.add();
                    case'B'->o.delete();
                    case'C'->o.connectDatabase();
                    case 'E' -> {
                        IO.println("退出系统！");
                        return;
                    }
                }
            }}catch(InputMismatchException ei){
            IO.println("请输入正确文本！");
        }finally{
            scanner1.close();
        }
    }
}
public class CreatePackageSystem{
    void main() {
        Operator.operate();
    }
}