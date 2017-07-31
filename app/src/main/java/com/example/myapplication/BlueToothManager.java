package com.example.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by 叶明林 on 2017/7/31.
 */

public class BlueToothManager extends ListActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        intent.putExtra("serviceName","asasxasx");
        setResult(RESULT_OK, intent);
        finish();
    }
}
