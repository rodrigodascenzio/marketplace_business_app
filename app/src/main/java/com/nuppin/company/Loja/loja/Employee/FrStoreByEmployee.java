package com.nuppin.company.Loja.loja.Employee;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nuppin.company.Loja.loja.AcompanharAgendamentoAdapter;
import com.nuppin.company.Loja.loja.AcompanharPedidoAdapter;
import com.nuppin.company.Loja.loja.FrStoreViewPager;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Chat;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.model.Status;
import com.nuppin.company.model.Mobile;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.Util;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.service.UpdateLocationToCloudService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.DismissListener;

public class FrStoreByEmployee extends Fragment
        implements
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        AcompanharPedidoAdapter.AcompanharPedidoOnClickListener,
        AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener {

    public static boolean active = false;
    private static final String PENDING = "pending";
    private static final String ATIVO = "active";
    private static final String INATIVO = "inactive";
    private static final String SUSPENSO = "suspended";
    private Company company;
    private LoaderManager loaderManager;
    private AcompanharPedidoAdapter adapter;
    private AcompanharAgendamentoAdapter adapterServico;
    private RecyclerView mRecyclerView;
    private LinearLayout linearEmpty;
    private SimpleDraweeView fotoLoja;
    private TextView rating, onlineOrNot, inativo, badgeChat, invisible, arquivo;
    private CardView aviso;
    private ImageView chatIcon;
    private Map<String, Object> data;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private ConstraintLayout mainContainer;
    private FloatingActionButton fabError, manual;
    private BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrStoreByEmployee.this);
        }
    };

    private FancyShowCaseView fancyShowCaseView2;
    private FancyShowCaseView fancyShowCaseView3;
    private FancyShowCaseView fancyShowCaseView4;
    private FancyShowCaseView fancyShowCaseView5;
    private FancyShowCaseView fancyShowCaseView6;
    private DismissListener listenerTutorial = new DismissListener() {
        @Override
        public void onDismiss(String s) {
            chainTutorial();
        }

        @Override
        public void onSkipped(String s) {

        }
    };
    private LinearLayout linearStatus;

    private void chainCreatedTutorial() {

        if (fancyShowCaseView2 == null) {
            fancyShowCaseView2 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(linearStatus)
                    .title(getString(R.string.unique_tutorial_company_linear_status_string))
                    .dismissListener(listenerTutorial)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .enableAutoTextPosition()
                    .showOnce(getString(R.string.unique_tutorial_company_linear_status))
                    .closeOnTouch(true)
                    .build();
        }

        if (fancyShowCaseView3 == null) {
            fancyShowCaseView3 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(manual)
                    .title(!company.getCategory_company_id().equals("3") ? getString(R.string.unique_tutorial_company_manual_string_produtos) : getString(R.string.unique_tutorial_company_manual_string_servicos))
                    .enableAutoTextPosition()
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .dismissListener(listenerTutorial)
                    .showOnce(getString(R.string.unique_tutorial_company_manual))
                    .closeOnTouch(true)
                    .build();
        }

        if (fancyShowCaseView4 == null) {
            fancyShowCaseView4 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(fotoLoja)
                    .title(getString(R.string.unique_tutorial_company_foto_loja_string))
                    .enableAutoTextPosition()
                    .dismissListener(listenerTutorial)
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .showOnce(getString(R.string.unique_tutorial_company_foto_loja))
                    .closeOnTouch(true)
                    .build();
        }

        if (fancyShowCaseView5 == null) {
            fancyShowCaseView5 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(arquivo)
                    .title(!company.getCategory_company_id().equals("3") ? getString(R.string.unique_tutorial_company_arquivo_string_produtos) : getString(R.string.unique_tutorial_company_arquivo_string_servicos))
                    .enableAutoTextPosition()
                    .dismissListener(listenerTutorial)
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .showOnce(getString(R.string.unique_tutorial_company_arquivo))
                    .closeOnTouch(true)
                    .build();
        }
        if (fancyShowCaseView6 == null) {
            fancyShowCaseView6 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(chatIcon)
                    .title(getString(R.string.unique_tutorial_company_chat_icon_string))
                    .enableAutoTextPosition()
                    .dismissListener(listenerTutorial)
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .showOnce(getString(R.string.unique_tutorial_company_chat_icon))
                    .closeOnTouch(true)
                    .build();
        }

    }

    private void chainTutorial() {
        try {
            if (!fancyShowCaseView2.isShownBefore()) {
                fancyShowCaseView2.show();
                return;
            }
            if (!fancyShowCaseView3.isShownBefore()) {
                fancyShowCaseView3.show();
                return;
            }

            if (!fancyShowCaseView4.isShownBefore()) {
                fancyShowCaseView4.show();
                return;
            }
            if (!fancyShowCaseView5.isShownBefore()) {
                fancyShowCaseView5.show();
                return;
            }
            if (!fancyShowCaseView6.isShownBefore()) {
                fancyShowCaseView6.show();
                return;
            }
        } catch (Exception e) {
            return;
        }
    }

    public FrStoreByEmployee() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loaderManager = LoaderManager.getInstance(this);

        if (getActivity() != null) {
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newOrder"));
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newMessage"));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fr_company, container, false);
        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrStoreByEmployee.this);
            }
        });

        manual = view.findViewById(R.id.manual);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        dots = view.findViewById(R.id.dots);
        mainContainer = view.findViewById(R.id.container);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, 0);

        aviso = view.findViewById(R.id.cardAviso);
        chatIcon = view.findViewById(R.id.chatIcon);
        badgeChat = view.findViewById(R.id.chatQtd);


        aviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(aviso.getId(), company);
                }
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(chatIcon.getId(), company);
                }
            }
        });

        arquivo = view.findViewById(R.id.txtArquivo);
        arquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(arquivo.getId(), company);
                }
            }
        });

        rating = view.findViewById(R.id.rating);
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(rating.getId(), company);
                }
            }
        });

        onlineOrNot = view.findViewById(R.id.onlineOrNot);
        inativo = view.findViewById(R.id.inativo);
        invisible = view.findViewById(R.id.invisible);

        mRecyclerView = view.findViewById(R.id.acompanhaStatus);

        manual.hide();
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(manual.getId(), company);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        adapterServico = new AcompanharAgendamentoAdapter(this);
        adapter = new AcompanharPedidoAdapter(this);

        fotoLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClickEmployeeMode(fotoLoja.getId(), company);
                }
            }
        });
        return view;
    }


    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        return new StoresLoader(getActivity(), data);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        initializeStore(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, Object>> loader) {
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        loaderManager.restartLoader(0, null, this);
        Util.cancelNotifyOnOff(getContext(), 10);
    }

    private void initializeStore(Map<String, Object> data) {
        onlineOrNot.setVisibility(View.GONE);
        inativo.setVisibility(View.GONE);
        invisible.setVisibility(View.GONE);

        if (data != null) {
            this.data = data;
            if (data.get(AppConstants.COMPANY) != null && data.get(AppConstants.COMPANY) instanceof Company) {
                company = (Company) data.get(AppConstants.COMPANY);
                UtilShaPre.setDefaults(AppConstants.COMPANY, company.getCompany_id(), getContext());

                manual.show();

                Util.hasPhoto(company, fotoLoja);
                if (company.getNum_rating() != 0) {
                    rating.setVisibility(View.VISIBLE);
                    rating.setText(getResources().getString(R.string.rating_and_number, Util.formaterRating(company.getRating() / company.getNum_rating()), company.getNum_rating()));
                } else {
                    rating.setVisibility(View.GONE);
                }

                if (company.getStatus().equals(ATIVO) && company.getVisibility() == 1) {
                    onlineOrNot.setVisibility(View.VISIBLE);
                    if (company.getIs_online() == 1) {
                        onlineOrNot.setText(R.string.online);
                        onlineOrNot.setTextColor(getResources().getColor(R.color.colorPrimary));
                        onlineOrNot.setBackground(getResources().getDrawable(R.drawable.online));
                    } else {
                        onlineOrNot.setText(R.string.offline);
                        onlineOrNot.setBackground(getResources().getDrawable(R.drawable.num_rating));
                        onlineOrNot.setTextColor(getResources().getColor(R.color.icons));
                    }
                } else {
                    boolean isInativo = false;
                    inativo.setVisibility(View.VISIBLE);
                    switch (company.getStatus()) {
                        case PENDING:
                            isInativo = true;
                            inativo.setText(R.string.em_espera);
                            break;
                        case INATIVO:
                            isInativo = true;
                            inativo.setText(R.string.inativo);
                            break;
                        case SUSPENSO:
                            isInativo = true;
                            inativo.setText(R.string.suspenso);
                            break;
                    }
                    if (!isInativo) {
                        if (company.getVisibility() != 1) {
                            invisible.setVisibility(View.VISIBLE);
                            inativo.setVisibility(View.GONE);
                        }
                    }
                }


                if (company.getCategory_company_id().equals("3")) {
                    mRecyclerView.setAdapter(adapterServico);

                    if (data.get(AppConstants.SCHEDULING) != null && data.get(AppConstants.SCHEDULING) instanceof List) {
                        if (!((List) data.get(AppConstants.SCHEDULING)).isEmpty()) {
                            adapterServico.setStatusAcompanhar((List) data.get(AppConstants.SCHEDULING));
                            linearEmpty.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            linearEmpty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    mRecyclerView.setAdapter(adapter);

                    if (data.get(AppConstants.STATUS) != null) {
                        if (!((List) data.get(AppConstants.STATUS)).isEmpty()) {
                            if (data.get(AppConstants.STATUS) instanceof List) {
                                adapter.setStatusAcompanhar((List) data.get(AppConstants.STATUS));
                                linearEmpty.setVisibility(View.GONE);
                                mRecyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            linearEmpty.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                    }
                }

                if (data.get(AppConstants.CHAT) != null && data.get(AppConstants.CHAT) instanceof List) {
                    int fat_qtd = ((List) data.get(AppConstants.CHAT)).size();
                    if (fat_qtd > 0) {
                        badgeChat.setVisibility(View.VISIBLE);
                        badgeChat.setText(String.valueOf(fat_qtd));
                    } else {
                        badgeChat.setVisibility(View.GONE);
                    }
                }

                mainContainer.setVisibility(View.VISIBLE);
                chainCreatedTutorial();
                chainTutorial();
            } else if (data.get(AppConstants.HAS_COMPANY) != null && data.get(AppConstants.HAS_COMPANY) instanceof Boolean) {
                if (!(Boolean) data.get(AppConstants.HAS_COMPANY)) {
                    if (getActivity() instanceof MainFrEmployee.ToActivity) {
                        MainFrEmployee.ToActivity listener = (MainFrEmployee.ToActivity) getActivity();
                        UtilShaPre.setDefaults("mode", "company", getContext());
                        listener.CompanyMode();
                    }
                }
            } else {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickStatus(String index, String stoId) {
        if (getActivity() instanceof AcompanharPedidoAdapter.AcompanharPedidoOnClickListener) {
            AcompanharPedidoAdapter.AcompanharPedidoOnClickListener listener = (AcompanharPedidoAdapter.AcompanharPedidoOnClickListener) getActivity();
            listener.onClickStatus(index, stoId);
        }
    }

    @Override
    public void onClickStatusAgendamento(String index, String stoId) {
        if (getActivity() instanceof AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener) {
            AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener listener = (AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener) getActivity();
            listener.onClickStatusAgendamento(index, stoId);
        }
    }


    private static class StoresLoader extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Map<String, Object> data;

        StoresLoader(Context context, Map<String, Object> data) {
            super(context);
            ctx = context;
            this.data = data;
        }


        @Override
        protected void onStartLoading() {
            if (data != null) {
                deliverResult(data);
                forceLoad();
            } else {
                forceLoad();
            }
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Company company;
            List<Status> listStatus;
            List<Status> listStatusAge;
            List<Chat> listChats;
            Gson gson = new Gson();
            listStatus = new ArrayList<>();
            listStatusAge = new ArrayList<>();
            Status[] status;
            Status[] statusAge;
            listChats = new ArrayList<>();
            Chat[] chats;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            Boolean temLoja;

            String stringJson = ConnectApi.GET(ConnectApi.COMPANY_EMPLOYEE + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx) + Util.returnStringLatLonCountrOrEmpty(getContext()), getContext());
            JsonParser parser = new JsonParser();
            JsonObject jObj;

            try {
                jObj = parser.parse(stringJson).getAsJsonObject();
                company = gson.fromJson(jObj.getAsJsonObject(AppConstants.COMPANY), Company.class);
                UtilShaPre.setDefaults(AppConstants.COUNTRY_CODE, company.getCountry_code(), getContext());
                UtilShaPre.setDefaults(AppConstants.LATITUDE, String.valueOf(company.getLatitude()), getContext());
                UtilShaPre.setDefaults(AppConstants.LONGITUDE, String.valueOf(company.getLongitude()), getContext());
                mapOrdPro.put(AppConstants.COMPANY, company);
                try {
                    status = gson.fromJson(jObj.getAsJsonArray(AppConstants.STATUS), Status[].class);
                    listStatus.addAll(Arrays.asList(status));
                    mapOrdPro.put(AppConstants.STATUS, listStatus);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listStatus = null;
                }
                try {
                    statusAge = gson.fromJson(jObj.getAsJsonArray(AppConstants.SCHEDULING), Status[].class);
                    listStatusAge.addAll(Arrays.asList(statusAge));
                    mapOrdPro.put(AppConstants.SCHEDULING, listStatusAge);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listStatusAge = null;
                }
                try {
                    chats = gson.fromJson(jObj.getAsJsonArray(AppConstants.CHAT), Chat[].class);
                    listChats.addAll(Arrays.asList(chats));
                    mapOrdPro.put(AppConstants.CHAT, listChats);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listChats = null;
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                try {
                    jObj = parser.parse(stringJson).getAsJsonObject();
                    temLoja = gson.fromJson(jObj.getAsJsonPrimitive(AppConstants.COMPANY), Boolean.class);
                    mapOrdPro.put(AppConstants.HAS_COMPANY, temLoja);
                    return mapOrdPro;
                } catch (Exception e1) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e1);
                    return null;
                }
            }

            return mapOrdPro;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        active = false;
        loaderManager.destroyLoader(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            getActivity().unregisterReceiver(broadCastNewMessage);
        }
    }


    public interface ToActivity {
        void ToolbarClickEmployeeMode(int id, Company company);

        void CompanyMode();
    }
}