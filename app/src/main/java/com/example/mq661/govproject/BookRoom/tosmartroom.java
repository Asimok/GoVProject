package com.example.mq661.govproject.BookRoom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.mytoken.tokenDBHelper;

public class tosmartroom extends AppCompatActivity implements View.OnClickListener {
private  String size1,functions1;
EditText size,functions;
Button commit;
Intent data=new Intent();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tosmartroom_layout);

        initView();

    }

    private void initView() {

        size = findViewById(R.id.size);
        functions = findViewById(R.id.functions);
        commit=findViewById(R.id.commit);

        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {


        if(TextUtils.isEmpty(size.getText())||TextUtils.isEmpty(functions.getText()))
        {
            Toast.makeText(this, "请输入容量或功能", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(size.getText()))
        {
            size1=null;
        }
        else if(TextUtils.isEmpty(functions.getText()))
        {
            functions1=null;
        }
        else
        {
            size1=size.getText().toString();
            functions1=functions.getText().toString();
        }

        data.putExtra("Size", size1);
        data.putExtra("Functions", functions1);
        setResult(2, data);
        finish();
    }
}
