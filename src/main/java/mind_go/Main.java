/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mind_go;

import arc.Events;
import java.util.Calendar;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.game.EventType;
import mindustry.game.Gamemode;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;

/**
 *
 * @author Xusk
 */
public class Main extends Plugin {
    
    int time = 0;
    int shardedPlayers, bluePlayers = 0;
    int waitTime = (int) (60 * 60 *  1f);
    int endTime = (int) (60 * 60 *  5f);
    // use in tema setter
    int teamNumber = 0;
    
    boolean spawned = false;
    boolean seted = false;
    
    public Main() {
        Events.on(EventType.Trigger.update.getClass(), event ->{
            time++;
            
            Groups.unit.each(unit -> {
                if (unit.isAI()) {
                    unit.kill();
                }
            });
            
            if (time > waitTime && !Lobby.inLobby) {
                // start game
                if(!spawned) {
                    // set team
                    Groups.player.each(player ->{
                        if(teamNumber == 0) {
                            teamNumber = 1;
                            player.team(Team.sharded);
                        } else {
                            teamNumber = 0;
                            player.team(Team.blue);
                        }
                    });
                    // set unit
                    Groups.player.each(p -> {
                      Unit unit = UnitTypes.dagger.create(p.team());
                      unit.set(p.team().core().x(), p.team().core().y() + 32);
                        System.out.println(unit);
                      unit.add();
                      p.unit(unit);
                    });
                    spawned = true;
                    seted = true;
                }
                // kill core when game start + 2 sec
                  if (time > waitTime + 10 && seted) {
                         // kill cores wghen game start
                        Groups.build.each(build -> {
                            if (build.block() == Blocks.coreShard) {
                                build.kill();
                            }
                        });
                    // check if player in team < 1 then win game
                    if (bluePlayers < 1) {
                        Events.fire(new EventType.GameOverEvent(Team.sharded));
                        //Lobby.go();
                    } if(shardedPlayers < 1) {
                        Events.fire(new EventType.GameOverEvent(Team.blue));
                        //Lobby.go();
                    }
                  }

            }
            // end game if timeOut
            if (time > endTime) {
                Events.fire(new EventType.GameOverEvent(Team.derelict));
                /*
                if (!Lobby.inLobby) {
                    Lobby.go();
                } else {
                    Lobby.out();
                }*/
            }
            
            shardedPlayers = 0;
            bluePlayers = 0;
            
            Groups.unit.each(unit -> {
                if (unit.team == Team.sharded) {
                    shardedPlayers++;
                } if (unit.team == Team.blue) {
                    bluePlayers++;
                }
            });
        });
        
        Events.on(EventType.WorldLoadEvent.class, event ->{
            time = 0;
            spawned = false;
            seted = false;
        });
        /*
        Events.on(EventType.PlayerJoin.class, event ->{
            if(Lobby.inLobby) {
                Call.label(event.player.con,"[white]Unit: [gray]Fortress\n[lime]Cost:[gray]800", 360, 11 * 8, 37 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Dagger\n[lime]Cost:[gray]free", 360, 24 * 8, 37 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Mase\n[lime]Cost:[gray]400", 360, 38 * 8, 37 * 8);
            }
        });
        */
    }
}
