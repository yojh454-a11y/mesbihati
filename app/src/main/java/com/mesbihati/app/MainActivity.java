package com.mesbihati.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Typeface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.media.AudioManager;
import android.media.ToneGenerator;
import java.util.*;

public class MainActivity extends Activity {
    final int green=Color.rgb(17,82,58), gold=Color.rgb(197,157,75), cream=Color.rgb(249,246,235);
    int count=0; TextView counter, zikrView, vibrationToggle, soundToggle; LinearLayout root, content;
    boolean vibrationOn=true, soundOn=true; ToneGenerator tone;
    String currentZikr="سبحان الله";
    String[] morning={"أصبحنا وأصبح الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير.","اللهم بك أصبحنا وبك أمسينا وبك نحيا وبك نموت وإليك النشور.","رضيت بالله ربًا، وبالإسلام دينًا، وبمحمد ﷺ نبيًا."};
    String[] evening={"أمسينا وأمسى الملك لله، والحمد لله، لا إله إلا الله وحده لا شريك له، له الملك وله الحمد وهو على كل شيء قدير.","اللهم بك أمسينا وبك أصبحنا وبك نحيا وبك نموت وإليك المصير.","رضيت بالله ربًا، وبالإسلام دينًا، وبمحمد ﷺ نبيًا."};
    ArrayList<String> history=new ArrayList<>();
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(green); getWindow().setNavigationBarColor(green);
        count=getPreferences(0).getInt("count",0); vibrationOn=getPreferences(0).getBoolean("vibration",true); soundOn=getPreferences(0).getBoolean("sound",true); try{tone=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,70);}catch(Exception ignored){} showHome();}
    TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(17);t.setPadding(12,12,12,12);return t;}
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(16);b.setAllCaps(false);b.setBackground(bg(green,24));return b;}
    void base(String subtitle){root=new LinearLayout(this);root.setOrientation(1);root.setPadding(18,16,18,16);root.setBackgroundColor(cream);
        ScrollView sc=new ScrollView(this);sc.addView(root);setContentView(sc);
        TextView head=text("مِسْبَحَتِي",30,gold);head.setTypeface(null,Typeface.BOLD);root.addView(head);
        root.addView(text(subtitle,15,green));}
    int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
    TextView toggle(String icon,String label,boolean enabled){
        TextView t=text(icon+"  "+label+"\\n"+(enabled?"يعمل":"متوقف"),15,enabled?green:Color.GRAY);
        t.setTypeface(null,Typeface.BOLD);t.setBackground(bg(Color.WHITE,18));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(62),1);p.setMargins(dp(5),dp(8),dp(5),dp(8));return t;
    }
    void refreshToggles(){
        vibrationToggle.setText("📳  الهزاز\\n"+(vibrationOn?"يعمل":"متوقف"));vibrationToggle.setTextColor(vibrationOn?green:Color.GRAY);
        soundToggle.setText("🔊  الصوت\\n"+(soundOn?"يعمل":"متوقف"));soundToggle.setTextColor(soundOn?green:Color.GRAY);
    }
    void showHome(){base("رفيق ذكرك • يعمل دون إنترنت");
        TextView verse=text("﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",18,green);verse.setBackground(bg(Color.WHITE,24));root.addView(verse);
        zikrView=text(currentZikr,23,green);zikrView.setBackground(bg(Color.WHITE,24));root.addView(zikrView);
        LinearLayout switches=new LinearLayout(this);switches.setOrientation(0);switches.setGravity(Gravity.CENTER);
        vibrationToggle=toggle("📳","الهزاز",vibrationOn);soundToggle=toggle("🔊","الصوت",soundOn);
        switches.addView(vibrationToggle,new LinearLayout.LayoutParams(0,dp(62),1));switches.addView(soundToggle,new LinearLayout.LayoutParams(0,dp(62),1));root.addView(switches);
        vibrationToggle.setOnClickListener(v->{vibrationOn=!vibrationOn;getPreferences(0).edit().putBoolean("vibration",vibrationOn).apply();refreshToggles();});
        soundToggle.setOnClickListener(v->{soundOn=!soundOn;getPreferences(0).edit().putBoolean("sound",soundOn).apply();refreshToggles();});
        LinearLayout circle=new LinearLayout(this);circle.setOrientation(1);circle.setGravity(Gravity.CENTER);
        GradientDrawable cb=new GradientDrawable();cb.setShape(GradientDrawable.OVAL);cb.setColor(green);cb.setStroke(dp(7),gold);circle.setBackground(cb);circle.setElevation(dp(8));
        LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(dp(238),dp(238));cp.gravity=Gravity.CENTER;cp.setMargins(0,dp(18),0,dp(12));root.addView(circle,cp);
        counter=text(String.valueOf(count),58,Color.WHITE);counter.setTypeface(null,Typeface.BOLD);circle.addView(counter);
        circle.addView(text("اضغط للتسبيح",17,Color.rgb(245,220,160)));circle.setClickable(true);circle.setOnClickListener(v->increment());
        TextView reset=text("↺  تصفير العداد",16,green);reset.setBackground(bg(Color.WHITE,20));root.addView(reset);
        reset.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("تصفير العداد").setMessage("هل تريد تصفير عدد التسبيحات؟").setNegativeButton("إلغاء",null).setPositiveButton("تصفير",(d,w)->{count=0;counter.setText("0");getPreferences(0).edit().putInt("count",0).apply();}).show());
        Button change=button("تغيير الذكر");root.addView(change);change.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("اختر الذكر").setItems(new String[]{"سبحان الله","الحمد لله","الله أكبر","لا إله إلا الله","أستغفر الله"},(d,w)->{currentZikr=new String[]{"سبحان الله","الحمد لله","الله أكبر","لا إله إلا الله","أستغفر الله"}[w];zikrView.setText(currentZikr);}).show());
        Button morningBtn=button("أذكار الصباح");root.addView(morningBtn);morningBtn.setOnClickListener(v->showAzkar("أذكار الصباح",morning));
        Button eveningBtn=button("أذكار المساء");root.addView(eveningBtn);eveningBtn.setOnClickListener(v->showAzkar("أذكار المساء",evening));
        Button add=button("إضافة ذكر خاص");root.addView(add);add.setOnClickListener(v->addZikr());
        Button hist=button("سجل التسبيحات");root.addView(hist);hist.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("سجل التسبيحات").setMessage("إجمالي تسبيحاتك المحفوظة: "+count).setPositiveButton("حسنًا",null).show());
        root.addView(text("نسأل الله أن يعيننا على ذكره وشكره وحسن عبادته",13,green));
    }
    void increment(){
        count++;counter.setText(String.valueOf(count));getPreferences(0).edit().putInt("count",count).apply();
        if(vibrationOn){try{android.os.Vibrator vb=(android.os.Vibrator)getSystemService(VIBRATOR_SERVICE);if(vb!=null&&vb.hasVibrator()){if(android.os.Build.VERSION.SDK_INT>=26)vb.vibrate(android.os.VibrationEffect.createOneShot(35,android.os.VibrationEffect.DEFAULT_AMPLITUDE));else vb.vibrate(35);}}catch(Exception ignored){}}
        if(soundOn&&tone!=null){try{tone.startTone(ToneGenerator.TONE_PROP_BEEP,90);}catch(Exception ignored){}}
    }
    void showAzkar(String title,String[] list){base(title);for(String z:list){TextView t=text(z,19,green);t.setBackground(bg(Color.WHITE,20));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,10,0,10);root.addView(t,p);}Button back=button("العودة للرئيسية");root.addView(back);back.setOnClickListener(v->showHome());}
    void addZikr(){EditText e=new EditText(this);e.setHint("اكتب الذكر هنا");new AlertDialog.Builder(this).setTitle("إضافة ذكر خاص").setView(e).setPositiveButton("حفظ",(d,w)->{String s=e.getText().toString().trim();if(!s.isEmpty()){currentZikr=s;showHome();}}).setNegativeButton("إلغاء",null).show();}
}
