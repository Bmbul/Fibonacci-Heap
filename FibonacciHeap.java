import java.util.HashSet;
import java.util.Set;

public class FibonacciHeap
{
    private Node minNode;
    private int count;

    private static class Node
    {
        int key;
        Node parent;
        Node child;
        Node left;
        Node right;
        boolean isMarked;
        int degree;

        Node(int _key)
        {
            key = _key;
            degree = 0;
            isMarked = false;
        }
    }

    public int GetCount() { return count; }

    public void Insert(int key)
    {
        Node newNode = new Node(key);
        RootInsert(newNode);
        count++;
    }

    public void InsertRange(int[] keys)
    {
        for (int i = 0; i < keys.length; i++)
            Insert(keys[i]);
    }

    public void Union(FibonacciHeap otherHeap) 
    {
        if (otherHeap == null || otherHeap.minNode == null) {
            return;
        }

        if (minNode == null) {
            minNode = otherHeap.minNode;
        } else {
            Node thisMinLeft = minNode.left;
            Node otherMinLeft = otherHeap.minNode.left;

            thisMinLeft.right = otherHeap.minNode;
            otherHeap.minNode.left = thisMinLeft;
            minNode.left = otherMinLeft;
            otherMinLeft.right = minNode;

            if (otherHeap.minNode.key < minNode.key)
                minNode = otherHeap.minNode;
        }
        count += otherHeap.GetCount();
    }

    public Node ExtractMin()
    {
        Node min = minNode;
        if (min == null)
            return null;
        Node child = min.child;
        if (child != null)
        {
            do {
                Node nextChild = child.right;
                RootInsert(child);
                child = nextChild;
            } while (child != min.child);
        }
        RemoveNode(min);
        if (min == min.right)
            minNode = null;
        else
        {
            minNode = min.right;
            Consolidate();
        }
        return min;
    }

    //#region Required Part
    public int FindMax() {
        if (minNode == null)
            throw new IllegalStateException("The Fibonacci heap is empty.");
        return FindMaxRecursive(minNode, minNode.key, minNode);
    }

    private int FindMaxRecursive(Node current, int maxSoFar, Node start) {
        if (current == null)
            return maxSoFar;

        if (current.key > maxSoFar)
            maxSoFar = current.key;

        maxSoFar = Math.max(maxSoFar, FindMaxRecursive(current.child, maxSoFar, current.child));

        if (current.right != start)
            maxSoFar = Math.max(maxSoFar, FindMaxRecursive(current.right, maxSoFar, start));
        return maxSoFar;
    }

    //#endregion

    public void printHeap()
    {
        if (minNode == null) {
            System.out.println("The Fibonacci heap is empty.");
            return;
        }

        System.out.println("Fibonacci Heap Structure:");
        Set<Node> visitedNodes = new HashSet<>();
        printNode(minNode, "", visitedNodes);
    }

    // Inserts nodes before min node, but does not change the count
    private void RootInsert(Node node)
    {
        if (minNode== null) {
            minNode = node;
            minNode.left = minNode;
            minNode.right = minNode;
        } else {
            node.right = minNode;
            node.left = minNode.left;
            minNode.left.right = node;
            minNode.left = node;
        }
        if (node.key < minNode.key) {
            minNode = node;
        }
    }

    private void InsertChild(Node parent, Node node)
    {
        if (parent == null)
            return ;

        Node child = parent.child;
        if (child== null) {
            parent.child = node;
            parent.child.left = node;
            parent.child.right = node;
        } else {
            node.right = child;
            node.left = child.left;
            child.left.right = node;
            child.left = node;
        }
    }

    private void RemoveNode(Node node)
    {
        node.left.right = node.right;
        node.right.left = node.left;
        count--;
    }

    private void Consolidate()
    {
        int maxDegree = (int) (Math.log(count) / Math.log(2)) + 1; // Maximum degree is log2(count) + 1
        Node[] degreeArray = new Node[maxDegree];

        // Initialize the degreeArray
        for (int i = 0; i < maxDegree; i++) {
            degreeArray[i] = null;
        }

        Node current = minNode;
        do {
            Node x = current;
            current = current.right;
            int degree = x.degree;

            while (degreeArray[degree] != null) {
                Node y = degreeArray[degree];
                
                if (x.key > y.key) {
                    Node temp = x;
                    x = y;
                    y = temp;
                    if (minNode == y)
                        minNode = x;
                }
                Link(y, x);

                degreeArray[degree] = null;
                degree++;
            }

            degreeArray[degree] = x;
        } while (current != minNode);

        // Reconstruct the root list and find the new minimum
        minNode = null;

        for (int i = 0; i < maxDegree; i++) {
            if (degreeArray[i] != null)
                RootInsert(degreeArray[i]);
        }
    }

    private void Link(Node child, Node parent)
    {
        child.parent = parent;
        InsertChild(parent, child);
        parent.degree++;
        child.isMarked = false;
    }

    private void printNode(Node node, String indent, Set<Node> visitedNodes)
    {
        if (node == null || visitedNodes.contains(node)) {
            return;
        }

        visitedNodes.add(node);
        System.out.println(indent + "Key: " + node.key);

        // Recursively print the children of the current node
        if (node.child != null) {
            Node child = node.child;
            do {
                printNode(child, indent + "  ", visitedNodes);
                child = child.right;
            } while (child != node.child);
        }

        // Recursively print the siblings of the current node
        printNode(node.right, indent, visitedNodes);
    }
}

class Executor
{
    public static void main(String[] args)
    {
        FibonacciHeap heap = new FibonacciHeap();
        heap.InsertRange(new int[] {11, 2, 4, 6, 1, 9});
        heap.ExtractMin();
        heap.printHeap();
        System.out.println(heap.GetCount());

        heap.InsertRange(new int[] {7, 8, 10});
        heap.printHeap();
        heap.ExtractMin();
        System.out.println(heap.GetCount());

        heap.InsertRange(new int[] {3, 5, 12});
        heap.ExtractMin();
        heap.printHeap();
        System.out.println(heap.FindMax());
    }
}