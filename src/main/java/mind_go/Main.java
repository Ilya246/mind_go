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
import mindustry.content.Bullets;
import mindustry.gen.Player;
/**
 *
 * @author Xusk
 */
public class Main extends Plugin {
    
    int time = 0;
    int shardedPlayers, bluePlayers = 0;
    int waitTime = (int) (60 * 60 *  0.3f);
    int endTime = (int) (60 * 60 *  5f);
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
                if(!spawned && time > waitTime) {
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
                    /*

                    }
                    */
                    // set unit
                    
                    if(data.size > 0) {
                        for(PlayerData data : data) {
                          Unit unit = data.unit.create(data.player.team());
                          unit.set(data.player.team().core().x(), data.player.team().core().y() + 32);
                          unit.add();
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
                  if (time >waitTime + 10 && seted) {
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
                } 
            }
            if (Lobby.inLobby && time > waitTime) {
                    data = new Seq<>();
                    
                    players = new Seq<>();
                    
                    Groups.player.each(p ->{
                        players.add(p);
                    });
                    
                    for( Player player : players) {
                        if (player.con == null) continue; 
                        System.out.println(rect(player.unit(), 6, 38, 16, 46));
                        if(rect(player.unit(), 6, 38, 16, 46)) {
                            PlayerData playerData = new PlayerData(player);
                            playerData.unit = UnitTypes.fortress;
                            data.add(playerData);
                        } else if(rect(player.unit(), 20, 38, 29, 46)) {
                            PlayerData playerData = new PlayerData(player);
                            playerData.unit = UnitTypes.nova;
                            data.add(playerData);
                        } else if(rect(player.unit(), 33, 38, 43, 46)) {
                            PlayerData playerData = new PlayerData(player);
                            playerData.unit = UnitTypes.mace;
                            data.add(playerData);
                        } else {
                                data.add(new PlayerData(player));
                        }
                    }
                   
                    Lobby.out();
                    
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
            UnitTypes.mace.health = 350f;
            UnitTypes.mace.armor = 1;
            UnitTypes.nova.health = UnitTypes.dagger.health ; 
            UnitTypes.fortress.armor = UnitTypes.dagger.armor = UnitTypes.nova.armor =+ 2;
            UnitTypes.fortress.weapons.get(0).bullet.damage = Bullets.standardCopper.damage - 5f;
            time = 0;
            spawned = false;
            seted = false;         
        });
        Events.on(EventType.PlayerJoin.class, event ->{
            if(Lobby.inLobby) {
                Call.label(event.player.con,"[white]Unit: [gray]Fortress\n[accent]Class: [white]Tank", 360, 11 * 8, 35 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Nova\n[accent]Class: [white]Supporter\n[red]WARNING:[gray] nova can't fly", 360, 24 * 8, 35 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Mace\n[accent]Class: [white]Damager", 360, 38 * 8, 35 * 8);
                Call.label(event.player.con,"[white]Unit: [gray]Dagger\n[accent]Class: [white]Omni", 360, 25 * 8, 7 * 8);
            }
        });
    }
    
    boolean rect(Unit unit, float x, float y, float x1, float y1) {
        return unit.x() >= x * 8 && unit.x() <= x1 * 8 && unit.y() > y * 8 && unit.y() < y1 * 8; 
    }
}
