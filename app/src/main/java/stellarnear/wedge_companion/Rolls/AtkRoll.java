package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import stellarnear.wedge_companion.CalculationAtk;
import stellarnear.wedge_companion.CalculationSpell;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Tools;

public class AtkRoll {
    private Dice atkDice;

    private Integer preRandValue = 0;
    private Integer atk = 0;
    private Integer base = 0;

    private String mode; //le type d'attauqe fullround,barrrage,simple

    private Boolean miss = false;
    private Boolean hitConfirmed = false;
    private Boolean crit = false;
    private Boolean critConfirmed = false;
    private Boolean fail = false;
    private Boolean invalid = false;
    private Boolean manualDice;
    private Context mC;

    private SharedPreferences settings;

    private CheckBox hitCheckbox;
    private CheckBox critCheckbox;
    private Perso pj = PersoManager.getCurrentPJ();

    private OnRefreshEventListener mListener;
    private Tools tools=new Tools();

    public AtkRoll(Activity mA,Context mC, Integer base) {
        this.mC = mC;
        this.base=base;
        this.atkDice = new Dice(mA,mC,20);
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        manualDice = settings.getBoolean("switch_manual_diceroll", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));
        constructCheckboxes();
    }

    private void constructCheckboxes() {
        hitCheckbox = new CheckBox(mC);
        hitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hitConfirmed = false;
                if (isChecked) {
                    hitConfirmed = true;
                }
            }
        });

        hitCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                miss=true;
                hitCheckbox.setChecked(false);
                critCheckbox.setChecked(false);
                hitCheckbox.setEnabled(false);
                critCheckbox.setEnabled(false);
                return false;
            }
        });

        critCheckbox = new CheckBox(mC);
        critCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                critConfirmed = false;
                if (isChecked) {
                    hitCheckbox.setChecked(true);
                    critConfirmed = true;
                }
            }
        });
    }

    private int getBonusRangeAtk() {
       int bonusAtkRange=0;
        if (settings.getBoolean("thor_switch", mC.getResources().getBoolean(R.bool.thor_switch_def))) {
            bonusAtkRange+= 3;
        }
        if (settings.getBoolean("predil_switch", mC.getResources().getBoolean(R.bool.predil_switch_def))) {
            bonusAtkRange+= 1;
        }
        if (settings.getBoolean("predil_sup_switch", mC.getResources().getBoolean(R.bool.predil_sup_switch_def))) {
            bonusAtkRange+= 1;
        }
        if (settings.getBoolean("predil_epic_switch", mC.getResources().getBoolean(R.bool.predil_epic_switch_def))) {
            bonusAtkRange+= 2;
        }
        if (settings.getBoolean("neuf_m_switch", mC.getResources().getBoolean(R.bool.neuf_m_switch_def))) {
            bonusAtkRange+= 1;
        }
        if (settings.getBoolean("magic_switch", mC.getResources().getBoolean(R.bool.magic_switch_def))) {
            bonusAtkRange+= tools.toInt(settings.getString("magic_val", String.valueOf(mC.getResources().getInteger(R.integer.magic_val_def))));
        }
        if (this.mode.equalsIgnoreCase("fullround") && settings.getBoolean("tir_rapide", mC.getResources().getBoolean(R.bool.tir_rapide_switch_def))) {
            bonusAtkRange-=2;
        }
        if (settings.getBoolean("viser", mC.getResources().getBoolean(R.bool.viser_switch_def))) {
            bonusAtkRange-=tools.toInt(settings.getString("viser_val", String.valueOf(mC.getResources().getInteger(R.integer.viser_val_def))));
        }
        bonusAtkRange+= pj.getAbilityMod("ability_dexterite");

        if(this.mode.equalsIgnoreCase("barrage_shot")){
            bonusAtkRange+=tools.toInt(settings.getString("mythic_tier", String.valueOf(mC.getResources().getInteger(R.integer.mythic_tier_def))));
        }
        return bonusAtkRange;
    }

    //setters
    public void setMode(String mode){
        this.mode=mode;
    }

    //getters
    public Dice getAtkDice(){
        return this.atkDice;
    }

    public ImageView getImgAtk() {
        return atkDice.getImg();
    }

    public Integer getPreRandValue() {
        this.preRandValue = this.base + new CalculationAtk(mC).getBonusAtk() + getBonusRangeAtk();
        return preRandValue;
    }

    public void setAtkRand() {
        atkDice.rand(manualDice);
        if (manualDice) {
            atkDice.setRefreshEventListener(new Dice.OnRefreshEventListener() {
                @Override
                public void onEvent() {
                    calculAtk();
                    setCritAndFail();
                    if(mListener!=null){mListener.onEvent();}
                }
            });
        } else {
            calculAtk();
            setCritAndFail();
        }
    }

    private void setCritAndFail() {
        if (atkDice.getRandValue() == 1 && !pj.getAllMythicCapacities().mythiccapacityIsActive("mythiccapacity_still_a_chance")) { //si c'est un 1 et qu'on a pas le dons antifail
            this.fail = true;
            atkDice.getImg().setOnClickListener(null);
        }
        int critMin;
        if (settings.getBoolean("improved_crit_switch", mC.getResources().getBoolean(R.bool.improved_crit_switch_def))) {
            critMin = 18;
        } else {
            critMin = 20;
        }
        if (atkDice.getRandValue() >= critMin) { //c'est possiblement un crit
            this.crit = true;
        }
    }

    private void calculAtk() {
        this.atk = this.preRandValue + atkDice.getRandValue();
        if(this.atkDice.getMythicDice()!=null){
            this.atk+=this.atkDice.getMythicDice().getRandValue();
        }
    }

    public Integer getValue() {
        calculAtk();
        return atk;
    }

    public Boolean isInvalid() {
        return invalid;
    }

    public void invalidated() {
        this.invalid = true;
        atkDice.getImg().setImageDrawable(tools.resize(mC,R.drawable.d20_fail, mC.getResources().getDimensionPixelSize(R.dimen.icon_main_dices_combat_launcher_size)));
        atkDice.getImg().setOnClickListener(null);
    }

    public boolean isFailed() {
        return this.fail;
    }

    public boolean isCrit() {
        return this.crit;
    }

    public void isDelt() {
        invalid = true;
        hitCheckbox.setEnabled(false);
        critCheckbox.setEnabled(false);
    }

    public boolean isMissed(){
        return this.miss;
    }

    public boolean isHitConfirmed() {
        return this.hitConfirmed;
    }

    public boolean isCritConfirmed() {
        return this.critConfirmed;
    }

    public CheckBox getHitCheckbox() {
        return this.hitCheckbox;
    }

    public CheckBox getCritCheckbox() {
        return this.critCheckbox;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }
}