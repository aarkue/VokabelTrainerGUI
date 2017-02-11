package DataStructure;
import org.jetbrains.annotations.Nullable;

public class PriorityQueue<ContentType>
{
    /**
     * Inner Class Node which is one way linked to the next one.
     */
  private class Node{
        /**
         * Next Node in Queue/List.
         */
        @Nullable
        private Node next;
        /**
         * Content of the node.
         */
        private final ContentType content;
        /**
         * Priority of this node. The higher the closer to the "front" of the Queue/List.
         */
        private final int prio;

        /**
         * Constructor of Node.
         * @param con sets {@linkplain #content}
         * @param pri sets {@linkplain #prio}
         */
        public Node(ContentType con, int pri){
            content = con;
            prio = pri;
            next = null;
        }

        /**
         * Sets the next node.
         * @param n sets {@linkplain #next}
         */
        public void setNext(@Nullable Node n){
            next = n;
        }

        /**
         * Gets the next node. Return null if there is no next node.
         * @return next node or null
         */
        @Nullable
        public Node getNext(){
            return next;
        }

        /**
         * Returns the priority of this node saved in {@linkplain #prio}.
         * @return priority of this node ({@linkplain #prio})
         */
        public int getPrio(){
            return prio;
        }

        /**
         * Return the Content of this node.
         * @return content of this node {@linkplain #content}
         */
        public ContentType getContent(){
            return content;
        }
  }

    /**
     * Front node (with highest {@linkplain Node#prio}.
     */
    @Nullable
    private Node head;

    /**
     * Checks if the Queue/List is empty. True if {@linkplain #head} is null.
     * @return whether or not the Queue/List is empty
     */
    public boolean isEmpty(){
        return (head == null);
    }

    /**
     * Adds a new node to the Queue/List.
     * @param con Content to add (saved in {@linkplain Node#content})
     * @param pri Priority of the new node (saved in {@linkplain Node#prio})
     */
    public void add(ContentType con,int pri){
       Node current = head;
        Node newNode = new Node(con,pri);
        if(isEmpty()){current= newNode; head = current;}else{

            if((current != null ? current.getPrio() : 0) < newNode.getPrio()){
              newNode.setNext(current);
              head = newNode;
            }else{

                while((current != null ? current.getNext() : null) != null && newNode.getPrio() <= current.getNext().getPrio()){
                current = current.getNext();
        }
        newNode.setNext(current != null ? current.getNext() : null);
                //noinspection ConstantConditions
                current.setNext(newNode);
    }
        }
}

    /**
     * Deletes the front Node from the Queue/List.
     */
    public void del(){
    if(!isEmpty()) {

        head = head != null ? head.getNext() : null;
    }
}

    /**
     * Gets the content of the front Node. Null if the Queue/List is empty.
     * @return front node content ((saved in {@linkplain Node#content}) or null
     */
    @Nullable
    public ContentType front(){
        if(!isEmpty()){

            return head != null ? head.getContent() : null;
        }else{
            return null;
}
}

    /**
     * Gets the priority of the front Node. -1 if the Queue/List is empty.
     * @return front node priority (saved in {@linkplain Node#prio}) or -1
     */
    public int getFrontPrio(){
    if(!isEmpty()){

        return head != null ? head.getPrio() : 0;
        }else{
            return -1;
}
}
}
