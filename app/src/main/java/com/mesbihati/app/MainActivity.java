package com.mesbihati.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;

public class MainActivity extends Activity {
    final int green=Color.rgb(17,82,58), gold=Color.rgb(197,157,75), cream=Color.rgb(249,246,235);
    int count=0; TextView counter, zikrView, status; LinearLayout root, content;
    String currentZikr="سبحان الله";
    String[] morning={"أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير.","اللهم بك أصبحنا وبك أمسينا وبك نحيا وبك نموت وإليك النشور.","رضيت بالله ربًا، وبالإسلام دينًا، وبمحمد ﷺ نبيًا."};
    String[] evening={"أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير.","اللهم بك أمسينا وبك أصبحنا وبك نحيا وبك نموت وإليك المصير.","رضيت بالله ربًا، وبالإسلام دينًا، وبمحمد ﷺ نبيًا."};
    ArrayList<String> history=new ArrayList<>();
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(green); getWindow().setNavigationBarColor(green);
        count=getPreferences(0).getInt("count",0); showHome();}
    TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(17);t.setPadding(12,12,12,12);return t;}
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(16);b.setAllCaps(false);b.setBackground(bg(green,24));return b;}
    void base(String subtitle){root=new LinearLayout(this);root.setOrientation(1);root.setPadding(18,16,18,16);root.setBackgroundColor(cream);
        ScrollView sc=new ScrollView(this);sc.addView(root);setContentView(sc);
        TextView head=text("مِسْبَحَتِي",30,gold);head.setTypeface(null,Typeface.BOLD);root.addView(head);
        root.addView(text(subtitle,15,green));}
    void showHome(){base("رفيق ذكرك • يعمل دون إنترنت");
        TextView card=text("﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",19,green);card.setBackground(bg(Color.WHITE,24));root.addView(card);
        root.addView(text("التسبيح الحالي",17,green));
        zikrView=text(currentZikr,23,green);zikrView.setBackground(bg(Color.WHITE,24));root.addView(zikrView);
        counter=text(String.valueOf(count),54,gold);counter.setTypeface(null,Typeface.BOLD);root.addView(counter);
        Button tas=button("＋  تسبيح");tas.setTextSize(22);root.addView(tas);tas.setOnClickListener(v->{count++;counter.setText(""+count);getPreferences(0).edit().putInt("count",count).apply();});
        Button reset=button("تصفير العداد");root.addView(reset);reset.setOnClickListener(v->{count=0;counter.setText("0");getPreferences(0).edit().putInt("count",0).apply();});
        Button change=button("تغيير الذكر");root.addView(change);change.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("اختر الذكر").setItems(new String[]{"سبحان الله","الحمد لله","الله أكبر","لا إله إلا الله","أستغفر الله"},(d,w)->{currentZikr=new String[]{"سبحان الله","الحمد لله","الله أكبر","لا إله إلا الله","أستغفر الله"}[w];zikrView.setText(currentZikr);}).show());
        Button morningBtn=button("أذكار الصباح");root.addView(morningBtn);morningBtn.setOnClickListener(v->showAzkar("أذكار الصباح",morning));
        Button eveningBtn=button("أذكار المساء");root.addView(eveningBtn);eveningBtn.setOnClickListener(v->showAzkar("أذكار المساء",evening));
        Button add=button("إضافة ذكر خاص");root.addView(add);add.setOnClickListener(v->addZikr());
        Button hist=button("سجل التسبيحات");root.addView(hist);hist.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("سجل التسبيحات").setMessage("إجمالي تسبيحاتك المحفوظة: "+count).setPositiveButton("حسنًا",null).show());
        root.addView(text("نسأل الله أن يعيننا على ذكره وشكره وحسن عبادته",13,green));
    }
    void showAzkar(String title,String[] list){base(title);for(String z:list){TextView t=text(z,19,green);t.setBackground(bg(Color.WHITE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,10,0,10);root.addView(t,p);}Button back=button("العودة للرئيسية");root.addView(back);back.setOnClickListener(v->showHome());}
    void addZikr(){EditText e=new EditText(this);e.setHint("اكتب الذكر هنا");new AlertDialog.Builder(this).setTitle("إضافة ذكر خاص").setView(e).setPositiveButton("حفظ",(d,w)->{String s=e.getText().toString().trim();if(!s.isEmpty()){currentZikr=s;showHome();}}).setNegativeButton("إلغاء",null).show();}
}
