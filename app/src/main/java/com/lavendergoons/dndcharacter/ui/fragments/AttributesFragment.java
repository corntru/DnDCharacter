package com.lavendergoons.dndcharacter.ui.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lavendergoons.dndcharacter.DndApplication;
import com.lavendergoons.dndcharacter.models.SimpleCharacter;
import com.lavendergoons.dndcharacter.R;
import com.lavendergoons.dndcharacter.ui.adapters.AttributesAdapter;
import com.lavendergoons.dndcharacter.utils.CharacterManager2;
import com.lavendergoons.dndcharacter.utils.Constants;
import com.lavendergoons.dndcharacter.utils.Utils;

import java.util.ArrayList;

import javax.inject.Inject;


public class AttributesFragment extends BaseFragment implements AttributesAdapter.AttributesAdapterListener {

    public static final String TAG = "ATTRIBUTES_FRAG";

    private RecyclerView mAttributesRecyclerView;
    private AttributesAdapter mAttributeRecyclerAdapter;
    private RecyclerView.LayoutManager mAttributeLayoutManager;
    private ArrayList<String> attributesList = new ArrayList<>(Constants.ATTRIBUTES.length);
    private SimpleCharacter simpleCharacter;

    @Inject CharacterManager2 characterManager;

    private long characterId = -1;
    private final int NAME = 0;
    private final int LEVEL = 2;

    public AttributesFragment() {
        // Required empty public constructor
    }

    public static AttributesFragment newInstance(SimpleCharacter charIn, long characterId) {
        AttributesFragment frag = new AttributesFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.CHARACTER_KEY, charIn);
        args.putLong(Constants.CHARACTER_ID, characterId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DndApplication.get(this).getAppComponent().inject(this);
        if (getArguments() != null) {
            characterId = getArguments().getLong(Constants.CHARACTER_ID);
            simpleCharacter = getArguments().getParcelable(Constants.CHARACTER_KEY);
        }
        attributesList = characterManager.getAttributes(characterId);

        // Fill attribute list with empty data
        if (attributesList.size() == 0) {
            for (int i=0;i<Constants.ATTRIBUTES.length;i++) {
                attributesList.add(i, "");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_attributes, container, false);
        mAttributesRecyclerView = (RecyclerView) rootView.findViewById(R.id.attributesRecyclerView);

        // Keeps View same size on content change
        mAttributesRecyclerView.setHasFixedSize(true);

        mAttributeLayoutManager = new GridLayoutManager(this.getActivity(), Constants.ATTRIBUTES_GRID_SPAN);
        mAttributesRecyclerView.setLayoutManager(mAttributeLayoutManager);

        mAttributeRecyclerAdapter = new AttributesAdapter(this, attributesList, simpleCharacter);
        mAttributesRecyclerView.setAdapter(mAttributeRecyclerAdapter);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        writeAttributes();
    }

    private void writeAttributes() {
        attributesList = mAttributeRecyclerAdapter.getAttributeList();
        if (!Utils.isStringEmpty(attributesList.get(NAME))) {
            simpleCharacter.setName(attributesList.get(NAME));
        }
        simpleCharacter.setLevel(Utils.stringToInt(attributesList.get(LEVEL)));
        characterManager.setSimpleCharacter(characterId, simpleCharacter);
        characterManager.setAttributes(characterId, attributesList);
    }

    @Override
    public int getTitle() {
        return R.string.title_fragment_attributes;
    }

    @Override
    public String getFragmentTag() {
        return TAG;
    }
}