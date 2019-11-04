package com.yanhamer.app_utils.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yanhamer.app_utils.R;

/**
 * create Builder pattern with your context
 * and displayed message to run checking up of your App updates
 * inside App
 * and
 * check of your update status that must be equal of these
 * finals values that used to check up
 */
public class InAppUpdate {

    private static final String VERSION_URL = "01_version_name";
    private static final String UPDATE_MANDATORY = "00_update_mandatory";

    private DatabaseReference databaseReference;
    private Context context;
    private String message;

    private InAppUpdate(UpdateBuilder builder) {
        this.context = builder.context;
        this.message = builder.messageText;
    }

    private void appUpdate(final boolean status) {

        final Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.update_layout);

        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView update_action = dialog.findViewById(R.id.dialog_btn_update);
        TextView later_action = dialog.findViewById(R.id.dialog_btn_later);
        TextView message = dialog.findViewById(R.id.message_text);

        message.setText(this.message);

        if (status) {
            later_action.setVisibility(View.GONE);
        }

        later_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        update_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+ context.getPackageName()));
                context.startActivity(intent);
                if (!status)
                    dialog.dismiss();
            }
        });

        dialog.show();

    }


    public void checkAppVersion() {

        databaseReference = FirebaseDatabase.getInstance().getReference(VERSION_URL);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String versionName = dataSnapshot.getValue(String.class);
                    assert versionName != null;
                    onSuccess(versionName);
                } else {
                    onError("error");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onError("error");
            }
        });
    }

    private void getUpdateMandatory() {

        databaseReference = FirebaseDatabase.getInstance().getReference(UPDATE_MANDATORY);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean status = dataSnapshot.getValue(Boolean.class);
                    if (status != null) {
                        if (status)
                            appUpdate(status);
                        else
                            appUpdate(status);
                    }
                } else {
                    onError("not_exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onError(databaseError.getMessage());
            }
        });

    }


    private void onSuccess(String text) {

        if (!text.equals("error")) {
            try {
                PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                double appVersion = Double.parseDouble(info.versionName);
                double remoteVersion = Double.parseDouble(text);
                if (remoteVersion > appVersion) {
                    getUpdateMandatory();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private void onError(String text) {
        Toast.makeText(context, "Error in check app update: "+ text, Toast.LENGTH_LONG).show();
    }

    /**
     * create your own builder and run
     * @checkAppVersion method to check up your updates
     * @message some strings that display in dialog to
     * inform users to update your App
     *
     */
    public static class UpdateBuilder{

        private Context context;
        private String messageText;

        public UpdateBuilder(){}

        public UpdateBuilder createInstance(Context context){
            this.context = context;
            return this;
        }

        public UpdateBuilder setMessage(String message){
            this.messageText = message;
            return this;
        }

        public InAppUpdate build(){
            return new InAppUpdate(this);
        }

    }
}
