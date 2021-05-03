package it.polimi.ingsw.server.network;

import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket;
    private boolean status;
    private Connection connection;

    public ClientHandler(Socket socket){
        this.socket=socket;
        this.connection=new Connection(socket);
    }


    @Override
    public void run() {
        status=true;
        if(!Lobby.getLobby().isNotFull()){
            connection.sendMessage("cannot join");
            status = false;
        }
        if(status) {
            login();
        }
        connection.close();

        /*
        //3 while per login connection e game?
        while(status){
            //loop di recezione comandi
            String line = connection.getMessage();
            if(line.equals("quit")){
                kill();
            }
            else {
                connection.sendMessage(line+" from server");
                System.out.println(line);
            }
        }
        //close everything
        connection.close();

         */
    }

    private void login(){
        while (status){

        }
    }



    public void kill(){
        status=false;
        //maybe close connection properly before this
        Thread.currentThread().interrupt();
    }
}
