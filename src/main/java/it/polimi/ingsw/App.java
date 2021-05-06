package it.polimi.ingsw;

import com.google.gson.Gson;
import it.polimi.ingsw.server.events.receive.MarketReceiveEvent;

import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        /*
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");
        arrayList.add("4");
        arrayList.add("5");
        arrayList.subList(2, arrayList.size()).clear();
        System.out.println(arrayList.size());

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
        /*
        MarketReceiveEvent event = new MarketReceiveEvent("matteo",1);
        Gson gson = new Gson();
        String json = gson.toJson(event);
        System.out.println(json);
         */

    }
}
