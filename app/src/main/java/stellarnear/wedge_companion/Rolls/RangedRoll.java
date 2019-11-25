package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import stellarnear.wedge_companion.Activities.MainActivity;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class RangedRoll extends Roll {

    public RangedRoll(Activity mA, Context mC, Integer atkBase) {
        super(mA,mC,atkBase);
        this.atkRoll=new RangeAtkRoll(mA,mC,atkBase);
    }

    public void setDmgRand() {
        if (this.dmgRollList.isEmpty() && !isMissed()){
            int nthDmgRoll=1;
            this.dmgRollList.add(new RangeDmgRoll(mA,mC,atkRoll.isCritConfirmed(),atkRoll.getAtkDice().getRandValue()==20,this.nthAtkRoll,nthDmgRoll));
            if (pj.featIsActive("feat_manyshot_suprem")) {
                int multiVal = tools.toInt(settings.getString("feat_manyshot_suprem_val", String.valueOf(mC.getResources().getInteger(R.integer.feat_manyshot_suprem_val_def))));
                for(int i=1;i<multiVal;i++){
                    nthDmgRoll++;
                    this.dmgRollList.add(new RangeDmgRoll(mA,mC,false,false,this.nthAtkRoll,nthDmgRoll)); //seul l'attaque principale peut crit
                }
            }

            for(DmgRoll dmgRoll:this.dmgRollList){
                dmgRoll.setDmgRand();
            }
        }
    }

}
