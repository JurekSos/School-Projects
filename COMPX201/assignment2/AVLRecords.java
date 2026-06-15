/**
  Extends the RecordBST class to make it an AVL tree for self balancing
  */
class AVLRecords extends RecordBST{
	/**
	  Inserts a record into the tree
	  @param Record r, the Record to insert
	  */
	public void insert(Record r){
		super.insert(r);
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
			
			//Check balance factor after insertion
			subtreeRoot.updateNode();

			if(subtreeRoot.getBalanceFactor() > 1){
				//Left or Left-Right imbalance
				//if we inserted the new record to the right of the left subtree then we have a left-right imbalance
				boolean isLeftRight = subtreeRoot.left.value.compareTo(r) < 0;
				if(isLeftRight){
					subtreeRoot.left = rotateLeft(subtreeRoot.left);
				}
				//Now a pure left imbalance, so right rotation to fix
				subtreeRoot = rotateRight(subtreeRoot);
			}else if(subtreeRoot.getBalanceFactor() < -1){
				//Right or Right-Left imbalance
				
				boolean isRightLeft = subtreeRoot.right.value.compareTo(r) > 0;
				if(isRightLeft){
					subtreeRoot.right = rotateRight(subtreeRoot.right);
				}
				//Now pure right imbalance
				subtreeRoot = rotateLeft(subtreeRoot);
			}

			return subtreeRoot;
		}
	}

	/**
	  Removes a record from the tree
	  @param Record r, the record to remove
	  */
	public void remove(Record r){
		super.remove(r);
	}

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
				while(temp.left != null){
					temp = temp.left;
				}
				minInRightSubtree = temp.value;

				//Remove the minimum node in the left 
				subtreeRoot.right = removeR(minInRightSubtree, subtreeRoot.right);

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

		//Check balance and do rotations if necessary
		if(subtreeRoot != null){
			subtreeRoot.updateNode();

			if(subtreeRoot.getBalanceFactor() > 1){
				//Left or Left-Right imbalance
				//if we inserted the new record to the right of the left subtree then we have a left-right imbalance
				boolean isLeftRight = subtreeRoot.left.value.compareTo(r) < 0;
				if(isLeftRight){
					subtreeRoot.left = rotateLeft(subtreeRoot.left);
				}
				//Now a pure left imbalance, so right rotation to fix
				subtreeRoot = rotateRight(subtreeRoot);
			}else if(subtreeRoot.getBalanceFactor() < -1){
				//Right or Right-Left imbalance
				
				boolean isRightLeft = subtreeRoot.right.value.compareTo(r) > 0;
				if(isRightLeft){
					subtreeRoot.right = rotateRight(subtreeRoot.right);
				}
				//Now pure right imbalance
				subtreeRoot = rotateLeft(subtreeRoot);
			}
		}

		return subtreeRoot;
	}

	/**
	  Performs a left rotation on a subtree
	  @param Node grandparent, the root of the subtree to perform a rotation on
	  @return the new root of the subtree.
	  */
	private Node rotateLeft(Node grandparent){
		Node parent = grandparent.right;
		Node child = parent.left;

		parent.left = grandparent;
		grandparent.right = child;

		parent.updateNode();
		grandparent.updateNode();

		return parent;
	}

	/**
	  Performs a right rotation on a subtree
	  @param Node grandparent, the root of the subtree to perform a rotation on
	  @return the new root of the subtree.
	  */
	private Node rotateRight(Node grandparent){
		Node parent = grandparent.left;
		Node child = parent.right;

		parent.right = grandparent;
		grandparent.left = child;

		parent.updateNode();
		grandparent.updateNode();

		return parent;
	}
}
