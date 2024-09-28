#include <iostream>
#include <vector>
#include <algorithm>
#include <climits>
#include <cstdlib>
#include <ctime>

class UnionFind {
private:
	std::vector<int> parent;
	std::vector<int> rank;
	std::vector<int> size;

public:
	UnionFind(int n) : parent(n), rank(n, 0), size(n, 1) {
		for (int i = 0; i < n; ++i) {
			parent[i] = i;
		}
	}

	int find(int x) {
		/*if (parent[x] != x) {
		parent[x] = find(parent[x]); // 路径压缩
		}
		return parent[x];*/
		//不路径压缩
		if (parent[x] == x)
			return x;
		return find(parent[x]);
	}

	bool unionSet(int x, int y) {
		int rootX = find(x);
		int rootY = find(y);
		if (rootX == rootY) {
			return false; // 已经在同一个集合中
		}

		// 按秩合并
		if (rank[rootX] > rank[rootY]) {
			parent[rootY] = rootX;
			size[rootX] += size[rootY];
		}
		else if (rank[rootX] < rank[rootY]) {
			parent[rootX] = rootY;
			size[rootY] += size[rootX];
		}
		else {
			parent[rootY] = rootX;
			size[rootX] += size[rootY];
			rank[rootX]++;
		}
		return true;
	}

	int getSize(int x) {
		return size[find(x)];
	}

	// 用于测试的最大树深和平均树深
	int getMaxDepth(int x) {
		int depth = 1;
		while (parent[x] != x) {
			depth++;
			x = parent[x];
		}
		return depth;
	}
	double getMaxTree() {
		double MaxDepth = 0;
		for (int i = 0; i < parent.size(); ++i) {
			int height = getMaxDepth(i);
			if (height > MaxDepth)
				MaxDepth = height;
		}
		return MaxDepth;
	}
	double getAverageDepth() {
		double totalDepth = 0;
		for (int i = 0; i < parent.size(); ++i) {
			totalDepth += getMaxDepth(i);
		}
		return totalDepth / parent.size();
	}
};

int main() {
	int n = 10000; // 节点数量
	int m = 1000000; // 关系数量
	srand((unsigned)time(NULL)); // 初始化随机数生成器

	// 测试代码
	for (int z = 0; z < 100; z++) {
		UnionFind uf(n);
		std::vector<std::vector<int>> nums(n, std::vector<int>(n, 0));
		for (int i = 0; i < m; ++i) {
			int x = rand() % n;
			int y = rand() % n;
			if (x == y || nums[x][y] == 1) {
				i--;
				continue;
			}
			nums[x][y] = 1;
			nums[y][x] = 1;
			
		}
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if (nums[i][j]==1)
					uf.unionSet(i, j);
			}
		}

		// 输出最大树深和平均树深
		std::cout << z << " Max depth: " << uf.getMaxTree() << std::endl;
		std::cout << z << " Average depth: " << uf.getAverageDepth() << std::endl;
	}

	return 0;
}
