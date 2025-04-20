package com.bloodcare.fragment.startup;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.bloodcare.R;
import com.bloodcare.fragment.CustomBaseFragment;
import com.bloodcare.utility.CommonUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment  extends CustomBaseFragment {


    public ForgotPasswordFragment() {
        setTitleStringId(R.string.forgot_password_title);
        setResourceId(R.layout.fragment_forgot_password);
        setShowHomeAsUp(false);
        setHideKeyboardOnStop(true);
    }

    @Override
    public void onCreateFragmentView(View rootView, ViewGroup container, Bundle savedInstanceState) {
        try {

            Button sendPasswordResetEmail = rootView.findViewById(R.id.send_password_reset_email_btn);
            final EditText forgotPasswordEmail = rootView.findViewById(R.id.forgot_password_edit_text);

            if(sendPasswordResetEmail != null) {
                sendPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String email = CommonUtil.validateInputText(getActivity(), ForgotPasswordFragment.this, forgotPasswordEmail, R.string.email_text, 1, true, false);
                        if(email == null) {
                            return;
                        }

                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        CommonUtil.showProgressDialog(getActivity(), null, false);
                                        CommonUtil.showDialog(getContext(), getString(R.string.dialog_title_information), getString(R.string.dialog_message_email_successfully_send), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);
                                        ForgotPasswordFragment.this.dismissFragment(true, null);
                                    } else {
                                        CommonUtil.showProgressDialog(getActivity(), null, false);
                                        CommonUtil.showDialog(getContext(), getString(R.string.error_occurred), getString(R.string.dialog_title_error), android.R.drawable.ic_dialog_alert, getString(R.string.dialog_button_ok), null, null);

                                    }
                                }
                            });
                        CommonUtil.showProgressDialog(getActivity(), getString(R.string.dialog_message_sending_email), true);
                    }
                });
            }

        } catch (Exception e) {
            CommonUtil.showProgressDialog(getActivity(), null, false);
            e.printStackTrace();;
        }
    }
}
