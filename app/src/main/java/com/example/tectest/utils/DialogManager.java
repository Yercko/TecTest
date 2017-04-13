package com.example.tectest.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.example.tectest.R;

/**
 * Created by ymontero on 12/04/2017.
 */

public class DialogManager {
    /**
     * Flag
     */
    private static boolean hideProgressDialog = false;

    /**
     * Current alert dialog
     */
    private static AlertDialog currentAlertDialog = null;

    /**
     * Current dialog
     */
    private static Dialog currentDialog = null;

    /**
     * Muestra un dialogo de alerta
     * @param context - contexto de la aplicacion
     * @param title - titulo del dialogo
     * @param mensaje - mensaje del dialogo
     * @param buttonAcceptTitle - titulo del boton positivo. Si viene a null no se muestra boton
     * @param buttonCancelTittle - titulo del boton negativo. Si viene a null no se muestra boton
     * @param buttonAcceptListener - listener del boton positivo. Si viene a null no se meustra boton
     * @param buttonCancelListener - listener del boton negativo. Si viene a null no se meustra boton
     */
    public static void getTwoButtonAlertDialog(Context context, String title, String mensaje, String buttonAcceptTitle, String buttonCancelTittle, DialogInterface.OnClickListener buttonAcceptListener, DialogInterface.OnClickListener buttonCancelListener) {

        // Configuramos el dialogo
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(mensaje);
        alertDialog.setCancelable(false);

        if (buttonAcceptTitle == null) {
            buttonAcceptTitle = context.getString(R.string.PRF_EDITFORM_OK);
        }

        if (buttonCancelTittle == null) {
            buttonAcceptTitle = context.getString(R.string.BTN_CLOSE);
        }

        if (buttonAcceptListener != null) {
            alertDialog.setPositiveButton(buttonAcceptTitle, buttonAcceptListener);
        } else {

            alertDialog.setPositiveButton(buttonAcceptTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (buttonCancelListener != null) {
            alertDialog.setNegativeButton(buttonCancelTittle, buttonCancelListener);
        } else {

            alertDialog.setNegativeButton(buttonCancelTittle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        // Mostramos la alerta en el hilo principal
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {

            @Override
            public void run() {
                // Mostramos la alerta
                currentAlertDialog = alertDialog.show();
            }
        });
    }
    /**
     * Oculta el dialogo actual en pantalla
     */
    public static void hideCurrentDialog() {

        try {

            hideProgressDialog = true;

            if (currentAlertDialog != null && currentAlertDialog.isShowing()) {
                currentAlertDialog.dismiss();
                currentAlertDialog.hide();
            }

            currentAlertDialog = null;


            if (currentDialog != null && currentDialog.isShowing()) {
                currentDialog.dismiss();
            }

            currentDialog = null;

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            currentDialog = null;
            currentAlertDialog = null;
        }
    }
}
