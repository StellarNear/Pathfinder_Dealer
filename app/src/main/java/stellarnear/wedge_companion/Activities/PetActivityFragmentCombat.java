package stellarnear.wedge_companion.Activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;

import stellarnear.wedge_companion.DisplayRolls;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.PetTextFilling.PetDamages;
import stellarnear.wedge_companion.PetTextFilling.PetPostRandValues;
import stellarnear.wedge_companion.PetTextFilling.PetPreRandValues;
import stellarnear.wedge_companion.PetTextFilling.PetRangesAndProba;
import stellarnear.wedge_companion.PetTextFilling.PetSetupCheckboxes;
import stellarnear.wedge_companion.PostData;
import stellarnear.wedge_companion.PostDataElement;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Rolls.PetRollFactory;
import stellarnear.wedge_companion.Rolls.Roll;
import stellarnear.wedge_companion.Rolls.RollList;
import stellarnear.wedge_companion.Tools;

public class PetActivityFragmentCombat extends Fragment {
    public Perso pj = PersoManager.getCurrentPJ();
    private View mainPage;
    private ImageButton fabAtk;
    private ImageButton fabDmg;
    private ImageButton fabDmgDet;

    private RollList rollList;
    private RollList selectedRolls;
    private boolean firstDmgRoll = true;
    private boolean firstAtkRoll = true;
    private String mode;

    private PetPreRandValues preRandValues;
    private PetPostRandValues postRandValues;
    private PetSetupCheckboxes setupCheckboxes;
    private PetDamages damages;
    private PetRangesAndProba rangesAndProba;
    private DisplayRolls displayRolls;

    private Tools tools = Tools.getTools();


    public PetActivityFragmentCombat() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (container != null) {
            container.removeAllViews();
        }

        mainPage = inflater.inflate(R.layout.fragment_main_attack, container, false);

        ImageButton buttonMain = mainPage.findViewById(R.id.button_frag_combat_to_main);
        animate(buttonMain);
        View.OnClickListener listnerBackToMain = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PetActivityFragment();
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.infadefrag, R.animator.outtobotfrag);
                fragmentTransaction.replace(R.id.fragment_main_frame_layout, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };
        buttonMain.setOnClickListener(listnerBackToMain);
        setupScreen();
        return mainPage;
    }

    private void animate(final ImageButton buttonMain) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Animation anim = new ScaleAnimation(1f, 1.25f, 1f, 1.25f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                anim.setRepeatCount(1);
                anim.setRepeatMode(Animation.REVERSE);
                anim.setDuration(666);
                buttonMain.startAnimation(anim);
            }
        }, getResources().getInteger(R.integer.translationFragDuration));


    }

    public void setupScreen() {
        mode = "fullround";
        firstDmgRoll = true;
        firstAtkRoll = true;
        mainPage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                startPreRand();
                return true;//always return true to consume event
            }
        });

        mainPage.findViewById(R.id.button_simple_atk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstAtkRoll) {
                    mode = "simple";
                    startRandAtk();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer les jets d'attaque ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mode = "simple";
                                    startRandAtk();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        if (pj.getAllCapacities().capacityIsActive("capacity_leap")) {
            mainPage.findViewById(R.id.button_jump_attack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (firstAtkRoll) {
                        mode = "leap";
                        startRandAtk();
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Demande de confirmation")
                                .setMessage("Voulez vous relancer les jets d'attaque ?")
                                .setIcon(android.R.drawable.ic_menu_help)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        mode = "leap";
                                        startRandAtk();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, null).show();
                    }
                }
            });
        } else {
            mainPage.findViewById(R.id.button_jump_attack).setVisibility(View.GONE);
        }


        fabAtk = mainPage.findViewById(R.id.fabAtk);
        setListenerFabAtk();
        fabDmg = mainPage.findViewById(R.id.fab_damage);
        setListenerFabDmg();
        fabDmgDet = mainPage.findViewById(R.id.fab_damage_detail);
        setListenerFabDmgDet();

    }

    private void setListenerFabAtk() {
        fabAtk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firstAtkRoll) {
                    mode = "fullround";
                    startRandAtk();
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer les jets d'attaque ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    mode = "fullround";
                                    startRandAtk();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void clearStep(Integer step) {
        switch (step) {
            case 0:
                if (mainPage.getBackground() != null) {
                    mainPage.setBackground(null);
                }
                mainPage.setOnTouchListener(null);
                if (preRandValues != null) {
                    preRandValues.hideViews();
                }
            case 1:
                if (postRandValues != null) {
                    postRandValues.hideViews();
                }
                if (setupCheckboxes != null) {
                    setupCheckboxes.hideViews();
                    displayRolls = null;
                }
            case 2:
                if (damages != null) {
                    damages.hideViews();
                }
            case 3:
                if (rangesAndProba != null) {
                    rangesAndProba.hideViews();
                }
            case 4:
                if (displayRolls == null || displayRolls.size() == 0) {
                    fabDmgDet.setEnabled(false);
                }
        }
    }

    private void startPreRand() {
        clearStep(0);
        this.rollList = new PetRollFactory(getActivity(), getContext(), mode).getRollList();
        preRandValues = new PetPreRandValues(getContext(), mainPage, rollList);
        if (damages != null) {
            damages.hideViews();
        }
    }

    private void startRandAtk() {
        firstAtkRoll = false;
        firstDmgRoll = true;
        fabAtk.animate().setDuration(1000).translationX(-400).start();       //decale le bouton à gauche pour l'apparition du suivant
        startPreRand();
        if (postRandValues != null) {
            postRandValues.hideViews();
        }
        if (setupCheckboxes != null) {
            setupCheckboxes.hideViews();
        }
        postRandValues = new PetPostRandValues(getContext(), mainPage, rollList);
        setupCheckboxes = new PetSetupCheckboxes(getContext(), mainPage, rollList);
    }

    private void setListenerFabDmg() {
        fabDmg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabDmg.animate().setDuration(1000).translationX(+400).start();
                AlphaAnimation anim = new AlphaAnimation(0, 1);
                anim.setDuration(2000);

                checkSelectedRolls();
                if (firstDmgRoll) {
                    startDamage();
                    firstDmgRoll = false;
                } else {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Demande de confirmation")
                            .setMessage("Voulez vous relancer des jets de dégâts pour l'attaque en cours ?")
                            .setIcon(android.R.drawable.ic_menu_help)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    startDamage();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    private void startDamage() {
        displayRolls = null;
        clearStep(2);
        damages = new PetDamages(getActivity(), getContext(), mainPage, selectedRolls);  //calcul et affiche les degats
        if (selectedRolls.getDmgDiceList().getList().size() > 0) {
            rangesAndProba = new PetRangesAndProba(getContext(), mainPage, selectedRolls);
            fabDmgDet.setVisibility(View.VISIBLE);
            fabDmgDet.setEnabled(true);
        }
        postRandValues.refreshPostRandValues();
        new PostData(getContext(), new PostDataElement(selectedRolls, "dmg"));
    }

    private void checkSelectedRolls() {
        selectedRolls = new RollList();
        for (Roll roll : rollList.getList()) {
            if (roll.isInvalid() || (!roll.isHitConfirmed() && !roll.isMissed())) {
                continue;
            }
            selectedRolls.add(roll);
        }
    }

    private void setListenerFabDmgDet() {
        fabDmgDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayRolls == null) {
                    displayRolls = new DisplayRolls(getActivity(), getContext(), selectedRolls);
                }
                displayRolls.showPopup();
            }
        });

    }
}
