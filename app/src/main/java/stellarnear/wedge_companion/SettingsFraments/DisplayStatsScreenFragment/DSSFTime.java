package stellarnear.wedge_companion.SettingsFraments.DisplayStatsScreenFragment;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import stellarnear.wedge_companion.Elems.ElemsManager;
import stellarnear.wedge_companion.Perso.Perso;
import stellarnear.wedge_companion.Perso.PersoManager;
import stellarnear.wedge_companion.R;
import stellarnear.wedge_companion.Stats.Stat;
import stellarnear.wedge_companion.Stats.StatsList;
import stellarnear.wedge_companion.Tools;

public class DSSFTime {
    private Perso pj = PersoManager.getCurrentPJ();
    private Context mC;
    private View mainView;
    private ElemsManager elems;
    private List<String> elemsSelected;
    private LinkedHashMap<String, StatsList> mapDatetxtStatslist = new LinkedHashMap<>();
    private List<String> labelList = new ArrayList<>();
    private Map<String, CheckBox> mapElemCheckbox = new HashMap<>();
    private int infoTxtSize = 10;
    private LineChart chartAtk;
    private LineChart chartDmg;
    private DmgMode dmgMode = DmgMode.SUM;
    private Tools tools = Tools.getTools();

    public DSSFTime(View mainView, Context mC) {
        this.mainView = mainView;
        this.mC = mC;
        this.elems = ElemsManager.getInstance(mC);

        CheckBox checkPhy = mainView.findViewById(R.id.line_type_time_phy);
        CheckBox checkFire = mainView.findViewById(R.id.line_type_time_fire);
        CheckBox checkShock = mainView.findViewById(R.id.line_type_time_shock);
        CheckBox checkFrost = mainView.findViewById(R.id.line_type_time_frost);

        mapElemCheckbox.put("", checkPhy);  mapElemCheckbox.put("fire", checkFire);  mapElemCheckbox.put("shock", checkShock);  mapElemCheckbox.put("frost", checkFrost);
        setListeners();
        initLineCharts();
    }

    private void setListeners() {
        for (String elem : elems.getListKeysWedgeDamage()) {
            mapElemCheckbox.get(elem).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    calculateElemToShow();
                    resetChartDmg();
                    setDmgData();
                }
            });
        }
        mainView.findViewById(R.id.time_graph_moy_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmgMode=DmgMode.MOY;
                ((TextView)mainView.findViewById(R.id.time_graph_time_dmg_y_label)).setText("dmgMoy");
                resetChartDmg();
                setDmgData();
            }
        });
        mainView.findViewById(R.id.time_graph_moy_single_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmgMode=DmgMode.MOY_SINGLE;
                ((TextView)mainView.findViewById(R.id.time_graph_time_dmg_y_label)).setText("dmgMoy");
                resetChartDmg();
                setDmgData();
            }
        });
        mainView.findViewById(R.id.time_graph_sum_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dmgMode=DmgMode.SUM;
                ((TextView)mainView.findViewById(R.id.time_graph_time_dmg_y_label)).setText("dmgSum");
                resetChartDmg();
                setDmgData();
            }
        });
    }

    private void initLineCharts() {
        initLineChartAtk();
        calculateElemToShow();
        initLineChartDmg();
        buildCharts();
        chartAtk.animateXY(750, 1000);
        chartDmg.animateXY(750, 1000);
    }

    private void initLineChartAtk() {
        chartAtk = mainView.findViewById(R.id.line_chart_time);
        setChartPara(chartAtk);
        chartAtk.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC, e.getData().toString(), "center");
            }

            @Override
            public void onNothingSelected() {
                resetChartAtk();
            }
        });
    }

    private void setChartPara(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.getXAxis().setDrawGridLines(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1.0f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void calculateElemToShow() {
        elemsSelected = new ArrayList<>();
        for (String elem : elems.getListKeysWedgeDamage()) {
            if (mapElemCheckbox.get(elem).isChecked()) {
                elemsSelected.add(elem);
            }
        }
    }

    private void initLineChartDmg() {
        chartDmg = mainView.findViewById(R.id.line_chart_time_dmg);
        setChartPara(chartDmg);
        chartDmg.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tools.customToast(mC, e.getData().toString(), "center");
            }
            @Override
            public void onNothingSelected() {
                resetChartDmg();
            }
        });
    }

    private void buildCharts() {
        computeHashmaps();
        setAtkData();
        setDmgData();
        formatAxis(chartAtk);
        formatAxis(chartDmg);
    }

    private void formatAxis(LineChart chart) {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1.0f);
        leftAxis.setGranularityEnabled(true);

        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelList));
        xAxis.setLabelRotationAngle(-90);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);
    }

    private void computeHashmaps() {
        mapDatetxtStatslist = new LinkedHashMap<>();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy", Locale.FRANCE);
        for (Stat stat : pj.getStats().getStatsList().asList()) {
            String dateTxt = formater.format(stat.getDate());
            if (mapDatetxtStatslist.get(dateTxt) == null) {
                mapDatetxtStatslist.put(dateTxt, new StatsList());
            }
            mapDatetxtStatslist.get(dateTxt).add(stat);
        }
    }

    private void setAtkData() {
        labelList = new ArrayList<>();
        ArrayList<Entry> listValHit = new ArrayList<>();
        ArrayList<Entry> listValCrit = new ArrayList<>();
        ArrayList<Entry> listValCritNat = new ArrayList<>();
        int index = 0;
        for (String key : mapDatetxtStatslist.keySet()) {
            int sumHit = mapDatetxtStatslist.get(key).getNAtksHit();
            int nTot = mapDatetxtStatslist.get(key).getNAtksTot();
            int nCrit = mapDatetxtStatslist.get(key).getNCrit();
            int nCritNat = mapDatetxtStatslist.get(key).getNCritNat();

            listValHit.add(new Entry(index, (int) (100f * sumHit / nTot), (int) (100f * sumHit / nTot) + "% touché en moyenne le " + key));
            listValCrit.add(new Entry(index, (int) (100f * (nCrit - nCritNat) / nTot), (int) (100f * (nCrit - nCritNat) / nTot) + "% critique en moyenne le " + key));
            listValCritNat.add(new Entry(index, (int) (100f * nCritNat / nTot), (int) (100f * nCritNat / nTot) + "% critique naturel en moyenne le " + key));
            labelList.add(key);
            index++;
        }
        LineDataSet setHit = new LineDataSet(listValHit, "Hit");
        setLinePara(setHit, mC.getColor(R.color.hit_stat));

        LineDataSet setCrit = new LineDataSet(listValCrit, "Crit");
        setLinePara(setCrit, mC.getColor(R.color.crit_stat));

        LineDataSet setCritNat = new LineDataSet(listValCritNat, "CritNat");
        setLinePara(setCritNat, mC.getColor(R.color.crit_nat_stat));

        LineData data = new LineData();
        data.addDataSet(setHit);
        data.addDataSet(setCrit);
        data.addDataSet(setCritNat);
        data.setValueTextSize(infoTxtSize);
        chartAtk.setData(data);
    }

    private void setDmgData() {
        LineData data;
        if (elemsSelected.size() == 4) {
            data = getDmgDataSolo();
        } else {
            data = getDmgDataElems();
        }
        data.setValueTextSize(infoTxtSize);
        chartDmg.setData(data);
    }

    private LineData getDmgDataSolo() {
        ArrayList<Entry> listValDmg = new ArrayList<>();
        int index = 0;
        for (String key : mapDatetxtStatslist.keySet()) {
            if(dmgMode == DmgMode.MOY_SINGLE) {
                int dmg = mapDatetxtStatslist.get(key).getSumDmgTot()/mapDatetxtStatslist.get(key).getNAtksTot();
                listValDmg.add(new Entry(index, dmg, dmg + " dégâts en moyenne par flêche le " + key));
            } else if(dmgMode == DmgMode.MOY) {
                int dmg = mapDatetxtStatslist.get(key).getMoyDmg();
                listValDmg.add(new Entry(index, dmg, dmg + " dégâts en moyenne par cible le " + key));
            } else {
                int dmg = mapDatetxtStatslist.get(key).getSumDmgTot();
                listValDmg.add(new Entry(index, dmg, dmg + " dégâts au total le " + key));
            }
            index++;
        }
        LineDataSet setHit = new LineDataSet(listValDmg, "tout");
        setLinePara(setHit, mC.getColor(R.color.dmg_stat));
        LineData data = new LineData();
        data.addDataSet(setHit);
        return data;
    }

    private LineData getDmgDataElems() {
        LineData data = new LineData();
        for (String elem : elemsSelected) {
            ArrayList<Entry> listDmg = new ArrayList<>();
            int index = 0;
            for (String key : mapDatetxtStatslist.keySet()) {
                if(dmgMode == DmgMode.MOY_SINGLE) {
                    int dmg = mapDatetxtStatslist.get(key).getSumDmgTotElem(elem)/mapDatetxtStatslist.get(key).getNAtksTot();
                    listDmg.add(new Entry(index, dmg, dmg + " dégâts de " + elems.getName(elem) + " en moyenne par flêche le " + key));
                } else if(dmgMode == DmgMode.MOY) {
                    int dmg = mapDatetxtStatslist.get(key).getMoyDmgElem(elem);
                    listDmg.add(new Entry(index, dmg, dmg + " dégâts de " + elems.getName(elem) + " en moyenne par cible le " + key));
                } else {
                    int dmg = mapDatetxtStatslist.get(key).getSumDmgTotElem(elem);
                    listDmg.add(new Entry(index, dmg, dmg + " dégâts de " + elems.getName(elem) + " au total le " + key));
                }
                index++;
            }
            LineDataSet setVal = new LineDataSet(listDmg, elems.getName(elem));
            setLinePara(setVal, elems.getColorId(elem));
            data.addDataSet(setVal);
        }
        return data;
    }

    private void setLinePara(LineDataSet set, int color) {
        set.setColors(color);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setCircleColor(color);
        set.setValueFormatter(new LargeValueFormatter());
    }

    // Resets
    public void reset() {
        for (String elem : elems.getListKeysWedgeDamage()) {
            mapElemCheckbox.get(elem).setChecked(true);
        }
        resetChartAtk();
        resetChartDmg();
        buildCharts();
    }

    private void resetChartAtk() {
        chartAtk.invalidate();
        chartAtk.fitScreen();
        chartAtk.highlightValue(null);
    }

    private void resetChartDmg() {
        calculateElemToShow();
        chartDmg.invalidate();
        chartDmg.fitScreen();
        chartDmg.highlightValue(null);
    }

    //object
    public enum DmgMode {
        SUM,
        MOY_SINGLE,
        MOY
    }
}

