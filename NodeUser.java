public class NodeUser {
    private String name, state, server, room;
    private int port;
    private NodeUser next;

    NodeUser(String server, int port){
        this.name = null;
        this.server = server;
        this.port = port;
        this.state = "init";
        this.next = null;
        this.room = null;
    }

    NodeUser(String name, String server, String room, int port){
        this.name = name;
        this.server = server;
        this.port = port;
        this.state = "init";
        this.next = null;
        this.room = room;
    }

    public String getName(){ return name; }
    public String getState(){ return state; }
    public String getServer(){ return server; }
    public String getRoom(){ return room; }
    public int getPort(){ return port; }
    public NodeUser getNext(){ return next; }

    public void setName(String newName){ name = newName; }
    public void setState(String newState){ state = newState; }
    public void setRoom(String newRoom){ room = newRoom; }
    public void setNext(NodeUser newNode){ next = newNode; }

}
