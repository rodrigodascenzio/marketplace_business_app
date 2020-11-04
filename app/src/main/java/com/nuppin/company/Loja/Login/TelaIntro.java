package com.nuppin.company.Loja.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.nuppin.company.R;

public class TelaIntro extends AppCompatActivity {

    private MaterialButton email, celular;
    private ViewPager mImageViewPager;
    private AutoScrollViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        email = findViewById(R.id.email);
        celular = findViewById(R.id.celular);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it;
                it = new Intent(TelaIntro.this, TelaCadastroEmail.class);
                startActivity(it);
            }
        });

        celular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it;
                it = new Intent(TelaIntro.this, TelaCadastroNumber.class);
                startActivity(it);
            }
        });


        //mImageViewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        //tabLayout.setupWithViewPager(mImageViewPager, true);
        //mImageViewPager.setAdapter(new CustomPagerAdapter(this));

        viewPager = findViewById(R.id.viewPager);
        viewPager.startAutoScroll();
        viewPager.setInterval(3000);
        viewPager.setCycle(true);
        viewPager.setStopScrollWhenTouch(true);
        tabLayout.setupWithViewPager(viewPager, true);

        viewPager.setAdapter(new CustomPagerAdapter(this));

    }

    class CustomPagerAdapter extends PagerAdapter {

        int[] mResources = {
                R.drawable.ic_undraw_growing,
                R.drawable.ic_undraw_customer_rating,
                R.drawable.ic_undraw_customer,
                R.drawable.ic_undraw_request,
                R.drawable.ic_undraw_cashflow,
                R.drawable.ic_undraw_data,
                R.drawable.ic_undraw_reading,
        };

        String[] mResourcesString = {
                "Aumente sua base de clientes, da maneira certa",
                "Receba avaliações sobre os atendimentos",
                "Utilize ferramentas para fidelizar seus clientes",
                "Tenha todo o ciclo de vida de seus pedidos organizados",
                "Controle a parte financeira de maneira simples",
                "Obtenha relatorios para acompanhar o progresso",
                "Aproveite nossos artigos e aprimore seus conhecimentos"
        };

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.vp_slider, container, false);
            ImageView imageView = itemView.findViewById(R.id.viewPagerItem);
            TextView textView = itemView.findViewById(R.id.txt);
            imageView.setImageResource(mResources[position]);
            textView.setText(mResourcesString[position]);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ConstraintLayout) object);
        }
    }
}
