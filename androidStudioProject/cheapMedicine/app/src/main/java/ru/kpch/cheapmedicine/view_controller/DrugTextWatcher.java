package ru.kpch.cheapmedicine.view_controller;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class DrugTextWatcher implements android.text.TextWatcher {
    public EditText editText1;
    public EditText editText2;
    Button button;

    public DrugTextWatcher(EditText et1, EditText et2, Button b){
        super();
        editText1 = et1;
        editText2 = et2;
        button=b;
    }

    public void afterTextChanged(Editable s) {
            if ((editText1.getText().length() == 0) || (editText1.getText().charAt(0) == ' ')) {
                button.setEnabled(false);
            } else if (button.isEnabled() == false && (editText2.getText().length() != 0)) {
                button.setEnabled(true);
            }
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after){

    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

}
