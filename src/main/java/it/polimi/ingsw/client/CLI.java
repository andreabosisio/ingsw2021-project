package it.polimi.ingsw.client;

import java.net.Socket;

public class CLI {
    public void startCLI(){
        Socket clientSocket;
        /*
        try {
            clientSocket = new Socket("127.0.0.1",1337);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            Scanner inFromTerminal = new Scanner(System.in);
            while (true) {
                String answer;
                while(in.ready()){
                    answer = in.readLine();
                    System.out.println(answer);
                    //answer = in.readLine();
                }
                System.out.println("quitc for client/login/quit to leave");
                String command = inFromTerminal.nextLine();
                if (command.equals("quitc")) {
                    break;
                } else if (command.equals("login")) {
                    System.out.println("insert nickname");
                    List<String> login = new ArrayList<>();
                    String nickname = inFromTerminal.nextLine();
                    System.out.println("insert password");
                    String password = inFromTerminal.nextLine();
                    login.add(nickname);
                    login.add(password);
                    Gson gson = new Gson();
                    command = gson.toJson(login);
                }
                out.println(command);
                answer = in.readLine();
                System.out.println(answer);

                ChooseSetupEvent chooseSetupEvent = null;

                try {
                    JsonElement jsonElement = JsonParser.parseString(answer);
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if(jsonObject.has("payload")) {
                        String payload = jsonObject.get("payload").getAsString();
                        chooseSetupEvent = new Gson().fromJson(payload, ChooseSetupEvent.class);
                    }
                }catch (NullPointerException | JsonSyntaxException e){
                    //e.printStackTrace();
                    continue;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

         */
    }
}