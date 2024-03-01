/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root; //root of the DLB Trie
  private StringBuilder currentPrefix; //running prefix
  private DLBNode currentNode; //current DLBNode
  //TODO: Add more instance variables as needed
  private int total = 0;

  public AutoComplete() {
      this.root = new DLBNode('\0');
      this.currentPrefix = new StringBuilder();
      this.currentNode = root;
  }



  @Override
  public boolean add(String word) {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'add'");
    if(word == null || word.isEmpty()){
      throw new IllegalArgumentException("Word cant be null or empty");

    } else {
      return addWord(root, word, 0) != null;
    }
  }

  private DLBNode addWord(DLBNode node, String word, int index) {


    if (index == word.length()) {
        if (!node.isWord) {
            node.isWord = true;
            node.size = 1;
            DLBNode ancestor = node.parent;
            while (ancestor != null) {
                ancestor.size++;
                ancestor = ancestor.parent;
            }
        }
        return node;
    }

    char currentChar = word.charAt(index);
    DLBNode child = node.child;
    DLBNode lastChild = null;

    while (child != null) {
        if (child.data == currentChar) {
            return addWord(child, word, index + 1);
        }
        lastChild = child;
        child = child.nextSibling;
    }

    DLBNode newNode = new DLBNode(currentChar);
    newNode.parent = node;
    if (lastChild == null) {
        node.child = newNode;
    } else {
        lastChild.nextSibling = newNode;
        newNode.previousSibling = lastChild;
    }
    return addWord(newNode, word, index + 1);
}


  @Override
  public boolean advance(char c) {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'advance'");
    if (currentNode == null) return false;
   DLBNode temp = currentNode.child;
   while (temp != null) {
       if (temp.data == c) {
           currentNode = temp;
           currentPrefix.append(c);
           return true;
       }
       temp = temp.nextSibling;
   }
   currentNode = null;
   return false;
  }

  @Override
  public void retreat() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'retreat'");
    //System.out.println(currentPrefix);
    if (currentPrefix.length() == 0) throw new IllegalStateException("Current prefix is already empty");
     currentPrefix.deleteCharAt(currentPrefix.length() - 1);
     currentNode = (currentNode != null && currentNode.parent != null) ? currentNode.parent : root;
  }

  @Override
  public void reset() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'reset'");
    currentPrefix.setLength(0);
    currentNode = root;
  }

  @Override
  public boolean isWord() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'isWord'");
    return currentNode != null && currentNode.isWord;
  }

  @Override
  public void add() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'add'");
    if(currentPrefix.length() > 0 && (currentNode == null || !currentNode.isWord)){
      add(currentPrefix.toString());
    }
  }

  @Override
  public int getNumberOfPredictions() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'getNumberOfPredictions'");
    //System.out.println("Value of currentNode is " + Character.isLetter(currentNode.data));
    if(currentNode != null && Character.isLetter(currentNode.data)){
      return currentNode.size;
    } else {
      return 0;
    }
  }

  @Override
  public String retrievePrediction() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'retrievePrediction'");
    if (currentNode == null) return null;
     if (currentNode.isWord) return currentPrefix.toString();
     DLBNode temp = currentNode.child;
     StringBuilder prediction = new StringBuilder(currentPrefix);
     while (temp != null) {
         prediction.append(temp.data);
         if (temp.isWord) {
             return prediction.toString();
         }
         temp = temp.child;
     }
     return null;

  }

  @Override
  public ArrayList<String> retrievePredictions() {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'retrievePredictions'");
    //System.out.println(currentNode.data);
    ArrayList<String> predictions = new ArrayList<>();
    if(currentNode!=null && currentNode.child!=null){

      retrieveAllPredictions(currentNode, new StringBuilder(currentPrefix), predictions);
    }
    return predictions;
  }

  private void retrieveAllPredictions(DLBNode node, StringBuilder currentWord, ArrayList<String> predictions) {

    if (node.isWord) {
        predictions.add(currentWord.toString());
    }
    DLBNode child = node.child;
    while (child != null) {
        StringBuilder newWord = new StringBuilder(currentWord);
        newWord.append(child.data);
        retrieveAllPredictions(child, newWord, predictions);
        child = child.nextSibling;
    }
}

  @Override
  public boolean delete(String word) {
    // TODO Auto-generated method stub
    //throw new UnsupportedOperationException("Unimplemented method 'delete'");
    return false;
  }





   //The DLBNode class
   private class DLBNode{
    private char data; //letter inside the node
    private int size;  //number of words in the subtrie rooted at node
    private boolean isWord; //true if the node is at the end of a word
    private DLBNode nextSibling; //doubly-linked list of siblings
    private DLBNode previousSibling;
    private DLBNode child; // child reference
    private DLBNode parent; //parent reference

    private DLBNode(char data){ //constructor
        this.data = data;
        size = 0;
        isWord = false;
    }
  }

  /* ==============================
   * Helper methods for debugging
   * ==============================
   */

  //Prints the nodes in a DLB Trie for debugging. The letter inside each node is followed by an asterisk if
  //the node's isWord flag is set. The size of each node is printed between parentheses.
  //Siblings are printed with the same indentation, whereas child nodes are printed with a deeper
  //indentation than their parents.
  public void printTrie(){
    System.out.println("==================== START: DLB Trie ====================");
    printTrie(root, 0);
    System.out.println("==================== END: DLB Trie ====================");
  }

  //a helper method for printTrie
  private void printTrie(DLBNode node, int depth){
    if(node != null){
      for(int i=0; i<depth; i++){
        System.out.print(" ");
      }
      System.out.print(node.data);
      if(node.isWord){
        System.out.print(" *");
      }
      System.out.println(" (" + node.size + ")");
      printTrie(node.child, depth+1);
      printTrie(node.nextSibling, depth);
    }
  }
}
