package com.java.xuhaotian.mainpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.java.xuhaotian.R;
import com.java.xuhaotian.mainpage.fragment.AskAnswerFragment;
import com.java.xuhaotian.mainpage.fragment.EntityListFragment;
import com.java.xuhaotian.mainpage.fragment.LinkPageFragment;
import com.java.xuhaotian.mainpage.fragment.ReviewFragment;
import com.java.xuhaotian.mainpage.fragment.UserPageFragment;

import java.util.ArrayList;
import java.util.List;

public class MainPageActivity extends FragmentActivity implements View.OnClickListener {

    //主页按钮
    private LinearLayout mTabMain;
    private ImageButton mImageTabMain;
    //链接按钮
    private LinearLayout mTabLink;
    private ImageButton mImageTabLink;
    //问答按钮
    private LinearLayout mTabAnswer;
    private ImageButton mImageTabAnswer;
    //复习按钮
    private LinearLayout mTabReview;
    private ImageButton mImageTabReview;
    //个人按钮
    private LinearLayout mTabMe;
    private ImageButton mImageTabMe;

    private ViewPager mViewPager;
    private TabFragmentPagerAdapter mAdapter;
    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        initView();
        initClickListener();
    }

    private void initView(){
        mViewPager = findViewById(R.id.fragment_vp);

        mTabMain = findViewById(R.id.ll_tab_main);
        mImageTabMain = findViewById(R.id.btn_main_icon_grey);

        mTabAnswer = findViewById(R.id.ll_tab_answer);
        mImageTabAnswer = findViewById(R.id.btn_answer_cart_icon_grey);

        mTabLink = findViewById(R.id.ll_tab_link);
        mImageTabLink = findViewById(R.id.btn_link_icon_grey);

        mTabReview = findViewById(R.id.ll_tab_review);
        mImageTabReview = findViewById(R.id.btn_review_cart_icon_grey);

        mTabMe = findViewById(R.id.ll_tab_me);
        mImageTabMe = findViewById(R.id.btn_me_icon_grey);

        mFragments = new ArrayList<Fragment>();
        Fragment mTab_01 = new EntityListFragment();
        Fragment mTab_02 = new LinkPageFragment();
        Fragment mTab_03 = new AskAnswerFragment();
        Fragment mTab_04 = new ReviewFragment();
        Fragment mTab_05 = new UserPageFragment();
        mFragments.add(mTab_01);
        mFragments.add(mTab_02);
        mFragments.add(mTab_03);
        mFragments.add(mTab_04);
        mFragments.add(mTab_05);

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        setSelect(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int currentItem = mViewPager.getCurrentItem();
                initTabImages();
                switch (currentItem){
                    case 0:
                        mImageTabMain.setImageResource(R.drawable.homepage_blue);
                        break;
                    case 1:
                        mImageTabLink.setImageResource(R.drawable.link_blue);
                        break;
                    case 2:
                        mImageTabAnswer.setImageResource(R.drawable.answer_blue);
                        break;
                    case 3:
                        mImageTabReview.setImageResource(R.drawable.review_blue);
                        break;
                    case 4:
                        mImageTabMe.setImageResource(R.drawable.user_blue);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void initTabImages(){
        mImageTabReview.setImageResource(R.drawable.review_grey);
        mImageTabMain.setImageResource(R.drawable.homepage_grey);
        mImageTabLink.setImageResource(R.drawable.link_grey);
        mImageTabAnswer.setImageResource(R.drawable.answer_grey);
        mImageTabMe.setImageResource(R.drawable.user_grey);
    }

    public void initClickListener(){
        mTabMe.setOnClickListener(this);
        mTabLink.setOnClickListener(this);
        mTabAnswer.setOnClickListener(this);
        mTabMain.setOnClickListener(this);
        mTabReview.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_tab_main:
                setSelect(0);
                break;
            case R.id.ll_tab_link:
                setSelect(1);
                break;
            case R.id.ll_tab_answer:
                setSelect(2);
                break;
            case R.id.ll_tab_review:
                setSelect(3);
                break;
            case R.id.ll_tab_me:
                setSelect(4);
                break;
        }
    }

    private void setSelect(int i){
        switch (i){
            case 0:
                mImageTabMain.setImageResource(R.drawable.homepage_blue);
                break;
            case 1:
                mImageTabLink.setImageResource(R.drawable.link_blue);
                break;
            case 2:
                mImageTabAnswer.setImageResource(R.drawable.answer_blue);
                break;
            case 3:
                mImageTabReview.setImageResource(R.drawable.review_blue);
                break;
            case 4:
                mImageTabMe.setImageResource(R.drawable.user_blue);
                break;
        }
        mViewPager.setCurrentItem(i);
    }
}