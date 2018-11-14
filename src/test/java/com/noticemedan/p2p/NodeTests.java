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
    private ArrayList<Node> nodes;
    private static String localhost;

    /*
    Create the first node and initialize the localhost address used for all Nodes.
     */
    @Before
    public void setUp() {
        try {
            this.nodes = new ArrayList<>();
            localhost = InetAddress.getLocalHost().getHostAddress();
        }
        catch(IOException e){
            e.getMessage();
        }

    }

    @Test
    public void createFirsTNode(){
        try {
            Node firstNode = new Node(FIRST_NODE_PORT, localhost);
            nodes.add(firstNode);
            firstNode.startNodeThreads(new Message(MessageType.CONNECT));
        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void connectSecondNode() {
        Node second = null;
        try {
            second = new Node(SECOND_NODE_PORT, localhost);
            nodes.add(second);
            second.startNodeThreads(new Message(MessageType.CONNECT, localhost, FIRST_NODE_PORT, SECOND_NODE_PORT));
        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void connectThirdNode() {
        try {
            Node third = new Node(THIRD_NODE_PORT, localhost);
            nodes.add(third);
            third.startNodeThreads(new Message(MessageType.CONNECT, localhost, FIRST_NODE_PORT, THIRD_NODE_PORT));
        }
        catch(IOException e){
            Assert.fail(e.getMessage());
        }

    }

    @Test
    public void connectTenNodes() {

    }





    @After
    public void tearDown() {
        for(Node n: nodes){
            n.stop();
        }
    }

}
