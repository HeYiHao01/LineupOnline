package com.example.lineuponline;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lineuponline.Util.DBUtil;
import com.example.lineuponline.Util.Loading;
import com.example.lineuponline.Util.Order;
import com.example.lineuponline.Util.OrderDAO;
import com.example.lineuponline.Util.User;
import com.example.lineuponline.Util.UserDAO;

import java.sql.Connection;
import java.util.Calendar;

public class Reservation extends AppCompatActivity implements DatePicker.OnDateChangedListener,View.OnClickListener {
    private TextView position;
    private TextView count;
    private Spinner section;
    private Spinner hospital;
    private Spinner doctor;
    private EditText userTel;
    private ArrayAdapter<String> sectionAdapter;
    private ArrayAdapter<String> hospitalAdapter;
    private ArrayAdapter<String> doctorlAdapter;

    //private SwipeRefreshLayout refreshLayout;
    private DatePicker datePicker;
    private Button reserve;

    private TextView tvDate;
    //在TextView上显示的字符
    private StringBuffer date;
    private int year, month, day;
    private LinearLayout llDate;
    private Context context;

    private SharedPreferences shrf;
    private int id;

    private Dialog logDialog;

    String hospitals[] = {"马鞍山市第四人民医院","马鞍山市诗诚骨伤医院","马鞍山市中医院(北院)","马鞍山市人民医院","马鞍山市中心医院"};
    String sections1[] = {"内科","外科","妇产科","男科","儿科","肿瘤科","皮肤性病科","中医科"};
    String sections2[] = {"内科","外科","妇产科","男科","儿科","肿瘤科","皮肤性病科","中医科"};
    String sections3[] = {"内科","外科","妇产科","男科","肿瘤科","皮肤性病科"};
    String sections4[] = {"内科","外科","妇产科","男科","儿科","肿瘤科"};
    String sections5[] = {"内科","外科","妇产科","男科","肿瘤科","中医科"};
    String doctors1_1[] = {"张超","李立龙","张勇"};
    String doctors1_2[] = {"桂清华","唐立宇"};
    String doctors1_3[] = {"唐自爱","杨烨"};
    String doctors1_4[] = {"宋汉明","吴华"};
    String doctors1_5[] = {"王敏慧","夏桂兰"};
    String doctors1_6[] = {"吴承亮","黄俊"};
    String doctors1_7[] = {"唐的木"};
    String doctors1_8[] = {"沙静"};
    String doctors2_1[] = {"张平","王红玲"};
    String doctors2_2[] = {"胡先发","葛晓忠"};
    String doctors2_3[] = {"王金华","林浩"};
    String doctors2_4[] = {"杨洋","邓建"};
    String doctors2_5[] = {"邓吉安"};
    String doctors2_6[] = {"唐李玉","施志坚"};
    String doctors2_7[] = {"朱丽霞","朱立新"};
    String doctors2_8[] = {"吴军","钟康"};
    String doctors3_1[] = {"祝立新","郑磊"};
    String doctors3_2[] = {"顾东伟","王海燕"};
    String doctors3_3[] = {"谢向良"};
    String doctors3_4[] = {"梁明明"};
    String doctors3_5[] = {"冯玉兰"};
    String doctors3_6[] = {"陈伟鸿"};
    String doctors4_1[] = {"何良军","胡邦平"};
    String doctors4_2[] = {"丁少卿","冯朝涵","凤兆海"};
    String doctors4_3[] = {"李美英"};
    String doctors4_4[] = {"焦宝珠"};
    String doctors4_5[] = {"汤晓菲"};
    String doctors4_6[] = {"钱光荣"};
    String doctors5_1[] = {"方勤柳","刘富强","张燕"};
    String doctors5_2[] = {"沈寅忠","韩玉春"};
    String doctors5_3[] = {"王永胜"};
    String doctors5_4[] = {"郭向阳"};
    String doctors5_5[] = {"夏俊峰"};
    String doctors5_6[] = {"张平","谷和先"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        context = this;
        //position = (TextView) findViewById(R.id.position);
        //count = (TextView) findViewById(R.id.count);
        llDate = (LinearLayout) findViewById(R.id.ll_date);
        tvDate = (TextView) findViewById(R.id.tv_date);
        date = new StringBuffer();
        shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
        id = shrf.getInt("id",0);
        //refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        section = (Spinner) findViewById(R.id.section);
        hospital = (Spinner)findViewById(R.id.hospital);
        doctor = (Spinner)findViewById(R.id.doctor);

        userTel = (EditText)findViewById(R.id.user_tel);
        //datePicker = (DatePicker)findViewById(R.id.reserve_date);
        reserve = (Button) findViewById(R.id.reserve);

        hospitalAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hospitals);
        hospital.setAdapter(hospitalAdapter);
        hospital.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ((int) hospital.getSelectedItemId()){
                    case 0:
                        sectionAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, sections1);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        section.setAdapter(sectionAdapter);
                        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int) section.getSelectedItemId()){
                                    case 0:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_1);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 1:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_2);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 2:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_3);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 3:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_4);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 4:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_5);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 5:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_6);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 6:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_7);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 7:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors1_8);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    default:
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 1:
                        sectionAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, sections2);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        section.setAdapter(sectionAdapter);
                        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int) section.getSelectedItemId()){
                                    case 0:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_1);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 1:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_2);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 2:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_3);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 3:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_4);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 4:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_5);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 5:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_6);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 6:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_7);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 7:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors2_8);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    default:
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 2:
                        sectionAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, sections3);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        section.setAdapter(sectionAdapter);
                        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int) section.getSelectedItemId()){
                                    case 0:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_1);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 1:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_2);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 2:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_3);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 3:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_4);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 4:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_5);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 5:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors3_6);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    default:
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 3:
                        sectionAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, sections4);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        section.setAdapter(sectionAdapter);
                        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int) section.getSelectedItemId()){
                                    case 0:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_1);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 1:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_2);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 2:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_3);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 3:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_4);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 4:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_5);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 5:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors4_6);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    default:
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    case 4:
                        sectionAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, sections5);
                        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        section.setAdapter(sectionAdapter);
                        section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                switch ((int) section.getSelectedItemId()){
                                    case 0:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_1);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 1:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_2);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 2:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_3);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 3:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_4);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 4:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_5);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    case 5:
                                        doctorlAdapter = new ArrayAdapter<String>(Reservation.this, android.R.layout.simple_spinner_item, doctors5_6);
                                        doctorlAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        doctor.setAdapter(doctorlAdapter);
                                        break;
                                    default:
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        break;
                    default:
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initDateTime();
        llDate.setOnClickListener(this);
        reserve.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_date:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (date.length() > 0) { //清除上次记录的日期
                            date.delete(0, date.length());
                        }
                        tvDate.setText(date.append(String.valueOf(year)).append("年").append(String.valueOf(month+1)).append("月").append(day).append("日"));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(context, R.layout.dialog_date, null);
                final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);
                datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
                datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis()+216000000);

                dialog.setTitle("设置日期");
                dialog.setView(dialogView);
                dialog.show();
                //初始化日期监听事件
                datePicker.init(year, month, day, this);
                break;
            case R.id.reserve:
                shrf = getSharedPreferences("user",MODE_MULTI_PROCESS);
                id = shrf.getInt("id",0);
                Log.d("userId", String.valueOf(id));
                //Toast.makeText(this,"预约成功.",Toast.LENGTH_SHORT).show();
                if (id!=0 && doctor.getSelectedItem().toString()!="" && userTel.getText().toString().matches("^(13[0-9]|14[0-9]|15[0-9]|166|17[0-9]|18[0-9]|19[8|9])\\d{8}$")&&tvDate.getText().toString()!="") {
                    logDialog = Loading.createLoadingDialog(Reservation.this,"预约中...");
                    new Thread(runnable).start();
                }else {
                    Toast.makeText(this,"请填写全部正确信息",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Looper.prepare();
                Connection conn = DBUtil.getCon();
                Log.d("test", "connecting success ");
                Order order = new Order(id,doctor.getSelectedItem().toString(),userTel.getText().toString(),tvDate.getText().toString());
                int isReserved = OrderDAO.reserve(conn,order);
                if (isReserved == 0){
                    Toast.makeText(Reservation.this,"预约失败，或已预约过",Toast.LENGTH_SHORT).show();
                }else if (isReserved == -1){
                    Toast.makeText(Reservation.this,"请填写全部信息",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(Reservation.this,"预约成功,两秒跳转。。。",Toast.LENGTH_SHORT).show();
                    Thread.sleep(1500);
                    //conn.close();
                    finish();
                    Intent intent = new Intent(Reservation.this, UserInfo.class);
                    intent.putExtra("orderId",order.getId());
                    startActivity(intent);
                }
                Loading.closeDialog(logDialog);
                Looper.loop();
            } catch (Exception e) {
                Log.d("Reserve", "Failure ");
                e.printStackTrace();
            }
        }
    };
}
