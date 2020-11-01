/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mind_go;

import arc.Events;
import javax.sql.rowset.serial.SerialArray;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.maps.Map;
import mindustry.ui.dialogs.JoinDialog;

/**
 *
 * @author Xusk
 */
public class Lobby {
    
    public static boolean inLobby = false;
    
    public Lobby() {
        
    }
    
    public static void go() {
        Call.infoMessage("Loading shop...");
        Map map = Vars.maps.getNextMap(Gamemode.pvp, Vars.state.map);
        
        System.out.println(map.name());
        Lobby.inLobby = true;
        
        
    }
    
    public static void out() {
    }
}
