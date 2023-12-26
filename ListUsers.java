public class ListUsers {
    private NodeUser root;
    private int size;

    ListUsers(){
        root = null;
        size = 0;
    }

    public NodeUser getRoot(){ return root; }

    // retorna o tamanho da lista de usuários
    public int size(){
        return size;
    }

    // retorna se está vazio
    public boolean isEmpty() {
        return (size() == 0);
    }

    // adiciona um novo usuário
    public void add(NodeUser user){
        if (isEmpty()) {
            root = user;
        } else {
            NodeUser cur = root;
            while (cur.getNext() != null){
                cur = cur.getNext();
            }
            cur.setNext(user);         
        }
        size++;
    }

    // verifica se tal usuário já existe
    public boolean alreadyExists(String userName){
        if (isEmpty()) {
            return false;
        }
            
        NodeUser cur = root;
        while (cur.getNext() != null){
            if(cur.getName().equals(userName)){
                return true;
            }
            cur = cur.getNext();
        } 
        return false;     
    }

    // remove determinado usuário
    public void remove(String userName){
        if(root.getName().equals(userName)){
            root = root.getNext();
            return;
        }

        NodeUser prev = root;
        NodeUser cur = root.getNext();
        while (cur.getNext() != null){
            if(cur.getName().equals(userName)){
                prev.setNext(cur.getNext());
            }
            prev = cur;
            cur = cur.getNext();
        } 
        size--;
    }

    // retorna uma lista de usuarios que estão em determinanda sala
    public ListUsers getUsersFromRoom(String room) {
        ListUsers usersFromRoom = new ListUsers();

        NodeUser cur = root;
        while (cur != null) {
            if (cur.getRoom().equals(room)) {
                usersFromRoom.add(new NodeUser(cur.getName(), cur.getServer(), cur.getRoom(), cur.getPort())); // Adiciona um novo NodeUser à lista
            }
            cur = cur.getNext();
        }

        return usersFromRoom;
    }

    // verifica se um usuário com o nome especificado está na lista
    public boolean contains(String userName) {
        NodeUser cur = root;

        while (cur != null) {
            if (cur.getName().equals(userName)) {
                return true;
            }
            cur = cur.getNext();
        }

        return false;
    }
}
