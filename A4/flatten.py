'''
In Standard ML, the return type of a variable (which in our case is a function) has to be decided at compile time 
because of it's strict static type definitions. 
What this means is that the input and output types of a function have to be known before we call it.

	val flatten = fn : 'a list list -> string list
	val flatten3 = fn : 'a list list list -> string list

This stops us from writing a single function to effectively handle flattening of multi-level lists.
If the user inputs a list with multiple levels say of type 'a list list list, he would never be able to flatten it 
because it would throw a type error.	
'''

l = [[[1,2,3], [4]], [[]], [[5,6,7]]]
ans = []

def flatten(main_list):
	for l1_list in main_list:
		if isinstance(l1_list,list): 			#check whether the item is a list
			for sub_list in flatten(l1_list): 	#recursively flatten the sub-lists and compute them
				yield from sub_list		
		else: 									#yield element if not a list
			yield l1_list

ans = [x for x in flatten(l)]
print("Ans is", ans)
