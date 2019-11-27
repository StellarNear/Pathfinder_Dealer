package stellarnear.wedge_companion.PetTextFilling;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class PetPreRandValues {
    private Context mC;
    private View mainView;
    private RollList rollList;
    public PetPreRandValues(Context mC, View mainView, RollList rollList){
        this.mC=mC;
        this.mainView=mainView;
        this.rollList=rollList;
        addPreRandValues();
    }

    public void hideViews(){
        ((TextView)mainView.findViewById(R.id.mainLinearNAtk)).setVisibility(View.GONE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPreRand)).setVisibility(View.GONE);
    }

    private void addPreRandValues() {
        TextView nAtt = (TextView) mainView.findViewById(R.id.mainLinearNAtk);
        nAtt.setText(String.valueOf(rollList.getList().size()+" attaques"));
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPreRand)).setVisibility(View.VISIBLE);
        ((TextView)mainView.findViewById(R.id.mainLinearNAtk)).setVisibility(View.VISIBLE);
        ((LinearLayout)mainView.findViewById(R.id.mainLinearPreRand)).removeAllViews();
        for(Roll roll : rollList.getList()){
            LinearLayout scoreBox = new LinearLayout(mC);
            scoreBox.setLayoutParams(new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,0, 1));
            scoreBox.setGravity(Gravity.CENTER);
            TextView score = new TextView(mC);
            score.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            score.setGravity(Gravity.CENTER);
            score.setTextColor(Color.DKGRAY);
            score.setTextSize(22);
            score.setText("+"+String.valueOf(roll.getPreRandValue()));
            scoreBox.addView(score);
            ((LinearLayout)mainView.findViewById(R.id.mainLinearPreRand)).addView(scoreBox);
        }
    }
}