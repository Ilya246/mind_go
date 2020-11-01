/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mind_go;

import mindustry.content.UnitTypes;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

/**
 *
 * @author Xusk
 */
public class PlayerData {
    UnitType unit = UnitTypes.dagger;
    Player player;
    
    public PlayerData(Player player) {
        this.player = player;
    }
}
