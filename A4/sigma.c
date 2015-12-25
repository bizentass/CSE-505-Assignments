#include<stdio.h>

int i, j, k;

int sigma(int *k, int low, int high, int expr()) {
	int sum = 0;
	for (*k=low; *k<=high; (*k)++) {
		sum = sum + expr();	
	}
	return sum;
}

int thunkk(){
	return j*k-i; 
}

int thunkj(){
	return (i + j) * sigma(&k, 0, 4, thunkk);
}

int thunki(){
	return i * sigma(&j, 0, 4, thunkj);
}

int main(){
	int result =  sigma(&i, 0, 4, thunki);
	printf("%d \n", result);
	return 0;
}


