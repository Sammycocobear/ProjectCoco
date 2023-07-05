package me.scb.Abilities.Air.Sound.SoundElement;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AirAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import com.projectkorra.projectkorra.ability.WaterAbility;
import me.scb.ProjectCoco;
import org.bukkit.entity.Player;

public abstract class SoundAbility extends AirAbility implements SubAbility {
    public static Element.SubElement SOUND;


    static {
        for (Element.SubElement se : Element.getAddonSubElements()) {
            if (se.getName().equalsIgnoreCase("sound")) {
                SOUND = se;
            }
        }
        if (SOUND == null) {
            SOUND = new Element.SubElement("Sound", Element.AIR, Element.ElementType.BENDING, ProjectCoco.getPlugin());

        }

    }




    public SoundAbility(Player player) {
        super(player);
    }

    public Class<? extends Ability> getParentAbility() {
        return WaterAbility.class;
    }

    public Element getElement() {
        return SOUND;
    }

}
