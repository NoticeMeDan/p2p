package com.noticemedan.p2p;

        import org.junit.*;

        import java.io.IOException;
        import java.net.InetAddress;
        import java.util.ArrayList;


@SuppressWarnings("Duplicates")
public class NodeTests {
    private static final int FIRST_NODE_PORT = 34940;
    private static final int SECOND_NODE_PORT = 34931;
    private static final int THIRD_NODE_PORT = 34932;
    private static final int[] TEN_NODE_PORTS = {34920, 34921, 34922, 34923, 34924, 34925, 34926, 34927, 34928, 34929};
    private ArrayList<Thread> threads;
    private ArrayList<Node> nodes;
    private static String localhost;
    private Node first;

    /*
    Create the first node and initialize the localhost address used for all Nodes.
     */
    @Before
    public void setUp() {
        try {
            this.threads = new ArrayList<>();
            this.nodes = new ArrayList<>();
            localhost = InetAddress.getLocalHost().getHostAddress();
        }
        catch(IOException e){
            e.getMessage();
        }

    }

    @Test
    public void creatingFirstNodeSuccess(){
        try {
            first = new Node(FIRST_NODE_PORT, localhost);
            Thread t1 = new Thread(() -> {
                try {
                    first.startNodeThreads(new Message(MessageType.CONNECT));
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            });
            threads.add(t1);
            nodes.add(first);
            t1.start();
        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void creatingSecondNodeSuccess() {
        try {
            Node second = new Node(SECOND_NODE_PORT, localhost);
            Thread t2 = new Thread(() -> {
                try {
                    second.startNodeThreads(new Message(MessageType.CONNECT, localhost, FIRST_NODE_PORT, SECOND_NODE_PORT));
                } catch (IOException e) {
                    Assert.fail(e.getMessage());
                }
            });
            threads.add(t2);
            nodes.add(second);
            t2.start();

        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void creatingThirdNodeSuccess() {
        try {
            Node third = new Node(THIRD_NODE_PORT, localhost);
            Thread t3 = new Thread(() -> {
                try {
                    third.startNodeThreads(new Message(MessageType.CONNECT, localhost, FIRST_NODE_PORT, THIRD_NODE_PORT));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            threads.add(t3);
            nodes.add(third);
            t3.start();
        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getBack_onFirstNode_returnsSecond(){

    }



    @After
    public void tearDown() {
        for(Thread t: threads){
            t.interrupt();
        }
    }

}
