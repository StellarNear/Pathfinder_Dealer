package stellarnear.wedge_companion.Rolls;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import stellarnear.wedge_companion.CritConfirmAlertDialog;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Dices.Dice;
import stellarnear.wedge_companion.Rolls.Dices.Dice20;
import stellarnear.wedge_companion.Tools;

public abstract class AtkRoll {
    protected Dice20 atkDice;

    protected Integer preRandValue = 0;
    protected Integer atk = 0;
    protected Integer base = 0;

    protected String mode; //le type d'attauqe fullround,barrrage,simple

    protected Boolean miss = false;
    protected Boolean hitConfirmed = false;
    protected Boolean crit = false;
    protected Boolean critConfirmed = false;
    protected Boolean fail = false;
    protected Boolean invalid = false;
    protected Boolean manualDice;
    protected Context mC;
    protected Activity mA;

    protected SharedPreferences settings;

    protected CheckBox hitCheckbox;
    protected CheckBox critCheckbox;
    protected Perso pj = PersoManager.getCurrentPJ();

    protected OnRefreshEventListener mListener;
    protected Tools tools = Tools.getTools();

    public AtkRoll(Activity mA, Context mC, Integer base) {
        this.mA = mA;
        this.mC = mC;
        this.base = base;
        this.atkDice = new Dice20(mA, mC);
        settings = PreferenceManager.getDefaultSharedPreferences(mC);
        manualDice = settings.getBoolean("switch_manual_diceroll", mC.getResources().getBoolean(R.bool.switch_manual_diceroll_def));
        constructCheckboxes();
    }

    private void constructCheckboxes() {
        hitCheckbox = new CheckBox(mC);
        hitCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hitConfirmed = isChecked;
            }
        });

        hitCheckbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                miss = true;
                hitCheckbox.setChecked(false);
                critCheckbox.setChecked(false);
                hitCheckbox.setEnabled(false);
                critCheckbox.setEnabled(false);
                return true;
            }
        });

        critCheckbox = new CheckBox(mC);
        critCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                critConfirmed = false;
                if (isChecked) {
                    CritConfirmAlertDialog contactDialog = new CritConfirmAlertDialog(mA, mC, preRandValue);
                    contactDialog.showAlertDialog();
                    contactDialog.setSuccessEventListener(new CritConfirmAlertDialog.OnSuccessEventListener() {
                        @Override
                        public void onSuccessEvent() {
                            hitCheckbox.setChecked(true);
                            critConfirmed = true;
                        }
                    });
                    contactDialog.setFailEventListener(new CritConfirmAlertDialog.OnFailEventListener() {
                        @Override
                        public void onFailEvent() {
                            critCheckbox.setChecked(false);
                            critConfirmed = false;
                        }
                    });
                }
            }
        });
    }

    //setters
    public void setMode(String mode) {
        this.mode = mode;
    }

    //getters
    public Dice20 getAtkDice() {
        return this.atkDice;
    }

    public View getImgAtk() {
        return atkDice.getImg();
    }

    public Integer getPreRandValue() {
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
                    if (mListener != null) {
                        mListener.onEvent();
                    }
                }
            });
        } else {
            calculAtk();
            setCritAndFail();
        }
    }

    private void setCritAndFail() {
        if (atkDice.getRandValue() == 1) {
            this.fail = true;
            atkDice.getImg().setOnClickListener(null);
        }
        int critMin;
        if (pj.getAllFeats().featIsActive("feat_improved_crit")) {
            critMin = 19;
        } else {
            critMin = 20;
        }
        if (atkDice.getRandValue() >= critMin) { //c'est possiblement un crit
            this.crit = true;
        }
    }

    public void calculAtk() {
        this.atk = this.preRandValue + atkDice.getRandValue();
        if (this.atkDice.getMythicDice() != null) {
            this.atk += this.atkDice.getMythicDice().getRandValue();
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
        atkDice.invalidate();
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

    public boolean isMissed() {
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

    public void setRefreshEventListener(OnRefreshEventListener eventListener) {
        mListener = eventListener;
    }

    public interface OnRefreshEventListener {
        void onEvent();
    }
}
