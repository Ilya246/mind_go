/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mind_go;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.gen.Unit;

/**
 *
 * @author Xusk
 */
public class Lobby {
    
    public static boolean inLobby = false;
    
    public Lobby() {
        
    }
    
    public static void go() {
        Lobby.inLobby = true;
        
        Seq<Player> players = new Seq<>();
        
        for(Player p : Groups.player) {
            players.add(p);
            p.clearUnit();
        }
        
        Vars.logic.reset();
        
        Call.worldDataBegin();
        Vars.world.loadMap(Vars.maps.byName("shop"));
        Vars.logic.play();
        
        for(Player p : players) {
            if(p.con == null) continue;
            
            boolean wasAdmin = p.admin;
            p.reset();
            p.admin = wasAdmin;
            
            Call.label(p.con,"[white]Unit: [gray]Fortress", 360, 11 * 8, 37 * 8);
            Call.label(p.con,"[white]Unit: [gray]Dagger", 360, 24 * 8, 37 * 8);
            Call.label(p.con,"[white]Unit: [gray]Mace", 360, 38 * 8, 37 * 8);
            
            Unit fortress = UnitTypes.fortress.create(Team.derelict);
            fortress.set(11 * 8, 40 * 8);
            fortress.add();
            
            Unit dagger = UnitTypes.dagger.create(Team.derelict);
            dagger.set(24 * 8, 40 * 8);
            dagger.add();
            
            Unit mace = UnitTypes.mace.create(Team.derelict);
            mace.set(38 * 8, 40 * 8);
            mace.add();

            Vars.netServer.sendWorldData(p);
        }
        
        
    }
    
    public static void out() {
        Lobby.inLobby = false;
        
        Seq<Player> players = new Seq<>();
        
        for(Player p : Groups.player) {
            players.add(p);
            p.clearUnit();
        }
        
        Vars.logic.reset();
        
        Call.worldDataBegin();
        Vars.world.loadMap(Vars.maps.byName("pvp_area_" + (Mathf.floor(Mathf.random(3, 4)))));
        Vars.logic.play();
        
        for(Player p : players) {
            if(p.con == null) continue;
            
            boolean wasAdmin = p.admin;
            p.reset();
            p.admin = wasAdmin;
            
            Vars.netServer.sendWorldData(p);
        }
    }
}
