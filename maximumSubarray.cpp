//��ҵ1
/*
���η�ʵ������ӿ�� 
*/ 
#include<iostream>
#include<vector>
#include<fstream>
#include<string>
#include<tuple>
#include <ctime>


using namespace std;

#define MAXSIZE 1000000
#define INT_MIN -9999 
//Ѱ�ҿ��м����ֵ 
tuple<int,int,int> FindMaxCrossingSubarray(vector<int> &A,int low,int mid,int high){
	int leftSum = INT_MIN,leftMax = mid;
	int sum = 0;
	for(int i = mid;i>=low;--i){
		sum+=A[i];
		if(sum > leftSum){
			leftSum = sum;
			leftMax = i;
		}
	}
	
	int rightSum = INT_MIN,rightMax = mid + 1;
	sum = 0;
	for(int j = mid + 1; j <= high; ++j){
		sum+=A[j];
		if(sum > rightSum){
			rightSum  = sum;
			rightMax = j;
		}
	}
	
	return std::make_tuple(leftMax, rightMax, leftSum + rightSum);
}

tuple<int,int,int> FindMaximumSubarray(vector<int>&A,int low,int high){
//	cout<< "Input: low="<<low<<" high=" <<high<<endl;
	if(low == high) {
		return std::make_tuple(low,high,A[low]);	
	}
	else{
		int mid = low + ((high - low) >> 1);//��������==��2����
		int leftLow,leftHigh,leftSum;
		tie(leftLow,leftHigh,leftSum) = FindMaximumSubarray(A,low,mid);
//		cout<< "left side:low="<<leftLow<<" high="<<leftHigh<<" sum="<<leftSum<<endl;
		
		int rightLow,rightHigh,rightSum;
		tie(rightLow,rightHigh,rightSum) = FindMaximumSubarray(A,mid + 1,high);
//		cout<< "right side:low="<<rightLow<<" high="<<rightHigh<<" sum="<<rightSum<<endl;
		
		int crossLow,crossHigh,crossSum;
		tie(crossLow,crossHigh,crossSum) = FindMaxCrossingSubarray(A,low,mid,high);
//		cout<< "cross both side:low="<<crossLow<<" high="<<crossHigh<<" sum="<<crossSum<<endl;
		
		if(leftSum >= rightSum && leftSum >= crossSum) {
            return std::make_tuple(leftLow, leftHigh, leftSum);
        } else if(rightSum >= leftSum && rightSum >= crossSum) {
            return std::make_tuple(rightLow, rightHigh, rightSum);
        } else {
            return std::make_tuple(crossLow, crossHigh, crossSum);
        }
	}
	
} 





int main(){
	while(1){
		srand((unsigned)time(NULL)); // ��ʼ�������������
		vector<int> nums(MAXSIZE,0);
	string rode;
	cout<<"please input file rode:";
	cin>>rode;
	std::ifstream file(rode);
//	std::ifstream file("E:\\postgraduate\\�γ�\\�㷨\\��������ҵʾ��\\����ӿ��\\values1000000.txt"); // ���ļ�
    if (!file.is_open()) {
        std::cerr << "�޷����ļ�" << std::endl;
        return 1;
    }

    std::string line;
    // ���ж�ȡ��ֱ�������ļ�ĩβ
    int site = 0;
    while (std::getline(file, line)) {
        // �����ȡ����
        nums[site] = stoi(line);
        site++;
    }
    file.close(); // �ر��ļ�

	int Low,High,Sum;
	int orign,final;
	cout<<"please input orign:";
	cin>>orign;
	cout<<"please input final:";
	cin>>final;
	
	clock_t start0;
	clock_t finish0;
	start0 = clock();
	tie(Low,High,Sum) = FindMaximumSubarray(nums,orign,final);
	finish0 = clock();
	clock_t elapsed = finish0 - start0;
	// ��ʱ��������ת��Ϊ����
    double elapsedMilliseconds = static_cast<double>(elapsed) / (CLK_TCK / 1000);
	cout<<"======================="<<endl;
	cout<<"MinLow="<<Low<<" MinHigh="<<High<<" Sum="<<Sum<<" time:"<<elapsedMilliseconds<<"ms"<<endl;
	cout<<"======================="<<endl;
	}
	
	
	
	return 0;
	
}
