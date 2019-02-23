package com.example.mq661.govproject.Participants;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mq661.govproject.R;
import com.example.mq661.govproject.SearchRoom.personName;

import java.util.ArrayList;
import java.util.List;


public class MyAdapter_Person_handler_forAddPerson extends BaseAdapter {
    private List<Model> data;
    private Context context;
    private addPerson_handler.AllCheckListener allCheckListener;
    private List<personName> personName = new ArrayList<>();


    public MyAdapter_Person_handler_forAddPerson(List<Model> data, Context context, addPerson_handler.AllCheckListener allCheckListener) {
        this.data = data;
        this.context = context;
        this.allCheckListener = allCheckListener;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHoder hd;
        if (view == null) {
            hd = new ViewHoder();
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.person_item_list, null);
            hd.textView = view.findViewById(R.id.text_title);
            hd.textView1 = view.findViewById(R.id.text_title1);
            hd.textView2 = view.findViewById(R.id.text_title2);
            hd.checkBox = view.findViewById(R.id.ckb);
            view.setTag(hd);
        }
        Model mModel = data.get(i);
        hd = (ViewHoder) view.getTag();
        hd.textView.setText(mModel.getEmployeeNumber1());
        hd.textView1.setText(mModel.getMinistry());
        hd.textView2.setText(mModel.getName());

        Log.e("myadapter", mModel.getEmployeeNumber1() + "------" + mModel.ischeck());
        final ViewHoder hdFinal = hd;
        hd.checkBox.setChecked(mModel.ischeck());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = hdFinal.checkBox;
                if (checkBox.isChecked()) {
                    checkBox.setChecked(false);
                    data.get(i).setIscheck(false);
                    Log.e("myadapter", "" + i);
                    Toast.makeText(context, "取消添加  " + data.get(i).getName(), Toast.LENGTH_SHORT).show();
//                    Log.d("aa", "remove  "+data.get(i).getName());
////                    personName.remove(data.get(i).getName());
////                    Log.d("aa", "remove  了");
                    removeName(data.get(i).getName(), data.get(i).getEmployeeNumber1());
                } else {
                    checkBox.setChecked(true);
                    data.get(i).setIscheck(true);
                    Toast.makeText(context, "添加  " + data.get(i).getName(), Toast.LENGTH_SHORT).show();
                    addName(data.get(i).getName(), data.get(i).getEmployeeNumber1());
                    Log.d("aa", "addnum  " + data.get(i).getEmployeeNumber1());
                }
                //监听每个item，若所有checkbox都为选中状态则更改main的全选checkbox状态
                for (Model model : data) {
                    if (!model.ischeck()) {
                        allCheckListener.onCheckedChanged(false);
                        return;
                    }
                }
                allCheckListener.onCheckedChanged(true);


            }
        });


        return view;
    }

    private void removeName(String name, String num) {
        for (int i = personName.size() - 1; i >= 0; i--) {
            String value = personName.get(i).getName();
            String num1 = personName.get(i).getNum();
            if (value.equals(name) && num1.equals(num)) {
                personName.remove(i);
                Log.d("aa", " removeName   " + name);
            }
        }
        Log.d("aa", "del里   " + addPerson_handler.personName1.size());
        for (int i = addPerson_handler.personName1.size() - 1; i >= 0; i--) {
            Log.d("aa", "del里   " + addPerson_handler.personName1.get(i).getNum());
            String value = addPerson_handler.personName1.get(i).getName();
            String num1 = addPerson_handler.personName1.get(i).getNum();
            if (value.equals(name) && num1.equals(num)) {
                addPerson_handler.personName1.remove(i);
                Log.d("aa", " removeName  ParticipantsInfoAllCheck  " + name);
            }
        }

    }

    public void addName(String name, String num) {
        personName Name = new personName();
        Name.setName(name);
        Name.setNum(num);
        personName.add(Name);
        Log.d("aa", " personName.add(Name)   " + personName.get(0).getName() + "   " + personName.get(0).getNum());
    }

    public List<personName> getAddName() {
        return personName;

    }

    class ViewHoder {
        TextView textView;
        TextView textView1;
        TextView textView2;
        CheckBox checkBox;
    }

}
