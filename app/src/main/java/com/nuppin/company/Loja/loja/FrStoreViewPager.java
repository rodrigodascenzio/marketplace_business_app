package com.nuppin.company.Loja.loja;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Chat;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.model.Status;
import com.nuppin.company.model.Mobile;
import com.nuppin.company.R;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.GpsUtils;
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

public class FrStoreViewPager extends Fragment
        implements LojasBottomAdapter.LojasBottomOnClickListener,
        LoaderManager.LoaderCallbacks<Map<String, Object>>,
        AcompanharPedidoAdapter.AcompanharPedidoOnClickListener,
        AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener {

    public static boolean active = false;
    private static final String PENDING = "pending";
    private static final String ATIVO = "active";
    private static final String INATIVO = "inactive";
    private static final String SUSPENSO = "suspended";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private Company company;
    private Intent it;
    private LoaderManager loaderManager;
    private AcompanharPedidoAdapter adapter;
    private AcompanharAgendamentoAdapter adapterServico;
    private RecyclerView mRecyclerView, recyclerView;
    private LinearLayout linearEmpty;
    private SimpleDraweeView fotoLoja;
    private TextView rating, onlineOrNot, fatura_qtd_txt, analyze_info, inativo, badgeChat, invisible, arquivo;
    private CardView aviso, cardAnalyze;
    private ImageView chatIcon;
    private Switch onlineOrNotSwitch;
    private Mobile sms;
    private Map<String, Object> data;
    private LojasBottomAdapter adapterBottom;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout;
    private ConstraintLayout mainContainer;
    private FloatingActionButton fabError, manual;
    private BroadcastReceiver broadCastNewMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // here you can update your db with new messages and update the ui (chat message list)
            loaderManager.restartLoader(0, null, FrStoreViewPager.this);
        }
    };
    private FancyShowCaseView fancyShowCaseView1;
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

        if (fancyShowCaseView1 == null) {
            fancyShowCaseView1 = new FancyShowCaseView.Builder(requireActivity())
                    .focusOn(recyclerView)
                    .title(getString(R.string.unique_tutorial_company_recycler_view_string))
                    .showOnce(getString(R.string.unique_tutorial_company_recycler_view))
                    .delay(500)
                    .backgroundColor(getResources().getColor(R.color.primary_light))
                    .enableAutoTextPosition()
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .closeOnTouch(true)
                    .dismissListener(listenerTutorial)
                    .build();
        }

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
            if (!fancyShowCaseView1.isShownBefore()) {
                fancyShowCaseView1.show();
                return;
            }
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

    private CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (onlineOrNotSwitch.isPressed()) {
                onlineOrNotSwitch.setClickable(false);
                if (b) {
                    if (company.getIs_delivery() != 1) {
                        onlineOrNotSwitch.setOnCheckedChangeListener(null);
                        onlineOrNotSwitch.setChecked(false);
                        onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                        onlineOrNotSwitch.setClickable(true);
                        Toast.makeText(getContext(), R.string.warning_settings_before_on, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (checkPemission()) {
                        functionPutOn();
                    }
                } else {
                    functionPutOff();
                }
            }
        }
    };

    public FrStoreViewPager() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        loaderManager = LoaderManager.getInstance(this);


        if (getActivity() != null) {
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newOrder"));
            getActivity().registerReceiver(this.broadCastNewMessage, new IntentFilter("newMessage"));
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000 * 5); // 5 minutos
        locationRequest.setFastestInterval(10 * 1000 * 2); // 2 minutos
        locationRequest.setSmallestDisplacement(500);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Toast.makeText(getContext(), R.string.location_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        if (isAdded()) {
                            if (sms != null) {
                                sms.setLatitude(location.getLatitude());
                                sms.setLongitude(location.getLongitude());
                            }
                            if (it == null) {
                                it = new Intent(getActivity(), UpdateLocationToCloudService.class);
                            }
                            it.putExtra(AppConstants.MOBILE, sms);
                            requireContext().startService(it);
                        }
                    }
                }
            }
        };

        View view = inflater.inflate(R.layout.fr_company, container, false);
        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FrStoreViewPager.this);
            }
        });

        manual = view.findViewById(R.id.manual);
        fotoLoja = view.findViewById(R.id.fotoLoja);
        linearEmpty = view.findViewById(R.id.linearEmpty);
        dots = view.findViewById(R.id.dots);
        mainContainer = view.findViewById(R.id.container);
        cardAnalyze = view.findViewById(R.id.cardAnalyze);
        linearStatus = view.findViewById(R.id.linearStatus);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        Util.setaToolbar(this, 0, toolbar, getActivity(), true, 0);

        aviso = view.findViewById(R.id.cardAviso);
        chatIcon = view.findViewById(R.id.chatIcon);
        fatura_qtd_txt = view.findViewById(R.id.qtd);
        analyze_info = view.findViewById(R.id.analyzeInfo);
        badgeChat = view.findViewById(R.id.chatQtd);


        aviso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(aviso.getId(), company);
                }
            }
        });

        cardAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(cardAnalyze.getId(), company);
                }
            }
        });

        chatIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(chatIcon.getId(), company);
                }
            }
        });

        arquivo = view.findViewById(R.id.txtArquivo);
        arquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(arquivo.getId(), company);
                }
            }
        });

        rating = view.findViewById(R.id.rating);
        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(rating.getId(), company);
                }
            }
        });

        onlineOrNot = view.findViewById(R.id.onlineOrNot);
        onlineOrNotSwitch = view.findViewById(R.id.onlineOrNotSwitch);
        inativo = view.findViewById(R.id.inativo);
        invisible = view.findViewById(R.id.invisible);

        mRecyclerView = view.findViewById(R.id.acompanhaStatus);

        manual.hide();
        manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(manual.getId(), company);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        recyclerView = view.findViewById(R.id.bottom);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager2);
        adapterBottom = new LojasBottomAdapter(this);

        adapterServico = new AcompanharAgendamentoAdapter(this);
        adapter = new AcompanharPedidoAdapter(this);

        onlineOrNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(onlineOrNot.getId(), company);
                }
            }
        });
        inativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(inativo.getId(), company);
                }
            }
        });
        invisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(invisible.getId(), company);
                }
            }
        });

        fotoLoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.ToolbarClick(fotoLoja.getId(), company);
                }
            }
        });
        recyclerView.setAdapter(adapterBottom);

        return view;
    }


    @NonNull
    @Override
    public Loader<Map<String, Object>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new StoresLoader(getActivity(), data);
            case 1:
                return new Sms(getActivity(), sms);
            case 2:
                return new SmsA(getActivity(), sms);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished
            (@NonNull Loader<Map<String, Object>> loader, Map<String, Object> data) {
        dots.setVisibility(View.GONE);
        switch (loader.getId()) {
            case 0:
                initializeStore(data);
                break;
            case 1:
                sms(data);
                loaderManager.destroyLoader(loader.getId());
                break;
            case 2:
                smsA(data);
                loaderManager.destroyLoader(loader.getId());
                break;
        }
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
        onlineOrNotSwitch.setVisibility(View.GONE);
        inativo.setVisibility(View.GONE);
        invisible.setVisibility(View.GONE);

        if (data != null) {
            this.data = data;
            if (data.get(AppConstants.COMPANY) != null && data.get(AppConstants.COMPANY) instanceof Company) {
                company = (Company) data.get(AppConstants.COMPANY);
                UtilShaPre.setDefaults(AppConstants.COMPANY, company.getCompany_id(), getContext());

                if (company.getValidation() != null) {
                    cardAnalyze.setVisibility(View.VISIBLE);
                    switch (company.getValidation()) {
                        case "pending":
                            analyze_info.setText("Empresa em análise");
                            break;
                        case "denied":
                            analyze_info.setText("Acesso negado. Veja detalhes");
                            break;
                    }
                } else {
                    cardAnalyze.setVisibility(View.GONE);
                }

                manual.show();

                Util.hasPhoto(company, fotoLoja);
                if (company.getNum_rating() != 0) {
                    rating.setVisibility(View.VISIBLE);
                    rating.setText(getResources().getString(R.string.rating_and_number, Util.formaterRating(company.getRating() / company.getNum_rating()), company.getNum_rating()));
                } else {
                    rating.setVisibility(View.GONE);
                }

                if (company.getModel_type().equals("fixed") && company.getStatus().equals(ATIVO) && company.getVisibility() == 1) {
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
                } else if (company.getModel_type().equals("mobile") && company.getStatus().equals(ATIVO) && company.getVisibility() == 1) {
                    onlineOrNotSwitch.setVisibility(View.VISIBLE);
                    onlineOrNotSwitch.setOnCheckedChangeListener(listener);

                    if (company.getIs_online() == 1) {
                        if (data.get(AppConstants.MOBILE) != null && data.get(AppConstants.MOBILE) instanceof Mobile) {
                            sms = (Mobile) data.get(AppConstants.MOBILE);
                            getLocation();
                            onlineOrNotSwitch.setOnCheckedChangeListener(null);
                            onlineOrNotSwitch.setChecked(true);
                            onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                        }
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
                    adapterBottom.setBottom(getResources().getStringArray(R.array.servico_loja_bottom));
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
                    adapterBottom.setBottom(getResources().getStringArray(R.array.loja_bottom));
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


                if (data.get(AppConstants.INVOICE) != null && data.get(AppConstants.INVOICE) instanceof List) {
                    int fat_qtd = ((List) data.get(AppConstants.INVOICE)).size();
                    if (fat_qtd > 0) {
                        aviso.setVisibility(View.VISIBLE);
                        fatura_qtd_txt.setText(R.string.fatura_em_aberto);
                    } else {
                        aviso.setVisibility(View.GONE);
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
                    if (getActivity() instanceof ToActivity) {
                        ToActivity listener = (ToActivity) getActivity();
                        listener.emptyStore();
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

    private void sms(Map<String, Object> data) {
        if (data != null) {
            if (data.get(AppConstants.MOBILE) instanceof Mobile) {
                getLocation();
                Toast.makeText(getContext(), R.string.online, Toast.LENGTH_SHORT).show();
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(true);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
            }
        } else {
            onlineOrNotSwitch.setOnCheckedChangeListener(null);
            onlineOrNotSwitch.setChecked(false);
            onlineOrNotSwitch.setOnCheckedChangeListener(listener);
            Toast.makeText(getContext(), R.string.error_online, Toast.LENGTH_SHORT).show();
        }
        onlineOrNotSwitch.setClickable(true);
    }

    private void smsA(Map<String, Object> data) {
        if (data != null) {
            if (data.get(AppConstants.MOBILE_UPDATE) instanceof Integer) {
                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(false);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                this.data = null;
                Toast.makeText(getContext(), R.string.offline, Toast.LENGTH_SHORT).show();
            }
        } else {
            onlineOrNotSwitch.setOnCheckedChangeListener(null);
            onlineOrNotSwitch.setChecked(true);
            onlineOrNotSwitch.setOnCheckedChangeListener(listener);
            Toast.makeText(getContext(), R.string.offline_erro, Toast.LENGTH_SHORT).show();
        }
        onlineOrNotSwitch.setClickable(true);
    }


    @Override
    public void onClick(String index) {
        if (getActivity() instanceof ToActivity) {
            ToActivity listener = (ToActivity) getActivity();
            listener.FrStoreViewPagerBottom(index, company);
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
            List<Invoice> listInvoices;
            List<Chat> listChats;
            Gson gson = new Gson();
            listStatus = new ArrayList<>();
            listStatusAge = new ArrayList<>();
            Status[] status;
            Status[] statusAge;
            listInvoices = new ArrayList<>();
            listChats = new ArrayList<>();
            Invoice[] invoices;
            Chat[] chats;
            Boolean temLoja;
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            Mobile sms;


            String stringJson = ConnectApi.GET(ConnectApi.COMPANY_USER + "/" + UtilShaPre.getDefaultsString(AppConstants.USER_ID, ctx) + Util.returnStringLatLonCountrOrEmpty(getContext()), getContext());
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
                    invoices = gson.fromJson(jObj.getAsJsonArray(AppConstants.INVOICE), Invoice[].class);
                    listInvoices.addAll(Arrays.asList(invoices));
                    mapOrdPro.put(AppConstants.INVOICE, listInvoices);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    listInvoices = null;
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
                try {
                    sms = gson.fromJson(jObj.getAsJsonObject(AppConstants.MOBILE), Mobile.class);
                    mapOrdPro.put(AppConstants.MOBILE, sms);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                    FirebaseCrashlytics.getInstance().recordException(e);
                    sms = null;
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

    private static class Sms extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Mobile sms;

        Sms(Context context, Mobile sms) {
            super(context);
            ctx = context;
            this.sms = sms;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                sms = gson.fromJson(ConnectApi.POST(sms, ConnectApi.MOBILE, getContext()), Mobile.class);
                mapOrdPro.put(AppConstants.MOBILE, sms);
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class SmsA extends AsyncTaskLoader<Map<String, Object>> {

        Context ctx;
        Mobile sms;

        SmsA(Context context, Mobile sms) {
            super(context);
            ctx = context;
            this.sms = sms;
        }


        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public Map<String, Object> loadInBackground() {
            Gson gson = new Gson();
            Map<String, Object> mapOrdPro;
            mapOrdPro = new HashMap<>();
            try {
                Integer s = gson.fromJson(ConnectApi.PATCH(sms, ConnectApi.MOBILE, getContext()), Integer.class);
                mapOrdPro.put(AppConstants.MOBILE_UPDATE, s);
                return mapOrdPro;
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private boolean checkPemission() {
        onlineOrNotSwitch.setOnCheckedChangeListener(null);
        onlineOrNotSwitch.setChecked(false);
        onlineOrNotSwitch.setOnCheckedChangeListener(listener);

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    2000);
            return false;
        } else {
            return true;
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);
        } else {
            functionGPS();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                functionGPS();
            } else {
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
                functionPutOff();
            }
        } else if (requestCode == 2000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                functionPutOn();
            } else {
                onlineOrNotSwitch.setClickable(true);
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(false);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
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
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true;
                if (sms == null) {
                    sms = new Mobile();
                }
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(true);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                sms.setCompany_id(company.getCompany_id());
                loaderManager.restartLoader(1, null, FrStoreViewPager.this);
            } else if (requestCode == AppConstants.LOCATION_REQUEST) {
                isGPS = true;
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        } else {
            if (requestCode == AppConstants.GPS_REQUEST) {
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(false);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                onlineOrNotSwitch.setClickable(true);
                isGPS = false;
            } else if (requestCode == AppConstants.LOCATION_REQUEST) {
                functionPutOff();
            }
        }
    }

    private void functionGPS() {
        new GpsUtils(FrStoreViewPager.this, requireContext(), AppConstants.LOCATION_REQUEST).turnGPSOn(new GpsUtils.onGpsListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        });
    }


    private void functionPutOn() {
        new GpsUtils(FrStoreViewPager.this, requireContext(), AppConstants.GPS_REQUEST).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
                if (sms == null) {
                    sms = new Mobile();
                }
                onlineOrNotSwitch.setOnCheckedChangeListener(null);
                onlineOrNotSwitch.setChecked(true);
                onlineOrNotSwitch.setOnCheckedChangeListener(listener);
                sms.setCompany_id(company.getCompany_id());
                loaderManager.restartLoader(1, null, FrStoreViewPager.this);
            }
        });

        if (!isGPS) {
            onlineOrNotSwitch.setOnCheckedChangeListener(null);
            onlineOrNotSwitch.setChecked(false);
            onlineOrNotSwitch.setOnCheckedChangeListener(listener);
            Toast.makeText(getContext(), R.string.turn_on_gps, Toast.LENGTH_SHORT).show();
        }
    }

    private void functionPutOff() {
        loaderManager.restartLoader(2, null, FrStoreViewPager.this);
    }


    public interface ToActivity {
        void FrStoreViewPagerBottom(String index, Company company);

        void ToolbarClick(int id, Company company);

        void emptyStore();
    }
}