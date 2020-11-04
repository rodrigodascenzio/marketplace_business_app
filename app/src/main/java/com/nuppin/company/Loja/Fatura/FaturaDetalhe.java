package com.nuppin.company.Loja.Fatura;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.nuppin.company.Loja.dialogs.InfoDialogFragment;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Invoice;
import com.nuppin.company.R;
import com.nuppin.company.Util.Util;

import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.getSystemService;

public class FaturaDetalhe extends Fragment implements LoaderManager.LoaderCallbacks, InfoDialogFragment.InfoDialogListener {
    private String faturaId;
    private Invoice invoice;
    private static final String INVOICE_ID = "ID";
    private LoaderManager loaderManager;
    private TextView mes, venc, valor, status, boleto_num, data;
    private LottieAnimationView dots;
    private ConstraintLayout errorLayout, boletoAtivo, boletoCancelado;
    private FloatingActionButton fabError;
    private MaterialButton btnHistorico, gerarBoleto;
    private TextView plano, comissao;
    private CardView cardBoleto, progress;

    public FaturaDetalhe() {
        // Required empty public constructor
    }

    public static FaturaDetalhe newInstance(String id) {
        FaturaDetalhe fragment = new FaturaDetalhe();
        Bundle args = new Bundle();
        args.putString(INVOICE_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        if (getArguments().containsKey(INVOICE_ID)) {
            faturaId = getArguments().getString(INVOICE_ID);
        }

        loaderManager = LoaderManager.getInstance(this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fr_invoice_detail, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_top);
        Util.setaToolbar(this, R.string.detalhe_toolbar, toolbar, getActivity(), false, 0);

        dots = view.findViewById(R.id.dots);
        errorLayout = view.findViewById(R.id.error_layout);
        fabError = view.findViewById(R.id.fabError);
        fabError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dots.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                loaderManager.restartLoader(0, null, FaturaDetalhe.this);
            }
        });

        progress = view.findViewById(R.id.progress);

        plano = view.findViewById(R.id.valorPlano);
        comissao = view.findViewById(R.id.valorComissao);
        btnHistorico = view.findViewById(R.id.btnHistorico);

        boletoCancelado = view.findViewById(R.id.boletoCancelado);
        boletoAtivo = view.findViewById(R.id.boletoAtivo);
        cardBoleto = view.findViewById(R.id.cardDados);
        gerarBoleto = view.findViewById(R.id.gerarBoleto);

        btnHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ToActivity) {
                    ToActivity listener = (ToActivity) getActivity();
                    listener.historicoFaturaOrders(invoice.getCompany_id(), invoice.getCreated_date(), invoice.getCategory_company_id());
                }
            }
        });

        gerarBoleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFrag = InfoDialogFragment.newInstance(getResources().getString(R.string.gerar_novo_boleto_aviso), getResources().getString(R.string.gerar_boleto), getResources().getString(R.string.aguardar));
                dialogFrag.show(FaturaDetalhe.this.getChildFragmentManager(), "dialog_fragment");
            }
        });

        mes = view.findViewById(R.id.fat_data_cad);
        data = view.findViewById(R.id.fat_data);
        venc = view.findViewById(R.id.fat_vencimento);
        valor = view.findViewById(R.id.valor);
        status = view.findViewById(R.id.status);
        boleto_num = view.findViewById(R.id.dados);

        MaterialButton num = view.findViewById(R.id.num);
        num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = getSystemService(Objects.requireNonNull(getContext()), ClipboardManager.class);
                ClipData clip = ClipData.newPlainText("boleto_num", boleto_num.getText().toString());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getContext(), R.string.copy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        MaterialButton pdf = view.findViewById(R.id.pdf);
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setDataAndType(Uri.parse(invoice.getExternal_link()), "application/pdf");

                Intent chooser = Intent.createChooser(browserIntent, "pdf");
                chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // optional

                if (isActivityForIntentAvailable(Objects.requireNonNull(getContext()), chooser)) {
                    startActivity(chooser);
                } else {
                    Toast.makeText(getContext(), R.string.warning_pdf, Toast.LENGTH_SHORT).show();
                }


            }
        });

        loaderManager.restartLoader(0, null, this);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        loaderManager.destroyLoader(0);
    }

    private static boolean isActivityForIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @NonNull
    @Override
    public Loader onCreateLoader(int id, @Nullable Bundle args) {
        if (id == 0) {
            return new GetFaturaDetalhada(requireContext(), faturaId);
        } else {
            return new NovoBoleto(requireContext(), invoice);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Object data) {
        dots.setVisibility(View.GONE);
        if (loader.getId() == 0) {
            if (data instanceof Invoice) {
                invoice = (Invoice) data;

                if (invoice.getFee_amount() == 0) {
                    btnHistorico.setVisibility(View.GONE);
                }


                if (invoice.getStatus().equals("canceled")) {
                    boletoCancelado.setVisibility(View.VISIBLE);
                    boletoAtivo.setVisibility(View.GONE);
                } else {
                    boletoAtivo.setVisibility(View.VISIBLE);
                    boletoCancelado.setVisibility(View.GONE);
                }

                if (invoice.getStatus().equals("free") || invoice.getStatus().equals("paid") || invoice.getStatus().equals("completed")) {
                    cardBoleto.setVisibility(View.GONE);
                }

                boleto_num.setText(invoice.getCode_line());

                plano.setText(Util.formaterPrice(invoice.getSubtotal_amount()));
                comissao.setText(Util.formaterPrice(invoice.getFee_amount()));

                valor.setText(Util.formaterPrice(invoice.getTotal_amount()));
                this.data.setText(Util.timestampFormatDayMonthYear(invoice.getCreated_date()));
                status.setText(Util.statusFatura(invoice.getStatus()));
                mes.setText(Util.mesDaData(invoice.getCreated_date()));
                venc.setText(getResources().getString(R.string.vencimento, Util.timestampFormatDayMonthYear(invoice.getDue_date())));
                errorLayout.setVisibility(View.GONE);
            } else {
                errorLayout.setVisibility(View.VISIBLE);
            }
        } else if (loader.getId() == 1) {
            progress.setVisibility(View.GONE);
            loaderManager.destroyLoader(loader.getId());
            if (data instanceof Boolean && (Boolean)data) {
                Toast.makeText(getContext(), "Boleto gerado com sucesso", Toast.LENGTH_SHORT).show();
                dots.setVisibility(View.VISIBLE);
                loaderManager.restartLoader(0, null, this);
            } else {
                gerarBoleto.setClickable(true);
                Toast.makeText(getContext(), "Houve um erro ao gerar o boleto", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {

    }

    @Override
    public void onDialogOKClick(View view, InfoDialogFragment info) {
        switch (view.getId()) {
            case R.id.btnPositive:
                info.dismiss();
                progress.setVisibility(View.VISIBLE);
                gerarBoleto.setClickable(false);
                loaderManager.restartLoader(1, null, FaturaDetalhe.this);
                break;
            case R.id.btnNegative:
                info.dismiss();
                break;
        }

    }

    private static class GetFaturaDetalhada extends AsyncTaskLoader {

        String faturaId;
        Context ctx;

        GetFaturaDetalhada(@NonNull Context context, String faturaId) {
            super(context);
            this.faturaId = faturaId;
            this.ctx = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Object loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.GET(ConnectApi.COMPANY_INVOICE_DETAIL + "/" + faturaId, getContext()), Invoice.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    private static class NovoBoleto extends AsyncTaskLoader {

        Context ctx;
        Invoice invoice;

        NovoBoleto(@NonNull Context context, Invoice invoice) {
            super(context);
            this.invoice = invoice;
            this.ctx = context;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Nullable
        @Override
        public Boolean loadInBackground() {
            try {
                Gson gson = new Gson();
                return gson.fromJson(ConnectApi.POST(invoice, ConnectApi.COMPANY_UPDATE_BOLETO, getContext()), Boolean.class);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, getContext()));
                FirebaseCrashlytics.getInstance().recordException(e);
                return null;
            }
        }
    }

    public interface ToActivity {
        void historicoFaturaOrders(String stoId, String fatDataCad, String stoCategoria);
    }

}