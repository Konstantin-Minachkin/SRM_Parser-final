package com.example.warhammer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductActivity extends AppCompatActivity {

    final int DIALOG_SAVE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();

        int id = intent.getExtras().getInt("id");
        final Post post = MainActivity.tempAlbum.get(id);
        final TextView textView = (TextView) findViewById(R.id.test_id);
        final TextView name = (TextView) findViewById(R.id.name);
        ImageView imageView = (ImageView) findViewById(R.id.test_image);
        Picasso.get().load(post.getPhotoThumb()).into(imageView); //загрузить фото из url в нужный эулумент

        textView.setText(post.getDescription());
        name.setText(post.getName());
        Button saveBtn = (Button) findViewById(R.id.save_btn);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this,
                android.support.v7.appcompat.R.style.Base_Theme_AppCompat_Dialog_MinWidth);
        final Context context = this;
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = post.getNameSize();
                if (num > 0) {
                    String[] checkName = new String[num];
                    boolean[] mCheckedItem = new boolean[num];
                    for (int i = 0; i < num; i++) {
                        checkName[i] = post.getName(i);
                        mCheckedItem[i] = false;
                    }
                    final String[] checkNames = checkName;
                    final boolean[] mCheckedItems = mCheckedItem;
                    dialog.setTitle("Выберите нужные названия для сохранения").setCancelable(false)
                            .setIcon(android.R.drawable.btn_star_big_on)
                            .setMultiChoiceItems(checkNames, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    mCheckedItems[which] = isChecked;
                                }
                            })
                            // Добавляем кнопки
                            .setPositiveButton("Готово", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    DBHelper sql = new DBHelper(context); // создаем объект для создания и управления версиями БД
                                    try {
                                        for (int i = 0; i < checkNames.length; i++) {
                                            if (mCheckedItems[i]) { //если выбран, добавляем в закладки категорию и альбомы где она встречается
                                                sql.add(checkNames[i], post.getAlb_id());
                                            }
                                        }
                                    } catch (Exception exp){
                                        Toast toast = Toast.makeText(getApplicationContext(), "Ошбика при добавлении" + exp.toString(),
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                        System.out.println(exp.toString());
                                    }
                                }
                            })

                            .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dialog.create();
                    dialog.show();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Не нашли названий", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        Button vkBtn = (Button) findViewById(R.id.vk_btn);
        vkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //код открытия ссылки на продавца используй post.getUser()
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(post.getUser()));
                startActivity(browserIntent);
            }
        });
    }

}