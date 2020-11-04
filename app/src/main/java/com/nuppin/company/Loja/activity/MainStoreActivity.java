package com.nuppin.company.Loja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.nuppin.company.Loja.Fatura.FrAgeHistoricoFatura;
import com.nuppin.company.Loja.Fatura.FrOrdersHistoricoFatura;
import com.nuppin.company.Loja.loja.analise.FrValidation;
import com.nuppin.company.pos.produto.carrinho.FrCarrinhoManual;
import com.nuppin.company.pos.produto.carrinho.meiosPagamentoManual;
import com.nuppin.company.pos.produto.list.FrListaProdutosManual;
import com.nuppin.company.model.CartCompany;
import com.nuppin.company.pos.servico.ListaServicosManual;
import com.nuppin.company.pos.servico.MeiosPagamentoManual;
import com.nuppin.company.pos.servico.RvServicosAdapterManual;
import com.nuppin.company.pos.servico.scheduling.AgendamentosFr;
import com.nuppin.company.pos.servico.scheduling.HorariosFr;
import com.nuppin.company.pos.servico.scheduling.HorariosHeaderAdapter;
import com.nuppin.company.Loja.agendamentos.FrAgeHistorico;
import com.nuppin.company.Loja.Orders.FrOrdersHistorico;
import com.nuppin.company.Loja.loja.Employee.FrStoreByEmployee;
import com.nuppin.company.Loja.loja.Employee.MainFrEmployee;
import com.nuppin.company.Loja.loja.FrAvaliacoes;
import com.nuppin.company.Loja.loja.FrAvaliacoesAge;
import com.nuppin.company.Loja.loja.FrFeedback;
import com.nuppin.company.Loja.loja.perfil_user.FrPerfilUsuarioUpdateCelular;
import com.nuppin.company.Loja.loja.perfil_user.FrPerfilUsuarioUpdateEmail;
import com.nuppin.company.Loja.Config.ConfigLoja;
import com.nuppin.company.Loja.Config.ConfigServico;
import com.nuppin.company.Loja.Config.horarios.CriarEditarHorarioStore;
import com.nuppin.company.Loja.Config.horarios.StoreHorarios;
import com.nuppin.company.Loja.Config.horarios.StoreHorariosAdapter;
import com.nuppin.company.Loja.Config.pagamentos.meiosPagamento;
import com.nuppin.company.Loja.Cupom.AdicionarCupom;
import com.nuppin.company.Loja.Cupom.ListaCupom;
import com.nuppin.company.Loja.Equipe.AdicionarFuncionario;
import com.nuppin.company.Loja.Equipe.FuncionarioDetalhe;
import com.nuppin.company.Loja.Equipe.FuncionariosFr;
import com.nuppin.company.Loja.Fatura.FaturaDetalhe;
import com.nuppin.company.Loja.Fatura.FaturasFr;
import com.nuppin.company.Loja.Financeiro.CashFlowAdapter;
import com.nuppin.company.Loja.Financeiro.FrCriarEditarCashFlow;
import com.nuppin.company.Loja.Financeiro.FrDetalheCashFlowLoja;
import com.nuppin.company.Loja.Financeiro.FrFinanceiroStore;
import com.nuppin.company.Loja.Orders.FrOrders;
import com.nuppin.company.Loja.Relatorio.FrRelatorioStore;
import com.nuppin.company.Loja.Servico.CriarEditarServico;
import com.nuppin.company.Loja.Servico.ListaServicos;
import com.nuppin.company.Loja.Servico.RvServicosAdapter;
import com.nuppin.company.Loja.Servico.ServicoDetalhe;
import com.nuppin.company.Loja.agendamentos.FrAge;
import com.nuppin.company.Loja.loja.AcompanharAgendamentoAdapter;
import com.nuppin.company.Loja.loja.AcompanharPedidoAdapter;
import com.nuppin.company.Loja.loja.FrStoreViewPager;
import com.nuppin.company.Loja.loja.cadastro.unit1_1_cad;
import com.nuppin.company.Loja.loja.cadastro.unit1_cad;
import com.nuppin.company.Loja.loja.cadastro.unit2_1_cad;
import com.nuppin.company.Loja.loja.cadastro.unit2_cad;
import com.nuppin.company.Loja.loja.cadastro.unit3_cad;
import com.nuppin.company.Loja.loja.cadastro.unit4_cad;
import com.nuppin.company.Loja.loja.cadastro.unit5_cad;
import com.nuppin.company.Loja.loja.cadastro.unit6_cad;
import com.nuppin.company.Loja.loja.perfil.FrEditarLoja;
import com.nuppin.company.Loja.loja.perfil.MainFr;
import com.nuppin.company.Loja.loja.perfil_user.FrPerfilUsuario;
import com.nuppin.company.Loja.loja.perfil_user.FrPerfilUsuarioNav;
import com.nuppin.company.Loja.posts.FrDetalhePost;
import com.nuppin.company.Loja.posts.ListaPosts;
import com.nuppin.company.Loja.posts.RvPostsAdapter;
import com.nuppin.company.Loja.produto.FrCriarEditarProduto;
import com.nuppin.company.Loja.produto.FrDetalheProdutoLoja;
import com.nuppin.company.Loja.produto.RvProdutosAdapterLoja;
import com.nuppin.company.Loja.produto.conjunto.FrCriarEditarGroup;
import com.nuppin.company.Loja.produto.conjunto.FrDetalheGroup;
import com.nuppin.company.Loja.produto.conjunto.FrListaGroup;
import com.nuppin.company.Loja.produto.conjunto.RvListGroup;
import com.nuppin.company.Loja.produto.extra.FrCriarEditarExtra;
import com.nuppin.company.Loja.produto.extra.FrListaExtra;
import com.nuppin.company.Loja.produto.extra.RvExtraAdapter;
import com.nuppin.company.Loja.produto.FrListaProdutosLoja;
import com.nuppin.company.Loja.produto.size.FrCriarEditarProductSize;
import com.nuppin.company.Util.AppConstants;
import com.nuppin.company.chat.FrListChats;
import com.nuppin.company.chat.RvListChat;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.Collection;
import com.nuppin.company.model.Extra;
import com.nuppin.company.model.Order;
import com.nuppin.company.model.Size;
import com.nuppin.company.model.Scheduling;
import com.nuppin.company.model.Finance;
import com.nuppin.company.model.Chat;
import com.nuppin.company.model.Employee;
import com.nuppin.company.model.OpeningHours;
import com.nuppin.company.model.Material;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Service;
import com.nuppin.company.model.User;
import com.nuppin.company.R;
import com.nuppin.company.Util.UtilShaPre;
import com.nuppin.company.chat.FrChat;

import me.toptas.fancyshowcase.FancyShowCaseView;

public class MainStoreActivity extends AppCompatActivity implements
        RvProdutosAdapterLoja.RvProdutosLojaOnClickListener,
        RvServicosAdapter.RvServicosOnClickListener,
        StoreHorariosAdapter.diaSemanaOnClickListener,
        AcompanharPedidoAdapter.AcompanharPedidoOnClickListener,
        AcompanharAgendamentoAdapter.AcompanharAgendamentoOnClickListener,
        CashFlowAdapter.CashFlowAdapterVHOnClickListener,
        RvListGroup.RvConjuctOnClickListener,
        MainFr.ToActivity,
        FrStoreViewPager.ToActivity,
        FrListaProdutosLoja.ToActivity,
        FrDetalheProdutoLoja.ToActivity,
        ServicoDetalhe.ToActivity,
        FuncionariosFr.ToActivity,
        ListaServicos.ToActivity,
        StoreHorarios.ToActivity,
        ListaCupom.ToActivity,
        FaturasFr.ToActivity,
        FrFinanceiroStore.ToActivity,
        FrDetalheCashFlowLoja.ToActivity,
        unit1_cad.NovaEmpresa,
        unit2_cad.NovaEmpresa,
        unit2_1_cad.NovaEmpresa,
        unit3_cad.NovaEmpresa,
        unit4_cad.NovaEmpresa,
        unit5_cad.NovaEmpresa,
        unit6_cad.NovaEmpresa,
        unit1_1_cad.NovaEmpresa,
        ConfigLoja.ConfigDetails,
        ConfigServico.ConfigDetails,
        RvPostsAdapter.RvPostsOnClickListener,
        FrOrders.ToActivity,
        FrAge.ToActivity,
        FrPerfilUsuarioNav.ToActivity,
        FrPerfilUsuario.ToActivity,
        FaturaDetalhe.ToActivity,
        RvListChat.ChatOnClickListener,
        MainFrEmployee.ToActivity,
        FrStoreByEmployee.ToActivity,

        FrListaGroup.ToActivity,
        FrDetalheGroup.ToActivity,
        FrListaExtra.ToActivity,
        RvExtraAdapter.RvExtraItemsOnClickListener,
        RvServicosAdapterManual.RvServicosOnClickListener,
        HorariosHeaderAdapter.HorariosAdapterListener,
        AgendamentosFr.FrtoActivity,
        FrCarrinhoManual.FrtoActivity {

    private FragmentTransaction transaction;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        clearBackStack();

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseInstanceId.getInstance().getInstanceId();

        if (UtilShaPre.getDefaultsString("mode", this).equals("employee")) {
            openFragment(new FrStoreByEmployee(), "FrStoreByEmployee", false);
        } else {
            openFragment(new FrStoreViewPager(), "Home", false);
        }
        openFrag(getIntent());
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openFrag(intent);
    }

    public void openFrag(Intent it) {
        String json = it.getStringExtra(AppConstants.CHAT);
        Gson gson = new Gson();
        if (json != null) {
            try {
                Chat chat = gson.fromJson(json, Chat.class);
                Company company = new Company();
                company.setCompany_id(chat.getChat_to());
                if (getSupportFragmentManager().findFragmentByTag("FrChat") == null) {
                    openFragment(FrListChats.newInstance(company), "FrListChats", true);
                    //openFragment(FrChat.newInstance(chat.getChat_from(), chat.getChat_order_id(), chat.getChat_to(), ""), "FrChat", true);
                }
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log(UtilShaPre.getDefaultsString(AppConstants.USER_ID, this));
                FirebaseCrashlytics.getInstance().recordException(e);
                Toast.makeText(this, "Erro ao abrir o chat", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onClick(Product product) {
        openFragment(FrDetalheProdutoLoja.novaIntancia(product), "FrDetalheProdutoLoja", true);
    }

    private void openFragment(Fragment fragment, String tagBackStack, boolean tag) {
        transaction = getSupportFragmentManager().beginTransaction();

        if (tagBackStack.equals("Home")) {
            clearBackStack();
        }

        transaction.replace(R.id.container, fragment, tagBackStack);
        if (tag) {
            transaction.addToBackStack(tagBackStack);
        }

        transaction.replace(R.id.container, fragment, tagBackStack);
        transaction.commit();
        firebaseAnalytics.setCurrentScreen(this, tagBackStack, tagBackStack);
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        transaction = null;
    }

    @Override
    public void MainFrBottom(int index, Company company) {
        switch (index) {
            case R.id.btnCriar:
                openFragment(unit1_cad.newInstance(UtilShaPre.getDefaultsString(AppConstants.USER_ID, this)), "CriarLoja", true);
                break;
            case R.id.btnEditar:
                openFragment(FrEditarLoja.newInstance(company), "EditarLoja", true);
                break;
            case R.id.feedback:
                openFragment(new FrFeedback(), "FrFeedback", true);
                break;
            case R.id.fotoPerfil:
                openFragment(FrPerfilUsuarioNav.newInstance(UtilShaPre.getDefaultsString(AppConstants.USER_ID, this)), "PerfilUsuario", true);
                break;
            case R.id.config:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(ConfigServico.newInstance(company), "configServico", true);
                } else {
                    openFragment(ConfigLoja.newInstance(company), "configLoja", true);
                }
                break;
        }
    }

    @Override
    public void EmployeeMode() {
        clearBackStack();
        openFragment(new FrStoreByEmployee(), "FrStoreByEmployee", false);
    }

    @Override
    public void CompanyMode() {
        clearBackStack();
        openFragment(new FrStoreViewPager(), "Home", false);
    }

    @Override
    public void MainFrBottom(User user) {
        openFragment(FrPerfilUsuarioUpdateEmail.newInstance(user), "EditarEmail", true);
    }

    @Override
    public void FrStoreViewPagerBottom(String index, Company company) {
        switch (index) {
            case "Produtos":
                openFragment(FrListaProdutosLoja.novaInstancia(company), "frListaProdutosLoja", true);
                break;
            case "Equipe":
                openFragment(FuncionariosFr.newInstance(company), "FrFuncionarios", true);
                break;
            case "Faturas":
                openFragment(FaturasFr.newInstance(company), "FaturasFr", true);
                break;
            case "Cupom":
                openFragment(ListaCupom.newInstance(company), "FrCupons", true);
                break;
            case "Relatório":
                openFragment(FrRelatorioStore.newInstance(company), "FrRelatorioStore", true);
                break;
            case "Financeiro":
                openFragment(FrFinanceiroStore.newInstance(company), "FrFinanceiroStore", true);
                break;
            case "Serviços":
                openFragment(ListaServicos.newInstance(company), "ListaServicos", true);
                break;
            case "Área de estudos":
                if (!company.getStatus().equals("suspended")) {
                    openFragment(new ListaPosts(), "Blog", true);
                } else {
                    Toast.makeText(this, "Área Premium, regularize sua fatura", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void ToolbarClick(int id, Company company) {
        switch (id) {
            case R.id.cardAviso:
            case R.id.inativo:
                openFragment(FaturasFr.newInstance(company), "FaturasFr", true);
                break;
            case R.id.chatIcon:
                openFragment(FrListChats.newInstance(company), "FrListChats", true);
                break;
            case R.id.onlineOrNot:
                openFragment(StoreHorarios.newInstance(company), "StoreHorarios", true);
                break;
            case R.id.invisible:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(ConfigServico.newInstance(company), "configServico", true);
                } else {
                    openFragment(ConfigLoja.newInstance(company), "configLoja", true);
                }
                break;
            case R.id.rating:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(FrAvaliacoesAge.newInstance(company), "AgendamentosAvaliacoes", true);
                } else {
                    openFragment(FrAvaliacoes.newInstance(company), "OrdersAvaliacoes", true);
                }
                break;
            case R.id.fotoLoja:
                openFragment(new MainFr(), "MainFr", true);
                break;
            case R.id.txtArquivo:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(FrAgeHistorico.newInstance(company), "AgendamentosHistorico", true);
                } else {
                    openFragment(FrOrdersHistorico.newInstance(company), "OrdersHistorico", true);
                }
                break;
            case R.id.manual:
                if (!company.getStatus().equals("suspended")) {
                    if (company.getCategory_company_id().equals("3")) {
                        openFragment(ListaServicosManual.novaInstancia2(company), "ListaServicosManual", true);
                    } else {
                        if (company.getIs_local() == 0 && company.getIs_delivery() == 0 && company.getIs_pos() == 0) {
                            Toast.makeText(this, "Defina as configurações antes de iniciar", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "", this);
                        openFragment(new FrCarrinhoManual(), "FrCarrinhoManual", true);
                    }
                } else {
                    Toast.makeText(this, "Área Premium, regularize sua fatura", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cardAnalyze:
                openFragment(FrValidation.novaInstancia(company), "FrValidation", true);
                break;
        }
    }

    @Override
    public void emptyStore() {
        openFragment(new MainFr(), "MainFr", false);
    }

    @Override
    public void ListaProdutosBottom(int id, Company company) {
        if (id == R.id.fab) {
            openFragment(FrCriarEditarProduto.newInstance2(company), "FrCriarEditarProduto", true);
        } else if (id == R.id.fab2) {
            openFragment(FrListaGroup.novaInstancia(company), "FrListaConjunto", true);
        }
    }

    @Override
    public void DetalheProdutoBottom(int index, Product product) {
        if (index == R.id.fab) {
            openFragment(FrCriarEditarProduto.newInstance(product), "FrCriarEditarProduto", true);
        } else {
            Company company = new Company();
            company.setCompany_id(product.getCompany_id());
            if (index == 1) {
                openFragment(FrListaGroup.novaInstancia(company), "FrListaConjunto", true);
            }
        }
    }

    @Override
    public void addSize(Product product) {
        openFragment(FrCriarEditarProductSize.newInstanceAdd(product), "FrCriarEditarProductSize", true);
    }

    @Override
    public void editSize(Size product) {
        openFragment(FrCriarEditarProductSize.newInstanceEdit(product), "FrCriarEditarProductSize", true);
    }

    @Override
    public void ServicosDetalheBottom(int index, Service servico) {
        switch (index) {
            case 0:
                openFragment(CriarEditarServico.newInstance(servico), "EditarServico", true);
                break;
            case 1:
                openFragment(FuncionariosFr.newInstance(servico), "FuncionariosFr", true);
                break;
        }
    }

    @Override
    public void FrFuncionariosBottom(String companyId) {
        openFragment(AdicionarFuncionario.newInstance(companyId), "AdicionarFuncionario", true);
    }

    @Override
    public void FrFuncionarioDetalhe(Employee employee) {
        openFragment(FuncionarioDetalhe.newInstance(employee), "FuncionarioDetalhe", true);
    }


    @Override
    public void ServicosBottom(Company company) {
        openFragment(CriarEditarServico.newInstance(company), "AdicionarServico", true);
    }

    @Override
    public void onClick(Service service) {
        openFragment(ServicoDetalhe.newInstance(service), "ServicoDetalhe", true);
    }

    @Override
    public void cycleCad(String index, Company company) {
        switch (index) {
            case "1_1":
                openFragment(unit1_1_cad.newInstance(company), "clico1_1", true);
                break;
            case "2":
                openFragment(unit2_cad.newInstance(company), "clico2", true);
                break;
            case "2_1":
                openFragment(unit2_1_cad.newInstance(company), "clico2_1", true);
                break;
            case "3":
                openFragment(unit3_cad.newInstance(company), "clico3", true);
                break;
            case "4":
                openFragment(unit4_cad.newInstance(company), "clico4", true);
                break;
            case "5":
                openFragment(unit5_cad.newInstance(company), "clico5", true);
                break;
            case "6":
                openFragment(unit6_cad.newInstance(company), "clico6", true);
                break;
            case "listLojas":
                clearBackStack();
                openFragment(new FrStoreViewPager(), "Home", false);
                break;

        }
    }


    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void clickDetails(int index, Company company) {
        switch (index) {
            case R.id.cardHorarios:
                openFragment(StoreHorarios.newInstance(company), "StoreHorarios", true);
                break;
            case R.id.cardMeioPag:
                openFragment(meiosPagamento.newInstance(company), "meiosPagamento", true);
                break;
        }
    }

    @Override
    public void AddHorarioDia(Company company) {
        openFragment(CriarEditarHorarioStore.newInstance(company), "addHorario", true);
    }

    @Override
    public void AddCupom(Company company) {
        openFragment(AdicionarCupom.newInstance(company), "addCupom", true);
    }

    @Override
    public void onClickDiaSemana(OpeningHours openingHours, Company company) {
        openFragment(CriarEditarHorarioStore.newInstance(openingHours, company), "updateHorario", true);
    }

    @Override
    public void onClickStatus(String index, String stoId) {
        openFragment(FrOrders.newInstance(stoId, index), "FrOrders", true);
    }

    @Override
    public void onClickStatusAgendamento(String index, String stoId) {
        openFragment(FrAge.newInstance(stoId, index), "FrAge", true);
    }

    @Override
    public void FaturaClick(String id) {
        openFragment(FaturaDetalhe.newInstance(id), "FaturaDetalhe", true);

    }

    @Override
    public void CriarReceitaDespesa(Company company) {
        openFragment(FrCriarEditarCashFlow.newInstance2(company), "CriarEditarCashFlow", true);
    }

    @Override
    public void onClickFromCashFlow(Finance finance) {
        openFragment(FrDetalheCashFlowLoja.newInstance(finance), "DetalheCashFlow", true);
    }

    @Override
    public void FabCashDetalhe(Finance finance) {
        openFragment(FrCriarEditarCashFlow.newInstance(finance), "CriarEditarCashFlow", true);
    }

    @Override
    public void onClick(Material material) {
        openFragment(FrDetalhePost.novaIntancia(material.getMaterial_id()), "PostDetalhe", true);
    }

    @Override
    public void chat(Order order) {
        openFragment(FrChat.newInstance(order.getUser_id(), order.getOrder_id(), order.getCompany_id(), order.getUser_name()), "chat", true);
    }

    @Override
    public void chat(Scheduling agendamento) {
        openFragment(FrChat.newInstance(agendamento.getUser_id(), agendamento.getScheduling_id(), agendamento.getCompany_id(), agendamento.getUser_name()), "chat", true);
    }

    @Override
    public void editarUsuario(User user) {
        openFragment(FrPerfilUsuario.newInstance(user), "EditarPerfilUsuario", true);
    }

    @Override
    public void updateEmail(User user) {
        openFragment(FrPerfilUsuarioUpdateEmail.newInstance(user), "EditarEmail", true);
    }

    @Override
    public void updateCelular(User user) {
        openFragment(FrPerfilUsuarioUpdateCelular.newInstance(user), "EditarCelular", true);
    }

    @Override
    public void feedback() {
        openFragment(new FrFeedback(), "FrFeedback", true);
    }

    @Override
    public void historicoFaturaOrders(String stoId, String fatDataCad, String stoCategoria) {
        if (stoCategoria.equals("3")) {
            openFragment(FrAgeHistoricoFatura.newInstance(stoId, fatDataCad), "HistoricoAgendamentosFatura", true);
        } else {
            openFragment(FrOrdersHistoricoFatura.newInstance(stoId, fatDataCad), "HistoricoOrdersFatura", true);
        }
    }

    @Override
    public void onClickChat(String order_id, String userId, String userNome, String stoId) {
        openFragment(FrChat.newInstance(userId, order_id, stoId, userNome), "chat", true);
    }

    @Override
    public void onClick(Collection collection) {
        openFragment(FrDetalheGroup.novaIntancia(collection), "FrDetalheConjunto", true);
    }

    @Override
    public void DetalheConjuctBottom(int index, Collection collection) {
        if (index == R.id.fab) {
            openFragment(FrCriarEditarGroup.newInstance(collection), "FrCriarEditarConjunto", true);
        } else {
            Company company = new Company();
            company.setCompany_id(collection.getCompany_id());
            openFragment(FrListaExtra.novaInstancia(company), "FrListaExtra", true);
        }
    }

    @Override
    public void ListaConjuctBottom(int index, Company company) {
        if (index == R.id.fab) {
            openFragment(FrCriarEditarGroup.newInstance2(company), "FrCriarEditarConjunto", true);
        } else {
            openFragment(FrListaExtra.novaInstancia(company), "FrListaExtra", true);
        }
    }

    @Override
    public void ListaExtraItemBottom(Company company) {
        openFragment(FrCriarEditarExtra.newInstance2(company), "FrCriarEditarExtra", true);
    }

    @Override
    public void onClick(Extra extraItems) {
        openFragment(FrCriarEditarExtra.newInstance(extraItems), "FrCriarEditarExtra", true);
    }

    @Override
    public void onClickHorario(Scheduling horario, Service service, Company company) {
        openFragment(AgendamentosFr.newInstance(horario, service, company), "FrAgendamentos", true);
    }

    @Override
    public void onClick(Service service, Company company) {
        openFragment(HorariosFr.newInstance(service, company), "FrHorarios", true);
    }

    @Override
    public void meioPagamentoManual(Scheduling scheduling) {
        openFragment(MeiosPagamentoManual.newInstance(scheduling), "MeiosPagamentoManual", true);
    }


    @Override
    public void ToolbarClickEmployeeMode(int id, Company company) {
        switch (id) {
            case R.id.chatIcon:
                openFragment(FrListChats.newInstance(company), "FrListChats", true);
                break;
            case R.id.rating:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(FrAvaliacoesAge.newInstance(company), "AgendamentosAvaliacoes", true);
                } else {
                    openFragment(FrAvaliacoes.newInstance(company), "OrdersAvaliacoes", true);
                }
                break;
            case R.id.fotoLoja:
                openFragment(MainFrEmployee.newInstance(company), "MainFrEmployee", true);
                break;
            case R.id.txtArquivo:
                if (company.getCategory_company_id().equals("3")) {
                    openFragment(FrAgeHistorico.newInstance(company), "AgendamentosHistorico", true);
                } else {
                    openFragment(FrOrdersHistorico.newInstance(company), "OrdersHistorico", true);
                }
                break;
            case R.id.manual:
                if (!company.getStatus().equals("suspended")) {
                    if (company.getCategory_company_id().equals("3")) {
                        openFragment(ListaServicosManual.novaInstancia2(company), "ListaServicosManual", true);
                    } else {
                        if (company.getIs_local() == 0 && company.getIs_delivery() == 0 && company.getIs_pos() == 0) {
                            Toast.makeText(this, "As configurações ainda não estão definidas para iniciar essa função", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        UtilShaPre.setDefaults(AppConstants.ORDER_TYPE, "", this);
                        openFragment(new FrCarrinhoManual(), "FrCarrinhoManual", true);
                    }
                } else {
                    Toast.makeText(this, "Acesso não liberado", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void meioPagamentoManual(CartCompany cartCompany) {
        openFragment(meiosPagamentoManual.newInstance(cartCompany), "meiosPagamentos", true);
    }

    @Override
    public void companyCartManual(CartCompany company) {
        openFragment(FrListaProdutosManual.newInstance(company), "FrListaProdutosManual", true);
    }

    @Override
    public void onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this);
        } else {
            super.onBackPressed();
        }
    }
}