package ru.uu.voda;

import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.content.Intent;

import android.widget.LinearLayout;
import android.content.Context; //Для добавления стиля создаваемым view
import android.support.v7.view.ContextThemeWrapper;//Для добавления стиля создаваемым view

public class QuestionsActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.future_question, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_questions_fragment, container, false);

            String[] questions; //массив с вопросами

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {	//Определение номера таба
                case 1:
                    questions = getResources().getStringArray(R.array.questionsC);//секция common
                    break;
                case 2:
                    questions = getResources().getStringArray(R.array.questionsG);//секция гражданам
                    break;
                case 3:
                    questions = getResources().getStringArray(R.array.questionsO);//секция организациям
                    break;
                default:
                    questions = getResources().getStringArray(R.array.questionsC);//без дефаулта студия ругается
            }

            final Context contextThemeWrapper = new ContextThemeWrapper(this.getContext(), R.style.questiontext);   //стиль вопросов
            final Context contextMargin = new ContextThemeWrapper(this.getContext(), R.style.margin);   //отступ
            LinearLayout questionsList = (LinearLayout) rootView.findViewById(R.id.questionsList);      //находим лэйаут, куда будем добавлять вопросы

            for (int i=0, n=questions.length; i<n; i++)     //Для всех вопросов в массиве
            {
                TextView questionView = new TextView(contextThemeWrapper);  //создаём новый вью со стилем
                questionView.setText(questions[i]);                         //с текстом вопроса
                questionView.setId(i);                                      //с id по порядку
                questionsList.addView(questionView);                        //и добавляем его в список

                TextView margin = new TextView(contextMargin);  //элемент отступа
                questionsList.addView(margin);


                questionView.setOnClickListener(this);
            }

            return rootView;
        }

        @Override
        public void onClick(View view) {
                Intent intent = new Intent(getContext(), AnswerActivity.class);
                intent.putExtra("part", getArguments().getInt(ARG_SECTION_NUMBER));
                intent.putExtra("question", view.getId());
                startActivity(intent);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Общие вопросы";
                case 1:
                    return "Гражданам";
                case 2:
                    return "Организациям";
            }
            return null;
        }
    }
}
