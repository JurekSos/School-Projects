/**
  Implements the tree for storing records
  */
class RecordBST{
	//PART 1 CODE
	Node root = null;

	/**
	  Prints the tree in-order
	  */
	public void print(){
		printR(root);
	}

	/**
	  Recursively moves through the tree, printing it in order
	  */
	protected void printR(Node subtreeRoot){
		if(subtreeRoot != null){
			printR(subtreeRoot.left);
			System.out.println(subtreeRoot.value.toString());
			printR(subtreeRoot.right);
		}
	}

	/**
	  Inserts the record into the tree
	  @param Record r, the Record to insert
	  */
	public void insert(Record r){
		root = insertR(r, root);
	}

	/**
	  Recursively moves through the tree to insert a record
	  @param Record r, the Record to insert
	  @param Node subtreeRoot, the root of the current subtree
	  */
	protected Node insertR(Record r, Node subtreeRoot){
		if(subtreeRoot == null){
			return new Node(r);
		}else{
			if(r.compareTo(subtreeRoot.value) < 0){
				subtreeRoot.left = insertR(r, subtreeRoot.left);	
			}else{
				subtreeRoot.right = insertR(r, subtreeRoot.right);
			}
			return subtreeRoot;
		}
	}

	/**
	  Removes a record from the tree
	  @param Record r, the record to remove
	  */
	public void remove(Record r){
		root = removeR(r, root);
	}

	/**
	  Recursively moves through the tree to remove a node
	  @param Record r, the record to remove
	  @param Node parent, the parent of the node being considered for deletion
	  */
	protected Node removeR(Record r, Node subtreeRoot){
		if(subtreeRoot == null){
			return null;
		}

		if(r.compareTo(subtreeRoot.value) == 0){
			if(subtreeRoot.left == null && subtreeRoot.right == null){
				//No children
				subtreeRoot = null;
			}else if(subtreeRoot.left == null){
				//One child on right
				subtreeRoot = subtreeRoot.right;
			}else if(subtreeRoot.right == null){
				//One child on left
				subtreeRoot = subtreeRoot.left;
			}else{
				//Two children
				//Find minimum value in right subtree
				Record minInRightSubtree;
				Node temp = subtreeRoot.right;
				Node tempParent = subtreeRoot;
				boolean tempIsLeftOfParent = false;
				while(temp.left != null){
					tempParent = temp;
					temp = temp.left;
					tempIsLeftOfParent = true;
				}
				minInRightSubtree = temp.value;

				//Remove the minimum node in the left 
				if(tempIsLeftOfParent){
					tempParent.left = removeR(minInRightSubtree, tempParent.left);
				}else{
					tempParent.right = removeR(minInRightSubtree, tempParent.right);
				}

				//Update value in current node
				subtreeRoot.value = minInRightSubtree;
			}
		}else if(r.compareTo(subtreeRoot.value) < 0){
			//Record to remove would be in left subtree
			subtreeRoot.left = removeR(r, subtreeRoot.left);
		}else{
			//Record to remove would be in right subtree
			subtreeRoot.right = removeR(r, subtreeRoot.right);
		}

		return subtreeRoot;
	}

	/**
	  Checks if a record is in the tree
	  @param Record r, the record to search for
	  @return true if the record is stored in the tree, false otherwise
	  */
	public boolean search(Record r){
		return searchR(r, root);
	}

	/**
	  Recursively moves though the tree to check if a record is in the tree
	  @param Record r, the record to search for
	  @param Node subtreeRoot, the root of the current subtree being checked
	  @return true if the record is stored in the tree, false otherwise
	  */
	protected boolean searchR(Record r, Node subtreeRoot){
		if(subtreeRoot == null){
			return false;
		}else{
			if(r.compareTo(subtreeRoot.value) == 0){
				return true;
			}else if(r.compareTo(subtreeRoot.value) < 0){
				return searchR(r, subtreeRoot.left);
			}else{
				return searchR(r, subtreeRoot.right);
			}
		}
	}

	/**
	  Gets the height of the tree
	  @return the height of the tree
	  */
	public int getHeight(){
		return root.getHeight();
	}

	/**
	  Gets the minimum value in the tree
	  @return the value of the leftmost node in the tree
	  */
	public Record getMinimum(){
		Node leftmost = root;
		while(leftmost.left != null){
			leftmost = leftmost.left;
		}
		return leftmost.value;
	}

	/**
	  Gets the maximum value in the tree
	  @return the value of the rightmost node in the tree
	  */
	public Record getMaximum(){
		Node rightmost = root;
		while(rightmost.right != null){
			rightmost = rightmost.right;
		}
		return rightmost.value;
	}

	/**
	  Implements the nodes used in the tree structure
	  */
	protected class Node{
		public Record value;
		public Node left;
		public Node right;

		//PART 3 - PDF says both public and protected, I decided to go for protected with a public getter method.
		//PDF also says to update the existing class which is why this is changed here and not done in AVLRecords
		protected int balanceFactor;

		public Node(Record v){
			value = v;
			left = right = null;

			balanceFactor = 0;
		}

		/**
		  @return the balance factor of the tree
		  */
		public int getBalanceFactor(){
			return balanceFactor;
		}

		/**
		  Updates the balance factor of the tree
		  */
		public void updateNode(){
			int leftSubTreeHeight = 0;
			int rightSubTreeHeight = 0;
			
			if(left != null){
				leftSubTreeHeight = left.getHeight();
			}
			if(right != null){
				rightSubTreeHeight = right.getHeight();
			}

			balanceFactor =  leftSubTreeHeight - rightSubTreeHeight;
		}

		/**
		  @return the height of the node
		  */
		public int getHeight(){
			int lVal = 0;
			int rVal = 0;

			if(left != null){
				lVal = left.getHeight() + 1;
			}
			if(right != null){
				rVal = right.getHeight() + 1;
			}

			if(lVal > rVal){
				return lVal;
			}else{
				return rVal;
			}
		}
	}

	//PART 2 CODE

	/**
	  Prints every Record in the tree that matches the passed genre and has a release date within the passed range
	  @param String g, the genre to match
	  @param int earliest, the lower bound on the year range, inclusive
	  @param int latest, the upper bound on the year range, inclusive
	  */
	public void printGenreWithYearRange(String g, int earliest, int latest){
		printGenreWithYearRangeR(g, earliest, latest, root);
	}


	/**
	  Recursively moves through the tree to prints every Record in the tree that matches the passed genre and has a release date within the passed range
	  @param String g, the genre to match
	  @param int earliest, the lower bound on the year range, inclusive
	  @param int latest, the upper bound on the year range, inclusive
	  @param Node subtreeRoot, the Node which is the root of the current subtree
	  */
	protected void printGenreWithYearRangeR(String g, int earliest, int latest, Node subtreeRoot){
		if(subtreeRoot != null){
			Record val = subtreeRoot.value;
			boolean genreMatches = val.getGenre().equals(g);
			boolean yearIsWithinRange = earliest <= val.getYear() && val.getYear() <= latest;

			if(genreMatches){
				if(yearIsWithinRange){
					printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.left);
					System.out.println(val.toString());
					printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.right);
				}else{
					//Year is outside the range, so it is either too high or too low
					boolean yearIsTooHigh = val.getYear() > latest;
					if(yearIsTooHigh){
						//Current record has year too high, lower year records with same genre would be to the left
						printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.left);
					}else{
						//year is too low, so go to right
						printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.right);
					}
				}
			}else{
				//Genre doesn't match, so go to subtree which would contain records that do match	
				boolean genreIsNotOnLeft = val.getGenre().compareTo(g) < 0;
				if(genreIsNotOnLeft){
					printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.right);
				}else{
					printGenreWithYearRangeR(g, earliest, latest, subtreeRoot.left);
				}
			}
		}
	}

	/**
	  Prints every Record in the tree which matches the passed genre
	  @param String g, the genre to match with
	  */
	public void printGenre(String g){
		printGenreWithYearRange(g, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	/**
	  Prints every Record in the tree which matches the passed genre and was released after the passed year
	  @param String g, the genre to match with
	  @param int earliest, the earliest year for which to print records, inclusive
	  */
	public void printGenreAfterYear(String g, int earliest){
		printGenreWithYearRange(g, earliest, Integer.MAX_VALUE);
	}

	/**
	  Prints every Record in the tree which matches the passed genre and was released before the passed year
	  @param String g, the genre to match with
	  @param int latest, the latest year for which to print records, inclusive
	  */
	public void printGenreBelowYear(String g, int latest){
		printGenreWithYearRange(g, Integer.MIN_VALUE, latest);
	}
}
