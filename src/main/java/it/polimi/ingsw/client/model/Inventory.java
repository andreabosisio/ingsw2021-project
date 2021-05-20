package it.polimi.ingsw.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Inventory extends Printable{
    private Map<Integer, String> warehouse;
    private final int N_SLOTS = 50;

    public Inventory(Map<Integer, String> warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public List<String> getPrintable() {
        List<String> inventory = new ArrayList<>();
        String[] slots = setSLots();

        inventory.add("Leader Slots: |"+slots[10]+"||"+slots[11]+"| - |"+slots[12]+"||"+slots[13]+"|    Warehouse: |"+slots[4]+"|      ");
        inventory.add("              [10] [11]  - [12] [13]                 [4]       ");
        inventory.add("                                                 |"+slots[5]+"| |"+slots[6]+"|   ");
        inventory.add("From Market:  |"+slots[0]+"||"+slots[1]+"||"+slots[2]+"||"+slots[3]+"|                [5]   [6]    ");
        inventory.add("               [0]  [1]  [2]  [3]             |"+slots[7]+"| |"+slots[8]+"| |"+slots[9]+"|");
        inventory.add("                                               [7]   [8]   [9] ");
        inventory.add("╔══════════════════════════StrongBox══════════════════════════╗");
        inventory.add("║ |"+slots[14]+"||"+slots[15]+"||"+slots[16]+"||"+slots[17]+"||"+slots[18]+"||"+slots[19]+"||"+slots[20]+"||"+slots[21]+"||"+slots[22]+"||"+slots[23]+"||"+slots[24]+"||"+slots[25]+"|║");
        inventory.add("║ [14] [15] [16] [17] [18] [19] [20] [21] [22] [23] [24] [25] ║");
        inventory.add("║ |"+slots[26]+"||"+slots[27]+"||"+slots[28]+"||"+slots[29]+"||"+slots[30]+"||"+slots[31]+"||"+slots[32]+"||"+slots[33]+"||"+slots[34]+"||"+slots[35]+"||"+slots[36]+"||"+slots[37]+"|║");
        inventory.add("║ [26] [27] [28] [29] [30] [31] [32] [33] [34] [35] [36] [37] ║");
        inventory.add("║ |"+slots[38]+"||"+slots[39]+"||"+slots[40]+"||"+slots[41]+"||"+slots[42]+"||"+slots[43]+"||"+slots[44]+"||"+slots[45]+"||"+slots[46]+"||"+slots[47]+"||"+slots[48]+"||"+slots[49]+"|║");
        inventory.add("║ [38] [39] [40] [41] [42] [43] [44] [45] [46] [47] [48] [49] ║");
        inventory.add("╚═════════════════════════════════════════════════════════════╝");

        return inventory;
    }

    private String[] setSLots() {
        String[] slots = new String[N_SLOTS];
        for (int i = 0; i < N_SLOTS; i++) {
            if (warehouse.containsKey(i))
                slots[i] = Marble.getPrintable(warehouse.get(i));
            else
                slots[i] = "   ";
        }
        return slots;
    }
}
