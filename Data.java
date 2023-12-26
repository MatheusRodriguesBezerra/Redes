public class Data {
    private ListUsers users;

    Data(){
        users = new ListUsers();
    }

    public String event(String command, String message, String userName){
        NodeUser user = getUserByName(userName);

        if(user.getState() =="init" && command == "/nick" && users.alreadyExists(message)){
            return "ERROR\n";
        }

        if(user.getState() == "init" && command == "/nick" && !users.alreadyExists(message)){
            user.setState("outside");
            return "OK\n";
        }

        if(user.getState() == "outside" && command == "/join"){
            // eviar para os outros utilizadores da sala JOINED nome
            user.setRoom(message);
            user.setState("inside");
            return "OK\nJOINED " + user.getName() + "\n";
        }

        if(user.getState() == "outside" && command == "/nick" && users.alreadyExists(message)){
            return "ERROR\n";
        }

        if(user.getState() == "outside" && command == "/nick" && !users.alreadyExists(message)){
            user.setName(userName);
            return "OK\n";
        }

        if(user.getState() == "inside" && command == "message"){
            // enviar para todos os utilizadopres da sala MESSAGE nome mensagem
            return "MESSAGE " + user.getName() + " " + message + "\n"; 
        }

        if(user.getState() == "inside" && command == "/nick" && users.alreadyExists(message)){
            return "ERROR\n";
        }

        if(user.getState() == "inside" && command == "/nick" && !users.alreadyExists(message)){
            // enviar p o utilizador OK
            // enviar para os outros utilizadores da sala NEWNICK nome_antigo nome
            user.setName(message);
            return "NEWNICK " + userName + " " + message + " \n";
        }

        if(user.getState() == "inside" && command == "/join"){
            // enviar p o utilizador OK
            // enviar para os usuarios da sala atual LEFT nome
            user.setRoom(message);
            // enviar para os usuarios da nova sala JOINED nome
            user.setState("inside");
            return "OK\nJOINED " + user.getName() + "\n";
        }

        if(user.getState() == "inside" && command == "/leave"){
            // enviar p o utilizador OK
            // enviar para os outros utilizadores da sala LEFT nome
            user.setRoom(null);
            user.setState("outside"); 
            return "OK\nLEFT " + userName;
        }

        if(user.getState() == "inside" && command == "/bye"){
            // enviar p o utilizador BYE
            // enviar para os outros utilizadores da sala LEFT nome
            users.remove(userName);
            return "BYE\nLEFT " + userName;
        }

        if(user.getState() == "inside"){
            // enviar para os outros utilizadores da sala LEFT nome
            users.remove(userName);
        }

        if(user.getState() != "inside" && command == "/bye"){
            // enviar p o utilizador BYE
            users.remove(userName);
        }

        if(user.getState() != "inside" && command == "message"){
            // ERROR
        }

        if(user.getState() != "inside"){
            users.remove(userName);
        }

        // ERROR
        return "";
    }

    public NodeUser getUserByName(String name){
        if(!users.contains(name)){
            NodeUser user = new NodeUser(name);
            users.add(user);
            return user;
        }

        NodeUser cur = users.getRoot();
        while (!cur.getName().equals(name)) {
            cur =cur.getNext();
        }
        return cur;
    }
}
