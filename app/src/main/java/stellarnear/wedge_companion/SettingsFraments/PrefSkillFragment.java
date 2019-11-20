package stellarnear.wedge_companion.SettingsFraments;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceCategory;
import android.text.InputType;

import stellarnear.wedge_companion.EditTextPreference;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.Perso.Skill;


public class PrefSkillFragment {
        private Perso pj = PersoManager.getCurrentPJ();
        private Activity mA;
        private Context mC;

        public PrefSkillFragment(Activity mA, Context mC){
            this.mA=mA;
            this.mC=mC;
        }


    public void addSkillsList(PreferenceCategory rank,PreferenceCategory bonus ) {
        for (Skill skill : pj.getAllSkills().getSkillsList()) {
            EditTextPreference pref = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
            pref.setKey(skill.getId() + "_rank"+pj.getID());
            pref.setTitle(skill.getName());
            int rankDefId = mC.getResources().getIdentifier(skill.getId() + "_rankDEF"+pj.getID(), "integer", mC.getPackageName());
            int rankDef = mC.getResources().getInteger(rankDefId);
            pref.setDefaultValue(String.valueOf(rankDef));
            pref.setSummary("Valeur : %s");
            rank.addPreference(pref);
            EditTextPreference pref_bonus = new EditTextPreference(mC, InputType.TYPE_CLASS_TEXT);
            pref_bonus.setKey(skill.getId() + "_bonus"+pj.getID());
            pref_bonus.setTitle(skill.getName());
            int bonusDefId = mC.getResources().getIdentifier(skill.getId() + "_bonusDEF"+pj.getID(), "integer", mC.getPackageName());
            int bonusDef = mC.getResources().getInteger(bonusDefId);
            pref_bonus.setDefaultValue(String.valueOf(bonusDef));
            pref_bonus.setSummary("Valeur : %s");
            bonus.addPreference(pref_bonus);
        }
       
    }
}