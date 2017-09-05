package com.lavendergoons.dndcharacter.ui.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lavendergoons.dndcharacter.models.Item;
import com.lavendergoons.dndcharacter.R;
import com.lavendergoons.dndcharacter.utils.Utils;

import static com.lavendergoons.dndcharacter.utils.Utils.checkIntNotNull;
/**
 * Dialog to create and edit items
 */

public class ItemGeneralDialog {

    public static final String TAG = ItemGeneralDialog.class.getCanonicalName();
    private Activity activity;
    private Item item;
    private ItemsGeneralDialogListener listener;

    private EditText itemDialogNameEdit, itemDialogWeightEdit, itemDialogQuantityEdit;

    public ItemGeneralDialog(Fragment fragment, Item item) {
        this.activity = fragment.getActivity();
        this.item = item;
        if (fragment instanceof ItemsGeneralDialogListener) {
            this.listener = (ItemsGeneralDialogListener) fragment;
        } else {
            throw new RuntimeException(fragment.toString()
                + " must implement ItemsGeneralDialogListener.");
        }
    }

    public ItemGeneralDialog(Activity activity, Item item) {
        this.activity = activity;
        this.item = item;
        if (activity instanceof ItemsGeneralDialogListener) {
            this.listener = (ItemsGeneralDialogListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement ItemsGeneralDialogListener.");
        }
    }

    public interface ItemsGeneralDialogListener {
        void OnItemsPositive(Item item);
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.title_items_general_dialog));

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_item_general, null);

        itemDialogNameEdit = (EditText) view.findViewById(R.id.itemDialogNameEdit);
        itemDialogWeightEdit = (EditText) view.findViewById(R.id.itemDialogWeightEdit);
        itemDialogQuantityEdit = (EditText) view.findViewById(R.id.itemDialogQuantityEdit);

        if (item != null) {
            itemDialogNameEdit.setText(item.getName());
            itemDialogWeightEdit.setText(String.valueOf(item.getWeight()));
            itemDialogQuantityEdit.setText(String.valueOf(item.getQuantity()));
        } else {
            itemDialogQuantityEdit.setText(activity.getString(R.string.one));
        }

        builder.setView(view).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int weight = 0;
                int quantity = 0;
                String name = itemDialogNameEdit.getText().toString();

                weight = checkIntNotNull(itemDialogWeightEdit);
                quantity = checkIntNotNull(itemDialogQuantityEdit);

                if (!Utils.isStringEmpty(name) && item != null) {
                    item.setName(name);
                    item.setWeight(weight);
                    item.setQuantity(quantity);
                    listener.OnItemsPositive(null);
                } else if (!Utils.isStringEmpty(name) && item == null) {
                    listener.OnItemsPositive(new Item(name, weight, quantity));
                } else {
                    Toast.makeText(activity, activity.getString(R.string.warning_enter_required_fields), Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }
}
