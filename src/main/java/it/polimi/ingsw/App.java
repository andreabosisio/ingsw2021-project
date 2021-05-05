package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.receive.MarketReceiveEvent;
import it.polimi.ingsw.server.events.receive.SetupReceiveEvent;
import it.polimi.ingsw.server.events.send.SetupSendEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        /*
        List<Integer> leader = new ArrayList<>();
        leader.add(1);
        leader.add(2);
        List<String> res = new ArrayList<>();
        res.add("blue");
        res.add("yellow");
        SetupReceiveEvent event = new SetupReceiveEvent("matteo",leader,res);
        Gson gson = new Gson();
        String json = gson.toJson(event);
        System.out.println(json);

         */
        MarketReceiveEvent event = new MarketReceiveEvent("matteo",1);
        Gson gson = new Gson();
        String json = gson.toJson(event);
        System.out.println(json);

    }
}
