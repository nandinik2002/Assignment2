/**
 * An implementation of the AutoCompleteInterface using a DLB Trie.
 */

import java.util.ArrayList;

 public class AutoComplete implements AutoCompleteInterface {

  private DLBNode root = new DLBNode(' '); //root of the DLB Trie
  private StringBuilder currentPrefix = new StringBuilder(); //running prefix
  private DLBNode currentNode = root; //current DLBNode
  //TODO: Add more instance variables as needed



  @Override
  public boolean add(String word) {
    if(word == null){
      throw new IllegalArgumentException("calls add() with a null word");
    } if(word.length()==0){
      throw new IllegalArgumentException("calls add() with an empty word");
    } else return addNewWord(root, word, 0) != null;
    }


    public DLBNode addNewWord(DLBNode node, String word, int wordIndex) {
    if (wordIndex == word.length()) {
        if (!node.isWord) {
            node.isWord = true;
            node.size = 1;
            DLBNode anc = node.parent;
            while (anc != null) {
                anc.size++;
                anc = anc.parent;
            }
        }
        return node;
    }
    DLBNode child = node.child;
    DLBNode prevChild = null;

    while (node.child != null) {
        if (child.data == word.charAt(wordIndex)) {
            return addNewWord(child, word, wordIndex + 1);
        }
        prevChild = child;
        child = child.nextSibling;
    }

    DLBNode newNode = new DLBNode(word.charAt(wordIndex));
    newNode.parent = node;
    if (prevChild == null) {
        node.child = newNode;
    } else {
        prevChild.nextSibling = newNode;
        newNode.previousSibling = prevChild;
    }
    return addNewWord(newNode, word, wordIndex + 1);
}






  @Override
  public boolean advance(char c) {
    DLBNode tempVar = currentNode.child;
    if(c == '\0'){ //checking if the input character is null
      throw new IllegalArgumentException("calls advance() with a null character");
    }
    if (currentNode == null && tempVar != null) {
      return false;
    }
    while (tempVar != null) {
        if (tempVar.data == c) {
            currentNode = tempVar;
            currentPrefix.append(c);
            return true;
        }
        tempVar = tempVar.nextSibling;
    }
    currentNode = null;
    return false;
  }







  @Override
  public void retreat() {
    if (currentPrefix == null){
      throw new IllegalStateException("current prefix is already an empty string"); //we throw this exception because we cant remove the last character of a null string
    } else {
      currentPrefix.deleteCharAt(currentPrefix.length() - 1); //deletes the last character
      if(currentNode != null && currentNode.parent != null){ //if the currentNode isnt null and if its parent isnt null then we move back up the tree to the currentNode's parent
        currentNode = currentNode.parent;
      }
    }
  }







  @Override
  public void reset() {
    currentPrefix = null; //resets the current prefix to a null string
  }






  @Override
  public boolean isWord() {
    return currentNode != null && currentNode.isWord;
  }







  @Override
  public void add() {
    if(currentPrefix.length() > 0 && !currentNode.isWord){
      add(currentPrefix.toString()); //this add method I'm calling here is the add method I wrote above which adds the currentPrefix to the dictionary if it isnt already in it
    }
  }







  @Override
  public int getNumberOfPredictions() {
    if(currentNode!=null){
      return currentNode.size;
    } else{
      return 0;
    }
  }







  @Override
  public String retrievePrediction() {
    if (currentNode == null)
    {
      return null; //returns null if there arent any predictions aka when the currentNode is null
    }
     DLBNode scout = currentNode;
     while(scout!=null){
       if(scout.isWord){
         return currentPrefix.toString();
       }
       if(scout.child!=null){
         scout=scout.child;
         currentPrefix.append(scout.data);
       } else{
         scout=scout.nextSibling;
         if(scout!=null){
           currentPrefix.append(scout.data);
         }
       }
     }
     return null;
  }







  @Override
  public ArrayList<String> retrievePredictions() {
    DLBNode scout = currentNode; //assigns the currentNode to scout so begins at root
    if(scout == null){
      return new ArrayList<>(); //returns an empty ArrayList since
    }
    ArrayList<String> predictions = new ArrayList<>(); //Initalizes the ArrayList for the dictionary words we find that start with the prefix
    if(root != null){
      while(scout!=null){
        if(scout.isWord){ //if the current node is a word in the dictionary then add it to the ArrayList predictions
          predictions.add(currentPrefix.toString());
        }
        if(scout.child!=null){
          scout=scout.child; //scout moves to the current node's child
          currentPrefix.append(scout.data); //child nodes data is added to the prefix
        } else{
          scout=scout.nextSibling; //if the current node (scout) is null then it moves to the current node's (scouts) sibling node
          if(scout!=null){
            currentPrefix.append(scout.data);
          }
        }
      }
    }
    return predictions;
  }






  @Override
  public boolean delete(String word) {
    // TODO
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
