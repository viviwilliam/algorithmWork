import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import java.util.Queue;
import java.util.LinkedList; 

public class pourWater{

    public static void main(String[] args) {
        Cups cups = new Cups();
        cups.beginRecycle();
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
        for(int i=0;i<Rodes.size();i++){
            for(int j=0;j<Rodes.get(i).size();j++){
                for(int k=0;k<this.cupsNum;k++){
                    System.out.print(Rodes.get(i).get(j)[k]+" ");
                }
                System.out.print("<===");
            }
            System.out.println("0 0 0");
        }
        if(Rodes.size()==0){
            System.out.println("无法满足您的要求！");
        }
        
    }



}

