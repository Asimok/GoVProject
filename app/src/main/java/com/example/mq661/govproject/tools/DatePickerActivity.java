package com.example.mq661.govproject.tools;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.mq661.govproject.R;

import java.util.Calendar;

public class DatePickerActivity extends AppCompatActivity {
    private DatePicker mDatePicker = null; // 日期选择器
    private Calendar mCalendar = null; // 日历
    private int mYear; // 年
    private int mMonth; // 月
    private int mDay; // 日

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datepicker_layout);

        // 获取日历对象
        mCalendar = Calendar.getInstance();
        // 获取当前对应的年、月、日的信息
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH);
        mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        // 获取DatePicker组件
        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        // DatePicker初始化
        mDatePicker.init(mYear, mMonth, mDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(DatePickerActivity.this,
                        year + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //非默认值
        if (newConfig.fontScale != 1){
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {//还原字体大小
        Resources res = super.getResources();
        //非默认值
        if (res.getConfiguration().fontScale != 1) {
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}
