package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AdvancedSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AdvancedSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdvancedSearchFragment extends android.support.v4.app.Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ProgressDialog progressDialog;
    private Dictionary dictionary;
    private ArrayList<Word> matches;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdvancedSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AdvancedSearchFragment newInstance(String param1, String param2) {
        AdvancedSearchFragment fragment = new AdvancedSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AdvancedSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);

        final SeekBar seekBarNumLettersMin = (SeekBar) view.findViewById(R.id.seekBarNumLettersMin);
        final SeekBar seekBarNumLettersMax = (SeekBar) view.findViewById(R.id.seekBarNumLettersMax);
        final TextView txtSeekBarMinProgress = (TextView) view.findViewById(R.id.txtSeekBarMinProgress);
        final TextView txtSeekBarMaxProgress = (TextView) view.findViewById(R.id.txtSeekBarMaxProgress);
        int numMinLetters = seekBarNumLettersMin.getProgress();
        int numMaxLetters = seekBarNumLettersMax.getProgress();
        txtSeekBarMinProgress.setText(String.valueOf(numMinLetters));
        txtSeekBarMaxProgress.setText(String.valueOf(numMaxLetters));
        Button btnAdvancedSearch = (Button) view.findViewById(R.id.btnAdvancedSearch);
        final TextView editTextSearchTerm = (TextView) view.findViewById(R.id.editTextSearchTerm);
        final TextView editTextStartsWith = (TextView) view.findViewById(R.id.editTextStartsWith);
        final TextView editTextEndsWith = (TextView) view.findViewById(R.id.editTextEndsWith);

        matches = new ArrayList<>();
        Globals g = Globals.getInstance();
        dictionary = g.getDictionary();

        seekBarNumLettersMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final int numMinLetters = seekBarNumLettersMin.getProgress();
                txtSeekBarMinProgress.setText(String.valueOf(numMinLetters));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBarNumLettersMax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                final int numMaxLetters = seekBarNumLettersMax.getProgress();
                txtSeekBarMaxProgress.setText(String.valueOf(numMaxLetters));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        
        btnAdvancedSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchTerm = editTextSearchTerm.getText().toString();
                String prefix = editTextStartsWith.getText().toString();
                String suffix = editTextEndsWith.getText().toString();
                int minLength = seekBarNumLettersMin.getProgress();
                int maxLength = seekBarNumLettersMax.getProgress();
                SearchWordsTask searchWordsTask = null;

                if(minLength == 0 && maxLength == 0){
                    searchWordsTask = new SearchWordsTask(view, searchTerm, prefix, suffix, 0, 30);
                } else if(minLength == 0 && maxLength != 0){
                    searchWordsTask = new SearchWordsTask(view, searchTerm, prefix, suffix, 0, maxLength);
                } else if(minLength != 0 && maxLength == 0){
                    searchWordsTask = new SearchWordsTask(view, searchTerm, prefix, suffix, minLength, 30);
                } else {
                    searchWordsTask = new SearchWordsTask(view, searchTerm, prefix, suffix, minLength, maxLength);
                }

                searchWordsTask.execute();
            }
        });


        return view;
    }

    private class SearchWordsTask extends AsyncTask<Void, Void, Void> {

        private String searchTerm;
        private String prefix;
        private String suffix;
        private int minLength;
        private int maxLength;
        private View view;

        public SearchWordsTask(View view, String searchTerm, String prefix, String suffix, int minLength, int maxLength){
            this.searchTerm = searchTerm;
            this.prefix = prefix;
            this.suffix = suffix;
            this.minLength = minLength;
            this.maxLength = maxLength;
            this.view = view;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Searching...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (Word word : dictionary.getWordList()) {
                if (word.getWord().contains(searchTerm) && word.getWord().startsWith(prefix) && word.getWord().endsWith(suffix)) {
                    if(word.getWord().length() >= minLength && word.getWord().length() <= maxLength){
                        matches.add(word);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            if(progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog = null;
            }

            if(matches.size() > 0){
                mListener.onAdvancedSearchFragmentInteraction(view, matches);
            } else {
                Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onAdvancedSearchFragmentInteraction(View view, ArrayList<Word> matches);
    }

}