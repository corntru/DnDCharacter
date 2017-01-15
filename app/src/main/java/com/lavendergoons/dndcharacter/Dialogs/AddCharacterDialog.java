package com.lavendergoons.dndcharacter.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lavendergoons.dndcharacter.Activities.CharacterListActivity;
import com.lavendergoons.dndcharacter.Objects.Character;
import com.lavendergoons.dndcharacter.R;

/**
 * Created by t00530282 on 1/14/2017.
 */
public class AddCharacterDialog extends DialogFragment implements DialogInterface.OnClickListener {

    private OnCharacterCompleteListener mListener;
    private EditText nameEdit, levelEdit;

    public AddCharacterDialog() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LinearLayout dialogLayout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setLayoutParams(params);
        dialogLayout.setPadding(2, 2, 2, 2);

        nameEdit = new EditText(getActivity());
        nameEdit.setHint(R.string.hint_add_character_name);
        //nameEdit.setPadding(16, 0, 16, 0);

        levelEdit = new EditText(getActivity());
        levelEdit.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        levelEdit.setHint(R.string.hint_add_character_level);
        //levelEdit.setPadding(16, 0, 16, 0);
        dialogLayout.addView(nameEdit, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialogLayout.addView(levelEdit, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        builder.setTitle(getString(R.string.title_add_character));
        builder.setView(dialogLayout);
        builder.setPositiveButton(R.string.Ok, this).setNegativeButton(R.string.Cancel, null);

        return builder.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static interface OnCharacterCompleteListener {
        public abstract void onCharacterComplete(Character character);
    }

    @Override
    public void onClick(DialogInterface dialog, int position) {
        Character character = new Character(nameEdit.getText().toString(), Integer.parseInt(levelEdit.getText().toString()));
        Log.d("DEBUG / ADD_CHARACTER", "VALUES = "+nameEdit.getText().toString()+" "+levelEdit.getText().toString());
        CharacterListActivity activity = (CharacterListActivity) getActivity();
        activity.onCharacterComplete(character);
        dialog.dismiss();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCharacterCompleteListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCharacterCompleteListener");
        }
    }
}