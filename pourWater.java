import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.Queue;
import java.util.LinkedList; 

public class pourWater extends Application{
    int siteI;

    @Override
    public void start(Stage primaryStage) throws Exception{
        //整体布局
        BorderPane borderPane = new BorderPane();
        //上部布局
        HBox hbox = new HBox(10);
        // 创建文本字段
        TextField textTop = new TextField();
        textTop.setPromptText("请输入瓶子个数");
        // 创建按钮
        Button buttonTop = new Button("瓶子个数提交");

        hbox.getChildren().addAll(textTop,buttonTop);
        // 上部布局加入整体布局中
        borderPane.setTop(hbox);

        // 创建场景并设置舞台
        Scene scene = new Scene(borderPane, 1000, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("倒水问题");
        primaryStage.show();

        //button响应区域
        // 为按钮添加事件处理
        buttonTop.setOnAction(event -> showCups(textTop,borderPane));
    }
    //显示瓶子
    public void showCups(TextField str1,BorderPane borderPane){
        int cupsNum = Integer.parseInt(str1.getText());
        
        Cups cups = new Cups(cupsNum);

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setMinSize(900, 400);
        //一行当瓶子，一行当水
        Rectangle [][] cupsFX = new Rectangle[2][cupsNum];
        TextField [][] cupSetting = new TextField[2][cupsNum];
        for(int i = 0;i < cupsNum; i++){
            cupsFX[0][i] = new Rectangle(900/cupsNum,200);
            cupsFX[1][i] = new Rectangle(900/cupsNum,100);
            cupSetting[0][i] = new TextField();
            cupSetting[0][i].setPromptText("杯子容量");
            cupSetting[0][i].setPrefWidth(900/cupsNum);
            cupSetting[1][i] = new TextField();
            cupSetting[1][i].setPromptText("默认水量");
            cupSetting[1][i].setPrefWidth(900/cupsNum);
            
            cupsFX[0][i].setFill(Color.GRAY); 
            cupsFX[1][i].setFill(Color.BLUE);

            // 锚定在底部中央
            AnchorPane.setBottomAnchor(cupsFX[0][i], 60.0);
            AnchorPane.setLeftAnchor(cupsFX[0][i], 10.0+i*(900/cupsNum+10));
            AnchorPane.setBottomAnchor(cupsFX[1][i], 60.0);
            AnchorPane.setLeftAnchor(cupsFX[1][i], 10.0+i*(900/cupsNum+10));

            AnchorPane.setBottomAnchor(cupSetting[0][i], 30.0);
            AnchorPane.setLeftAnchor(cupSetting[0][i], 10.0+i*(900/cupsNum+10));
            AnchorPane.setBottomAnchor(cupSetting[1][i], 0.0);
            AnchorPane.setLeftAnchor(cupSetting[1][i], 10.0+i*(900/cupsNum+10));

            
            anchorPane.getChildren().add(cupsFX[0][i]);
            anchorPane.getChildren().add(cupsFX[1][i]);
            anchorPane.getChildren().add(cupSetting[0][i]);
            anchorPane.getChildren().add(cupSetting[1][i]);
        }
        Button submitBtn = new Button("水杯设置提交");
        AnchorPane.setBottomAnchor(submitBtn, 0.0);
        AnchorPane.setRightAnchor(submitBtn, 0.0);
        anchorPane.getChildren().add(submitBtn);
        
        
        borderPane.setCenter(anchorPane);
       

        submitBtn.setOnAction(event -> {
            int maxNub = 0;
            Integer [] cupsField = new Integer[cupsNum];
            Integer [] oriWaterField = new Integer[cupsNum];
            //获取最大值，以最大值作为200像素来同比缩放
            for(int i=0;i < cupsNum; i++){
                if(Integer.parseInt(cupSetting[0][i].getText()) > maxNub){
                    maxNub = Integer.parseInt(cupSetting[0][i].getText());
                }
            }
            double rate = 200.0/maxNub;
            for(int i=0;i < cupsNum; i++){
                cupsFX[0][i].setHeight(Double.parseDouble(cupSetting[0][i].getText())*rate);
                cupsFX[1][i].setHeight(Double.parseDouble(cupSetting[1][i].getText())*rate);
                cupsField[i] = Integer.parseInt(cupSetting[0][i].getText());
                oriWaterField[i] = Integer.parseInt(cupSetting[1][i].getText());
            }
            cups.setHeightWaterByFX(cupsField);
            cups.setOriWaterByFX(oriWaterField);
            cups.setVisitList();
            baginRecycle(rate,cups,borderPane,cupsFX,cupSetting);
        });
        

    }
    //启动运算，需要杯子的数量，各个杯子的初始数据，需要的水量
    public void baginRecycle(Double rate,Cups cups, BorderPane borderPane, Rectangle[][]cupsFX,TextField[][] cupSetting){
        VBox vbox = new VBox();
        TextField textTop2 = new TextField();
        textTop2.setPromptText("请输入您需要的水量");
        
        // 创建按钮
        Button buttonRight = new Button("开始运算");
        
        vbox.getChildren().addAll(textTop2,buttonRight);
        borderPane.setRight(vbox);


        buttonRight.setOnAction(e->{
            int finalWater = Integer.parseInt(textTop2.getText());
            cups.setFinalWaterByFX(finalWater);
            cups.beginRecycle();
            showFinal(rate,cups,borderPane,cupsFX,cupSetting);
        });
        
        

    }
    public void showFinal(Double rate,Cups cups, BorderPane borderPane, Rectangle[][]cupsFX,TextField[][] cupSetting){
        //初始已经满足
        if(cups.currectSum == cups.finalWater){
            Alert alert = new Alert(AlertType.WARNING, "当前状态已满足要求，共经过0次倒水动作", ButtonType.OK);
            alert.setTitle("警告");
            alert.setHeaderText("警告信息");
            // 显示警告框
            alert.showAndWait();
            borderPane.getChildren().remove(borderPane.getCenter());
            borderPane.getChildren().remove(borderPane.getRight());
            return;
        }
        
        
        //没有结果
        if(cups.minRode.size()==0){
            Alert alert = new Alert(AlertType.WARNING, "无法满足您的需求", ButtonType.OK);
            alert.setTitle("警告");
            alert.setHeaderText("警告信息");
            // 显示警告框
            alert.showAndWait();
            borderPane.getChildren().remove(borderPane.getCenter());
            borderPane.getChildren().remove(borderPane.getRight());
            return;
        }

        VBox vbox = new VBox();
        // 创建按钮
        Button buttonRight = new Button("按步显示最短路径");
        Button buttonLast = new Button("动画显示最长路径");
        Button buttonReset = new Button("重置");
        vbox.getChildren().addAll(buttonRight,buttonLast,buttonReset);
        borderPane.setRight(vbox);
        ArrayList<Integer[]> minRode =  cups.minRode.get(cups.minRode.size()-1);
        ArrayList<Integer[]> maxRode =  cups.minRode.get(0);
        siteI = minRode.size()-2;

        buttonReset.setOnAction(e->{
                borderPane.getChildren().remove(borderPane.getCenter());
                borderPane.getChildren().remove(borderPane.getRight());
                return;
        });

        buttonLast.setOnAction(e->{
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    int i;
                    for(i=maxRode.size()-2;i>=0;i--){
                        for(int j=0;j<cups.cupsNum;j++){
                            cupsFX[1][j].setHeight(maxRode.get(i)[j]*rate);
                            cupSetting[0][j].setText(String.valueOf(maxRode.get(i)[j]));
                        }
                        Thread.sleep(500); // 后台线程中暂停
                    }
                    return null;
                }
            };
            new Thread(task).start();
        });

        buttonRight.setOnAction(e->{
            if(cups.minRode.size()==0){
                Alert alert = new Alert(AlertType.WARNING, "无法满足您的需求", ButtonType.OK);
                alert.setTitle("警告");
                alert.setHeaderText("警告信息");
                // 显示警告框
                alert.showAndWait();
                borderPane.getChildren().remove(borderPane.getCenter());
                borderPane.getChildren().remove(borderPane.getRight());
                return;
            }
            if(siteI==-1){
                Alert alert = new Alert(AlertType.WARNING, "路径已显示结束，共经过了"+(cups.minRode.get(cups.minRode.size()-1).size()-1)+"次操作", ButtonType.OK);
                alert.setTitle("警告");
                alert.setHeaderText("警告信息");
                // 显示警告框
                alert.showAndWait();
                borderPane.getChildren().remove(borderPane.getCenter());
                borderPane.getChildren().remove(borderPane.getRight());
                return;
            }

            for(int j=0;j<cups.cupsNum;j++){
                cupsFX[1][j].setHeight(minRode.get(siteI)[j]*rate);
                cupSetting[0][j].setText(String.valueOf(minRode.get(siteI)[j]));
            }
            siteI--;
            
        });
    }

    public static void main(String[] args) {
        launch(args);
        // Cups cups = new Cups();
        // cups.beginRecycle();
    }
}


class Cups{
    public Integer []heightWater;//标注各个水杯的容量
    public Integer cupsNum;//标注水杯的数量
    public Integer []oriWater;//初始的水量
    public ArrayList<Integer[]> visitAllList;//已经访问过所有的节点,最后一位用标记去记录自己的父结点是谁
    public ArrayList<Integer[]> visitNoRepeatList;//不包含重复的已经访问过的情况
    public Queue<Integer[]> currectList;//当前执行bsf的队列，最后一位是自己的下标，方便子结点标注
    public Integer []currectNum;//存储当前节点数据
    public int currectSum;//当前的总水量
    public int currectTimes;//当前的次数
    public int finalWater;//预期的水量
    public ArrayList<ArrayList<Integer[]>> minRode;//最短路径

    //初始化
    Cups(){
        System.out.println("请输入水杯数量：");
        Scanner scanner = new Scanner(System.in); 
        this.cupsNum = scanner.nextInt();
        setHeightWater();
        setOriWater();
        setVisitList();
        setFinalWater();
    }
    //-----------可视化调用接口------------------------------
    Cups(int cupsNum){
        this.cupsNum = cupsNum;
    }

    public void setFinalWaterByFX(int finalWater){
        this.finalWater = finalWater;
    }

    public void setHeightWaterByFX(Integer[] heightWater){
        this.heightWater = new Integer[this.cupsNum];
        for(int i = 0;i < this.cupsNum; i++){
            this.heightWater[i] = heightWater[i];
        }

    }

    public void setOriWaterByFX(Integer[] oriWater){
        this.currectSum = 0;
        this.oriWater = new Integer[this.cupsNum];
        this.currectNum = new Integer[this.cupsNum];
        for(int i = 0;i < this.cupsNum; i++){
            this.oriWater[i] = oriWater[i];
            this.currectNum[i] = this.oriWater[i];
            this.currectSum += this.currectNum[i];
        }
    
    }
  //-----------------------------------------
    public void setFinalWater(){
        System.out.println("请输入您需要的水量：");
        Scanner scanner = new Scanner(System.in); 
        this.finalWater = scanner.nextInt();
        scanner.close();

    }

    public void setHeightWater(){
        this.heightWater = new Integer[this.cupsNum];
        System.out.println("请输入各个水杯容量：");
        Scanner scanner = new Scanner(System.in); 
        for(int i = 0;i < this.cupsNum; i++){
            System.out.println("请输入第"+i+"个水杯的容量：");
            this.heightWater[i] = scanner.nextInt();
        }

    }

    public void setOriWater(){
        this.currectSum = 0;
        this.oriWater = new Integer[this.cupsNum];
        this.currectNum = new Integer[this.cupsNum];
        System.out.println("请输入各个水杯的初始水量：");
        Scanner scanner = new Scanner(System.in); 
        for(int i = 0;i < this.cupsNum; i++){
            System.out.println("请输入第"+i+"个水杯的初始水量：");
            this.oriWater[i] = scanner.nextInt();
            this.currectNum[i] = this.oriWater[i];
            this.currectSum += this.currectNum[i];
        }
    
    }

    public void setVisitList(){
        this.visitAllList = new ArrayList<>();
        this.visitNoRepeatList = new ArrayList<>();
        this.currectList = new LinkedList<>();
    }

    /*
     * 广度优先，每一层加入到队列中，然后去循环队列，直到队列为空
     * 分为三种情况，自己加水，自己减水，把自己的水倒入别的杯子，别的杯子依次加水
     * 先得确定当前杯子的状况，再来进行操作
     * 每一层需要确定当前的状态,总共有几个水杯就得循环几次
     * 用哈希表来排除重复操作
     * 传入当前处理的节点，传入当前节点所在的全部节点的坐标
     */
    public void bsfForCups(int sumVisited){
        //队列首元素出队
        this.currectNum = this.currectList.poll();
        //从当前情况的每一杯水开始
        for(int i = 0; i < this.cupsNum; i++){
            //自己加水
            if(this.currectNum[i]!=this.heightWater[i]){
                Integer []newNode = this.currectNum.clone();
                newNode[i] = this.heightWater[i];
                //如果加了水之后的情况未存在在已经经历过的情况中，就把他加入我们的循环中
                if(!containsValue(newNode)){
                    Integer[] newAllNode = new Integer[this.cupsNum+1];
                    Integer[] newListNode = new Integer[this.cupsNum+1];
                    for(int j=0;j<this.cupsNum;j++){
                        newAllNode[j] = newNode[j];
                        newListNode[j] = newNode[j];
                        System.out.print(newAllNode[j]+" ");
                    }
                    System.out.println();
                    //自己父结点的下标
                    newAllNode[this.cupsNum] = sumVisited;
                    //自己的下标
                    this.currectTimes++;
                    newListNode[this.cupsNum] = this.currectTimes;

                    this.visitNoRepeatList.add(newNode);
                    this.currectList.offer(newListNode);
                    this.visitAllList.add(newAllNode);
                }

            }
            //自己倒空
            if(this.currectNum[i] > 0){
                Integer []newNode = this.currectNum.clone();
                newNode[i] = 0;
                //如果倒了水之后的情况未存在在已经经历过的情况中，就把他加入我们的循环中
                if(!containsValue(newNode)){
                    Integer[] newAllNode = new Integer[this.cupsNum+1];
                    Integer[] newListNode = new Integer[this.cupsNum+1];
                    for(int j=0;j<this.cupsNum;j++){
                        newAllNode[j] = newNode[j];
                        newListNode[j] = newNode[j];
                        System.out.print(newAllNode[j]+" ");
                    }
                    System.out.println();
                    //自己父结点的下标
                    newAllNode[this.cupsNum] = sumVisited;
                    //自己的下标
                    this.currectTimes++;
                    newListNode[this.cupsNum] = this.currectTimes;

                    this.visitNoRepeatList.add(newNode);
                    this.currectList.offer(newListNode);
                    this.visitAllList.add(newAllNode);
                }

            }
            //把自己的水依次倒给其他节点
            if(this.currectNum[i] > 0){
                for(int k = 0;k < this.cupsNum;k++){
                    if(k==i){
                        continue;
                    }
                    else{
                       //倒到别的杯子中，先判定别的杯子中的剩余水量，如果为零就下一个，如果大于等于我当前杯水量就全倒进去，如果小于当前杯水量就倒满，剩下的留在杯中
                        int lastWater = this.heightWater[k] - this.currectNum[k];
                        if(lastWater == 0){
                            continue;
                        }
                        else if(lastWater >= this.currectNum[i]){
                            Integer []newNode = this.currectNum.clone();
                            newNode[k] += newNode[i];
                            newNode[i] = 0;
                            //如果倒了水之后的情况未存在在已经经历过的情况中，就把他加入我们的循环中
                            if(!containsValue(newNode)){
                                Integer[] newAllNode = new Integer[this.cupsNum+1];
                                Integer[] newListNode = new Integer[this.cupsNum+1];
                                for(int j=0;j<this.cupsNum;j++){
                                    newAllNode[j] = newNode[j];
                                    newListNode[j] = newNode[j];
                                    System.out.print(newAllNode[j]+" ");
                                }
                                System.out.println();
                                //自己父结点的下标
                                newAllNode[this.cupsNum] = sumVisited;
                                //自己的下标
                                this.currectTimes++;
                                newListNode[this.cupsNum] = this.currectTimes;

                                this.visitNoRepeatList.add(newNode);
                                this.currectList.offer(newListNode);
                                this.visitAllList.add(newAllNode);
                            }
                            continue;
                        }
                        else{
                            Integer []newNode = this.currectNum.clone();
                            newNode[k] = this.heightWater[k];
                            newNode[i] -= lastWater;
                            //如果倒了水之后的情况未存在在已经经历过的情况中，就把他加入我们的循环中
                            if(!containsValue(newNode)){
                                Integer[] newAllNode = new Integer[this.cupsNum+1];
                                Integer[] newListNode = new Integer[this.cupsNum+1];
                                for(int j=0;j<this.cupsNum;j++){
                                    newAllNode[j] = newNode[j];
                                    newListNode[j] = newNode[j];
                                    System.out.print(newAllNode[j]+" ");
                                }
                                System.out.println();
                                //自己父结点的下标
                                newAllNode[this.cupsNum] = sumVisited;
                                //自己的下标
                                this.currectTimes++;
                                newListNode[this.cupsNum] = this.currectTimes;

                                this.visitNoRepeatList.add(newNode);
                                this.currectList.offer(newListNode);
                                this.visitAllList.add(newAllNode);
                            }
                            continue;
                        }
                    }
                }
            }

        }

        //如果队空就返回
        if(this.currectList.isEmpty()){
            return;
        }else{
            bsfForCups(this.currectList.element()[this.cupsNum]);
        }

    }



    public void beginRecycle(){
        if(this.currectSum == this.finalWater){
            System.out.println("当前的水量已满足您的要求，共经过0次倒水操作！");
            return;
        }
        Integer[] firstNode = new Integer[this.cupsNum+1];
        Integer[] firstListNode = new Integer[this.cupsNum+1];
        Integer[] firstVisitNode = new Integer[this.cupsNum];
        for(int i=0;i<this.cupsNum;i++){
            firstNode[i] = this.oriWater[i];
            firstListNode[i] = this.oriWater[i];
            firstVisitNode[i] = this.oriWater[i];
        }
        //初始状态的父结点置为-1,后续节点父结点记录为数组下标
        firstNode[this.cupsNum] = -1;
        //初始队列的首元素下标为0
        firstListNode[this.cupsNum] = 0;
        this.visitAllList.add(firstNode);
        this.visitNoRepeatList.add(firstVisitNode);
        this.currectList.offer(firstVisitNode);

        //把第一个节点传进去，然后开始广度优先遍历
        this.currectTimes=0;
        bsfForCups(0);

        getRode();
    }

    private boolean containsValue(Integer [] newNode){
        for(int i=0;i<this.visitNoRepeatList.size();i++){
            for(int j=0;j<this.cupsNum;j++){
                if(newNode[j]!=this.visitNoRepeatList.get(i)[j]){
                    break;
                }
                if(j == this.cupsNum - 1){
                    return true;
                }
            }
        }

        return false;
    }

    public int countNum(Integer[] node){
        int sum = 0;
        for(int i=0;i<this.cupsNum;i++){
            sum += node[i];
        }
        return sum;
    }

    public void getRode(){
        ArrayList<ArrayList<Integer[]>> Rodes = new ArrayList<>();

        for(int i = this.visitAllList.size()-1;i >= 0; i--){
                if(countNum(this.visitAllList.get(i)) == this.finalWater){
                    ArrayList<Integer[]> newArray = new ArrayList<>();
                    Integer []newRode = this.visitAllList.get(i);
                    newArray.add(newRode);
                    while(true){
                        if(newRode[this.cupsNum] == 0){
                            break;
                        }
                        else{
                            newRode = this.visitAllList.get(newRode[this.cupsNum]);
                            newArray.add(newRode);
                        }
                    };
                    Rodes.add(newArray);
                }
        }
        System.out.println("--------------方案展示----------------");
        minRode = new ArrayList<ArrayList<Integer[]>>();
        for(int i=0;i<Rodes.size();i++){
            ArrayList<Integer[]> newRode = new ArrayList<>();
            for(int j=0;j<Rodes.get(i).size();j++){
                Integer[] newNode = new Integer[this.cupsNum];
                for(int k=0;k<this.cupsNum;k++){
                    System.out.print(Rodes.get(i).get(j)[k]+" ");
                    newNode[k] = Rodes.get(i).get(j)[k];
                }
                newRode.add(newNode);
                System.out.print("<===");
            }
            newRode.add(this.oriWater);
            minRode.add(newRode);
            for(int m = 0;m<this.cupsNum;m++){
                System.out.print(this.oriWater[m]+" ");    
            }
            System.out.println("");
        }
        if(Rodes.size()==0){
            System.out.println("无法满足您的要求！");
        }
        
    }



}

