/*****************************************************************
 * Licensed Materials - Property of IBM
 * Copyright IBM Corp. 2010  All Rights Reserved
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *****************************************************************/

package ro.iss.salvatirosiamontana.common;

import java.util.ArrayList;

import ro.iss.salvatirosiamontana.R;
import ro.iss.salvatirosiamontana.R.string;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;

public class UIUtilities {











    public static AlertDialog createCommunicationErrorDialog(Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        return dialog
        .setTitle(context.getResources().getString(R.string.comm_error_text))
        .setMessage(context.getResources().getString(R.string.comm_error_msg_text))
        .setCancelable(false)
        .setPositiveButton(context.getResources().getString(R.string.ok_text), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        })
        .create();
    }



}

