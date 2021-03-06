package stellarnear.wedge_companion.PetTextFilling;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;

public class PetSetupCheckboxes {
    private Context mC;
    private View mainView;
    private RollList rollList;

    public PetSetupCheckboxes(Context mC, View mainView, RollList rollList) {
        this.mC = mC;
        this.mainView = mainView;
        this.rollList = rollList;
        showViews();
        addCheckboxes();
    }

    public void hideViews() {
        mainView.findViewById(R.id.mainLinearCheckboxCritTitle).setVisibility(View.GONE);
        mainView.findViewById(R.id.mainLinearCheckboxHitTitle).setVisibility(View.GONE);
        mainView.findViewById(R.id.mainLinearHitCheckbox).setVisibility(View.GONE);
        mainView.findViewById(R.id.mainLinearCritCheckbox).setVisibility(View.GONE);
    }

    private void showViews() {
        mainView.findViewById(R.id.mainLinearCheckboxHitTitle).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.mainLinearHitCheckbox).setVisibility(View.VISIBLE);
        mainView.findViewById(R.id.mainLinearCritCheckbox).setVisibility(View.VISIBLE);
    }

    private void addCheckboxes() {
        ((LinearLayout) mainView.findViewById(R.id.mainLinearHitCheckbox)).removeAllViews();
        for (Roll roll : rollList.getList()) {
            LinearLayout frame = new LinearLayout(mC);
            frame.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1);
            frame.setLayoutParams(params);
            if (!roll.isInvalid() && !roll.isFailed()) {
                frame.addView(roll.getHitCheckbox());
            }
            ((LinearLayout) mainView.findViewById(R.id.mainLinearHitCheckbox)).addView(frame);
        }

        ((LinearLayout) mainView.findViewById(R.id.mainLinearCritCheckbox)).removeAllViews();
        if (rollList.haveAnyCritValid()) {
            mainView.findViewById(R.id.mainLinearCheckboxCritTitle).setVisibility(View.VISIBLE);
            for (final Roll roll : rollList.getList()) {
                LinearLayout frame = new LinearLayout(mC);
                frame.setGravity(Gravity.CENTER);
                frame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0, 1));
                if (!roll.isInvalid() && !roll.isFailed() && roll.isCrit()) {
                    CheckBox check = roll.getCritCheckbox();
                    frame.addView(check);
                    Animation animCheck = AnimationUtils.loadAnimation(mC, R.anim.zoomin);
                    check.startAnimation(animCheck);
                }
                ((LinearLayout) mainView.findViewById(R.id.mainLinearCritCheckbox)).addView(frame);
            }
        }
    }
}
