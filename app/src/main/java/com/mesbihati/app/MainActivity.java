package com.mesbihati.app;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.*;
import android.media.*;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
    final int bg=Color.rgb(4,48,39), panel=Color.rgb(0,38,31), green=Color.rgb(38,205,91), gold=Color.rgb(195,157,78), white=Color.WHITE;
    final Handler handler=new Handler(Looper.getMainLooper());
    final ArrayList<Dhikr> adhkar=new ArrayList<>();
    int count=0,target=100,interval=1; boolean soundOn=true,vibrationOn=true,running=false;
    String currentName="سبحان الله";
    TextView counter,nameView,progressView,statusView,startButton;
    ToneGenerator tone;
    Runnable ticker;
    SharedPreferences prefs;

    static class Dhikr {
        String name; int seconds,target;
        Dhikr(String n,int s,int t){name=n;seconds=s;target=t;}
    }
    @Override public void onCreate(Bundle b){super.onCreate(b);
        prefs=getSharedPreferences("mesbihati",0);
        getWindow().setStatusBarColor(bg);getWindow().setNavigationBarColor(bg);
        count=prefs.getInt("count",0);soundOn=prefs.getBoolean("sound",true);vibrationOn=prefs.getBoolean("vibration",true);
        currentName=prefs.getString("currentName","سبحان الله");interval=prefs.getInt("interval",1);target=prefs.getInt("target",100);
        loadAdhkar();
        try{tone=new ToneGenerator(AudioManager.STREAM_NOTIFICATION,65);}catch(Exception ignored){}
        showHome();
    }
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    TextView label(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER);t.setPadding(dp(8),dp(7),dp(8),dp(7));return t;}
    Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextColor(white);b.setTextSize(15);b.setAllCaps(false);b.setBackground(shape(Color.rgb(0,67,53),28));return b;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER);return l;}
    void persist(){prefs.edit().putInt("count",count).putInt("target",target).putInt("interval",interval).putString("currentName",currentName).putBoolean("sound",soundOn).putBoolean("vibration",vibrationOn).apply();}
    void loadAdhkar(){adhkar.clear();String raw=prefs.getString("adhkar","[]");try{JSONArray a=new JSONArray(raw);for(int i=0;i<a.length();i++){JSONObject o=a.getJSONObject(i);adhkar.add(new Dhikr(o.getString("name"),o.getInt("seconds"),o.getInt("target")));}}catch(Exception ignored){}
        if(adhkar.isEmpty()){adhkar.add(new Dhikr("سبحان الله",1,100));adhkar.add(new Dhikr("الحمد لله",2,100));adhkar.add(new Dhikr("أستغفر الله",3,100));saveAdhkar();}
    }
    void saveAdhkar(){JSONArray a=new JSONArray();try{for(Dhikr d:adhkar){JSONObject o=new JSONObject();o.put("name",d.name);o.put("seconds",d.seconds);o.put("target",d.target);a.put(o);}}catch(Exception ignored){}prefs.edit().putString("adhkar",a.toString()).apply();}
    void base(String subtitle){LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(16),dp(12),dp(16),dp(12));root.setBackgroundColor(bg);
        ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.addView(root);setContentView(sc);
        TextView head=label("مِسْبَحَتِي",27,gold);head.setTypeface(null,1);root.addView(head);
        root.addView(label(subtitle,13,Color.rgb(190,215,203)));
        content=root;
    }
    LinearLayout content;
    void showHome(){
        stopAuto();base("رفيق ذكرك • يعمل دون إنترنت");
        TextView quote=label("﴿ أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ ﴾",17,gold);content.addView(quote);
        nameView=label(currentName,22,white);nameView.setBackground(shape(panel,18));content.addView(nameView);
        LinearLayout toggles=row();
        TextView vib=label((vibrationOn?"📳 الهزاز: يعمل":"📳 الهزاز: متوقف"),14,vibrationOn?green:Color.LTGRAY);vib.setBackground(shape(panel,18));
        TextView snd=label((soundOn?"♪ الصوت: يعمل":"♪ الصوت: متوقف"),14,soundOn?green:Color.LTGRAY);snd.setBackground(shape(panel,18));
        LinearLayout.LayoutParams half=new LinearLayout.LayoutParams(0,dp(55),1);half.setMargins(dp(4),dp(6),dp(4),dp(6));toggles.addView(vib,half);toggles.addView(snd,half);content.addView(toggles);
        vib.setOnClickListener(v->{vibrationOn=!vibrationOn;persist();showHome();});
        snd.setOnClickListener(v->{soundOn=!soundOn;persist();showHome();});
        LinearLayout circle=new LinearLayout(this);circle.setOrientation(1);circle.setGravity(Gravity.CENTER);
        GradientDrawable cd=new GradientDrawable();cd.setShape(GradientDrawable.OVAL);cd.setColor(panel);cd.setStroke(dp(5),green);circle.setBackground(cd);
        circle.setElevation(dp(5));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(dp(250),dp(250));cp.gravity=Gravity.CENTER;cp.setMargins(0,dp(10),0,dp(8));content.addView(circle,cp);
        counter=label(arabic(count),58,green);counter.setTypeface(null,1);circle.addView(counter);
        circle.addView(label("اضغط للتسبيح",14,Color.rgb(190,215,203)));
        circle.setOnClickListener(v->increment());
        progressView=label("التقدم: "+count+" / "+target,14,green);content.addView(progressView);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);bar.setMax(Math.max(1,target));bar.setProgress(Math.min(count,target));content.addView(bar,new LinearLayout.LayoutParams(-1,dp(8)));
        statusView=label("مدة التسبيحة: "+interval+" ثانية",14,Color.LTGRAY);content.addView(statusView);
        LinearLayout actions=row();
        Button start=btn(running?"إيقاف مؤقت":"▶ بدء تلقائي");Button reset=btn("↻");
        LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(0,dp(52),1);ap.setMargins(dp(4),dp(8),dp(4),dp(8));actions.addView(start,ap);actions.addView(reset,new LinearLayout.LayoutParams(dp(60),dp(52)));content.addView(actions);
        start.setOnClickListener(v->{if(running)stopAuto();else startAuto();showHome();});
        reset.setOnClickListener(v->confirmReset());
        LinearLayout bottom=row();
        Button settings=btn("⚙ الإعدادات");Button list=btn("☷ الأذكار");Button minus=btn("−");Button clear=btn("↻");
        bottom.addView(settings,new LinearLayout.LayoutParams(0,dp(50),2));bottom.addView(list,new LinearLayout.LayoutParams(0,dp(50),2));bottom.addView(minus,new LinearLayout.LayoutParams(0,dp(50),1));bottom.addView(clear,new LinearLayout.LayoutParams(0,dp(50),1));content.addView(bottom);
        settings.setOnClickListener(v->editDhikr(findCurrent(),false));list.setOnClickListener(v->showDhikrList());
        minus.setOnClickListener(v->{if(count>0){count--;persist();refreshCounter();}});
        clear.setOnClickListener(v->confirmReset());
        content.addView(label("يمكنك التسبيح بالضغط على الدائرة أو تشغيل العد التلقائي",12,Color.LTGRAY));
    }
    String arabic(int n){return String.format(java.util.Locale.forLanguageTag("ar"),"%d",n);}
    Dhikr findCurrent(){for(Dhikr d:adhkar)if(d.name.equals(currentName))return d;return new Dhikr(currentName,interval,target);}
    void refreshCounter(){if(counter!=null){counter.setText(arabic(count));progressView.setText("التقدم: "+count+" / "+target);}}
    void increment(){count++;if(count>=target){count=target;stopAuto();}persist();refreshCounter();feedback();}
    void feedback(){if(vibrationOn)try{android.os.Vibrator v=(android.os.Vibrator)getSystemService(VIBRATOR_SERVICE);if(v!=null&&v.hasVibrator()){if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(28,75));else v.vibrate(28);}}catch(Exception ignored){}
        if(soundOn&&tone!=null)try{tone.startTone(ToneGenerator.TONE_PROP_ACK,75);}catch(Exception ignored){}}
    void startAuto(){if(running)return;if(count>=target)count=0;running=true;statusView.setText("العد التلقائي يعمل • كل "+interval+" ثانية");scheduleTick();}
    void scheduleTick(){if(ticker!=null)handler.removeCallbacks(ticker);ticker=()->{if(!running)return;increment();if(running)scheduleTick();};handler.postDelayed(ticker,Math.max(1,interval)*1000L);}
    void stopAuto(){running=false;if(ticker!=null)handler.removeCallbacks(ticker);ticker=null;}
    void confirmReset(){new AlertDialog.Builder(this).setTitle("البدء من جديد").setMessage("هل تريد البدء من جديد؟").setNegativeButton("لا",null).setPositiveButton("نعم",(d,w)->{stopAuto();count=0;persist();showHome();}).show();}
    void showDhikrList(){base("الأذكار المحفوظة ومؤقتاتها");
        Button add=btn("+ إضافة ذكر جديد");content.addView(add);add.setOnClickListener(v->editDhikr(null,true));
        for(Dhikr d:new ArrayList<>(adhkar)){LinearLayout item=new LinearLayout(this);item.setOrientation(1);item.setPadding(dp(8),dp(6),dp(8),dp(6));item.setBackground(shape(panel,16));
            TextView title=label(d.name,19,white);item.addView(title);item.addView(label("كل "+d.seconds+" ثانية • الهدف "+d.target,13,green));
            LinearLayout buttons=row();Button use=btn("اختيار");Button edit=btn("تعديل");Button del=btn("حذف");buttons.addView(use,new LinearLayout.LayoutParams(0,dp(44),1));buttons.addView(edit,new LinearLayout.LayoutParams(0,dp(44),1));buttons.addView(del,new LinearLayout.LayoutParams(0,dp(44),1));item.addView(buttons);
            LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,-2);ip.setMargins(0,dp(6),0,dp(6));content.addView(item,ip);
            use.setOnClickListener(v->{stopAuto();currentName=d.name;interval=d.seconds;target=d.target;count=0;persist();showHome();});
            edit.setOnClickListener(v->editDhikr(d,false));
            del.setOnClickListener(v->new AlertDialog.Builder(this).setMessage("هل تريد حذف هذا الذكر؟").setNegativeButton("لا",null).setPositiveButton("نعم",(x,y)->{adhkar.remove(d);saveAdhkar();showDhikrList();}).show());
        }
        Button back=btn("العودة إلى المسبحة");content.addView(back);back.setOnClickListener(v->showHome());
    }
    void editDhikr(Dhikr old,boolean isNew){
        LinearLayout form=new LinearLayout(this);form.setPadding(dp(12),dp(4),dp(12),dp(4));form.setOrientation(1);
        EditText name=new EditText(this);name.setSingleLine(true);name.setHint("اسم الذكر أو التسبيح");name.setText(old==null?"":old.name);form.addView(name);
        EditText seconds=new EditText(this);seconds.setInputType(2);seconds.setHint("المدة بين كل تسبيحة بالثواني");seconds.setText(String.valueOf(old==null?1:old.seconds));form.addView(seconds);
        EditText goal=new EditText(this);goal.setInputType(2);goal.setHint("العدد المستهدف");goal.setText(String.valueOf(old==null?100:old.target));form.addView(goal);
        new AlertDialog.Builder(this).setTitle(isNew?"إضافة ذكر محفوظ":"إعدادات الذكر").setView(form).setNegativeButton("إلغاء",null).setPositiveButton("حفظ",(dialog,which)->{
            String n=name.getText().toString().trim();int sec=parse(seconds.getText().toString(),1),t=parse(goal.getText().toString(),100);
            if(n.isEmpty()){Toast.makeText(this,"اكتب اسم الذكر أولاً",Toast.LENGTH_SHORT).show();return;}
            sec=Math.max(1,Math.min(sec,3600));t=Math.max(1,Math.min(t,1000000));
            if(isNew){adhkar.add(new Dhikr(n,sec,t));currentName=n;count=0;}
            else if(old!=null){old.name=n;old.seconds=sec;old.target=t;currentName=n;}
            interval=sec;target=t;persist();saveAdhkar();showHome();
        }).show();
    }
    int parse(String s,int fallback){try{return Integer.parseInt(s.trim());}catch(Exception e){return fallback;}}
    @Override protected void onPause(){super.onPause();persist();}
    @Override protected void onDestroy(){stopAuto();if(tone!=null)tone.release();super.onDestroy();}
}
