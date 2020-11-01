/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mind_go;

import arc.Events;
import mindustry.content.Blocks;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.mod.Plugin;
import arc.struct.Seq;
import mindustry.gen.Player;
/**
 *
 * @author Xusk
 */
public class Main extends Plugin {
    
    int time = 0;
    int shardedPlayers, bluePlayers = 0;
    int waitTime = (int) (60 * 60 *  0.1f);
    int endTime = (int) (60 * 60 *  0.4);
    // use in tema setter
    int teamNumber = 0;
    
    boolean spawned = false;
    boolean seted = false;
    Seq<Player> players;
    Seq<PlayerData> data;
    
    public Main() {
        players = new Seq<>();
        data = new Seq<>();
        
        //UnitTypes.nova.canBoost = false;
        Events.on(EventType.Trigger.update.getClass(), event ->{
            time++;
            
            Groups.unit.each(unit -> {
                if (unit.isAI() && unit.team != Team.derelict) {
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
                    
                    if(data.size > 0) {
                        for(PlayerData data : data) {
                          Unit unit = data.unit.create(data.player.team());
                          unit.set(data.player.team().core().x(), data.player.team().core().y() + 32);
                          unit.add();
                          System.out.println(unit);
                          data.player.unit(unit);
                        }
                    } else {
                        Groups.player.each(p -> {
                          Unit unit = UnitTypes.nova.create(p.team());
                          unit.set(p.team().core().x(), p.team().core().y() + 32);
                          unit.add();
                          p.unit(unit);
                        });
                    }
                    spawned = true;
                    seted = true;
                }
                // kill core when game start + 2 sec
                  if (time >10 && seted) {
                         // kill cores wghen game start
                        Groups.build.each(build -> {
                            if (build.block() == Blocks.coreShard) {
                                build.kill();
                            }
                        });
                    // check if player in team < 1 then win game
                    if (bluePlayers < 1) {
                        //Events.fire(new EventType.GameOverEvent(Team.sharded));
                        Call.infoMessage("[#" + Team.sharded.color.toString() + "]Sharded [white] team win!");
                        Lobby.go();
                    } if(shardedPlayers < 1) {
                        //Events.fire(new EventType.GameOverEvent(Team.blue));
                        Call.infoMessage("[#" + Team.blue.color.toString() + "]Blue [white] team win!");
                        Lobby.go();
                    }
                  }

            } 
            // end game if timeOut
            if (time > endTime) {
                if (!Lobby.inLobby) {
                    Lobby.go();
                } else {
                    data = new Seq<>();
                    
                    players = new Seq<>();
                    
                    Groups.player.each(p ->{
                        players.add(p);
                    });
                    
                    for( Player player : players) {
                        System.out.println(rect(player.unit(), 6, 38, 16, 46));
                        if(rect(player.unit(), 6, 38, 16, 46)) {
                            data.add(new PlayerData(player));
                        }
                    }
                    System.out.println(data.size);
                    Lobby.out();
                    
                }
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
            UnitTypes.nova.canBoost = false;
            time = 0;
            spawned = false;
            seted = false;         
        });
        Events.on(EventType.PlayerJoin.class, event ->{
            if(Lobby.inLobby) {
                Call.label(event.player.con,"[white]Unit: [gray]Fortress", 360, 11 * 8, 37 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Dagger", 360, 24 * 8, 37 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Mace", 360, 38 * 8, 37 * 8);
            }
        });
    }
    
    boolean rect(Unit unit, float x, float y, float x1, float y1) {
        return unit.x() >= x * 8 && unit.x() <= x1 * 8 && unit.y() > y * 8 && unit.y() < y1 * 8; 
    }
}
