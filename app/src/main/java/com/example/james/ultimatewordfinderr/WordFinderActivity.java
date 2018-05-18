package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WordFinderActivity extends AppCompatActivity implements WordFinderMainFragment.OnFragmentInteractionListener, AdvancedSearchFragment.OnFragmentInteractionListener, WordFinderScoreComparisonFragment.OnFragmentInteractionListener, WordFinderDictionaryFragment.OnFragmentInteractionListener, WordDefinitionFragment.OnFragmentInteractionListener, SynonymResultListFragment.OnFragmentInteractionListener, WordFinderSearchResultsFragment.OnFragmentInteractionListener, HelpFeedbackFragment.OnFragmentInteractionListener, BugReportFragment.OnFragmentInteractionListener, WordDetailsFragment.OnFragmentInteractionListener {


    private com.example.james.ultimatewordfinderr.Dictionary dictionary;
    private WordFinderMainFragment wordFinderMainFragment;
    private WordFinderDictionaryFragment wordFinderDictionaryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_finder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.word_finder_activity_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getBundleExtra("selection");
        int selection = bundle.getInt("selection");

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        if (savedInstanceState == null) {
            if (selection == 1) {
                wordFinderDictionaryFragment = new WordFinderDictionaryFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, wordFinderDictionaryFragment);
            } else {
                wordFinderMainFragment = new WordFinderMainFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, wordFinderMainFragment);
            }

            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHelpFeedback:
                HelpFeedbackFragment helpFeedbackFragment = new HelpFeedbackFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerWordFinder, helpFeedbackFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSearchFragmentInteraction(View view, LinkedHashMap<String, Integer> searchMatches) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.btnSearch:
                WordFinderSearchResultsFragment searchResultsFragment = new WordFinderSearchResultsFragment();

                Gson gson = new Gson();
                String mapJson = gson.toJson(searchMatches, new TypeToken<LinkedHashMap<String, Integer>>(){}.getType());

                Bundle bundle = new Bundle();
                bundle.putString("Search Results", mapJson);
                searchResultsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
                fragmentTransaction.addToBackStack(null);
                break;
        }

        fragmentTransaction.commit();
    }

    private static Map<String, Integer> sortByValue(Map<String, Integer> unsortedMap){
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        for(Map.Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    public void onResultsFragmentButtonInteraction(String action, ArrayList<String> selectedWords) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (action) {
            case "definition": {
                WordDefinitionFragment wordDefinitionFragment = new WordDefinitionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("word", selectedWords.get(0));
                wordDefinitionFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, wordDefinitionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            case "compare": {
                WordFinderScoreComparisonFragment scoreComparisonFragment = new WordFinderScoreComparisonFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("wordsToCompare", selectedWords);
                scoreComparisonFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, scoreComparisonFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            }
            case "details":
                WordDetailsFragment wordDetailsFragment = new WordDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("word", selectedWords.get(0));
                wordDetailsFragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.containerWordFinder, wordDetailsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                break;
        }
    }

    @Override
    public void onResultsFragmentInteraction(String word, DefinitionList definitionList) {
        loadDefinitionsFragment(word, definitionList);
    }

    @Override
    public void onResultsFragmentInteraction(String word, ArrayList<String> synonyms) {
        loadSynonymsFragment(word, synonyms);
    }

    @Override
    public void onAdvancedSearchFragmentInteraction(View view, ArrayList<Word> matches) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        WordFinderSearchResultsFragment searchResultsFragment = new WordFinderSearchResultsFragment();
        ArrayList<String> words = new ArrayList<>();

        for (Word word : matches) {
            words.add(word.getWord());
        }

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Search Results", words);
        searchResultsFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, searchResultsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onScoreComparisonFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (wordFinderMainFragment != null) {
            wordFinderMainFragment.backButtonWasPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onDictionaryFragmentInteraction(String word, DefinitionList definitionList) {
        loadDefinitionsFragment(word, definitionList);
    }

    @Override
    public void onDictionaryFragmentInteraction(String word, ArrayList<String> synonyms) {
        loadSynonymsFragment(word, synonyms);
    }

    private void loadSynonymsFragment(String word, ArrayList<String> synonyms) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        SynonymResultListFragment synonymFragment = new SynonymResultListFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("Synonyms", synonyms);
        bundle.putString("Word", word);
        synonymFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, synonymFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void loadDefinitionsFragment(String word, DefinitionList definitionList) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        WordDefinitionFragment wordDefinitionFragment = new WordDefinitionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Definition List", new Gson().toJson(definitionList));
        bundle.putString("Word", word);
        wordDefinitionFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.containerWordFinder, wordDefinitionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(String option) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        switch (option) {
            case "Report Bug":
                BugReportFragment bugReportFragment = new BugReportFragment();
                fragmentTransaction.replace(R.id.containerWordFinder, bugReportFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onFragmentInteraction(String word, ArrayList<String> synonyms) {
        loadSynonymsFragment(word, synonyms);
    }

    @Override
    public void onFragmentInteraction(String word, DefinitionList definitionList) {
        loadDefinitionsFragment(word, definitionList);
    }
}
