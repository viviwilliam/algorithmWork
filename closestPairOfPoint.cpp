//��ҵ2
/*
���η�ʵ��ƽ����ӽ���� 
�����С�������⣺�ݺ����궼��0��1֮�䣬�����100��
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

//���ַ��Ҿ���Y���������
int findMinY(vector<point>&points,double Y,int low,int high){
	int site;
	//���ֻʣ���һ�����˾���� 
	if(high==low){
		return high;
	}
	int mid = low + ((high - low) >> 1);//��������==��2����
	if(points[mid].siteY == Y){
		return mid;
	} else if(points[mid].siteY > Y){
		site = findMinY(points,Y,low,mid-1);
	}else{
		site = findMinY(points,Y,mid+1,high);
	}
	
	return site;
} 

// �鲢�����������������
void merge(std::vector<point>& points, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;

    // ������ʱ����
    std::vector<point> L(n1);
    std::vector<point> R(n2);

    // �������ݵ���ʱ����
    for (int i = 0; i < n1; i++) {
        L[i] = points[left + i];
    }
    for (int j = 0; j < n2; j++) {
        R[j] = points[mid + 1 + j];
    }

    // �ϲ���ʱ�����ԭ����
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

    // ���� L ʣ���Ԫ��
    while (i < n1) {
        points[k] = L[i];
        i++;
        k++;
    }

    // ���� R ʣ���Ԫ��
    while (j < n2) {
        points[k] = R[j];
        j++;
        k++;
    }
}

// �鲢����
void sort(std::vector<point>& points, int left, int right) {
    if (left < right) {
        // �ҵ��е�
        int mid = left + (right - left) / 2;

        // �ݹ����������
        sort(points, left, mid);
        sort(points, mid + 1, right);

        // �ϲ�����
        merge(points, left, mid, right);
    }
}
 



//�����Ծ���
double countDistense(double x1,double x2,double y1,double y2){
	return sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
} 



/*
�Ȱ��������ٰ������꽫�����������
���εķ�����ߺ��ұ� 
*/
tuple<double,int,int> divide(vector<point>&points,int low,int high){
	double Min;
	int A,B;
	
	//�ж�ֻ��һ�����ݺ��������ݵ���� 
	if(high-low<1){
		return std::make_tuple(MAXRANGE,low,low);
	}
	else if(high-low == 1){
		return std::make_tuple(countDistense(points[high].siteX,points[low].siteX,points[high].siteY,points[low].siteY),points[low].index,points[high].index);
	}
	else{
		int mid = low + ((high - low) >> 1);//��������==��2����
		//���еݹ�
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
		//�ָǰ����Ϊ������,��ѡ������ĵ�������,Ȼ����Ҳ��������,Ȼ�����������㿪ʼ����߿�ʼ����,
		//�����ߵĵ�������������ĺ�������������֮ǰ��С��ֵ,�ͽ������� 
		//ÿ����ͨ�����ֲ��ҷ�ȥ���Ҳ������һ���������,�������������ֵ��Ȼ����Сֵ��,���˳�,
		//Ȼ�����������꿪ʼ��������֮���ŷ�Ͼ���,Ȼ��������������ĵ�����,���¸�������,�ճ��߸��� 
		//������Ŀǰ����С�ľ�����������˳�� 
 		vector<point> rightPoints(high-mid);
 		int site = 0;
		for(int i=mid+1;i<=high;i++){
			rightPoints[site].index = points[i].index; 
			rightPoints[site].siteX = points[i].siteX; 
			rightPoints[site].siteY = points[i].siteY; 
		}
		
		sort(rightPoints, 0, rightPoints.size() - 1);
		int findSite = mid; //���м俪ʼ�� 
		while(1){
			int findY = findMinY(rightPoints,points[findSite].siteY,0,high-mid-1);
			//ÿ����ͨ�����ֲ��ҷ�ȥ���Ҳ������һ���������,�������������ֵ��Ȼ����Сֵ��,���˳�,
			//Ȼ�����������꿪ʼ��������֮���ŷ�Ͼ���,Ȼ��������������ĵ�����,���¸�������,�ճ��߸��� 
			//������Ŀǰ����С�ľ�����������˳�� 
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
	//�����м�λ�ý��㼯��Ϊ������
	//�Ҳ�㼯�������������� 
	 
} 


int main(){	
	srand((unsigned)time(NULL)); 
	while(1){
		std::vector<double> points(MAXRANGE,0);//��
		//���ζ���ĵ� 
		vector<double> pointX(MAXRANGE,0);
		vector<double> pointY(MAXRANGE,0);
		//ɸѡ��ʣ�µĵ� 
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
	        std::cerr << "�޷����ļ�" << std::endl;
	        return 1;
	    }
	    
	    std::string line;
	    // ���ж�ȡ��ֱ�������ļ�ĩβ
	    int index = 0;
	    while (std::getline(file, line)) {
	        // �����ȡ����
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
	    file.close(); // �ر��ļ�


	    //ɸѡ��λ
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
		// ��ʱ��������ת��Ϊ����
	    double elapsedMilliseconds = static_cast<double>(elapsed) / (CLK_TCK / 1000);
		cout<<"Screened-out coordinate count: "<<index<<endl;		
		cout<<A<<"~~"<<B<<"::   "<<Min<<endl;
		cout<<"Divide and Conquer time: "<<elapsedMilliseconds<<"ms"<<endl;
		
	}

	return 0;
}
