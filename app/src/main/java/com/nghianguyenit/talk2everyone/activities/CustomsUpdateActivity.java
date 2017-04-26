package com.nghianguyenit.talk2everyone.activities;

import android.support.v4.app.Fragment;

import com.kcode.lib.dialog.UpdateActivity;
import com.nghianguyenit.talk2everyone.CustomsUpdateFragment;
import com.nghianguyenit.talk2everyone.R;

/**
 * Created by NghiaNH on 4/23/2017.
 */

public class CustomsUpdateActivity extends UpdateActivity {

    @Override
    protected Fragment getUpdateDialogFragment() {
        return CustomsUpdateFragment.newInstance(mModel,getString(R.string.update_dialog_title));
    }
}
