package ru.kpch.cheapmedicine.view_controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import ru.kpch.cheapmedicine.R;
import ru.kpch.cheapmedicine.interfaces.IAppLogic;
import ru.kpch.cheapmedicine.model.AppLogicImpl;

/**
 * Created by kostyazxcvbn on 20.06.2017.
 */

public abstract class AppActivity extends AppCompatActivity {
    protected IAppLogic appLogic = AppLogicImpl.getInstance();
    protected boolean isAppWillBeClosed;

    protected void setActivityActionBar(AppActivity activity, int imageResourceId) {
        ActionBar actionbar = getSupportActionBar();

        actionbar.setDefaultDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayHomeAsUpEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayUseLogoEnabled(false);
        actionbar.setHomeButtonEnabled(false);

        View actionBarView = activity.getLayoutInflater().inflate(R.layout.action_bar_app, null);
        ImageView image = (ImageView) actionBarView.findViewById(R.id.iv_docImage);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        InputStream is = activity.getResources().openRawResource(imageResourceId);
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);

        try {
            is.close();
        } catch (IOException e) {
            closeWithFatalError(activity);
        }

        BitmapDrawable drawable = new BitmapDrawable(activity.getResources(), bitmap);
        image.setImageDrawable(drawable);

        actionbar.setCustomView(actionBarView);
        Toolbar parent = (Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
    }

    protected void closeWithFatalError(AppActivity activity) {
        Toast errorMessageContainer = Toast.makeText(getApplicationContext(),
                getString(R.string.messageOpenDbFatal), Toast.LENGTH_SHORT);
        errorMessageContainer.show();
        isAppWillBeClosed =true;
        activity.finish();
    }

    protected String clearClassName(String className) {
        int charIndex=className.indexOf('$');
        return (charIndex==-1)?className:className.substring(0, charIndex);
    }
}
