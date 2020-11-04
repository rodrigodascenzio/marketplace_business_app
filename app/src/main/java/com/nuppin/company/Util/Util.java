package com.nuppin.company.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.net.Uri;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nuppin.company.Loja.Login.SplashScreen;
import com.nuppin.company.connection.ConnectApi;
import com.nuppin.company.model.Finance;
import com.nuppin.company.model.Material;
import com.nuppin.company.model.Product;
import com.nuppin.company.model.Company;
import com.nuppin.company.model.User;
import com.nuppin.company.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Util {

    public static String formater(double d) {
        DecimalFormat formatador;
        formatador = new DecimalFormat("0.0 km");
        return formatador.format(d);
    }

    public static String formaterRating(double d) {
        DecimalFormat formatador;
        formatador = new DecimalFormat("0.00");
        return formatador.format(d);
    }

    public static String formaterPrice(double d) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return numberFormat.format(d);
    }

    public static String formatDecimaisDoubleToString(double d) {
        return String.format("%.2f", d);
    }

    public static double unmaskPrice(String d) {
        return Double.parseDouble(d.replaceAll("[.]", "").replaceAll("[,]", ".").replaceAll("[^.0-9]", ""));//replaceAll("[^.0-9] para erro que só acontece em xiaomi android 9
    }

    public static double unmaskPriceToNegative(String d) {
        double b = Double.parseDouble(d.replaceAll("[.]", "").replaceAll("[,]", ".").replaceAll("[^.0-9]", ""));
        return -b;
    }


    public static String formaterPriceCupom(double d) {
        DecimalFormat formatador;
        if (d > 1000) {
            formatador = new DecimalFormat("R$#,##0.00");
            return formatador.format(d);
        } else {
            formatador = new DecimalFormat("R$0.00");
            return formatador.format(d);
        }
    }

    public static void hasPhoto(Object o, SimpleDraweeView foto) {
        if (o != null) {
            if (o instanceof Company) {
                if (foto.getId() == R.id.imageBanner) {
                    if (((Company) o).getBanner_photo() != null) {
                        foto.setImageURI(Uri.parse(((Company) o).getBanner_photo()));
                    }
                } else {
                    if (((Company) o).getphoto() != null) {
                        foto.setImageURI(Uri.parse(((Company) o).getphoto()));
                    }
                }
            } else if (o instanceof Product) {
                if (((Product) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Product) o).getphoto()));
                } else {
                    foto.setImageURI("");
                }
            } else if (o instanceof Finance) {
                if (((Finance) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Finance) o).getphoto()));
                }
            } else if (o instanceof User) {
                if (((User) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((User) o).getphoto()));
                }
            } else if (o instanceof Material) {
                if (((Material) o).getphoto() != null) {
                    foto.setImageURI(Uri.parse(((Material) o).getphoto()));
                } else {
                    foto.setImageURI("");
                }
            } else if (o instanceof String) {
                if (!o.equals("")) {
                    foto.setImageURI(Uri.parse(o.toString()));
                }
            }
        }
    }

    public static String nomeDiaSemana(String index) {
        if (index != null) {
            switch (index) {
                case "1":
                    return "Domingo";
                case "2":
                    return "Segunda";
                case "3":
                    return "Terça";
                case "4":
                    return "Quarta";
                case "5":
                    return "Quinta";
                case "6":
                    return "Sexta";
                case "7":
                    return "Sabado";
                default:
                    return "";
            }
        }
        return "";
    }

    public static String zeroToCalendar(int day, int month, int year) {
        if (day < 10) {
            if (month < 10) {
                return ("0" + day + "/0" + month + "/" + year);
            } else {
                return ("0" + day + "/" + month + "/" + year);
            }
        } else {
            if (month < 10) {
                return (day + "/0" + month + "/" + year);
            } else {
                return (day + "/" + month + "/" + year);
            }
        }
    }

    public static String timestampFormatDayMonth(String string) {
        String[] separated = string.split("-");
        return separated[2] + "/" + separated[1];
    }

    public static String timestampFormatDayMonthYear(String string) {
        String[] separated = string.split("-");
        return separated[2] + "/" + separated[1] + "/" + separated[0];
    }

    public static String zeroToCalendar(int month, int year) {

        if (month < 10) {
            return ("01" + "/0" + month + "/" + year);
        } else {
            return ("01" + "/" + month + "/" + year);
        }
    }


    public static String zeroToCalendarToMysql(int day, int month, int year) {
        if (day < 10) {
            if (month < 10) {
                return (year + "-0" + month + "-0" + day);
            } else {
                return (year + "-" + month + "-0" + day);
            }
        } else {
            if (month < 10) {
                return (year + "-0" + month + "-" + day);
            } else {
                return (year + "-" + month + "-" + day);
            }
        }
    }

    public static String zeroToCalendarToMysql(int month, int year) {

        if (month < 10) {
            return (year + "-0" + month + "-" + "01");
        } else {
            return (year + "-" + month + "-01");
        }
    }

    public static String zeroToTime(int minute, int hour) {
        if (hour < 10) {
            if (minute < 10) {
                return ("0" + hour + ":0" + minute);
            } else {
                return ("0" + hour + ":" + minute);
            }
        } else {
            if (minute < 10) {
                return (hour + ":0" + minute);
            } else {
                return (hour + ":" + minute);
            }
        }
    }

    public static String clearNotNumber(String txt) {
        return txt.replaceAll("[^0-9]", "");
    }

    public static String clearName(String name){
        return name.replaceAll("\\n", " ");
    }

    public static String clearExternalId(String txt) {
        return txt.replaceAll("[^a-zA-Z0-9]", "");
    }


    public static String mesDaData(String index) {
        if (index != null) {
            String[] separated = index.split("-");
            switch (separated[1]) {
                case "01":
                    return "Janeiro";
                case "02":
                    return "Fevereiro";
                case "03":
                    return "Março";
                case "04":
                    return "Abril";
                case "05":
                    return "Maio";
                case "06":
                    return "Junho";
                case "07":
                    return "Julho";
                case "08":
                    return "Agosto";
                case "09":
                    return "Setembro";
                case "10":
                    return "Outubro";
                case "11":
                    return "Novembro";
                case "12":
                    return "dezembro";
                default:
                    return "";
            }
        }
        return "";
    }

    public static String statusFatura(String index) {
        if (index != null) {
            switch (index) {
                case "pending":
                    return "Aguardando Pagamento";
                case "free":
                    return "Fatura Isenta";
                case "paid":
                    return "Fatura Paga";
                case "completed":
                    return "Fatura Paga";
                case "canceled":
                    return "Boleto cancelado";
                default:
                    return "";
            }
        }
        return "";
    }

    public static void cancelNotifyOnOff(Context context, int id) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(id);
    }

    /*public static void showNotifyOnOff(String message, String title, Context context) {
        //Configuraçõe para notificação
        String canal = context.getString(R.string.default_notification_channel_id);
        Uri soundOgg = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName() + "/" + R.raw.online);
        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(context, canal)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.racing_helmet)
                .setSound(soundOgg)
                .setSound(soundOgg)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setGroup("MovelOngoing");

        //Recupera notificationManager
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        //Verifica versão do Android a partir do Oreo para configurar canal de notificação
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(canal, "canal1", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(soundOgg, attributes);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notificacao.build());
    }
    */

    public static void setaToolbar(final Fragment fragment, int stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
            if (stringNome != 0) {
                toolbar.setTitle(stringNome);
            }
            if (changeIconNavigation) {
                if (iconNav != 0) {
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                    toolbar.setNavigationIcon(iconNav);
                }
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment != null && fragment.getFragmentManager() != null) {
                    fragment.getFragmentManager().popBackStack();
                }
            }
        });
    }

    public static void setaToolbar(final Fragment fragment, String stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayShowTitleEnabled(false);
            toolbar.setTitle(stringNome);
            if (changeIconNavigation) {
                if (iconNav != 0) {
                    activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                    toolbar.setNavigationIcon(iconNav);
                }
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fragment != null && fragment.getFragmentManager() != null) {
                    fragment.getFragmentManager().popBackStack();
                }
            }
        });
    }

    public static void setaToolbarActivity(int stringNome, Toolbar toolbar, final Activity actv, boolean changeIconNavigation, int iconNav) {
        final AppCompatActivity activity = (AppCompatActivity) actv;
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            if (stringNome != 0) {
                toolbar.setTitle(stringNome);
            }
            if (changeIconNavigation) {
                activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
                toolbar.setNavigationIcon(iconNav);
            } else {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    public static void backFragmentFunction(Fragment fragment) {
        if (fragment != null && fragment.getFragmentManager() != null) {
            fragment.getFragmentManager().popBackStack();
        }
    }


    public static String joinAddress(String rua, String numero, String bairro, String cidade, String estado, Context context) {
        return context.getResources().getString(R.string.joined_address, rua, numero, bairro, cidade, estado);
    }
    public static String joinAddress(String rua, String numero, String bairro, String cidade, String estado, String complemento, Context context) {
        return context.getResources().getString(R.string.joined_address_complement, rua, numero, bairro, cidade, estado, complemento);
    }

    public static String returnStringLatLonCountr(Context context) {
        return UtilShaPre.getDefaultsString(AppConstants.LATITUDE, context) + "," + UtilShaPre.getDefaultsString(AppConstants.LONGITUDE, context) + "," + UtilShaPre.getDefaultsString(AppConstants.COUNTRY_CODE, context);
    }

    public static String returnStringLatLonCountrOrEmpty(Context context) {
        if (UtilShaPre.getDefaultsString(AppConstants.COUNTRY_CODE, context).equals("")) {
            return "";
        }
        return "/" + UtilShaPre.getDefaultsString(AppConstants.LATITUDE, context) + "/" + UtilShaPre.getDefaultsString(AppConstants.LONGITUDE, context) + "/" + UtilShaPre.getDefaultsString(AppConstants.COUNTRY_CODE, context);
    }


    public static void splitAddressLocation(String endereco, Company company) {
        company.setFull_address(endereco);
        String[] separated = endereco.split(",");
        company.setStreet(separated[0].trim());
        String[] separatedNumBairro = separated[1].split("-");
        company.setStreet_number(Util.clearNotNumber(separatedNumBairro[0].trim()));
        company.setDistrict(separatedNumBairro[1].trim());
        String[] separatedCidadeEstado = separated[2].split("-");
        company.setCity(separatedCidadeEstado[0].trim());
        company.setState_code(separatedCidadeEstado[1].trim());
    }

    public static void splitAddressLocationWithoutNumber(String endereco, Company company) {
        company.setFull_address(endereco);
        String[] separated = endereco.split(",");
        company.setStreet(separated[0].trim());
        String[] separatedNumBairro = separated[1].split("-");
        company.setDistrict(separatedNumBairro[1].trim());
        String[] separatedCidadeEstado = separated[2].split("-");
        company.setCity(separatedCidadeEstado[0].trim());
        company.setState_code(separatedCidadeEstado[1].trim());
    }


    public static boolean validarTelefone(String telefone) {
        //retira todos os caracteres menos os numeros
        telefone = Util.clearNotNumber(telefone);

        //verifica se tem a qtde de numero correto
        if (!(telefone.length() > 10 && telefone.length() <= 11)) return false;

        //Se tiver 11 caracteres, verificar se começa com 9 o celular
        if (Integer.parseInt(telefone.substring(2, 3)) != 9) return false;

        //DDDs validos
        String[] codigosDDD = {"11", "12", "13", "14", "15", "16", "17", "18", "19",
                "21", "22", "24", "27", "28", "31", "32", "33", "34",
                "35", "37", "38", "41", "42", "43", "44", "45", "46",
                "47", "48", "49", "51", "53", "54", "55", "61", "62",
                "64", "63", "65", "66", "67", "68", "69", "71", "73",
                "74", "75", "77", "79", "81", "82", "83", "84", "85",
                "86", "87", "88", "89", "91", "92", "93", "94", "95",
                "96", "97", "98", "99"};

        //verifica se o DDD é valido
        return Arrays.asList(codigosDDD).indexOf(telefone.substring(0, 2)) != -1;//se passar por todas as validações acima, então está tudo certo
    }

    public static boolean retryAddressError(Context context) {
        if (UtilShaPre.getDefaultsInt("count_address", context) < 3) {
            UtilShaPre.setDefaults("count_address", UtilShaPre.getDefaultsInt("count_address", context) + 1, context);
            return true;
        }else {
            cleanRetryAddress(context);
            return false;
        }
    }

    public static void cleanRetryAddress(Context context) {
            UtilShaPre.setDefaults("count_address", 0, context);
    }

    public static double roundHalf(double num) {
        return Math.round(num * 10) / 10.0;
    }
}
