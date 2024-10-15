//作业2
/*
分治法实现平面最接近点对 
点对最小距离问题：纵横坐标都在0和1之间，点个数100万
*/ 
#include<iostream>
#include<vector>
#include<algorithm> 
#include<fstream>
#include<string>
#include<tuple>
#include<ctime>
#include<sstream>
#include<cmath>
#include<stdlib.h>
using namespace std;

#define MAXRANGE  800000


struct point{
	double siteX;
	double siteY;
	int index;
}; 

//二分法找距离Y最近的坐标
int findMinY(vector<point>&points,double Y,int low,int high){
	int site;
	//如果只剩最后一个点了就输出 
	if(high==low){
		return high;
	}
	int mid = low + ((high - low) >> 1);//算术右移==除2操作
	if(points[mid].siteY == Y){
		return mid;
	} else if(points[mid].siteY > Y){
		site = findMinY(points,Y,low,mid-1);
	}else{
		site = findMinY(points,Y,mid+1,high);
	}
	
	return site;
} 

// 归并两个已排序的子数组
void merge(std::vector<point>& points, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;

    // 创建临时数组
    std::vector<point> L(n1);
    std::vector<point> R(n2);

    // 复制数据到临时数组
    for (int i = 0; i < n1; i++) {
        L[i] = points[left + i];
    }
    for (int j = 0; j < n2; j++) {
        R[j] = points[mid + 1 + j];
    }

    // 合并临时数组回原数组
    int i = 0, j = 0, k = left;
    while (i < n1 && j < n2) {
        if (L[i].siteX <= R[j].siteX) {
            points[k] = L[i];
            i++;
        } else {
            points[k] = R[j];
            j++;
        }
        k++;
    }

    // 复制 L 剩余的元素
    while (i < n1) {
        points[k] = L[i];
        i++;
        k++;
    }

    // 复制 R 剩余的元素
    while (j < n2) {
        points[k] = R[j];
        j++;
        k++;
    }
}

// 归并排序
void sort(std::vector<point>& points, int left, int right) {
    if (left < right) {
        // 找到中点
        int mid = left + (right - left) / 2;

        // 递归地排序两半
        sort(points, left, mid);
        sort(points, mid + 1, right);

        // 合并两半
        merge(points, left, mid, right);
    }
}
 



//计算点对距离
double countDistense(double x1,double x2,double y1,double y2){
	return sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
} 



/*
先按横坐标再按纵坐标将坐标进行排序
分治的访问左边和右边 
*/
tuple<double,int,int> divide(vector<point>&points,int low,int high){
	double Min;
	int A,B;
	
	//判定只有一个数据和两个数据的情况 
	if(high-low<1){
		return std::make_tuple(MAXRANGE,low,low);
	}
	else if(high-low == 1){
		return std::make_tuple(countDistense(points[high].siteX,points[low].siteX,points[high].siteY,points[low].siteY),points[low].index,points[high].index);
	}
	else{
		int mid = low + ((high - low) >> 1);//算术右移==除2操作
		//进行递归
		double leftMin;
		int leftA,leftB;
		tie(leftMin,leftA,leftB) = divide(points,low,mid);
		double rightMin;
		int rightA,rightB;
		tie(rightMin,rightA,rightB) = divide(points,mid+1,high);
		if(leftMin < rightMin){
			Min = leftMin;
			A = leftA;
			B = leftB;
		}
		else{
			Min = rightMin;
			A = rightA;
			B = rightB;
		}
		//分割当前区域为两部分,把选择的中心点放在左边,然后对右侧进行排序,然后从中心这个点开始往左边开始遍历,
		//如果左边的点距离中心这个点的横坐标距离大于了之前最小的值,就结束遍历 
		//每个点通过二分查找法去找右侧最近的一个纵坐标点,如果这个纵坐标的值依然比最小值大,就退出,
		//然后从这个纵坐标开始计算两者之间的欧氏距离,然后根据这个纵坐标的点往上,往下各找三个,凑出七个点 
		//最后输出目前的最小的距离和两个点的顺序 
 		vector<point> rightPoints(high-mid);
 		int site = 0;
		for(int i=mid+1;i<=high;i++){
			rightPoints[site].index = points[i].index; 
			rightPoints[site].siteX = points[i].siteX; 
			rightPoints[site].siteY = points[i].siteY; 
		}
		
		sort(rightPoints, 0, rightPoints.size() - 1);
		int findSite = mid; //从中间开始找 
		while(1){
			int findY = findMinY(rightPoints,points[findSite].siteY,0,high-mid-1);
			//每个点通过二分查找法去找右侧最近的一个纵坐标点,如果这个纵坐标的值依然比最小值大,就退出,
			//然后从这个纵坐标开始计算两者之间的欧氏距离,然后根据这个纵坐标的点往上,往下各找三个,凑出七个点 
			//最后输出目前的最小的距离和两个点的顺序 
			if (abs(rightPoints[findY].siteY - points[findSite].siteY) >= Min){
				break;
			}
			else{
				double sum = countDistense(rightPoints[findY].siteX, points[findSite].siteX, rightPoints[findY].siteY, points[findSite].siteY);
				if(sum<Min){
					Min = sum;
					A = points[findSite].index;
					B = rightPoints[findY].index;
				}
				for(int i=1;i<=3;i++){
					if(findY+i>high-mid-1){
						break;
					}
					if (abs(rightPoints[findY + i].siteY - points[findSite].siteY) >= Min){
						break;
					}else{
						double sum = countDistense(rightPoints[findY + i].siteX, points[findSite].siteX, rightPoints[findY + i].siteY, points[findSite].siteY);
						if(sum<Min){
							Min = sum;
							A = points[findSite].index;
							B = rightPoints[findY + i].index;
						}
					}
				}
				for(int i=1;i<=3;i++){
					if(findY-i<0){
						break;
					}
					if (abs(rightPoints[findY - i].siteY - points[findSite].siteY) >= Min){
						break;
					}else{
						double sum = countDistense(rightPoints[findY - i].siteX, points[findSite].siteX, rightPoints[findY - i].siteY, points[findSite].siteY);
						if(sum<Min){
							Min = sum;
							A = points[findSite].index;
							B = rightPoints[findY - i].index;
						}
					}
				}
			}
			findSite--;
			if(findSite<0){
				break;
			}else if(abs(points[findSite].siteX-points[mid].siteX)>=Min){
				break;
			}
		}
		
		
		
		
		
	}
	
	return make_tuple(Min,A,B);
	//根据中间位置将点集分为两部分
	//右侧点集按照纵坐标排序 
	 
} 


int main(){	
	srand((unsigned)time(NULL)); 
	while(1){
		std::vector<double> points(MAXRANGE,0);//点
		//初次读入的点 
		vector<double> pointX(MAXRANGE,0);
		vector<double> pointY(MAXRANGE,0);
		//筛选后剩下的点 
		vector<double> selected_pointX(MAXRANGE,0);
		vector<double> selected_pointY(MAXRANGE,0);
	 	string path;
	 	int divA,divB;
	 	
	 	cout<<"Data file path: ";
	 	cin>>path;

	    cout<<"Screening divisors:";
	    cin>>divA>>divB;
	 	std::ifstream file(path);

		clock_t start0;
		clock_t finish0;
		
		start0 = clock();
	    if (!file.is_open()) {
	        std::cerr << "无法打开文件" << std::endl;
	        return 1;
	    }
	    
	    std::string line;
	    // 按行读取，直到到达文件末尾
	    int index = 0;
	    while (std::getline(file, line)) {
	        // 输出读取的行
	        std::istringstream iss(line);
		    std::vector<std::string> sites(2,"");
		    std::string site;
		    int m=0;
		    while (std::getline(iss, site, ',')) {
		        sites[m] = site;
		        m++;
		    }
	        pointX[index] = stod(sites[0]);
	        pointY[index] = stod(sites[1]);
	        index++;
	    }
	    file.close(); // 关闭文件


	    //筛选点位
		index = 0; 
		
	    for(int i=0;i<MAXRANGE;i++){
			if(i%divA==0||i%divB==0){
				points[index] = i;
				selected_pointX[index] = pointX[i];
				selected_pointY[index] = pointY[i];
				index++;
			}
		}
		std::vector<point> selected_points(index);
		for(int i=0;i<index;i++){
			selected_points[i].index = points[i];
			selected_points[i].siteX= selected_pointX[i];
			selected_points[i].siteY = selected_pointY[i];
		}

	 	sort(selected_points, 0, selected_points.size() - 1);	

		double Min;
		int A,B;

		tie(Min,A,B) = divide(selected_points,0,index-1);
		finish0 = clock();
		clock_t elapsed = finish0 - start0;
		// 将时钟周期数转换为毫秒
	    double elapsedMilliseconds = static_cast<double>(elapsed) / (CLK_TCK / 1000);
		cout<<"Screened-out coordinate count: "<<index<<endl;		
		cout<<A<<"~~"<<B<<"::   "<<Min<<endl;
		cout<<"Divide and Conquer time: "<<elapsedMilliseconds<<"ms"<<endl;
		
	}

	return 0;
}
