package com.example.warhammer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Post implements Comparable<Post>{
    private String photo_id;
    private String alb_id;
    private String description;
    private ArrayList<String> name;
    private ArrayList<String> price;
    private String normPhoto;
    private String photoThumb;
    private String user_id;

    public Post(String id, String text, String url_photo, String url_photoThumb, String alb_id, String user_id) {
        this.photo_id = id;
        this.description = text;
        this.normPhoto = url_photo;
        this.alb_id = alb_id;
        this.photoThumb = url_photoThumb;
        this.name = new ArrayList<String>();
        this.price = new ArrayList<String>();
        if (!user_id.equals("100")) this.user_id = "id"+user_id; //нужно открывать их по вот этой ссылке "https://vk.com/id"
        else this.user_id = "public"+ MainActivity.id_SRM;
        makeNameAndPrice(description);
    }

    private Post() {    }

    public String getPhoto() {        return normPhoto;    }

    public String getPhotoThumb() {        return photoThumb;    }

    public String getPhoto_id() {        return photo_id;    }

    public String getAlb_id() {        return alb_id;    }

    public String getDescription() {        return description;    }

    public void setDescription(String text) {
        this.description = text;
        makeNameAndPrice(description);
    }

    public String getName() {
        String a = this.name.toString();
        return a.substring(1).substring(0,a.length() - 2);
    }

    public String getName(int id) {
        return this.name.get(id);
    }

    public int getNameSize() {
        return this.name.size();
    }

    public String getPrice(int id) {
        String a = this.price.get(this.price.size() - 1);
        if (!a.equals("см описание") && !a.equals("нету")) return price.get(id)+" руб";
        else return a;
    }

    private int getPrice(int id, boolean b) {
        String a = this.price.get(this.price.size() - 1);
        if (!a.equals("см описание") && !a.equals("нету")) return Integer.valueOf(price.get(id));
        else return -1;
    }

    public String getUser() {        return "https://vk.com/" + this.user_id;    }

    @Override
    public int compareTo(Post anoth_post) {
        //давайте будем сортировать объекты по значению поля от меньшего к большему
        //будем возвращать отрицательное число, 0 или положительное число по каждому сравнению объектов
        //сортируем по цене (первый элемент), если элементов = 1
        return Integer.compare(this.getPrice(0, false),anoth_post.getPrice(0, false));
    }

    private void makeNameAndPrice(String description) {
        String lastWord = ""; StringBuffer lastName = new StringBuffer("");
        AtomicBoolean flag = new AtomicBoolean(false);
        //ищем цену 1250р 2450(цифр больше 2) больше одной цены см описание 2к
        String[] words = description.split("[^0-9a-zA-Zа-яА-Я%_-]+"); //все равно пробелы не убирает все
        for (String word : words) {
            if (word.toLowerCase().matches("[тк]") || word.toLowerCase().matches("ты[сшщ]") ){ //tolewercase чтобы искал без учета регистра
                if (lastWord.matches("[-+]?\\d+") ) //если предыдущее слов - число
                {
                    if (!this.price.contains(lastWord+"000")) this.price.add(lastWord + "000");
                }
            }
            else if (word.toLowerCase().matches("сот.*")){
                if (lastWord.matches("[-+]?\\d+") ) //если предыдущее слов - число
                {
                    if (!this.price.contains(lastWord+"00")) this.price.add(lastWord + "00");
                }
            }
            else if (word.toLowerCase().matches("[рp](уб)?л?([еийя]+)?\\.?")){
                if (lastWord.matches("[-+]?\\d+") ) //если предыдущее слов - число
                {
                    if (!this.price.contains(lastWord)) this.price.add(lastWord);
                }
            }
            else if (!word.equals("")) {
                int i = 0;
                while (!(i + 1 >= word.length()) & (word.charAt(i) == '1' || word.charAt(i) == '2' || word.charAt(i) == '3' || word.charAt(i) == '4' || word.charAt(i) == '5'
                        || word.charAt(i) == '6' || word.charAt(i) == '7' || word.charAt(i) == '8' || word.charAt(i) == '9'
                        || word.charAt(i) == '0')) i++;
                if (word.charAt(i) == '1' || word.charAt(i) == '2' || word.charAt(i) == '3' || word.charAt(i) == '4' || word.charAt(i) == '5'
                        || word.charAt(i) == '6' || word.charAt(i) == '7' || word.charAt(i) == '8' || word.charAt(i) == '9'
                        || word.charAt(i) == '0') i++;
                if (i >= word.length() & word.length() > 2) { //если i вышло за пределы слова
                    if (lastWord.matches("[-+]?\\d+") ) {
                        if (!this.price.contains(lastWord+word)) this.price.add(lastWord+word); //если предыдущее слов - из цифр
                    }
                    else {
                        if (!this.price.contains(word)) this.price.add(word);
                    }
                } else if (i < word.length() & i > 0)
                    if (word.substring(i).equalsIgnoreCase("руб") || word.substring(i).equalsIgnoreCase("р") || word.substring(i).equalsIgnoreCase("p")) {
                        //this.price = word.substring(0, i);
                        if (lastWord.matches("[-+]?\\d+") ) {
                            if (!this.price.contains(lastWord+word.substring(0, i))) this.price.add(lastWord+word.substring(0, i)); //если предыдущее слов - из цифр
                        }
                        else {
                            if (!this.price.contains(word.substring(0, i))) this.price.add(word.substring(0, i));
                        }
                    }
                    else if (word.substring(i).equalsIgnoreCase("тыс") || word.substring(i).equalsIgnoreCase("т")) {
                        if (!this.price.contains(word.substring(0, i) + "000")) this.price.add(word.substring(0, i) + "000");
                    }
                    else if (word.substring(i).toLowerCase().matches("сот.{0,3}")) {
                        if (!this.price.contains(word.substring(0, i) + "00")) this.price.add(word.substring(0, i) + "00");
                    }
            }
            //теперь обрабатываем название
            String a = findRoot(word, lastName, flag);
            if (!a.equals("") & !flag.get() & !this.name.contains(a)) this.name.add(a);

            //запомнили последнее слово
            lastWord = word;
        }
        if (this.price.size() > 1)this.price.add("см описание");
        else if (this.price.size() <= 0)this.price.add("нету");
    }

    private String findRoot(String word, StringBuffer lastName, AtomicBoolean flag ) {
        String s = word.toLowerCase();
        if (!flag.get())
            switch (alb_id) {
                case "222401877": //necrons
                    if (s.matches("([wv]ar+i[oa]r|воин|в[ао]р[р]*и[оа]р).*")) word = "Necron Warriors";
                    else if (s.contains("immortal") || s.contains("imortal") || s.contains("имморт") || s.contains("иморт")
                            || s.contains("бесмерт") || s.contains("бессмерт")) word = "Necron Immortals";
                    else if (s.contains("deathmar") || s.contains("dethmar") || s.contains("десмар") || s.contains("дезмар") ||
                            s.contains("дэсмар") || s.contains("дэзмар")) word = "Necron Dethmarks";
                    else if (s.contains("lord") || s.contains("лорд")) word = "Necron Overlord";
                    else if (s.contains("cryptek") ||s.contains("криптек")) word = "Necron Cryptek";
                    else if (s.contains("wraiths") || s.matches("вра[йи]т.*")) word = "Necron Canoptek Wraiths";
                    else if (s.contains("scarab") || s.contains("скараб")) word = "Necron Canoptek Scarabs";
                    else if (s.contains("pretorian") || s.contains("praetorian") || s.contains("преториан"))
                        word = "Necron Triarch Praetorians";
                    else if (s.contains("blade") || s.matches("бл[эе][йи]д.*") || s.matches("ба[йи]к.*")) word = "Necrons Tomb Blades";
                    else if (s.contains("heav") || s.contains("тяжел") || s.contains("тяжёл")) {
                        word = ""; lastName.replace(0, lastName.length(), "heavy");
                    }
                    else if (s.matches("(destro[yi]?er|д[еэ]стро[ий][еэ]р).*")){
                        if (lastName.toString().equals("heavy")) word = "Heavy Destroyers";
                        else {
                            word = "";
                            lastName.replace(0, lastName.length(), "destroyer");
                            flag.set(true);
                        }
                    } else if (s.contains("anrak") || s.contains("анрак")) word = "Anrakyr the Traveller";
                    else if (s.contains("catacomb") || s.contains("катакомб")) word = "Catacomb Command Barge";
                    else if (s.contains("luminor") || s.contains("люминор") || s.contains("луминор")) word = "Illuminor Szeras";
                    else if (s.contains("imote") || s.contains("имоте")) word = "Imotekh the Stormlord";
                    else if (s.contains("nemesor") || s.contains("немезор")) word = "Nemesor Zahndrekh";
                    else if (s.contains("orikan") || s.contains("орикан")) word = "Orikan the Diviner";
                    else if (s.contains("traz") || s.contains("тразин")) word = "Trazyn the Infinite";
                    else if (s.contains("obyron") || s.contains("obiron") || s.contains("обирон")) word = "Vargard Obyron";
                    else if (s.contains("kutla") || s.contains("кутал") || s.contains("kutal") || s.contains("кутла")) word = "Kutlakh the World Killer";
                    else if (s.contains("toho") || s.contains("тохо")) word = "Toholk the Blinded";
                    else if (s.contains("deceiver") || s.contains("десивер")) word = "Ctan Shard of the Deceiver";
                    else if (s.contains("nightbringer") || s.contains("nigtbringer") || s.matches("на[йи]тбринг.*")) word = "Ctan Shard of the Nightbringer";
                    else if (s.contains("flayed") || s.matches("флэ[йи]д.*")) word = "Flayed Ones";
                    else if (s.matches("l[yu][cs]hguard.*") ||  s.contains("личстра") || s.contains("личгард") || s.contains("личгвард"))
                        word = "Lychguard";
                    else if (s.contains("tomb") || s.contains("томб")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "tomb");
                    } else if (s.contains("stalk") || s.contains("сталк")) {
                        if (lastName.toString().equals("tomb")) word = "Canoptek Tomb Stalker";
                        else word = "Triarch Stalker";
                    } else if (s.contains("acan") || s.contains("акан")) word = "Canoptek Acanthrites";
                    else if (s.contains("sentinel") || s.contains("сентин")) word = "Canoptek Tomb Sentinel";
                    else if (s.matches("арк.") || s.matches("ark.")) {
                        if (lastName.toString().equals("doom")) word = "Doomsday Ark";
                        else if (lastName.toString().equals("ghost")) word = "Ghost Ark";
                        else word = "Doomsday/Ghost Ark";
                        lastName.replace(0, lastName.length(), "");
                    }
                    else if (s.contains("ghost") || s.contains("gost") || s.contains("гост")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "ghost");}
                    else if (s.contains("doomsd") || s.contains("думсд")){
                        word = "";
                        lastName.replace(0, lastName.length(), "doom");}
                    else if (s.equals("doom") || s.equals("дум") || s.matches("кос.")) word = "Doom Scythe";
                    else if (s.contains("night") || s.contains("nigt") || s.matches("на[йи]т.*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "night");
                        flag.set(true);
                    } else if (s.contains("annihil") || s.contains("anihil") || s.contains("аннигил") || s.contains("анигил"))
                        word = "Annihilation Barge";
                    else if (s.contains("spyder") || s.contains("пау")) word = "Canoptek Spyders";
                    else if (s.contains("monoli") || s.contains("моноли")) word = "Monolith";
                    else if (s.contains("transcendent") || s.contains("трансцен") || s.contains("ctan") || s.contains("c'tan") ||
                            s.contains("ктан")) word = "Transcendent Ctan";
                    else if (s.contains("sentry") || s.contains("сетнри") || s.contains("сэтнри")) word = "Sentry Pylon";
                    else if (s.contains("gauss") || s.contains("гаус")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "gauss");
                    } else if (s.contains("pylon") || s.contains("пилон")) { if (lastName.toString().equals("gauss")) word = "Gauss Pylon";}
                    else if (s.contains("tesseract") || s.contains("тессеракт") || s.contains("тесеракт")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "tesseract");
                        flag.set(true);
                    } else if (s.contains("obelisk") || s.contains("обелиск")) word = "Obelisk";
                    else if (s.contains("construct") || s.contains("констр") || s.contains("титан")) word = "Seraptek Heavy Construct";
                    else if (s.contains("citadel") || s.contains("цитадел"))  word = "Tomb Citadel";
                    else if (s.contains("force") || s.contains("форс")) word = "Battleforce";
                    else if (s.matches("(ford?gebane|форд?жб).*")) word = "Fordgebane";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Necrons Start Collecting";
                    else word = "";
                    break;

                case "222401920": //demons
                    if (s.contains("herald") || s.contains("геральд") || s.contains("герольд") || s.contains("гаральд") ||
                            s.contains("гарольд")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "herald");
                        flag.set(true); }
                    else if (s.contains("be'lakor") || s.contains("belakor") || s.contains("belacor") ||  s.contains("be'lacor") ||
                            s.contains("белакор")) word = "Be'lakor";
                    else if (s.contains("master") || s.contains("бладмастер") || s.contains("блудмастер")) word = "Bloddmaster";
                    else if (s.contains("enrapture") || s.contains("арф") || s.contains("восхищающая")) word = "Enrapturess";
                    else if (s.contains("spawn") || s.contains("спавн") || s.contains("спаун"))word = "Chaos spawns";
                    else if (s.matches("(raptur.*|р[эе]пч.*|восторг)")) {
                        word = "Wraith and Rapture";
                    }
                    else if (s.contains("thister") || s.matches("бл[уа]дф[ие]ст[ие]р.*") || s.matches("ф[ие]ст[ие]р.*")) word = "Bloodthirster";
                    else if (s.contains("crusher") || s.contains("крашер")) word = "Bloodcrushers";
                    else if (s.contains("letter") || s.contains("леттер") || s.contains("летер") || s.contains("кровопуск")) word = "Bloodletters";
                    else if (s.contains("blood") || s.contains("блад") || s.contains("блуд")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "blood");
                        flag.set(true); }
                    else if (s.contains("changecas") || s.contains("ченджка")) word = "Changecaster";
                    else if (s.contains("epidemi") || s.contains("эпидеми")) word = "Epidemius";
                    else if (s.contains("fateskim") || s.matches("фэ[йи]тским.*")) word = "Fateskimmer";
                    else if (s.contains("fluxm") || s.contains("флюксм") || s.contains("флуксм")) word = "Fluxmaster";
                    else if (s.contains("kairos") || s.matches("ка[йи]рос.*"))  word = "Kairos Fateweaver";
                    else if (s.contains("karanak") || s.contains("каран"))  word = "Karanak";
                    else if (s.contains("poxwalker") || s.contains("поксволк")) word = "Poxwalkers";
                    else if (s.contains("poxbr") || s.contains("поксбринг")) word = "Poxbringer";
                    else if (s.contains("rotig") || s.contains("ротиг")) word = "Rotigus";
                    else if (s.contains("skarbrand") || s.contains("skarband") || s.contains("скарбанд")) word = "Skarbrand";
                    else if (s.contains("skullmast") || s.contains("skulmast") ||  s.contains("скалмаст") || s.contains("скулмаст"))
                        word = "Skullmaster";
                    else if (s.contains("skulltaker") || s.contains("skultak") || s.matches("ск[уа]лт[aэ][йи]?к.*")) word = "Skulltaker";
                    else if (s.contains("самус") || s.contains("samus"))  word = "Samus";
                    else if (s.contains("urak") || s.contains("урак")) word = "Uraka the Warfiend";
                    else if (s.contains("changeling") || s.contains("чэнжелинг") || s.contains("чэнджелинг") || s.contains("перевертыш")
                            || s.contains("перевёртыш") ||  s.contains("ченжелинг") || s.contains("ченджелинг")) word = "The Changeling";
                    else if (s.contains("masq") || s.contains("маск")) word = "The Masque of Slaanesh";
                    else if (s.contains("drone") || s.contains("дрон"))  word = "Plague Drones";
                    else if (s.contains("horror") || s.contains("horor") || s.contains("хорор") || s.contains("ужас") || s.contains("пинк"))
                        word = "Horrors";
                    else if (s.contains("nurglin") || s.contains("нургли")) word = "Nurglings";
                    else if (s.matches("flay?mer.*") || s.matches("фл[еэ][йи]мер.*")) word = "Flamers";
                    else if (s.contains("flayer") || s.matches("флэ[йи]ер.*")) word = "Hellflayer";
                    else if (s.contains("daemonet") || s.contains("demonet") || s.contains("демоне")) word = "Daemonettes";
                    else if (s.contains("prince") || s.contains("принц") || s.contains("дп")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "dp");
                        flag.set(true); }
                    else if (s.contains("daemons") || s.contains("демоны") || s.contains("демоно") || s.contains("demons")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "daemons");
                        flag.set(true); }
                    else if (s.contains("unclean") || s.contains("unclen") || s.contains("нечист") || s.contains("анклин"))
                        word = "Great Unclean One";
                    else if (s.matches("сл[йи]м[уа]кс.*") ||  s.contains("slimux") || s.contains("улитк")) word = "Horticulous Slimux";
                    else if (s.contains("bilepiper") || s.matches("ба[йи]лпа[йи]пер.*")) word = "Sloppity Bilepiper";
                    else if (s.contains("cor’bax") || s.contains("corbax") ||  s.contains("карбакс") || s.contains("корбакс"))
                        word = "Cor’bax Utterblight";
                    else if (s.contains("change") || s.contains("чэндж") || s.contains("чендж") || s.equals("лоч")) word = "Lord of Change";
                    else if (s.contains("secret") || s.contains("секрет") || s.contains("сикрет")) word = "Keeper of Secrets";
                    else if (s.matches("(fi?e?nd|фи?е?нд|изверг).{0,3}")) word = "Fiends";
                    else if (s.contains("scrib") || s.matches("скра[йи]б.*")) word = "The Blue Scribes";
                    else if (s.matches("(b[eai]r+e?r?.{0,2}|б[еи][ао]?р+[еэ]р.{0,2}|плаг[иаов]+)")) word = "Plaguebearers";
                    else if (s.contains("furie") || s.contains("фури")) word = "Furies";
                    else if (s.contains("beast") || s.contains("бист") || s.contains("звер")) {
                        if (lastName.toString().equals("spined")) {
                            word = "Spined Chaos Beast";
                            lastName.replace(0, lastName.length(), "");
                        } else if (lastName.toString().equals("giant")) {
                            word = "Spined Chaos Beast";
                            lastName.replace(0, lastName.length(), "");
                        } else {
                            word = "";
                            lastName.replace(0, lastName.length(), "beast");
                            flag.set(true);
                        } }
                    else if (s.matches("(s[ck]r[eiao]+m[ei]r|скр[иеао]+м[еиэ]р).*")) word = "Screamers";
                    else if (s.contains("seeker") || s.contains("сикер") || s.contains("рыбк") || s.contains("крикун")) word = "Seekers";
                    else if (s.matches("(hou?nd.?|х[ао]?унд.?|свор.?|гончи?.?|пс[ыов]+|собач?е?к.{0,2})")) word = "Flesh Hounds";
                    else if (s.contains("toad") || s.contains("жаб") || s.contains("тоды")) word = "Plague Toads of Nurgle";
                    else if (s.contains("hulk") || s.contains("халк")) word = "Plague Hulk of Nurgle";
                    else if (s.contains("grind") || s.matches("гра[йи]нд.*"))  word = "Soul Grinder";
                    else if (s.contains("rider") || s.matches("(ра[йи]дер|наездник|всадник|мух[аи]).*")) word = "Pox Riders of Nurgle";
                    else if (s.contains("altar") || s.contains("алтар")) word = "Skull Altar";
                    else if (s.contains("cannon") || s.contains("пушк") || s.contains("canon") || s.contains("кэнон")) word = "Skull Cannon";
                    else if (s.contains("Aetaos") || s.contains("аетос") || s.contains("аэтос")) word = "Aetaos’rau’keres";
                    else if (s.contains("unbound") || s.contains("несвязан") || s.contains("анбаунд") || s.matches("an'?g+rath") ||
                            s.contains("ангра")) word = "An’ggrath the Unbound";
                    else if (s.contains("unbound") || s.contains("несвязан") || s.contains("анбаунд") || s.contains("scabeiat") ||
                            s.contains("скабеи")) word = "Scabeiathrax the Bloated";
                    else if (s.contains("zarakynel") || s.contains("zarakinel") || s.contains("заракинел")) word = "Zarakynel the Bringer of Torments";
                    else if (s.contains("feculent") || s.contains("фесулент")) word = "Feculent Gnarlmaw";
                    else if (s.contains("spined") || s.contains("спинд") || s.contains("колюч")) {
                        word = ""; lastName.replace(0, lastName.length(), "spined"); }
                    else if (s.contains("giant") || s.contains("гиган")) {
                        word = ""; lastName.replace(0, lastName.length(), "giant"); }
                    else if (s.contains("burn") || s.contains("горящ") || s.contains("бёрн") || s.contains("берн")) {
                        word = ""; lastName.replace(0, lastName.length(), "burning"); }
                    else if (s.matches("(chariot|колесниц|коляс|ч[аеэ]ри[оа]т).*")) {
                        if (lastName.toString().equals("burning")) {
                            word = "Burning Chariot"; lastName.replace(0, lastName.length(), "");
                        } else {
                            word = "Chariot"; lastName.replace(0, lastName.length(), "");
                        } }
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Chaos Daemons Start Collecting";
                    else word = "";
                    break;

                case "222401895": //tau
                    if (s.matches("aun'?va") || s.matches("аун'?ва") ) word = "Aun Va";
                    else if (s.matches("aun'?shi") || s.matches("аун'?ши")) word = "Aun Shi";
                    else if (s.matches("(aun | аун)")){
                        flag.set(true);
                        lastName.replace(0, lastName.length(), "aun");
                        word = "";
                    }
                    else if (s.contains("fireblad") || s.matches("огнен.*") || s.matches("фа[йи]?[еэ]р?бл(а|[еэ][йи])д.*")) word = "Cadre Fireblade";
                    else if (s.matches("com+ander.*") || s.matches("к[оа]м+анд[еэи]р.*")) {
                        flag.set(true);
                        lastName.replace(0, lastName.length(), "command");
                        word = "";
                    }
                    else if (s.matches("darkst?ri[kc]er") || s.matches("даркст?р(а[йи]|и)к[еэи]р.*")) word = "Darkstriker";
                    else if (s.matches("[ie]th[ei]r[ie]al") || s.matches("з?[еэ]фирн.*")) word = "Ethereal";
                    else if (s.matches("long?strike?.*") || s.matches("лонг?стр(а[йи] | и)к.*")) word = "Longstrike";
                    else if (s.matches("shas’?or'?alai") || s.matches("шасо[еэ]р'?ала.*")) word = "Shas’o R'alai";
                    else if (s.matches("shas’?or'?m[yu]r") || s.matches("шасо[эе]?р'?м[иу]р")) word = "Shas'o R'myr";
                    else if (s.matches("shas’?o.*") || s.matches("шасо")){
                        flag.set(true);
                        lastName.replace(0, lastName.length(), "shaso");
                        word = "";
                    }
                    else if (s.matches("(fire[wv][oa]r+iar|фа[еиэ]рв[ао]р+и[ао]р).*")) word = "Fire Warriors";
                    else if (s.matches("([wv]ar+i[oa]r|воин|в[ао]р[р]*и[оа]р).*")) {
                        flag.set(true);
                        lastName.replace(0, lastName.length(), "fire");
                        word = "";
                    }
                    else if (s.matches("(hazard|хазард|опасн).*")) word = "XV9 Hazard Support Team";
                    else if (s.matches("(pathfind|па[сз]?ф+(а[йи]|и)+нд[еэ]р).*")) word = "Pathfinder Team";
                    else if (s.matches("(tetra|speeder.*)") || s.matches("(т[эе]тр|спидер).*")) word = "Tetra Scout Speeder Team";
                    else if (s.matches("devilfish.*") || s.matches("д[эе]вилфиш.*")) word = "TY7 Devilfish";
                    else if (s.matches("marksman.*") || s.matches("(марксм[эа]н|сна[йи]пер).*")) word = "Firesight Marksman";
                    else if (s.matches("(carnivor|shaper).*") || s.matches("(пл[оа]т[оа]яд|за.*о[йи]щик).*")) word = "Kroot Carnivores|Shaper";
                    else if (s.matches("(кру{1,2}т|kro{1,2}t).*")) {
                        flag.set(true);
                        lastName.replace(0, lastName.length(), "kroot");
                        word = "";
                    }
                    else if (s.matches("(krootox|кр[уо]{1,2}т[уо]кс).*")) word = "Krootox Riders";
                    else if (s.matches("(riptid|рипта[йи]д).*")) word = "XV104 Riptide Battlesuit";
                    else if (s.matches("(stealt?h?|стелс).{0,2}")) word = "XV25 Stealth Battlesuits";
                    else if (s.matches("(cr[iy]sis|кризис).*")) word = "XV8 Crisis";
                    else if (s.matches("(vespid|веспид).*")) word = "Vespid Stingwings";
                    else if (s.matches("(gh?ostk[ei]+l|гоу?стк[еи]+л).*")) word = "XV95 Ghostkeel Battlesuit";
                    else if (s.matches("(dah?y?ak|grekh|даху?а?я?к|грек?х).*")) word = "Dahyak Grekh";
                    else if (s.matches("(piranh?y?a|пиран).*")) word = "TX4 Piranha";
                    else if (s.matches("(tur{1,2}et|тур{1,2}ел|платформ).*")) word = "Drone Sentry Turret";
                    else if (s.matches("(dron|дрон|remor|р[еи]мор).{0,3}")) word = "Drone";
                    else if (s.matches("(sensor|сенсор|башн|tower).*")) word = "Remote sensor tower";
                    else if (s.matches("(great|велик|больш).*")) {
                        lastName.replace(0, lastName.length(), "great");
                        word = "";
                    }
                    else if (s.matches("(k?narlo[ck]|к?н[ао]рл[оа]к).*")) {
                        if (lastName.toString().equals("great")) word = "Great Knarloc";
                        else word = "Knarloc Riders";
                        lastName.replace(0, lastName.length(), "");
                    }
                    else if (s.matches("(razorshark|раз[оа]ршарк).*")) word = "AX3 Razorshark Strike Fighter";
                    else if (s.matches("(bomber|shark|акул|саншарк).*")) word = "AX39 Sun Shark Bomber";
                    else if (s.matches("(bar+acud|бар+акуд).*")) word = "Barracuda AX-5-2";
                    else if (s.matches("(tiger|тигров|та[йи]гершарк).*")) word = "Tiger Shark";
                    else if (s.matches("(ham+erhea?d|skyra|хам+[еэ]рх[еэ]д|ска[йи]р[эе]).*")) word = "Hammerhead|SkyRay Gunship";
                    else if (s.matches("(bro?a?dsid|бро[ау]?дса[йи]д).*")) word = "XV88 Broadside Battlesuits";
                    else if (s.matches("(stormsurg|штормс.рд?|штормов).*")) word = "KV128 Stormsurge";
                    else if (s.matches("(eight|восмо.*)")) word = "The Eight";
                    else if (s.matches("(orca|к[оа]сатк|десант).*")) word = "Orca Dropship";
                    else if (s.matches("(mant|мант).*")) word = "Manta Super-heavy Dropship";
                    else if (s.matches("(droneport|дроу?нпорт|порт).*")) word = "Tidewall Droneport";
                    else if (s.matches("(gungrig|ганриг|установк).*")) word = "Tidewall Gunrig";
                    else if (s.matches("(shie?ldlin|шилдла[йи]н|[шщ]ит).*")) word = "Tidewall Shieldline";
                    else if (s.matches("(y'?vahra|[йия]а?вахр).*")) word = "XV109 Y’vahra Battlesuit";
                    else if (s.matches("(r'?varna|[еэ]?рварн).*")) word = "XV107 R’varna Battlesuit";
                    else if (s.matches("(ta'?unar|таунар).*")) word = "KX139 Ta’unar Supremacy Armour";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Tau Start Collecting";
                    else word = "";
                    break;

                case "222401931": //гвардия
                    if (s.matches("(straken|стра[йи]?ке).*")) word = "Colonel Iron Hand Straken";
                    else if (s.matches("(yar+ick|яр+ик).*")) word = "Commissar Yarrick";
                    else if (s.matches("(cre+d|кри+д).*")) word = "Lord Castellan Creed";
                    else if (s.matches("(lord|лорд).*")) {
                        lastName.replace(0, lastName.length(), "lord");
                        word = "";
                    }
                    else if (s.matches("(com+is+ar|к[оа]м+ис+ар).*")) {
                        if (lastName.toString().equals("lord")) word = "Lord Commissar";
                        else word = "Commissar";
                        lastName.replace(0, lastName.length(), "");
                    }
                    else if (s.matches("(severin|северин).*")) word = "Severina Raine";
                    else if (s.matches("(primaris|примарис).*")) {
                        lastName.replace(0, lastName.length(), "primaris");
                        word = "";
                    }
                    else if (s.matches("(psyker|пса[йи]кер).*")) {
                        if (lastName.toString().equals("primaris")) word = "Primaris Psyker";
                        else word = "Wyrdvane Psykers";
                        lastName.replace(0, lastName.length(), "");
                    }
                    else if (s.matches("(company|к[ао]мпани).*")) word = "Company Commander";
                    else if (s.matches("(pask|паск).*")) word = "Knight Commander Pask";
                    else if (s.matches("(tank|танк).*")) word = "Tank Commander";
                    else if (s.matches("(plato+n|платун|взводн).*")) word = "Platoon Commander";
                    else if (s.matches("(command|к[оа]м+андн.*)")) word = "Command Squad";
                    else if (s.matches("(janus|янус).*")) word = "Janus Draik";
                    else if (s.matches("(marbo|марбо).*")) word = "Sly Marbo";
                    else if (s.matches("(tad+eus|тад+еус).*")) word = "Taddeus the Purifier";
                    else if (s.matches("(tempestor|т[еэ]мп[еэ]стор).*")) word = "Tempestor Prime";
                    else if (s.matches("(conscript|новобранц).*")) word = "Conscripts";
                    else if (s.matches("(infantry|пехот|взвод).*")) word = "Infantry Squad";
                    else if (s.matches("(tempestus|т[эе]мпестус|scion|с[цк]ион|отпрыск).*")) word = "Militarum Tempestus Scions";
                    else if (s.matches("(chimer|химер).*")) word = "Chimera";
                    else if (s.matches("(taurox|таурокс).*")) word = "Taurox";
                    else if (s.matches("(tauros|таурос).*")) word = "Tauros";
                    else if (s.matches("(centaur|кентавр).*")) word = "Centaur Light Carrier";
                    else if (s.matches("(gryphon|грифон).*")) word = "Gryphonne Pattern Chimera";
                    else if (s.matches("(trojan|троян).*")) word = "Trojan Support Vehicle";
                    else if (s.matches("(astropath|астропа[тф]).*")) word = "Astropath";
                    else if (s.matches("(bul+gr[iy]n|бул+грин).*")) word = "Bullgryns";
                    else if (s.matches("(ogr[iy]n|огрин).*")) word = "Ogryns";
                    else if (s.matches("(kel+|кел+).*")) word = "Colour Sergeant Kell";
                    else if (s.matches("(crusader|крусад[еэ]р|крестоносе?ц).*")) word = "Crusaders";
                    else if (s.matches("(priest|прист|священ+ик|министорум).*")) word = "Ministorum Priest";
                    else if (s.matches("(tech|техно(жрец|прист)?).*")) word = "Tech-Priest Enginseer";
                    else if (s.matches("(nork|норк).*")) word = "Nork Deddog";
                    else if (s.matches("(fleet|флот).*")) word = "Officer of the Fleet";
                    else if (s.matches("(ordnanc|артил+ер).*")) word = "Master of Ordnance";
                    else if (s.matches("(ratling|ратлинг).*")) word = "Ratlings";
                    else if (s.matches("(harker|харкер).*")) word = "Sergeant Harker";
                    else if (s.matches("(servitor|сервитор).*")) word = "Servitors";
                    else if (s.matches("(veteran|ветер[ае]н).*")) word = "Veteran";
                    else if (s.matches("(espern|[еэ]сперн).*")) word = "Espern Locarno";
                    else if (s.matches("(pio?u?s|пио?у?с).*")) word = "Pious Vorne";
                    else if (s.matches("(rein|raus|р[эе][йи]н|раус).*")) word = "Rein and Raus";
                    else if (s.matches("(atlas|атлас).*")) word = "Atlas Recovery Tank";
                    else if (s.matches("(dril|дрел).*")) word = "Hades Breaching Drill Squadron";
                    else if (s.matches("(sal+amand|сал+аманд[еэ]?р).*")) word = "Salamander Vehicle";
                    else if (s.matches("(sentinel|с[еэ]нтин[эеа]л).*")) word = "Sentinels";
                    else if (s.matches("(hel+ho?und|х[эе]лх[оа]?у?нд).*")) word = "Hellhounds";
                    else if (s.matches("(r(ou|a)gh|криговс).*")) {
                        lastName.replace(0, lastName.length(), "krigrider");
                        word = "";
                    }
                    else if (s.matches("(mika+l|мука+л).*")) {
                        lastName.replace(0, lastName.length(), "mukrider");
                        word = "";
                    }
                    else if (s.matches("(rider|всадник|кавалери|всадник).*")) {
                        if (lastName.toString().equals("krigrider")) word = "Rough Riders"; //криговские всадники
                        else if (lastName.toString().equals("mukrider")) word = "Mukaali Riders";
                        else word = "Riders";
                        lastName.replace(0, lastName.length(), "");
                    }
                    else if (s.matches("(valk[yi]ri|валькири).*")) word = "Valkyries";
                    else if (s.matches("(aqv?u?il+a|аквил).*")) word = "Aquila Lander";
                    else if (s.matches("(arvus|арвус).*")) word = "Arvus Lighter";
                    else if (s.matches("(avenger|[эа]в[еэ]нд?ж[эе]р).*")) word = "Avenger Strike Fighter";
                    else if (s.matches("(lig?h?tning|ла[йи]г?х?тнин).*")) word = "Lightning Strike Fighter";
                    else if (s.matches("(th?underbolt|[фт]андерболт).*")) word = "Thunderbolt Heavy Fighter";
                    else if (s.matches("(vendet+a|венд+[эе]т|возмезд).*")) word = "Vendetta Gunship";
                    else if (s.matches("(vulture|в[уа]лт[уа]р|хищни).*")) word = "Vulture Gunship";
                    else if (s.matches("(lem+an|лем+ан|panish|demolish|паниш[еэ]р|демолиш[еэ]р).*") || s.matches("бт|bt"))
                        word = "Leman Russ";
                    else if (s.matches("(basilisk|василиск).*")) word = "Basilisks";
                    else if (s.matches("(dea?(th|f)strik|д[эе][фсз]тра[йи]к).*")) word = "Deathstrike";
                    else if (s.matches("(hydr|гидр).*")) word = "Hydras";
                    else if (s.matches("(manticor|мантикор).*")) word = "Manticore";
                    else if (s.matches("(w[yi]vern|виверн).*")) word = "Wyverns";
                    else if (s.matches("(colos+[ua]s|колосальн).*")) word = "Colossus Bombard";
                    else if (s.matches("(dominus|доминус).*")) word = "Dominus Armoured Siege Bombard";
                    else if (s.matches("(cyclop|циклоп).*")) word = "Cyclops Demolition Vehicle";
                    else if (s.matches("(earf?t?h?shaker|[её]рфш[эе][йи]кер).*")) word = "Earthshaker Battery";
                    else if (s.matches("(grif+on|гриф+он).*")) word = "Griffon Mortar Carrier";
                    else if (s.matches("(special|специальног).*")) word = "Special Weapons Squad";
                    else if (s.matches("(heavy|х[эе]ви|тяжел).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "heavy");
                        flag.set(true);
                    }
                    else if (s.matches("(хвт|расч[её]т)")) word = "Heavy Weapons Squad";
                    else if (s.matches("(mortar|морт[иа]р).*")) word = "Heavy Mortar Battery";
                    else if (s.matches("(quad|ку?в?ад|четверн).*")) word = "Heavy Quad Launcher Battery";
                    else if (s.matches("(gorgon|горгон).*")) word = "Gorgon Heavy Transporter";
                    else if (s.matches("(mac?harius|махар).*")) word = "Macharius Heavy Tank";
                    else if (s.matches("(malcador|маль?кадор).*")) word = "Malcador";
                    else if (s.matches("(medusa|медуза).*")) word = "Medusa Carriage Battery";
                    else if (s.matches("(rapie?r|рапир).*")) word = "Rapier Laser Destroyer";
                    else if (s.matches("(sabre|сабе?л).*")) word = "Sabre Weapons Battery";
                    else if (s.matches("(stygies|стиги[йи][сц]).*")) word = "Stygies Destroyer Tank Hunter";
                    else if (s.matches("(tarantul|тарантул).*")) word = "Tarantula Battery";
                    else if (s.matches("(baneblade|б[эе][йи]нбл[йи][эе]д).*")) word = "Baneblade";
                    else if (s.matches("(baneham+er|б[эе][йи]нхам+[эе]р).*")) word = "Banehammer";
                    else if (s.matches("(banesword|б[эе][йи]су?в?орд).*")) word = "Banesword";
                    else if (s.matches("(do+mham+er|дум+хам+[эе]р).*")) word = "Doomhammer";
                    else if (s.matches("(hel+ham+er|х[эе]л+хам+[эе]р).*")) word = "Hellhammer";
                    else if (s.matches("(shadosword|ш[эе]доу?су?в?орд).*")) word = "Shadowsword";
                    else if (s.matches("(storm+lord|[шс]торм+лорд).*")) word = "Stormlord";
                    else if (s.matches("(storm+sword|[шс]торм+су?в?орд).*")) word = "Stormsword";
                    else if (s.matches("(storm+blade|[шс]торм+бл[йи][эе]д).*")) word = "Arkurian Pattern Stormblade";
                    else if (s.matches("(storm+ham+er|[шс]торм+хам+[эе]р).*"))  word = "Arkurian Pattern Stormhammer";
                    else if (s.matches("(cras+us|крас+ус).*")) word = "Crassus Armoured Assault Vehicle";
                    else if (s.matches("(marau?der|марад[её]р).*")) word = "Marauder Bomber";
                    else if (s.matches("(minota[uv]r|минота[ву]р).*")) word = "Minotaur Artillery Tank";
                    else if (s.matches("(pra?etor|претор).*")) word = "Praetor Armoured Assault Launcher";
                    else if (s.matches("(valdor|валь?дор).*")) word = "Valdor Tank Hunter";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Imperial Guard Start Collecting";
                    else word = "";
                    break;

                case "222401908": //рыцари
                    if (s.matches("(armiger|арми(дж|г)ер).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "armig");
                        flag.set(true);
                    }
                    else if (s.matches("(helv[ei]rin|х[эе]ль?в[еэ]рин).*")) word = "Armiger Helverin";
                    else if (s.matches("(warglai?v|в[ао]ргл[эе][йи]в).*")) word = "Armiger Warglaive";
                    else if (s.matches("(canis|re(x|k[sc])|канис|р[еэ]кс).*")) word = "Canis Rex"; //
                    else if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "knight");
                        flag.set(true);
                    }
                    else if (s.matches("(castel+an|каст[еэ]л+ь?[яа]н).*")) word = "Knight Castellan";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Knight Crusader";
                    else if (s.matches("(er+ant|[эе]р+ант|блуждающ).*")) word = "Knight Errant";
                    else if (s.matches("(gal+ant|г[ао]л+ант).*")) word = "Knight Gallant";
                    else if (s.matches("(pal+adin|пал+адин).*")) word = "Knight Paladin";
                    else if (s.matches("(preceptor|пр[еэ]с+[еэ]пт[оа]р|наставник).*")) word = "Knight Preceptor";
                    else if (s.matches("(val+iant|вал+иант|доблест?н).*")) word = "Knight Valiant";
                    else if (s.matches("(warden|[ву][ао]рд[еэ]н|смотрител).*")) word = "Knight Warden";
                    else if (s.matches("(a[ck]astus|акаст[уоа]с|por&ph[yi]ri?on|п[оа]рфири?он).*")) word = "Acastus Knight Porphyrion";
                    else if (s.matches("(cerastus|ц[еэ]раст[уоа]с).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "cerastus");
                        flag.set(true);
                    }
                    else if (s.matches(".*-?(acheron|ах[еэ]рон|[эа][йи][чх][еэ]рон).*")) word = "Cerastus Knight-Acheron";
                    else if (s.matches(".*-?(atropos|атроп[оа]с).*")) word = "Cerastus Knight-Atropos";
                    else if (s.matches(".*-?(castigator|к[аэ]стиг(а|[еэ][йи])тор).*")) word = "Cerastus Knight-Castigator";
                    else if (s.matches(".*-?(lancer|л[эе]нс[еэ]р).*")) word = "Cerastus Knight-Lancer";
                    else if (s.matches("(questoris|к[ву]есторис).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "qvestor");
                        flag.set(true);
                    }
                    else if (s.matches("(magaer|м[аеэ]га?[еэ]р).*")) word = "Questoris Knight Magaera";
                    else if (s.matches("(st[yi]rix|ст[иу]рикс).*")) word = "Questoris Knight Styrix";
                    else if (s.matches("(sacristan|forgeshrin|сакристи?ан|святили[щш]).*")) word = "Sacristan Forgeshrine";
                    else if (s.matches("(rea?ver|риа?в[еэ]р|грабит).*")) word = "Reaver Battle Titan";
                    else if (s.matches("(warho?und|варха?унд|гончи).*")) word = "Warhound Scout Titan";
                    else if (s.matches("(warlord|варлорд).*")) word = "Warlord Battle Titan";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Knights Start Collecting";
                    else word = "";
                    break;

                case "240229103": //волки
                    if (s.matches("(iron|а[йи]р[оа]н|железн).*")) word = "Iron Priest";
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*") && lastName.toString().equals("raider"))
                        word = "Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*") && lastName.toString().equals("raider"))
                        word = "Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*") && lastName.toString().equals("raider"))
                        word = "Redeemer";
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Rhino";
                    else if (s.matches("(rune|рун+).*")) word = "Rune Priest";
                    else if (s.matches("(leader|лидер).*")) word = "Battle Leader";
                    else if (s.matches("(wolf|вуль?[фв]|волчи).*") && !lastName.toString().equals("lone")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "wolf");
                        flag.set(true);
                    }
                    else if (s.matches("(haldo?r|х[ао]ль?д[оа]р).{0,2}")) word = "Haldor Icepelt";
                    else if (s.matches("(scout|скаут).*")) word = "Wolf Scouts";
                    else if (s.matches("(wolf-?lord|вуль?[фв]-?лорд).*")) word = "Wolf Lord";
                    else if (s.matches("(wolf-?priest|вуль?[фв]-?прист|священ|жрец).*")) word = "Wolf Priest";
                    else if (s.matches("(wolf-?guard|вуль?[фв]-?г(у|ь)?[ая]рд|стражник).*")) word = "Wolf Guard";
                    else if (s.matches("(swiftcl[ao]w|свифткл).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "swift");
                        flag.set(true);
                    }
                    else if (s.matches("(bike|ба[йи]к).*")) word = "Swiftclaw Attack Bikes";
                    else if (s.matches("(lone|лоу?н|одинок).*")) {
                        word = "Lone Wolf";
                        lastName.replace(0, lastName.length(), "lone");
                    }
                    else if (s.matches("(skycl[ao]w|ска[йи]кл|небесн).*")) word = "Skyclaws";
                    else if (s.matches("(fenrisian|фенри[йи]с).*")) word = "Fenrisian Wolves";
                    else if (s.matches("(thunderwol|тандервуль?в|наездник|кавалери).*")) word = "Thunderwolf Cavalry";
                    else if (s.matches("(arjac|rockfist|рокфист|ар(ь|ж)?[ая]к).*")) word = "Arjac Rockfist";
                    else if (s.matches("(bjorn|б[ьъ][оеё]рн).*")) word = "Bjorn the Fell-handed";
                    else if (s.matches("(canis|канис|волчерожд[её]н).*")) word = "Canis Wolfborn";
                    else if (s.matches("(harald|[хг]ар[ао]ль?д).*")) word = "Harald Deathwolf";
                    else if (s.matches("(krom|кром).*")) word = "Krom Dragongaze";
                    else if (s.matches("(logan|логан).*")) word = "Logan Grimnar";
                    else if (s.matches("(stormc[oa]l+er|[шс]тормкол+ер|призывател).*") || s.matches("(n[jy]al|н(д?ж|ь)[ая]л).{0,2}"))
                        word = "Njal Stormcaller";
                    else if (s.matches("(ragnar|рагнар).*")) word = "Ragnar Blackmane";
                    else if (s.matches("(ulrik|уль?рик).*")) word = "Ulrik the Slayer";
                    else if (s.matches("(li?e[ui]?t[ei]nant|ле[ий]т[еи]нант).*")) word = "Lieutenant";
                    else if (s.matches("(blo+d|бл[аоу]+д).*")) word = "Blood Claws";
                    else if (s.matches("((gre[yi])?-?hunter|(гр[еэий]+)?-?хант[еэ]р|охотник).*")) word = "Hunters";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Infiltrator Squad";
                    else if (s.matches("(inter[cs]+es+or|интер[сц]+[еэ]с+ор).*")) {
                        word = "Intercessor Squad";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(stormh[ao]wk|[шс]тормх[оа][увф]к).*")) {
                        word = "Stormhawk Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(xiphon|(кс|х)ифон).*") && !lastName.toString().equals("ne_int")) {
                        word = "Xiphon Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(drop|дроп).*")) word = "Drop Pod";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(repulsor|репуль?сор).*")) word = "Repulsor";
                    else if (s.matches("(r[ae]zorbac?|р[аеэ][йи]?[зс]орб[эе]к|секач).*")) word = "Razorback";
                    else if (s.matches("(marin.*|м[аэ]ри[нк].*|sm|см|мар.{0,2}|десантник.*)")) word = "Imperial Space Marine";
                    else if (s.matches("(murderfang|м[еёуо]р?д[эе]рф[аэ]нг).*")) word = "Murderfang";
                    else if (s.matches("(lukas|лукас).*")) word = "Lukas the Trickster";
                    else if (s.matches("(servitor|сервитор).*")) word = "Servitors";
                    else if (s.matches("(w[uo]lfen|вуль?фен).*"))
                        word = "Wulfen";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут).*")) word = "Dreadnought";
                    else if (s.matches("(serge?a?nt|сержант).*")) word = "Sergeant";
                    else if (s.matches("(примарис|primaris).*")) word = "Primaris";
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminator Squad";
                    else if (s.matches("(inceptor|инцептор).*")) word = "Inceptor Squad";
                    else if (s.matches("(re?iver|ривер).*")) word = "Reiver Squad";
                    else if (s.matches("(ag+res+or|агрес+ор).*")) word = "Aggressor Squad";
                    else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Ancient";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Land Speeder";
                    else if (s.matches("(supres+or|с[ау]прес+ор).*")) word = "Suppressor Squad";
                    else if (s.matches("(stormfang|[шс]тормф[эе]нг).*")) word = "Stormfang Gunship";
                    else if (s.matches("(stormwol|[шс]тормвуль?[фв]).*")) word = "Stormwolf";
                    else if (s.matches("(hel+blaster|х[еэ]л+бласт[еэ]р).*")) word = "Hellblasters";
                    else if (s.matches("(long|лонг|длин+ы).*")) word = "Long Fangs";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Predator";
                    else if (s.matches("(stalker|сталкер).*")) word = "Stalker";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Vindicator";
                    else if (s.matches("(cyberwol|киберв[уо]ль?).*")) word = "Cyberwolves";
                    else if (s.matches("(wh?irlwind|вирлвинд|вихр).*")) word = "Whirlwind";
                    else if (s.matches("(eliminator|[еэ]лиминат[оа]р).*")) word = "Eliminator Squad";
                    else if (s.matches("(tactic|тактич|парн(и|ей|я)).*")) word = "Tactical Squad";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Space Wolves Start Collecting";
                    else word = "";
                    break;

                case "222402011": //спэйс марины
                    if (s.matches("(chaplai?n|[кч]апе?л+ан).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "chaplan");
                        flag.set(true);
                    }
                    else if (s.matches("(titus|титу?с?).?")) word = "Chaplain Dreadnought Titus";
                    else if (s.matches("(cas+ius|кас+и).*")) word = "Chaplain Cassius";
                    else if (s.matches("(grima?la?dus|грима?ла?д).*")) word = "Chaplain Grimaldus";
                    else if (s.matches("(ivanus|иван).*")) word = "Chaplain Ivanus Enkomi";
                    else if (s.matches("(thulsa|[тфв]улс).{0,2}")) word = "High Chaplain Thulsa Kane";
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else if (s.matches("(librari|либр|библи|пса[ий]кер).{0,8}")){
                        word = "";
                        lastName.replace(0, lastName.length(), "libra");
                        flag.set(true);
                    }
                    else if (s.matches("(tiguri|тигури).*")) word = "Chief Librarian Tigurius";
                    else if (s.matches("(captai?n|к[аэ]пи?т[аэ]н|к[еэ]п).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "cap");
                        flag.set(true);
                    }
                    else if (s.matches("(sicari?us|сикари).*")) word = "Captain Sicarius";
                    else if (s.matches("(l[yi]sander|ли[сз]андер).*")) word = "Captain Lysander";
                    else if (s.matches("(z?h?g?rukh?al|androcle|д?ж?рукх?ал|андрокл?ес).*")) word = "Captain Zhrukhal Androcles";
                    else if (s.matches("(sumatris|с[уиа]матрис).*")) word = "Captain Corien Sumatris";
                    else if (s.matches("(mordaci|морда[кс]).*")) word = "Captain Mordaci Blaylock";
                    else if (s.matches("(pel+as|пел+ас).*")) word = "Captain Pellas Mir’san";
                    else if (s.matches("(sil+as|сил+ас).*")) word = "Captain Silas Alberec";
                    else if (s.matches("(tarnus|тарн|vale|в[эае][ий]л).{0,4}")) word = "Captain Tarnus Vale";
                    else if (s.matches("(elam|[еэ]лам).*")) word = "Knight-Captain Elam Courbray";
                    else if (s.matches("(calgar|калгар).*")) word = "Marneus Calgar";
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Rhino";
                    else if (s.matches("(techmarin|т[эе][кх]марин).*")) word = "Techmarine";
                    else if (s.matches("(helbrecht|х[еэ]лбр[еэ]хт).*")) word = "High Marshal Helbrecht";
                    else if (s.matches("([ck]antor|кантор).*")) word = "Pedro Kantor";
                    else if (s.matches("(khan|кх?ан).{0,2}")) word = "Kor’sarro Khan";
                    else if (s.matches("(shrike|шра[ий]к).*")) word = "Kayvaan Shrike";
                    else if (s.matches("(li?e[ui]?t[ei]nant|ле[ий]т[еи]нант).*")) word = "Lieutenant";
                    else if (s.matches("(anton|антон).*")) word = "Commander Anton Narvaez";
                    else if (s.matches("(примарис|primaris).*")) word = "Primaris";
                    else if (s.matches("(serge?a?nt|сержант).*")) word = "Sergeant";
                    else if (s.matches("(chronus|[хк]ронус).*")) word = "Sergeant Chronus";
                    else if (s.matches("(tel+ion|т[эе]л+ион).*")) word = "Sergeant Telion";
                    else if (s.matches("(vulkan|вулкан).*")) word = "Vulkan He’stan";
                    else if (s.matches("(ahazra|ах?а?зр).{0,4}")) word = "Ahazra Redth";
                    else if (s.matches("(com+odus|ком+од).*")) word = "Arch-Centurion Carnac Commodus";
                    else if (s.matches("(valthex|валь[фвз][еэ]кс).*")) word = "Armenneus Valthex";
                    else if (s.matches("(ashmantl|[аэ]шм[еэ]нтл).*")) word = "Bray’arth Ashmantle";
                    else if (s.matches("(angelos|ангелос).*")) word = "Gabriel Angelos";
                    else if (s.matches("(shen|ш[еэ]н).{0,3}")) word = "Harath Shen";
                    else if (s.matches("(hecaton|х[еэ]кат[оа]н).*")) word = "Hecaton Aiakos";
                    else if (s.matches("(lias|лиас).*")) word = "Lias Issodon";
                    else if (s.matches("(moloc|молок).*")) word = "Lord Asterion Moloc";
                    else if (s.matches("(carab|караб).*")) word = "Lord High Commander Carab Culln";
                    else if (s.matches("(huron|[гх]урон).*")) word = "Lugft Huron";
                    else if (s.matches("(seve?rin|севе?рин).*")) word = "Magister Sevrin Loth";
                    else if (s.matches("(t[yi]beros|тибер).*")) word = "Tyberos the Red Wake";
                    else if (s.matches("(malakim|малаким).*")) word = "Malakim Phoros";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Crusader Squad";
                    else if (s.matches("(tactic|тактич|парн(и|ей|я)).*")) word = "Tactical Squad";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Infiltrator Squad";
                    else if (s.matches("(inter[cs]+es+or|интер[сц]+[еэ]с+ор).*")) {
                        word = "Intercessor Squad";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(stormh[ao]wk|[шс]тормх[оа][увф]к).*")) {
                        word = "Stormhawk Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(xiphon|(кс|х)ифон).*") && !lastName.toString().equals("ne_int")) {
                        word = "Xiphon Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(drop|дроп).*")) word = "Drop Pod";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Land Speeder";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(repulsor|репуль?сор).*")) word = "Repulsor";
                    else if (s.matches("(r[ae]zorbac?|р[аеэ][йи]?[зс]орб[эе]к|секач).*")) word = "Razorback";
                    else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Apothecary";
                    else if (s.matches("(veteran|ветер[ае]н).*") && !lastName.toString().equals("comp")) word = "Veteran";
                    else if (s.matches("(v[ae]ngu?[vw]?ard|в[аеэ]нгв?у?а).*")) {
                        word = "Vanguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(st[ae]ng[vw]?u?ard|[сш]т[аеэ]нгв?у?а).*")) {
                        word = "Sternguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(scout|скаут).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "scout");
                        flag.set(true);
                    }
                    else if (s.matches("(ба[йи]к.*|biker.*|мотоцикл.*)")) word = "Bike Squad";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")){
                        if (!lastName.toString().equals("ne_dred")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "dred");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(contem?ptor|контем?птор).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "cont_dred");
                        flag.set(true);
                    }
                    else if (s.matches("(mortis|мортис).*")) {
                        word = "Mortis Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) {
                        word = "Ironclad Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) {
                        word = "Venerable Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(levia(th|f)an|левиафан).*")) {
                        word = "Leviathan Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(sieg|сидж|осадн).{0,4}")) {
                        word = "Siege Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Ancient";
                    else if (s.matches("(damned|проклят).*")) word = "Damned Legionnaires";
                    else if (s.matches("(marin.*|м[аэ]рин.*|sm|см|мар.{0,2}|десантник.*)")) word = "Imperial Space Marine";
                    else if (s.matches("(ag+res+or|агрес+ор).*")) word = "Aggressor Squad";
                    else if (s.matches("(servitor|сервитор).*")) word = "Servitors";
                    else if (s.matches("(as+au?lt|штурмов|ас+[ао]лт).*")) {
                        if (!lastName.toString().equals("ne_ass")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "asalt");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(centurion|центури|колоб).*")) {
                        word = "Centurion Assault Squad";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(ca?estus|ца?[еэ]стус|ram|р[эе]м|баран).*")) {
                        word = "Caestus Assault Ram";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(raptor|рапт[ао]р).*")) {
                        word = "Fire Raptor Assault Gunship";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(eagl|.{0,5}[ие]а?г[еэ]?л|ор[её]?л).{0,2}")) {
                        word = "Storm Eagle Assault Gunship";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(honou?r|х?онор|гь?[ая]рд).*")) word = "Honour Guard";
                    else if (s.matches("(re?iver|ривер).*")) word = "Reiver Squad";
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminator Squad";
                    else if (s.matches("(inceptor|инцептор).*")) word = "Inceptor Squad";
                    else if (s.matches("(sicaran|сикари?а?н).*")) word = "Relic Sicaran Tank";
                    else if (s.matches("(wh?irlwind|вирлвинд|вихр).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "wirl");
                        flag.set(true);
                    }
                    else if (s.matches("(h[yi]periou?s|гипери).*")) word = "Whirlwind Hyperios";
                    else if (s.matches("(scorpius|скорпи).*")) word = "Relic Whirlwind Scorpius";
                    else if (s.matches("(supres+or|с[ау]прес+ор).*")) word = "Suppressor Squad";
                    else if (s.matches("(bat+er|б[аэ]т[аэ]р+[еи]).*")) word = "Tarantula Air Defence Battery";
                    else if (s.matches("(sentry|с[еэ]нтр|турел).{0,4}")) word = "Tarantula Sentry Gun";
                    else if (s.matches("(stormtalon|[шс]тормт[аэе]л[оа]н).*")) word = "Stormtalon Gunship";
                    else if (s.matches("(stromraven|[шс]тормр[эе][ий]в[эе]?н).*")) word = "Stormraven Gunship";
                    else if (s.matches("(devastator|девастатор|д[еэ]выч).*")) word = "Devastator Squad";
                    else if (s.matches("(hel+blaster|х[еэ]л+бласт[еэ]р).*")) word = "Hellblaster Squad";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Predator";
                    else if (s.matches("(stalker|сталкер).*")) word = "Stalker";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Vindicator";
                    else if (s.matches("(thunderfir.*|[тф]анд[эе]р.*|тфк)")) word = "Thunderfire Cannon";
                    else if (s.matches("(eliminator|[еэ]лиминат[оа]р).*")) word = "Eliminator Squad";
                    else if (s.matches("(rapie?r|рапир).*")) word = "Rapier Carrier";
                    else if (s.matches("(gil+iman|[гж]ил+иман).*")) word = "Roboute Guilliman";
                    else if (s.matches("(terminus|термин).{0,3}")) word = "Terminus Ultra";
                    else if (s.matches("(astraeus|астрае?ус).*")) word = "Astraeus Super-heavy Tank";
                    else if (s.matches("(cerberus|ц[еэ]рб[еэ]р).*")) word = "Cerberus Heavy Tank Destroyer";
                    else if (s.matches("(falchion|фаль?чион).*")) word = "Falchion Super-heavy Tank Destroyer";
                    else if (s.matches("(fel+blade|ф[еэ]л+бл[эе][ий]д).*")) word = "Fellblade Super-heavy Tank";
                    else if (s.matches("(mastodon|мастодон).*")) word = "Mastodon Super-heavy Siege Transport";
                    else if (s.matches("(spartan|спартан).*")) word = "Spartan Assault Tank";
                    else if (s.matches("(typhon|тифон).*")) word = "Typhon Heavy Siege Tank";
                    else if (s.matches("(thunder(h[ao]wk)?|танд[еэ]р(хоук)?|громов).*")) word = "Thunderhawk";
                    else if (s.matches("(stormbird|штормб[ёео]рд).*")) word = "Sokar Pattern Stormbird";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Space Marines Start Collecting";
                    else if (s.matches("(shadowspear|ш[эе]доу?сп[еи]а)")) word = "Shadowspear";
                    else if (s.matches("(dark|дарк|т[её]мн...?)")) word = "Dark Imperium";
                    else word = "";
                break;

                case "222401962": //хаос марины
                    if (s.matches("(lord|лорд).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "lord");
                        flag.set(true);
                    }
                    else if (s.matches("(arkos|аркос).*")) word = "Lord Arkos";
                    else if (s.matches("(skul|ск[уа]лтр|трактор).*")) word = "Khorne Lord of Skulls";
                    else if (s.matches("(plagu?e?-?lord.{0,3}|плаг[уие]?-?лорд.{0,3}|contagion.*|разложе.*)")) word = "Lord of Contagion";
                    else if (s.matches("(blig?h?tlord|блайг?х?[тд]лорд).*")) word = "Blight Lord";
                    else if (s.matches("((sor[cs]er|сорс[еэ]р[еэ]|колдун|сорк|чернокниж).*).*")) word = "Sorcerer";
                    else if (s.matches("(ab+ad+on|аб+ад+он).*")) word = "Abaddon the Despoiler";
                    else if (s.matches("(cypher|(са|ш)[ий]фе?р).*")) word = "Cypher";
                    else if (s.contains("prince") || s.contains("принц") || s.contains("дп")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "dp");
                        flag.set(true);
                    }
                    else if (s.matches("([ao]post[ao]?l|апост[ао]?л).*")) word = "Dark Apostle";
                    else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Exalted Champion";
                    else if (s.matches("(fabius|фаби([йи]|ус)).*")) word = "Fabius Bile";
                    else if (s.matches("(ha+rken|ха+рк[еи]н).*")) word = "Haarken Worldclaimer";
                    else if (s.matches("(huron|[гх]урон).*")) word = "Huron Blackheart";
                    else if (s.matches("(kh?arn|кх?арн).{0,3}")) word = "Kharn the Betrayer";
                    else if (s.matches("(licius|люци([йи]|ус)).*")) word = "Lucius the Eternal";
                    else if (s.matches("(execution|[эе]к[зс][еэ]кь?[юу][шт]ио?н|казн).{0,4}")) word = "Master of Executions";
                    else if (s.matches("(pos+es+io?n|по[сз][эе]ши?о?н|одержимост).{0,4}")) word = "Master of Possession";
                    else if (s.matches("([wv]arpsmit|варпсми).*")) word = "Warpsmith";
                    else if (s.matches("(mal+e(x|k[sc])|мал+[еи]кс).*")) word = "Obsidius Mallex";
                    else if (s.matches("(hel+[wv]rig?h?t|х[эе]л+в?ра[ий]т).*")) word = "Chaos Hellwright";
                    else if (s.matches("((zh|g)ufor|жуфор).*")) word = "Zhufor the Impaler";
                    else if (s.matches("([ck]ul+tist.*|куль?тист.*|культ.?)")) word = "Chaos Cultists";
                    else if (s.matches("(legion+[ae]r|легионер).*")) word = "Black Legionnaires";
                    else if (s.matches("(bea?st|б[ие]а?стм[еэа]н|укротител|зверовод).*")) word = "Chaos Beastman";
                    else if (s.matches("(guardsm|гварде|гь?[ая]рдсм|ренегат).*")) word = "Traitor Guardsman";
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Chaos Rhino";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(chosen|чоу?[зс][эе]н|избран).*")) word = "Chosen";
                    else if (s.matches("(dea?(th|[fv])shr[oa]u?d|д[еэ][фв]+[сш]р[оа]у?д|тел[оа]хран).*")) word = "Deathshroud Bodyguard";
                    else if (s.matches("(discipli?e|дисципл|ученик).*")) word = "Dark Disciples";
                    else if (s.matches("(fal+en|падши|фоу?л+[еэ]н).*")) word = "Fallen";
                    else if (s.matches("(pos+es|по[зс]+[еэ][сз]).*")) word = "Possessed";
                    else if (s.matches("(hel+brut|х[еэ]л+брут).*")) word = "Helbrute";
                    else if (s.matches("(berserker|берс[еа]р?к).*")) word = "Khorne Berzerkers";
                    else if (s.matches("(mutilator|мутил[яа]тор).*")) word = "Mutilators";
                    else if (s.matches("(noi[sc]e|но[ий][сз]|какфон).*")) word = "Noise Marines";
                    else if (s.matches("(marin.*|м[аэ]рин.*|sm|см|мар.{0,3}|tactic.*|такти[чк].*|парн(и|ей|я).*)"))
                        word = "Chaos Space Marines";
                    else if (s.matches("(venom([ck]r[ao][wu]ler)?|в[ие]н[оа]м(кр[ао]у?л+ер)?|пау[кч]).{0,3}")) {
                        word = "Venomcrawler";
                        lastName.replace(0, lastName.length(), "ne_crawl");
                    }
                    else if (s.matches("(plagu?e?[ck]r[ao][wu]ler|плагкр[ао]у?л+ер).*")) {
                        word = "Plague Crawler";
                        lastName.replace(0, lastName.length(), "ne_crawl");
                    }
                    else if (s.matches("(plagu?e?b[ue]r?st-?([ck]r[ao][wu]ler)?|плагб[еёуо]р?ст-?(кр[ао]у?л+ер)?).{0,4}")) {
                        word = "Plagueburst Crawler";
                        lastName.replace(0, lastName.length(), "ne_crawl");
                    }
                    else if (s.matches("([kc]r[ao][wu]ler|кр[ао]у?л+ер).*")) {
                        if (!lastName.toString().equals("ne_crawl")) {
                            word = "Crawler";
                        }
                    }
                    else if (s.matches("(plagu?e?marin.*|плагм[аэ]рин.*|plagu?e?-?sm|плаг.{0,2}|плагмар.{0,3}|плаг-?парн(и|ей|я).*)"))
                        word = "Plague Marines";
                    else if (s.matches("(plagu?e?caster|плагкаст[еэ]р).*")) word = "Malignant Plaguecaster";
                    else if (s.matches("((plagu?e?)-?drone|(плаг)?-?дрон).{0,3}")) word = "Plague Drone";
                    else if (s.matches("(surgeon|сург[еи]он|хирург).*")) word = "Plague Surgeon";
                    else if (s.matches("(plague??|плаг)")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "plag");
                        flag.set(true);
                    }
                    else if (s.matches("(rubri|рубри).*")) word = "Rubric Marines";
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminators";
                    else if (s.matches("(decimat|д[еэ]цимат).*")) word = "Chaos Decimator";
                    else if (s.matches("(psyker|пса[йи]кер).*"))  word = "Rogue Psyker";
                    else if (s.matches("(sicaran|сикари?а?н).*")) word = "Hellforged Sicaran";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Hellforged Predator";
                    else if (s.matches("(ба[йи]к.*|biker.*|мотоцикл.*)")) word = "Biker";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")){
                        if (!lastName.toString().equals("ne_dred")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "dred");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(contem?ptor|контем?птор).*")){
                        word = "Hellforged Contemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(deredeo|д[еэ]р[еэ]д[еэ]о).*")) {
                        word = "Hellforged Deredeo Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(infernus|инферн).*")) {
                        word = "Ferrum Infernus Chaos Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(levia(th|f)an|левиафан).*")) {
                        word = "Hellforged Leviathan Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(spa[wv]n|спау?в?н).*")) word = "Chaos Spawn";
                    else if (s.matches("(raptor|раптор).*")) word = "Raptors";
                    else if (s.matches("(talon|талон|когт).*")) word = "Warp Talons";
                    else if (s.matches("(hel+|х[эе]л+).?.?")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "hel");
                    }
                    else if (s.matches("(sl[oa]u?g?h?ter|слоу?тер).*")) word = "Blood Slaughterer of Khorne";
                    else if (s.matches("(drea?dcl[ao]|др[эе]дкл).*")) word = "Hellforged Dreadclaw Drop Pod";
                    else if (s.matches("(as+au?lt|штурмов|ас+[ао]лт).*")) {
                        if (!lastName.toString().equals("ne_ass")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "asalt");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(raptor|рапт[ао]р).*")) {
                        word = "Chaos Fire Raptor Assault Gunship";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(eagl|.{0,5}[ие]а?г[еэ]?л|ор[её]?л).{0,2}")) {
                        word = "Chaos Storm Eagle Assault Gunship";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(hel+dra[kc]|х[еэ]л+др[эе][йи]к).*")) word = "Heldrake";
                    else if (s.matches("(xiphon|(кс|х)ифон).*")) word = "Chaos Xiphon Interceptor";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Chaos Vindicator";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Chaos Predator";
                    else if (s.matches("(defiler|дефа[йи]л[еэ]р).*")) word = "Defiler";
                    else if (s.matches("(forgefie?n|форд?жфие?нд).*")) word = "Forgefiend";
                    else if (s.matches("(havoc|хавок).*")) word = "Havocs";
                    else if (s.matches("(maulerfie?n|м[ао]у?лерфие?нд).*")) word = "Maulerfiend";
                    else if (s.matches("(obliter.*|облитер.*|обл[ияа?].{0,2})")) word = "Obliterators";
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Hellforged Land Raider Achilles";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Hellforged Land Raider Proteus";
                    else if (s.matches("(rapie?r|рапир).*")) word = "Hellforged Rapier Battery";
                    else if (s.matches("(scorpion|скорпион).*")) word = "Greater Brass Scorpion of Khorne";
                    else if (s.matches("(k[yi]tan|к[иу]тан).*")) word = "Kytan Ravager";
                    else if (s.matches("(nocti?li(th|[fv])|нокт?ли[фв]).*")) word = "Noctilith Crown";
                    else if (s.matches("(scorpius|скорпи).*")) word = "Hellforged Scorpius";
                    else if (s.matches("(stormbird|штормб[ёео]рд).*")) word = "Chaos Stormbird Gunship";
                    else if (s.matches("(thunder(h[ao]wk)?|танд[еэ]р(хоук)?|громов).*")) word = "Chaos Thunderhawk Assault Gunship";
                    else if (s.matches("(cerberus|ц[еэ]рб[еэ]р).*")) word = "Hellforged Cerberus Heavy Destroyer";
                    else if (s.matches("(falchion|фаль?чион).*")) word = "Hellforged Falchion";
                    else if (s.matches("(fel+blade|ф[еэ]л+бл[эе][ий]д).*")) word = "Hellforged Fellblade";
                    else if (s.matches("(mastodon|мастодон).*")) word = "Hellforged Mastodon";
                    else if (s.matches("(spartan|спартан).*")) word = "Hellforged Spartan Assault Tank";
                    else if (s.matches("(typhon|тифон).*")) word = "Hellforged Typhon Heavy Siege Tank";
                    else if (s.matches("(м[оа]рт[ао]рион|m[oa]rt[ao]rion).*")) word = "Mortarion";
                    else if (s.matches("(магнус|magnus).*")) word = "Magnus the Red";
                    else if (s.matches("(tza+ng|тза+нг).*")) word = "Tzaangor";
                    else if (s.matches("(ah?riman|ариман).*")) word = "Ahriman";
                    else if (s.matches("(vorte|вортекс).*")) word = "Mutalith Vortex Beast";
                    else if (s.matches("(hauler|х[ао]у?лер).*")) word = "Myphitic Blight-Haulers";
                    else if (s.matches("(тал+иман|tal+yman).*")) word = "Tallyman";
                    else if (s.matches("(noxiou?s|носки([йи]|[уо]с)|колокол).*")) word = "Noxious Blightbringer";
                    else if (s.matches("(foul|фол|blig?h?tsp[ao]w|бла[ий]г?х?т?спаун|бла[ий]т|blig?h?t).{0,3}")) word = "Foul Blightspawn";
                    else if (s.matches("(biologus|биолог([йи]|[уо]с)|putrif[ai]er|путрифа[еэ]р).*")) word = "Biologus Putrifier";
                    else if (s.matches("(mamon|мамон).*")) word = "Mamon Transfigured";
                    else if (s.matches("(necros|некро[зс]([йи]|ус)).*")) word = "Necrosius the Undying";
                    else if (s.matches("(typhus|тифус).*")) word = "Typhus";
                    else if (s.matches("(hor+or|хор+ор|ужас|пинк).{0,3}")) word = "Horrors";
                    else if (s.contains("nurglin") || s.contains("нургли")) word = "Nurglings";
                    else if (s.matches("(poxwalker|поксволк|покс).{0,5}")) word = "Poxwalkers";
                    else if (s.contains("poxbr") || s.contains("поксбринг")) word = "Poxbringer";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Chaos Space Marines Start Collecting";
                    else if (s.matches("(shadowspear|ш[эеа]доу?сп[еи]а?р?).{0,2}")) word = "Shadowspear";
                    else if (s.matches("(vengea?n[cs]|мест|отомщен).*")) word = "Dark Vengeance";
                    else if (s.matches("(imperi[uo]m|импер[иеуао]+м).{0,2}")) word = "Dark Imperium";
                    else word = "";
                    break;

                case "222401939": //блады
                    if (s.matches("(captai?n|к[аэ]пи?т[аэ]н|к[еэ]п).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "cap");
                        flag.set(true);
                    }
                    else if (s.matches("(t[yi]c?h|ти[хш]).{0,4}")) word = "Tycho the Lost";
                    else if (s.matches("(sangu?v?inor|сангвинор).*")) word = "The Sanguinor";
                    else if (s.matches("(sangu?v?inar|с[ао]нгу?в?ин[ао]рн).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "sang");
                        flag.set(true);
                    }
                    else if (s.matches("(astora(t|[fv])|аст[оа]рат).*")) word = "Astorath";
                    else if (s.matches("(corbul|к[оа]рбул).*")) word = "Brother Corbulo";
                    else if (s.matches("(dant|дант).{0,3}")) word = "Commander Dante";
                    else if (s.matches("(gabrie?l|га[бв]ри[еэ]л).*")) word = "Gabriel Seth";
                    else if (s.matches("(lemart|л[еэ]март[еэ]с).*")) word = "Lemartes";
                    else if (s.matches("(chaplai?n|[кч]апе?л+ан).*")) word = "Chaplain";
                    else if (s.matches("(librari|либр|библи|пса[ий]кер).{0,8}")){
                        word = "";
                        lastName.replace(0, lastName.length(), "libra");
                        flag.set(true);
                    }
                    else if (s.matches("(librari|либр|библиар(иан)?|пса[ий]к).*(drea?d|др[еэ]д).*")) word = "Librarian Dreadnought";
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Rhino";
                    else if (s.matches("(techmarin|т[эе][кх]марин).*")) word = "Techmarine";
                    else if (s.matches("(примарис|primaris).*")) word = "Primaris";
                    else if (s.matches("(scout|скаут).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "scout");
                        flag.set(true);
                    }
                    else if (s.matches("(ба[йи]к.*|biker.*|мотоцикл.*)")) word = "Bike Squad";
                    else if (s.matches("(marin.*|м[аэ]рин.*|sm|см|мар.{0,2}|десантник.*)")) word = "Imperial Space Marine";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")){
                        if (!lastName.toString().equals("ne_dred")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "dred");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(contem?ptor|контем?птор).*")){
                        word = "Contemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(furiou?[sc]|фь?[ую]рио[сз]|яростн|бешен).{0,5}")) {
                        word = "Furioso Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) {
                        word = "Ironclad Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) {
                        word = "Venerable Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(levia(th|f)an|левиафан).*")) {
                        word = "Leviathan Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(sieg|сидж|осадн).{0,4}")) {
                        word = "Siege Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else if (s.matches("(compan|к[оа]мп[оа]н).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "company");
                        flag.set(true);
                    }
                    else if (s.matches("(champion|ч[еэа]мпион).*") && !lastName.toString().equals("comp")) word = "Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*") && !lastName.toString().equals("comp")) word = "Ancient";
                    else if (s.matches("(veteran|ветер[ае]н).*") && !lastName.toString().equals("comp")) word = "Veteran";
                    else if (s.matches("(v[ae]ngu?[vw]?ard|в[аеэ]нгв?у?а).*")) {
                        word = "Vanguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(st[ae]ng[vw]?u?ard|[сш]т[аеэ]нгв?у?а).*")) {
                        word = "Sternguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(ag+res+or|агрес+ор).*")) word = "Aggressor Squad";
                    else if (s.matches("(servitor|сервитор).*")) word = "Servitors";
                    else if (s.matches("(tactic|тактич|парн(и|ей|я)).*")) word = "Tactical Squad";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Infiltrator Squad";
                    else if (s.matches("(inter[cs]+es+or|интер[сц]+[еэ]с+ор).*")) {
                        word = "Intercessor Squad";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(stormh[ao]wk|[шс]тормх[оа][увф]к).*")) {
                        word = "Stormhawk Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(xiphon|(кс|х)ифон).*") && !lastName.toString().equals("ne_int")) {
                        word = "Xiphon Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(drop|дроп).*")) word = "Drop Pod";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Land Speeder";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(repulsor|репуль?сор).*")) word = "Repulsor";
                    else if (s.matches("(r[ae]zorbac?|р[аеэ][йи]?[зс]орб[эе]к|секач).*")) word = "Razorback";
                    else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Apothecary";
                    else if (s.matches("(li?e[ui]?t[ei]nant|ле[ий]т[еи]нант).*")) word = "Lieutenant";
                    else if (s.matches("(as+au?lt|ас+[ао]лт).*")) {
                        if (!lastName.toString().equals("ne_ass")) {
                            word = "Assault Squad";
                        }
                    }
                    else if (s.matches("(centurion|центури|колоб).*")) {
                        word = "Centurion Assault Squad";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminator Squad";
                    else if (s.matches("(re?iver|ривер).*")) word = "Reiver Squad";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Land Speeder";
                    else if (s.matches("(supres+or|с[ау]прес+ор).*")) word = "Suppressor Squad";
                    else if (s.matches("(inceptor|инцептор).*") && !lastName.toString().equals("ne_ass")) word = "Inceptor Squad";
                    else if (s.matches("(stormtalon|[шс]тормт[аэе]л[оа]н).*")) word = "Stormtalon Gunship";
                    else if (s.matches("(stromraven|[шс]тормр[эе][ий]в[эе]?н).*")) word = "Stormraven Gunship";
                    else if (s.matches("(devastator|девастатор|д[еэ]выч).*")) word = "Devastator Squad";
                    else if (s.matches("(hel+blaster|х[еэ]л+бласт[еэ]р).*")) word = "Hellblaster Squad";
                    else if (s.matches("(hunter|хант[еэ]р|охотник).*")) word = "Hunters";
                    else if (s.matches("(ba+l|[вб]а+л).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "baal");
                        flag.set(true);
                    }
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Predator";
                    else if (s.matches("(stalker|сталкер).*")) word = "Stalker";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Vindicator";
                    else if (s.matches("(wh?irlwind|вирлвинд|вихр).*")) word = "Whirlwind";
                    else if (s.matches("(eliminator|[еэ]лиминат[оа]р).*")) word = "Eliminator Squad";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Blood Angels Start Collecting";
                    else word = "";
                    break;

                case "222401988": //дарк ангелы
                    if (s.matches("(jetfig?h?t|д?ж[эе]тф[ао][йи]т).*")) word = "Nephilim Jetfighter";
                    else if (s.matches("(sam+ael|с[ао]м+[ао][еэ]л).*")) word = "Sammael";
                    else if (s.matches("(asm+[ao]de|[ао]см+[ао]д[еэ]).*")) word = "Asmodai";
                    else if (s.matches("(a[zsc]rael|[ао][зс]р[ао][еэ]л).*")) word = "Azrael";
                    else if (s.matches("([bv]elial|[бв][еэ]ли[ао]л).*")) word = "Belial";
                    else if (s.matches("(ezekie?l|[еиэ]+[зс][еэ]ки[еэ]?л).*")) word = "Ezekiel";
                    else if (s.matches("(master|маст[еэ]р).*")) word = "Master";
                    else if (s.matches("(крыл).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "wing");
                        flag.set(true);
                    }
                    else if (s.matches("(dea?(th|[fv])[wv]ing|ду?[еэ]у?[фсз]в?ин).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "death");
                        flag.set(true);
                    }
                    else if (s.matches("(rave?n[wv]ing|р[еэ][ий]в[еэ]н?в?ин).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raven");
                        flag.set(true);
                    }
                    else if (s.matches("(command|к[оа]м+андн.*)")) word = "Command Squad";
                    else if (s.matches("(dark|дарк|т[её]мн).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "dark");
                    }
                    else if (s.matches("(talonmast|талонмаст|маст[еэ]р).*")) word = "Ravenwing Talonmaster";
                    else if (s.matches("(d[ao]rkshr[ao]u?d|д[ао]рк[шс]р[ао]у?о?д).*")) word = "Ravenwing Darkshroud";
                    else if (s.matches("(d[ao]rkt[ao]l[oa]n|д[ао]ркт[еэ]л[оа]н).*")) word = "Ravenwing Dark Talon";
                    else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Apothecary";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Ravenwing Land Speeder";
                    else if (s.matches("(chaplai?n|[кч]апе?л+ан).*") && !lastName.toString().equals("ne_chap")) word = "Chaplain";
                    else if (s.matches("(inter+[oa]g[ao]t[ao]r|интер+[оа]г[ао]т[оа]р|допр[ао][шс]).*")) {
                        word = "Interrogator-Chaplain";
                        lastName.replace(0, lastName.length(), "ne_chap");
                    }
                    else if (s.matches("(librari|либр|библи|пса[ий]кер).{0,8}")) word = "Librarian";
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminator Squad";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")){
                        if (!lastName.toString().equals("ne_dred")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "dred");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(contem?ptor|контем?птор).*")){
                        word = "Contemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) {
                        word = "Ironclad Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) {
                        word = "Venerable Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(levia(th|f)an|левиафан).*")) {
                        word = "Leviathan Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(sieg|сидж|осадн).{0,4}")) {
                        word = "Siege Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else if (s.matches("(compan|к[оа]мп[оа]н).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "company");
                        flag.set(true);
                    }
                    else if (s.matches("(champion|ч[еэа]мпион).*") && !lastName.toString().equals("comp")) word = "Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*") && !lastName.toString().equals("comp"))
                        word = "Ancient";
                    else if (s.matches("(veteran|ветер[ае]н).*") && !lastName.toString().equals("comp")) word = "Veteran";
                    else if (s.matches("(v[ae]ngu?[vw]?ard|в[аеэ]нгв?у?а).*")) {
                        word = "Vanguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(st[ae]ng[vw]?u?ard|[сш]т[аеэ]нгв?у?а).*")) {
                        word = "Sternguard Veteran Squad";
                        lastName.replace(0, lastName.length(), "comp");
                    }
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Rhino";
                    else if (s.matches("(techmarin|т[эе][кх]марин).*")) word = "Techmarine";
                    else if (s.matches("(примарис|primaris).*")) word = "Primaris";
                    else if (s.matches("(scout|скаут).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "scout");
                        flag.set(true);
                    }
                    else if (s.matches("(ба[йи]к.*|biker.*|мотоцикл.*)")) word = "Bike Squad";
                    else if (s.matches("(marin.*|м[аэ]рин.*|sm|см|мар.{0,2}|десантник.*)")) word = "Imperial Space Marine";
                    else if (s.matches("(ag+res+or|агрес+ор).*")) word = "Aggressor Squad";
                    else if (s.matches("(servitor|сервитор).*")) word = "Servitors";
                    else if (s.matches("(tactic|тактич|парн(и|ей|я)).*")) word = "Tactical Squad";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Infiltrator Squad";
                    else if (s.matches("(inter[cs]+es+or|интер[сц]+[еэ]с+ор).*")) {
                        word = "Intercessor Squad";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(stormh[ao]wk|[шс]тормх[оа][увф]к).*")) {
                        word = "Stormhawk Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(xiphon|(кс|х)ифон).*") && !lastName.toString().equals("ne_int")) {
                        word = "Xiphon Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(drop|дроп).*")) word = "Drop Pod";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(repulsor|репуль?сор).*")) word = "Repulsor";
                    else if (s.matches("(r[ae]zorbac?|р[аеэ][йи]?[зс]орб[эе]к|секач).*")) word = "Razorback";
                    else if (s.matches("(li?e[ui]?t[ei]nant|ле[ий]т[еи]нант).*")) word = "Lieutenant";
                    else if (s.matches("(as+au?lt|ас+[ао]лт).*")) {
                        if (!lastName.toString().equals("ne_ass")) {
                            word = "Assault Squad";
                        }
                    }
                    else if (s.matches("(centurion|центури|колоб).*")) {
                        word = "Centurion Assault Squad";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(re?iver|ривер).*")) word = "Reiver Squad";
                    else if (s.matches("(supres+or|с[ау]прес+ор).*")) word = "Suppressor Squad";
                    else if (s.matches("(inceptor|инцептор).*") && !lastName.toString().equals("ne_ass")) word = "Inceptor Squad";
                    else if (s.matches("(stormtalon|[шс]тормт[аэе]л[оа]н).*")) word = "Stormtalon Gunship";
                    else if (s.matches("(stromraven|[шс]тормр[эе][ий]в[эе]?н).*")) word = "Stormraven Gunship";
                    else if (s.matches("(devastator|девастатор|д[еэ]выч).*")) word = "Devastator Squad";
                    else if (s.matches("(hel+blaster|х[еэ]л+бласт[еэ]р).*")) word = "Hellblaster Squad";
                    else if (s.matches("(hunter|хант[еэ]р|охотник).*")) word = "Hunters";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Predator";
                    else if (s.matches("(stalker|сталкер).*")) word = "Stalker";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Vindicator";
                    else if (s.matches("(wh?irlwind|вирлвинд|вихр).*")) word = "Whirlwind";
                    else if (s.matches("(eliminator|[еэ]лиминат[оа]р).*")) word = "Eliminator Squad";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Dark Angels Start Collecting";
                    else word = "";
                    break;

                case "222401924"://орки
                    if (s.matches("(bi[gk]|би[гк]).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "big");
                        flag.set(true);
                    }
                    else if (s.matches("(bi[gk]-?tr[ae][kc]|би[гк]-?тр[ауеэ]к).*")) word = "Big Trakk";
                    else if (s.matches("(m[ea][kc]-?a?drea?d|м[еэ]ка?-?др[еэ]д).{0,4}]")) word = "Meka-Dread";
                    else if (s.matches("(m[ea][kc]|м[еэ]к).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "mek");
                        flag.set(true);
                    }
                    else if (s.matches("(d[ea](th|[fv]+)drea?d|(д[еэ][сзфв]|смерт.?.?)-?др[еэ]д).{0,2}")) word = "Deff Dreads";
                    else if (s.matches("(d[ea](th|[fv]+)-?[kc]il+|(д[еэ][сзфв]|смерт.?.?)-?(кил+|уби[ий])|[wv][ao]r-?tr[ia]+ke?|в[аоу]+р-?тра[ий]к|трицикл).{0,4}"))
                        word = "Deffkilla Wartrike";
                    else if (s.matches("(d[ea](th|[fv]+)-?[kc]opt|(д[еэ][сзфв]|смерт.?.?)-?(копт|вертол)).*")) word = "Deffkoptas";
                    else if (s.matches("(d[ea](th|[fv]+)|д[еэ][сзфв]).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "death");
                        flag.set(true);
                    }
                    else if (s.matches("(мастерска|workshop|.*[ву]орк[шс]оп).*")) word = "Mekboy Workshop";
                    else if (s.matches("([wv][ao]rbos|в[ао]рбос).*")) word = "Warboss";
                    else if (s.matches("(sh?ik[ou]t|[шсз]икр[оу]т).*")) word = "Boss Snikrot";
                    else if (s.matches("([zs]agstr[ua][kc]|з[ао]гст?р[ауо]к).*")) word = "Boss Zagstruk";
                    else if (s.matches("(b[ao]dr[uao]k|б[аеэ]др[уао]к).*")) word = "Kaptin Badrukk";
                    else if (s.matches("(rip+er|рип+[еэ]р).*")) word = "Zhadsnark da Ripper";
                    else if (s.matches("(mad|do[ck]|м[эеа]д|док|сумашед).{0,4}")) word = "Mad Dok Grotsnik";
                    else if (s.matches("(we?ird|в[еэ]?ирд|пса[ий]к|к[ао]лдун).*")) word = "Weirdboy";
                    else if (s.matches("(storm|[сш]торм).*")) word = "Stormboyz";
                    else if (s.matches("(pain|п[эе][ий]?н|до[кх]т[аоу]р).*")) word = "Painboy";
                    else if (s.matches("(bo(y|ie?)[sz]?|б[оа][ий][зс]).{0,2}")) word = "Boyz";
                    else if (s.matches("(b[uy]rn[ao]?-?bo(y|ie?)[sz]?|б[оеё]рн[ао]?-?б[оа][ий][зс]).*")) word = "Burna Boyz";
                    else if (s.matches("(b[uy]rn[ao]?-?bom+b?er|б[оеё]рн[ао]?-?б[оа]м+б?[еэ]р).*")) word = "Burna-bommer";
                    else if (s.matches("(b[uy]rn|б[оеё]рн).*"))
                    {
                        word = "";
                        lastName.replace(0, lastName.length(), "burn");
                        flag.set(true);
                    }
                    else if (s.matches("(blit?[zs]блит?[цсз]).*")) word = "Blitza-bommer";
                    else if (s.matches("(bom+b?er|б[оа]м+б?[еэ]р).*")) word = "Bommer";
                    else if (s.matches("(fig?h?t[ao]?-?bom+b?er|фл[аоэе][ий]?т[ао]?-?б[оа]м+б?[еэ]р).*"))
                    {
                        word = "Fighta-Bommer";
                        lastName.replace(0, lastName.length(), "ne_fli");
                    }
                    else if (s.matches("(fig?h?t|фл[аоэе][ий]?т|лета(ю?[шщ]|л)).*")) {
                        if (!lastName.toString().equals("ne_fli")) word = "Attack Fighta";
                        else {
                            word = "";
                            lastName.replace(0, lastName.length(), "flight");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(gr[ae]t?chi?n|гр[еэ]т?чин).*")) word = "Gretchin";
                    else if (s.matches("(tr[ua][kc]+|тр[еэау]к|тачи?(к|л)).*")) word = "Trukk";
                    else if (s.matches("([ck]art.{0,2}|карт)")) word = "Kart";
                    else if (s.matches("([wv][ao]r[ck]opt|[ву][ао]ркопт).*")) word = "‘Chinork’ Warkopta";
                    else if (s.matches("([kc][oa]m+and[oae]s|к[ао]м+анд[аоеэ]?с).*")) word = "Kommandos";
                    else if (s.matches("(meg[ao]-?nob|м[еэ]г[ао]-?но[бп]).*"))
                    {
                        word = "Meganobz";
                        lastName.replace(0, lastName.length(), "ne_nob");
                    }
                    else if (s.matches("(mega-?tr[ae][kc]|м[еэ]г[ао]?-?тр[ауеэ]к|sca?rap-?[jdg]+[ea]t|скр[аэе]-?пд?ж[еэа]т).*")) {
                        word = "Megatrakk Scrapjets";
                        lastName.replace(0, lastName.length(), "ne_nob");
                    }
                    else if (s.matches("(meg[ao]|м[еэ]г[ао]).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "mega");
                        flag.set(true);
                    }
                    else if (s.matches("(nob|но[бп]).{0,3}") && !lastName.toString().equals("ne_nob")) word = "Nobz";
                    else if (s.matches("(ren(th?|[fv])[ea]rd|od+bo[yie]|р[уа]н[тфв][еэ]рд|од+бо).*")) word = "Runtherd";
                    else if (s.matches("(tankb[uy]st[ae]|т[аеэ]нкб[ау]ст).*")) word = "Tankbustas";
                    else if (s.matches("(s[kc]orch|скорч).*")) word = "Skorchas";
                    else if (s.matches("(gh?[aj][zs]g?h?k?ul|г[ао][зс]г[уа]л).*")) word = "Ghazghkull Thraka";
                    else if (s.matches("([wv][ao]r-?b[ua]g+[iy].*|[ву][ао]р-?баг.*|баг.{0,3})")) word = "Warbuggies";
                    else if (s.matches("(([wv][ao]r)?-?bi[kc]e?r|([ву][ао]р)?-?ба[йи]к|мотоцикл).{0,4}")) word = "Warbikers";
                    else if (s.matches("([wv][ao]r-?tr[ae][kc]|[ву][ао]р-?тр[ауеэ]к).*")) word = "Wartrakks";
                    else if (s.matches("([wv][ao]r|бо[еи]во])")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "war");
                        flag.set(true);
                    }
                    else if (s.matches("(fortr[ea]s|фортр[еэ]с|крепост).*")) word = "Battle Fortress";
                    else if (s.matches("([wv][ao][zs]b[ou]+m|в[ао][сз]б[уо]+м|bl[aou][sz]t-?[jdg]+[ea]t|бл[аоу]ст-?пд?ж[еэа]т).*")) word = "Wazbom Blastajet";
                    else if (s.matches("((bo+[sz]t[ao])?-?bl[aou][sz]t|(б[уо]+ст[ао]?)?-?бл[аоу]ст).{0,2}")) word = "Kustom Boosta-blastas";
                    else if (s.matches("(da[kc]+[ao]|дак+[ао]).*")) word = "Dakkajet";
                    else if (s.matches("(ru[ck]+[ao]tr[au][ck]|sq?v?u?i[gzs]-?b[ua]g+[iy]|р[уа]к+атр[ауеэ]к|скв?у?и?[гк]-?баг).*")) word = "Rukkatrukk Squigbuggies";
                    else if (s.matches("(b[ou]+m-?da[kc]+[ao]|б[уо]+м-?дак+[ао]|[sz]n?[ao][zsc][wv][aeo]gon|[шс]н?[ао][зс]в[еэа]г).*")) word = "Boomdakka Snazzwagons";
                    else if (s.matches("([wv][aeo]gon|в[еэа]г).{0,6}")) word = "Wagon";
                    else if (s.matches("(bat+l+e?-?[wv][aeo]gon|б[аэе]т+л+[еэ]?-?в[еэа]г).{0,6}")) word = "Battlewagon";
                    else if (s.matches("(gun-?[wv][aeo]gon|г[ау]н-?в[еэа]г).{0,6}")) word = "Gunwagon";
                    else if (s.matches("(lift|лифт).*")) word = "Lifta Wagon";
                    else if (s.matches("(sho[kc]+d?[jg][uao]mp|[шс]х?ок+д?ж[ауо]мп).{0,4}")) word = "Shokkjump Dragstas";
                    else if (s.matches("(launch|лаунч|метател).*")) word = "Grot Bomm Launcha";
                    else if (s.matches("(gro+t|гр[оу]+т).{0,3}")) lastName.replace(0, lastName.length(), "grot");
                    else if (s.matches("([kc]il+|кил+|убиваю?[шщ]).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "kill");
                    }
                    else if (s.matches("(tank|танк).{0,4}") && lastName.toString().equals("grot")) word = "Grot Tank";
                    else if (s.matches("(tank|танк).{0,4}") && lastName.toString().equals("kill")) word = "Kill Tank";
                    else if (s.matches("(bo[uy]?ne?-?bre?a?k|боу?н-?бр[эе][ий]к|крушител).*")) word = "Bonebreaka";
                    else if (s.matches("(gor[kc]anau?v?t|г[ао]рк[ао]н[ао][вф]т).*")) word = "Gorkanaut";
                    else if (s.matches("(mor[kc]anau?v?t|м[ао]рк[ао]н[ао][вф]т).*")) word = "Morkanaut";
                    else if (s.matches("(([kc]il+)?-?[kc]an|(кил+[ао]?)?-?банк[иеу])")) word = "Killa Kans";
                    else if (s.matches(".*([kc]r[ua]sh|кр[уао][сш]).?")) word = "Kill Krusha";
                    else if (s.matches("(fl[ae]sh|фл[эе][шс]|git[zs]|гит[сц]).{0,2}")) word = "Flash Gitz";
                    else if (s.matches("(l[ou]+t|л[уо]+т).{0,4}")) word = "Lootas";
                    else if (s.matches("(sq?v?u?i[gzs]-?got|скв?у?и?[гк][оа][тфв]).*")) word = "Squiggoth";
                    else if (s.matches("(sq?v?u?i[gzs]|скв?у?и?[гк]).*")) word = "Squig";
                    else if (s.matches("(stomp|стомп).*")) word = "Stompa";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Orks Start Collecting";
                    else word = "";
                    break;

                case "222401887": //мехи
                    if (s.matches("(c[ao][uw]*l|коу?л).{0,3}")) word = "Belisarius Cawl";
                    else if (s.matches("(domin[uo]s|домин[уо]с).*")) word = "Tech-Priest Dominus";
                    else if (s.matches("(eng[ie]nse+r|[иэ]нд?ж[ие]н(ер|с[иеэ]+р)).*")) word = "Tech-Priest Enginseer";
                    else if (s.matches("(manipul[uo]s|манипул[уо]с).*")) word = "Tech-Priest Manipulus";
                    else if (s.matches("(brea?cher|бр[ие]а?ч[еэ]р).*")) word = "Kataphron Breachers";
                    else if (s.matches("(destro[yi]?er|д[еэ]стро[ий][еэ]р).*")) word = "Kataphron Destroyers";
                    else if (s.matches("(rand?g[ea]r|р[еэ][ий]?нд?ж).*"))
                    {
                        word = "Skitarii Rangers";
                        lastName.replace(0, lastName.length(), "ne_skit");
                    }
                    else if (s.matches("(v[ae]ngu?ard|в[аеэ]нгу?ард).*"))
                    {
                        word = "Skitarii Vanguard";
                        lastName.replace(0, lastName.length(), "ne_skit");
                    }
                    else if (s.matches("(s[kc][ie]tar|скитар).*") && !lastName.toString().equals("ne_scit")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "scit");
                        flag.set(true);
                    }
                    else if (s.matches("(hopl?il?t|хопила[ий]).*")) word = "Secutarii Hoplites";
                    else if (s.matches("(pelta+s|п[еэ]лт[оа]с).{0,4}")) word = "Secutarii Peltasts";
                    else if (s.matches("(c[oa]rpus[ck]ar|к[ао]рпуск).*")) {
                        word = "Corpuscarii Electro-Priests";
                        lastName.replace(0, lastName.length(), "ne_prist");
                    }
                    else if (s.matches("(fulgur|фуль?гур).*")) {
                        word = "Fulgurite Electro-Priests";
                        lastName.replace(0, lastName.length(), "ne_prist");
                    }
                    else if (s.matches("((electr[oa])?-?prie?st|([эе]лектр[оа])-?(прие?ст|жрец)).*") && !lastName.toString().equals("ne_prist")) word = "Electro-Priests";
                    else if (s.matches("(c[yi]bern[ea]ti[ck]|киберн[эе]тик).*")) word = "Cybernetica Datasmith";
                    else if (s.matches("(ruststalk|р[ау]стсталк).*")) word = "Sicarian Ruststalkers";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Sicarian Infiltrators";
                    else if (s.matches("(si[ck]ari|сикариа?н).*")){
                        word = "";
                        lastName.replace(0, lastName.length(), "sicar");
                        flag.set(true);
                    }
                    else if (s.matches("(servitor|сервитор|тракт[ао]р).*")) word = "Servitors";
                    else if (s.matches("(ur-?0?2?5?|ур-?0?2?5?|чернокам).{0,3}")) word = "UR-025";
                    else if (s.matches("(ir[oa]ns?t?rid|а[ий]р[ао]нс?т?р[ао][ий]?д).*")) word = "Ironstrider";
                    else if (s.matches("(drago+n|драг[уо]+н).*")) word = "Sydonian Dragoons";
                    else if (s.matches("([kc]ast[ea]l|к[ао]ст[еэ]л).*")) word = "Kastelan Robots";
                    else if (s.matches("(onager|[ао]н[аеэ][ий]?г[еэ]р|d[uy]ne?cr[ao][uw]l|д[ую]нкр[ао]у?л).*")) word = "Onager Dunecrawler";
                    else if (s.matches("(armiger|арми(дж|г)ер).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "armig");
                        flag.set(true);
                    }
                    else if (s.matches("(helv[ei]rin|х[эе]лв[еэ]рин).*")) word = "Armiger Helverin";
                    else if (s.matches("(warglai?v|в[ао]ргл[эе][йи]в).*")) word = "Armiger Warglaive";
                    else if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?)[^-].{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "knight");
                        flag.set(true);
                    }
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Knight Crusader";
                    else if (s.matches("(er+ant|[эе]р+ант|блуждающ).*")) word = "Knight Errant";
                    else if (s.matches("(gal+ant|г[ао]л+ант).*")) word = "Knight Gallant";
                    else if (s.matches("(pal+adin|пал+адин).*")) word = "Knight Paladin";
                    else if (s.matches("(preceptor|пр[еэ]с+[еэ]пт[оа]р|наставник).*")) word = "Knight Preceptor";
                    else if (s.matches("(val+iant|вал+иант|доблест?н).*")) word = "Knight Valiant";
                    else if (s.matches("(warden|[ву][ао]рд[еэ]н|смотрител).*")) word = "Knight Warden";
                    else if (s.matches("(dril|дрел).*")) word = "Terrax-pattern Termite Assault Drill";
                    else if (s.matches("(ford?gebane|форд?жб).*")) word = "Fordgebane";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Adeptus Mechanicus Start Collecting";
                    else word = "";
                    break;

                case "222401873": //иквизиция
                    if (s.matches("([ck]an[oa]nes|[кс][ао]н[оа]н[еэ]с).*")) word = "Canoness";
                    else if (s.matches("(c[ea]le[sc]tine?|[цс][еиэ]л[еиэ]стин[eауы]?)")) word = "Celestine";
                    else if (s.matches("(c[ea]le[sc]tian|[цс][еэ]л[еэ]стин).*")) word = "Celestian Squad";
                    else if (s.matches("(mis+[ie][oa]n|м[ие]с+[ие][оа]н).*")) word = "Missionary";
                    else if (s.matches("(im+[ao]l[ya]+t).*")) word = "Immolator";
                    else if (s.matches("((nul+)?-?mai?d[ea]?n|(н[уо]л+ь?)?-?(м[еэ][ий]д[еэ]?н|сделан)).*")) {
                        word = "Null-Maiden Rhino";
                        lastName.replace(0, lastName.length(), "ne_rino");
                    }
                    else if (s.matches("(rh?ino|ринк?[оаы]).*")){
                        if (!lastName.toString().equals("ne_rino")) {
                            word = "Rhino";
                            lastName.replace(0, lastName.length(), "");
                        }
                    }
                    else if (s.matches("(s[oa]r[oa]r[ie]t|с[оа]р[оа]?р?[иеэ]та).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "sorit");
                        flag.set(true);
                    }
                    else if (s.matches("(sist|с[иеё]ст).*")) word = "Battle Sister Squad";
                    else if (s.matches("(r[ea]p+r+[ea]s+[oa]r|р[еэ]п+р+[еэ]с+[ао]р).*")) word = "Sororitas Repressor";
                    else if (s.matches("((l[ea]nd)?-?raider|(л[эе]нд)?-?р[эе][йи]д[эе]р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raider");
                        flag.set(true);
                    }
                    else if (s.matches("(chimer|химер).*")) word = "Chimera";
                    else if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus"; //"Inquisition Land Raider Prometheus"
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else if (s.matches("(im[ao]d?g[ie][fv]|[ие]м[эеао]д?ж[еиэ][фв]).*")) word = "Imagifier";
                    else if (s.matches("(priest|прист|священ+ик|министорум).*")) word = "Ministorum Priest";
                    else if (s.matches("((ar[ck][aoe]?)?-?[fv]l[aeo]d?g[eao]l+[aoe]n|арм[еэ][ий]?с|маз[ао]хи|([ао]рк[ао]еэ?)?-?[фв]л[ао]д?ж[еэ]л[ао]н).*")) word = "Arco-flagellants";
                    else if (s.matches("(crusader|крусад[еэ]р|крестоносе?ц).*")) word = "Crusaders";
                    else if (s.matches("(d[ie][aoe]l+og|д[ие][ао]л+[оа]г).*")) word = "Dialogus";
                    else if (s.matches("(d?g[ea]min|д?ж[еэ]мин|близн[еяэ]).*")) word = "Geminae Superia";
                    else if (s.matches("(h[ao][scz]p[ie]t[ao]l|[хг][ао]сп[ие]т[ао]л).*")) word = "Hospitaller";
                    else if (s.matches("(mist?r?[eao]|мист?р?[еаэо]|г[оа]сп[оа]ж|раска[ий][ая]|r[ea]p[ea]nt|р[еэа]п[еэ]нт).*")) word = "Mistress of Repentance";
                    else if (s.matches("(pre[ao]?ch|пр[иеэао]+с?х?ч?[еэ]р|пр[оа]п[оа]ведн).*")) word = "Preacher";
                    else if (s.matches("(r[ea]p[ea]nt|р[еэ]п[еэ]нт).*")) word = "Repentia Squad";
                    else if (s.matches("(d[oa]m[ie]n|д[оа]мин).*")) word = "Dominion Squad";
                    else if (s.matches("(s[ei]r[ao]phim|с[еэ]р[ао]фим).*")) word = "Seraphim Squad";
                    else if (s.matches("(exorcis|[еэ]к[сз][ао]рци).*")) word = "Exorcist";
                    else if (s.matches("(penit|п[еэ]н[ие]т|каь?[уяю][шщт]).*")) word = "Penitent Engines";
                    else if (s.matches("([rp]etr[ie]b|[рп][еэ]тр[еи]б|в[оа]зд[ао]).*")) word = "Retributor Squad";
                    else if (s.matches("(in[qck][vu]i[szc]it[ao]r|инкв[иеэ]зит[ао]р).*")) word = "Inquisitor";
                    else if (s.matches("(a[ck][ao]l|ак[ао]л).*")) word = "Acolytes";
                    else if (s.matches("((da?[ei]m[oa]n)?-?host|(д[еиэ]м[оа]н)?-?хост).*")) word = "Daemonhost";
                    else if (s.matches("(as+asin|[оа]с[ао]син).*")) {
                        if (!lastName.toString().equals("ne_asasin")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "asasin");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("((dea?(th|f))?-?[ck]ult|(д[эе][фсз])?-?куль?т).*")) {
                        word = "Death Cult Assassins";
                        lastName.replace(0, lastName.length(), "ne_asasin");
                    }
                    else if (s.matches("(cal+[ie]d|[кс][ао]л+[ие]д).*")) {
                        word = "Callidus Assassin";
                        lastName.replace(0, lastName.length(), "ne_asasin");
                    }
                    else if (s.matches("(c[uy]l+[ie](x|k?c?)|[кс]ул+[ие]к?с?).*")) {
                        word = "Culexus Assassin";
                        lastName.replace(0, lastName.length(), "ne_asasin");
                    }
                    else if (s.matches("(eversor|[еэ]в[еэ]р[сз]ор).*")) {
                        word = "Eversor Assassin";
                        lastName.replace(0, lastName.length(), "ne_asasin");
                    }
                    else if (s.matches("([vw][ie]nd[ie][kc]a|в[ие]нд[ие][кс][ау]).*")) {
                        word = "Vindicare Assassin";
                        lastName.replace(0, lastName.length(), "ne_asasin");
                    }
                    else if (s.matches("(pr[ao][sz]e[cq][uy]t|пр[оа][зс][еэ]к|прокур).*")) word = "Prosecutors";
                    else if (s.matches("([vw][ie]d?g[ie]lat|вид?жил|охран).*")) word = "Vigilators";
                    else if (s.matches("([wv]it?ch|([wv]it?ch)?-?se+[kc]er|[ув]ит?ч|([ув]ит?ч)?-?си?к[еэ]р).{0,4}")) word = "Witchseekers";
                    else if (s.matches("(neme[sz]|н[еэ]м[еэ][зс]).{0,4}") || s.matches("((dred)?-?k?nigh?t|(др[еэ]д)?-?к?на[йи]т|рыцар).{0,3}")) word = "Nemesis Dreadknight";
                    else if (s.matches("(purd?gat|пур(г|дж)[еэ][ий][шс]).*")) word = "Purgation Squad";
                    else if (s.matches("(pur[ie]f|пь?[ую]ри[фв]|очи[шщ]ен).*")) word = "Purifier Squad";
                    else if (s.matches("(paladin|паладин).*")) word = "Paladin";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Ancient";
                    else if (s.matches("(strik|тра[йи]к).{0,3}")) word = "Strike Squad";
                    else if (s.matches("(drai?g|дра[ий]?г).{0,3}")) word = "Kaldor Draigo";
                    else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Brotherhood Champion";
                    else if (s.matches("(captai?n|к[аэ]пи?т[аэ]н|к[еэ]п).{0,3}")) word = "Brother-Captain";
                    else if (s.matches("(stern|[шс]т[еэ]рн).{0,2}")) word = "Stern";
                    else if (s.matches("(master|маст[еэ]р).*")) word = "Grand Master";
                    else if (s.matches("(voldus|волд).{0,4}")) word = "Voldus";
                    else if (s.matches("([ck]astel+y?an|к[ао]ст[еэ]л+ь?[ая]н).*")) word = "Castellan Crowe";
                    else if (s.matches("(librari|либр|библи|пса[ий]к[еэ]р).{0,8}")) word = "Librarian";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")){
                        if (!lastName.toString().equals("ne_dred")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "dred");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("(contem?ptor|контем?птор).*")){
                        word = "Contemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(do+mgl[ae]i?v|д[уо]+мгл[еэ][ий]в).*")) {
                        word = "Doomglaive Pattern Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) {
                        word = "Venerable Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) {
                        word = "Ironclad Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) {
                        word = "Redemptor Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(levia(th|f)an|левиафан).*")) {
                        word = "Leviathan Dreadnought";
                        lastName.replace(0, lastName.length(), "ne_dred");
                    }
                    else if (s.matches("(as+au?lt|ас+[ао]лт).*")) {
                        if (!lastName.toString().equals("ne_ass")) {
                            word = "Assault Squad";
                        }
                    }
                    else if (s.matches("(centurion|центури|колоб).*")) {
                        word = "Centurion Assault Squad";
                        lastName.replace(0, lastName.length(), "ne_ass");
                    }
                    else if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Terminator Squad";
                    else if (s.matches("(re?iver|ривер).*")) word = "Reiver Squad";
                    else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Land Speeder";
                    else if (s.matches("(supres+or|с[ау]прес+ор).*")) word = "Suppressor Squad";
                    else if (s.matches("(inter[cs]+es+or|интер[сц]+[еэ]с+ор).*")){
                        word = "Intercessor Squad";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(stormh[ao]wk|[шс]тормх[оа][увф]к).*")) {
                        word = "Stormhawk Interceptor";
                        lastName.replace(0, lastName.length(), "ne_int");
                    }
                    else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Apothecary";
                    else if (s.matches("(r[ae]zorbac?|р[аеэ][йи]?[зс]орб[эе]к|секач).*")) word = "Razorback";
                    else if (s.matches("(stormtalon|[шс]тормт[аэе]л[оа]н).*")) word = "Stormtalon Gunship";
                    else if (s.matches("(stromraven|[шс]тормр[эе][ий]в[эе]?н).*")) word = "Stormraven Gunship";
                    else if (s.matches("(thunder(h[ao]wk)?|танд[еэ]р(хоук)?|громов).*")) word = "Thunderhawk";
                    else if (s.matches("(devastator|девастатор|д[еэ]выч).*")) word = "Devastator Squad";
                    else if (s.matches("(hel+blaster|х[еэ]л+бласт[еэ]р).*")) word = "Hellblaster Squad";
                    else if (s.matches("(veteran|ветер[ае]н).*")) word = "Veteran";
                    else if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Predator";
                    else if (s.matches("(stalker|сталкер).*")) word = "Stalker";
                    else if (s.matches("(vindicator|виндикатор).*")) word = "Vindicator";
                    else if (s.matches("(wh?irlwind|вирлвинд|вихр).*")) word = "Whirlwind";
                    else if (s.matches("(eliminator|[еэ]лиминат[оа]р).*")) word = "Eliminator Squad";
                    else if (s.matches("(techmarin|т[эе][кх]марин).*")) word = "Techmarine";
                    else if (s.matches("(chaplai?n|[кч]апе?л+ан).*") && !lastName.toString().equals("ne_chap")) word = "Chaplain";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Start Collecting";
                    else if (s.matches("([ck][oa]t[ei][ao]?[zs]|к[оа]т[еэ][ао]?[зс]).*")) word = "Coteaz";
                    else if (s.matches("(gr[ea][yi]?fa|гр[еэ][ий]?фак).*")) word = "Greyfax";
                    else if (s.matches("([kc][ao]r[ao]ma[zs]|к[ао]р[ао]м[ао][зс]).*")) word = "Karamazov";
                    else if (s.matches("([ea]i?[sz][ei]nho|[эе][ий]?[зс][еэ]нхо).*")) word = "Eisenhorn";
                    else if (s.matches("(hect[oa]r|[гх][еэ]кт[ао]р).*")) word = "Hector Rex";
                    else if (s.matches("(s[oa]l[oa]mo|с[оа]л[оа]мо).*")) word = "Solomon Lok";
                    else if (s.matches("(servitor|сервитор|тракт[ао]р).*")) word = "Servitors";
                    else if (s.matches("((j|dg)[oa][kc]a[ei]r|д?ж[ао]к[ао][еэ][ий]?р).*")) word = "Jokaero Weaponsmith";
                    else if (s.matches("([jy]a[ck][ao]b|[ий]?[ая]к[оа][вб]).*")) word = "Uriah Jacobus";
                    else word = "";
                    break;

                case"222401861"://друкхари
                    if (s.matches("(arc?hon|[ао]рк?хон).*")) word = "Archon";
                    else if (s.matches("(drazh?[ao]r|дразх?[оа]р).*")) word = "Drazhar";
                    else if (s.matches("(h[ao][ie]?mun[ck]|[хг][ао]мунку?л).*")) word = "Haemonculus";
                    else if (s.matches("(l[ie]l+i(t|[fv])|лил+ит).*")) word = "Lelith Hesperax";
                    else if (s.matches("(su[ck]+ub|сук+уб).*")) word = "Succubus";
                    else if (s.matches("(uri?en|ури[эе]н).*")) word = "Urien Rakarth";
                    else if (s.matches("([wv]ar+i[oa]r|воин|в[ао]р[р]*и[оа]р).*")) word = "Kabalite Warriors";
                    else if (s.matches("(tru+e?bor|тру+бор|чист[оа]кров).*")) word = "Kabalite Trueborn";
                    else if (s.matches("([wv]r[ae][ck]|вр[еэ]к|развалин).*")) word = "Wracks";
                    else if (s.matches("(w[yi]+t?ch|вит?ч|ведь?м).{0,3}")) word = "Wyches";
                    else if (s.matches("(venom|в[еи]н[оа]м).*")) word = "Venom";
                    else if (s.matches("(h[ea][kc]atr|х[аеэо]к[ао]трик|(bl[oau]+d)?-?brid|(бл[ауо]+д)?-?бра[ий][тд]).{0,4}"))
                        word = "Hekatrix Bloodbrides";
                    else if (s.matches("(bea?st-?mas|б[ие]а?ст-?м[еэа]с|укротител|зверовод).*")) word = "Beastmaster";
                    else if (s.matches("(gr[aou]?tes[qk]|гр[оа]т[еэ]ск).*")) word = "Grotesques";
                    else if (s.matches("(in[ck]ub|инкуб).*")) word = "Incubi";
                    else if (s.matches("(rai?d|р[эе][ий]д).{0,4}")) word = "Raider";
                    else if (s.matches("(mandrai?k|м[аеэ]ндр[эе][ий]к|мандраг).*")) word = "Mandrakes";
                    else if (s.matches("(lh?[ao]m[ao]en|лх?[ао]м[ао][еэ]?н).{0,2}")) word = "Lhamaean";
                    else if (s.matches("(medus|м[еэ]ду[зс]).*")) word = "Medusae";
                    else if (s.matches("(s+[yi]l(t|[fv])|с+л?ил?[фтв]).{0,2}")) word = "Sslyth";
                    else if (s.matches("(([ua]r)?-?gh?ul+|([уа]р)?-?гул+ь?).{0,3}")) word = "Ur-Ghul";
                    else if (s.matches("(fi?e?nd|фи?е?нд|изверг).{0,3}")) word = "Clawed Fiends";
                    else if (s.matches("(flo[ck]+|флок+).{0,3}")) word = "Razorwing Flocks";
                    else if (s.matches("(fig?h?t|фл[аоэе][ий]?т|лета(ю?[шщ]|л)).*")) word = "Raven Strike Fighter";
                    else if (s.matches("(jetfig?h?t|д?ж[эе]тф[ао][йи]т).*")) word = "Razorwing Jetfighter";
                    else if (s.matches("([kc]h?[yi]mer|к?хим[еэ]р).*")) word = "Khymerae";
                    else if (s.matches("(hel+ion|[гх][еэ]л+и[ао]н).*")) word = "Hellions";
                    else if (s.matches("(rea?ver|риа?в[еэ]р|грабит).*")) word = "Reavers";
                    else if (s.matches("(s[ck][oa]u?rd?g|ск?[оа]у?рд?ж|карат).*")) word = "Scourges";
                    else if (s.matches("(bomb|бомб).{0,5}")) word = "Voidraven Bomber";
                    else if (s.matches("([ck]ron|[кх]рон).{0,4}")) word = "Cronos";
                    else if (s.matches("(ravad?g|р[аеэ]в[еаэ]д?[жг]|опуст[ао]ш).*")) word = "Ravager";
                    else if (s.matches("(tal[oa]s|тал[оа]с).*")) word = "Talos";
                    else if (s.matches("(r[eia]+per|р[иеа]+п[еэ]р|жнец).*")) word = "Reaper";
                    else if (s.matches("(t[ao]nt[ao]l|т[ао]нт[ао]л).*")) word = "Tantalus";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Drukhari Start Collecting";
                    else word = "";
                    break;

                case "222401845": //эльдары и арлекины
                    if (s.matches("(a[sz][uy]rm|[ао][зс][уа]рм).*")) word = "Asurmen";
                    else if (s.matches("(autar[ckh]|[ао]у?тар[хк]).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "aut");
                        flag.set(true);
                    }
                    else if (s.matches("(sk(y|ai)r[uy]n|ск[уа][ий]?р[ау]н).*")) word = "Skyrunner";
                    else if (s.matches("(f[ao]rs[ei]+r|ф[ао]рс[ие]+р).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "far");
                        flag.set(true);
                    }
                    else if (s.matches("(kh?[ea]i?n|к?х[еэ][ий]?н|аватар|avatar).{0,3}")) word = "Avatar of Khaine";
                    else if (s.matches("(b[ao]h[ao]r+[oa][thfv]|б[ао]х[ао]ро).*")) word = "Baharroth";
                    else if (s.matches("([uy]lth?t?ran|[уа]ль?тран).*")) word = "Eldrad Ulthran";
                    else if (s.matches("(fu?e?u?g[ao]n|фь?[ую]г[ао]н).*")) word = "Fuegan";
                    else if (s.matches("(н[ао][ий]т|na?ig?h?t|ночно).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "night");
                        flag.set(true);
                    }
                    else if (s.matches("(sp[ie]r[ie]?t?-?s[ei]+a?r|[сз]п[ие]+р[ие]?т?-?с[иеэ]+р).*")) word = "Spiritseer";
                    else if (s.matches("((n[ia]+g?h?t)?-?sp[ei][ao]?r|(н[аиой]+т)?-?сп[ие][ао]?р|к[ао]пь?[её]|il+i[ck]|ил+ик).{0,2}"))
                        word = "Illic Nightspear";
                    else if (s.matches("((na?ig?h?t)?-?spin+[eao]?r|(н[ао][ий]?т)?-?спин+[еэао]?р|в[оа]лчо?к).*")) word = "Night Spinner";
                    else if (s.matches("(na?ig?h?t-?[wv]ing?|н[ао][ий]?-?[ув]инг?|крыль?[ея]?).{0,2}")) word = "Nightwing";
                    else if (s.matches("(k?nigh?t|к?на[йи]т|рыцар).*")) word = "Wraithknight";
                    else if (s.matches("([wv]r[ae]i?([fv]|th)-?gu?a?rd.*|[ву]р[еэ][ий]?[фв]-?гу?[ао]?рд|стражн?и?к?.{0,2})")) word = "Wraithguard";
                    else if (s.matches("([wv]r[ae]i?([fv]|th)-?bl[ae]+i?d|[ву]р[еэ][ий]?[фв]-?бл[еэ]?[ий]?д|клино?к).*")) word = "Wraithblades";
                    else if (s.matches("([wv]r[ae]i?([fv]|th)-?f[ae]?i?g?h?t[ea]?r?|[ву]р[еэ][ий]?[фв]+-?ф[аоеэ]?[ий]?т[аоеэи]?р?|бо[ийе]+ц).{0,3}"))
                        word = "Hemlock Wraithfighter";
                    else if (s.matches("([wv]r[ae]i?([fv]|th)-?lord|[ву]р[еэ][ий]?[фв]+-?лорд|лорд).{0,2}")) word = "Wraithlord";
                    else if (s.matches("([wv]r[ae]i?([fv]|th)-?s[ei]+a?r|[ву]р[еэ][ий]?[фв]+-?с[иеэ]+р).*")) word = "Wraithseer";
                    else if (s.matches("(sh?ad[oa][uw]?-?s[ei]+a?r|[шс]х?[еэао]+д[ао][увф]-?с[иеэ]+р).*")) word = "Shadowseer";
                    else if (s.matches("(s[ei]+a?r|с[иеэ]+р|(про)?вид[ея]?[шщц]).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "seer");
                        flag.set(true);
                    }
                    else if (s.matches("([jy]ain-?(zar)?|д?ж?аин-?([зс]ар)?).{0,2}")) word = "Jain Zar";
                    else if (s.matches("(k[ao]r[ao]nd?r|к[ао]р[ао]нд?р).*")) word = "Karandras";
                    else if (s.matches("(m[ao]u?g[ae]n|м[ао]у?г[ао]н).*")) word = "Maugan Ra";
                    else if (s.matches("(iv?ril+[iy]([fv]|t?h?)|ив?рил+и[тфв]).{0,2}")) word = "Irillyth";
                    else if (s.matches("([wv][ao]rlo[ck]+|[ву][ао]рло?к?).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "warlock");
                        flag.set(true);
                    }
                    else if (s.matches("([ck][oa]n[ck]l[ae]v|к[оа]нклав).*")) word = "Warlock Conclave";
                    else if (s.matches("([iey]r[ie]+l|[иеэ]+рль?).{0,2}")) word = "Prince Yriel";
                    else if (s.matches("([ae]v[ae]nd?g?|[эе]в[еэ]нд?ж?|мститель?).*")) word = "Dire Avengers";
                    else if (s.matches("(d[ei]fend|д[еэ]ф[еэ]нд|за[шщ]итн).*")) word = "Guardian Defenders";
                    else if (s.matches("(gu?a?rdi?a?n|гу?[ао]?рд[ие][ао]н|стражн?и?к?).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "guard");
                        flag.set(true);
                    }
                    else if (s.matches("(rai?nd?g[eia]r|р[эе][ий]нд?ж).*")) word = "Rangers";
                    else if (s.matches("(r[ei]a?v[ei]?r?|р[иеэ][ао]?в[еэ]?р?).{0,2}")) word = "Corsair Reaver Band";
                    else if (s.matches("(f[ao]l[ck][oa]?n|[фв][ао]ль?к[ао]?н).{0,3}")) word = "Corsair Falcon";
                    else if (s.matches("(venom|в[еи]н[оа]м).*")) word = "Corsair Venom";
                    else if (s.matches("(d[ae]n[cs][eao]?r?|д[еэ]нс[аоеэ]?р?|танц.*).{0,2}")) word = "Corsair Cloud Dancer Band";
                    else if (s.matches("(bo[uae]?nsin|бо[уа]н[сз]ин[гдж]+).*")) word = "Bonesinger";
                    else if (s.matches("(sp[ei][ck][ei]?r|[сз]п[еэи]кт[еиа]?р).*")) word = "Shadow Spectres";
                    else if (s.matches("(s[ei]rp[eai]?n?t|[сз][еэ]рп[еэ]н?т?).{0,3}")) word = "Wave Serpent";
                    else if (s.matches("(dr[ae]g[ao]?n|др[аеэ][кг][аеэо]?н).*")) word = "Fire Dragons";
                    else if (s.matches("(bans?h?[ei]?|банш).{0,3}")) word = "Howling Banshees";
                    else if (s.matches("(stri[ck]+in|стр[аеий]+к[ие]н|ударн).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "strike");
                        flag.set(true);
                    }
                    else if (s.matches("(scorpion|скорпион).*")) word = "Scorpion";
                    else if (s.matches("(am+[ao]l+[yiao]+n|[ао]м[аоеэ]+л+ин|sh?[ae]d[oa][uw]+-?g[uayi]+de?|[шсх]+[еэ]д[оауеэ]+-?га[ий]д).{0,3}")) word = "Amallyn Shadowguide";
                    else if (s.matches("(sp[eai]+r|[сз]п[еэиао]+р|копь?).{0,3}")) word = "Shining Spears";
                    else if (s.matches("(h[aouy]+[wv][ck]|х[оау]+в?к|ястреб).{0,3}")) word = "Swooping Hawks";
                    else if (s.matches("(v[yi]p[ei]r|[ву][ие]+п[еи]р|гадюк).{0,3}")) word = "Vypers";
                    else if (s.matches("([wv]ind-?r[iae]+d[ei]?r?|[ву]инд-?р[аеэий]?д[еэ]?р?).*")) word = "Windriders";
                    else if (s.matches("(as+au?lt|ас+[ао]лт|напад).*")) word = "Wasp Assault Walker";
                    else if (s.matches("([wv][ao]r|[ву][ао]р|воен.*)")) word = "War Walkers";
                    else if (s.matches("([hg][oa]rn[ei]t|[хг][оа]рн[еиэ]т).*")) word = "Hornet";
                    else if (s.matches("([ck]r[ie]m[szc][aoe]?n?|[кс]рим[зс][еэао]?н?|кровав.{0,4}).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "crimson");
                        flag.set(true);
                    }
                    else if (s.matches("([eia]([ksc]+|x)[aoe]rc?h?|[еэи]к[зс][аоеэ]р?х?).{0,3}")) word = "Exarch";
                    else if (s.matches("([wv][ae]rp|[ву][аеэо]+рпо?в?).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "warp");
                        flag.set(true);
                    }
                    else if (s.matches("(([wv][ao]rp)?-?sp[aie]+d[ea]?r?|(в[оуа]+рп)?-?сп[иаий]+д[еэ]?р?|(в[оуа]+рп)?-?паук).{0,2}"))
                        word = "Warp Spiders";
                    else if (s.matches("([vw][aeoi]+mp[ie]?[ie]?r|[ву][аоеэ]+мп[иеэ]?р?).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "vamp");
                        flag.set(true);
                    }
                    else if (s.matches("(rai?d|р[эе][ий]д).{0,4}")) word = "Raider";
                    else if (s.matches("(hunter|хант[еэ]р|охотник).*")) word = "Hunter";
                    else if (s.matches("((ph|f)[eiao]+n[ie](x|ks)|(ф|пх)[оеэи]+н[иеэ]к).*")) word = "Phoenix";
                    else if (s.matches("(r[eia]+per|р[иеа]+п[еэ]р|жнец).*"))  word = "Dark Reapers";
                    else if (s.matches("(f[ao]l[ck][ao]n|[фв][ао]ль?к[ао]н|сокол).*")) word = "Falcon";
                    else if (s.matches("(pr[iea]+[sc]m|при[зс]м).{0,4}")) word = "Fire Prism";
                    else if (s.matches("(s[ua]p+ort|с[ауо]+п+орт|под+ерж).*")) word = "Support Weapons";
                    else if (s.matches("(f[aei]+r-?sh?torm|ф[аийеэ]+р[сш]торм).*")) word = "Firestorm";
                    else if (s.matches("(l[iy]n([ksc]+|x)|л[иуе]нкс|ърысь?).{0,2}")) word = "Lynx";
                    else if (s.matches("([ck]obr|[кс]обр).{0,2}")) word = "Cobra";
                    else if (s.matches("(t[ie]t[ae]n|т[ие]т[аеэ]н).*")) {
                        if (!lastName.toString().equals("ne_tit")) {
                            word = "";
                            lastName.replace(0, lastName.length(), "titan");
                            flag.set(true);
                        }
                    }
                    else if (s.matches("((f|ph)[aeo]+ntom|(ф|пх?)[аеоэ]+нтом).*")) {
                        word = "Phantom Titan";
                        lastName.replace(0, lastName.length(), "ne_tit");
                    }
                    else if (s.matches("(r[eia]+v[eaoi]+n[ei]nt?|р[еэий]+в[аоиеэ]+н[иеэ]нт?н?).{0,3}")) {
                        word = "Revenant Titan";
                        lastName.replace(0, lastName.length(), "ne_tit");
                    }
                    else if (s.matches("(tr[ouy]+p+|тр[уоа]+п+).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "trup");
                        flag.set(true);
                    }
                    else if (s.matches("(master|маст[еэ]р).*")) word = "Troupe Master";
                    else if (s.matches("(star-?[wv][ieao]+v[ei]?r?|ст[ауеоэ]р-?в[иеэао]+в[еэи]?р?).{0,2}")) word = "Starweaver";
                    else if (s.matches("(s[kc][yai]+-?[wv][ieao]+v[ei]?r?|ск[ау][ий]?-?в[иеэао]+в[еэи]?р?).{0,2}")) word = "Skyweavers";
                    else if (s.matches("(vo[iy]d-?[wv][ieao]+v[ei]?r?|во[ийа]д-?в[иеэао]+в[еэи]?р?).{0,2}")) word = "Voidweaver";
                    else if (s.matches("(ткач|[wv]i?[eao]+v[ei]?r|в[иеэао]+в[еэи]?р).{0,2}")){
                        word = "";
                        lastName.replace(0, lastName.length(), "weav");
                        flag.set(true);
                    }
                    else if (s.matches("(зв[её]зд?н?|star|ст[ауеоэ]рн?).{0,2}")){
                        word = "";
                        lastName.replace(0, lastName.length(), "star_weav");
                        flag.set(true);
                    }
                    else if (s.matches("(неб[еа]с?н?|s[kc][yai]+|ск[ау][ий]?).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "sky_weav");
                        flag.set(true);
                    }
                    else if (s.matches("(пустотн?|vo[iy]d|во[ийа]д).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "void_weav");
                        flag.set(true);
                    }
                    else if (s.matches("((dea?(th|f))?-?[jdg]+este?r?|(д[эе][фсз])?-?д?ж[еэи]ст[еэи]?р?|шут).{0,2}")) word = "Death Jester";
                    else if (s.matches("([sc][oa]l[ei]t|с[оа]л[иеэ]с?т).{0,5}")) word = "Solitaire";
                    else if (s.matches("(g[aei]+t|г[еэаий]+т|в[оа]?р[оа]т).{0,2}")) word = "Webway Gate";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Eldar Start Collecting";
                    else word = "";
                    break;

                case "222401851"://тираниды и генокульт
                    if (s.matches("(b[rl][oua]+dlord|б[рл][ауо]+длорд).*")) word = "Broodlord";
                    else if (s.matches("(s[wv][oa]+rmlord|с[ву][ауо]+рмлорд).*")) word = "The Swarmlord";
                    else if (s.matches("(r[eia]+per|р[иеа]+п[еэ]р|жнец).*")) {
                        word = "Ripper Swarm";
                        lastName.replace(0, lastName.length(), "ne_swarm");
                    }
                    else if (s.matches("(sk[aiy]+-?slash?|ск[аийуо]+-?сл[еэа][шсх]+).*")) {
                        word = "Sky-Slasher Swarm";
                        lastName.replace(0, lastName.length(), "ne_swarm");
                    }
                    else if (s.matches("(s[wv][oa]+rm|с[ву][ауо]+рм).{0,2}") && !lastName.toString().equals("ne_swarm")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "swarm");
                        flag.set(true);
                    }
                    else if (s.matches("(lord|лорд).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "lord");
                        flag.set(true);
                    }
                    else if (s.matches("(t[yi]ran.{0,2}|тиран[аов].{0,2}|тиран-св[ао]рм.*)")){
                        word = "";
                        lastName.replace(0, lastName.length(), "tiran");
                        flag.set(true);
                    }
                    else if (s.matches("(h[ai]+[fv]e?|х[аий]+[фв]|уль?[яеий]+).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "hive");
                        flag.set(true);
                    }
                    else if (s.matches("(cr[oau]+n|[кс]р[аоу]+н).{0,2}")) word = "Hive Crone";
                    else if (s.matches("(ne[ui]r[oa]th?rop|н[еэий]+р[оа][тфв]роп).*")) word = "Neurothrope";
                    else if (s.matches("(eye|(одн[оа])?-?глаз.*)")) word = "Old One Eye";
                    else if (s.matches("(tervig|т[еэи]рвиг).*")) word = "Tervigon";
                    else if (s.matches("(pr[ai]+me?|пр[аий]?м).{0,2}")) word = "Tyranid Prime";
                    else if (s.matches("([wv]ar+i[oa]r|воин|в[ао]р[р]*и[оа]р).*")) word = "Tyranid Warriors";
                    else if (s.matches("(sh?r[ai]+[kc]e?s?|([сх]?|ш)р[аий]+к).{0,2}")) word = "Tyranid Shrikes";
                    else if (s.matches("(m[ae]l[ae]th?rop|м[аоеэ]+л[оаеэ][тфв]роп).*")) word = "Malanthrope";
                    else if (s.matches("(d?g[ei]n[eoi][sc]t[ei]a?l|д?[гж][еи]н[оеиа](ст[ие]+л+|крад)).*")) word = "Genestealers";
                    else if (s.matches("(h[oa]?rm[ao]g[ao]u?n|х[оа]рм[ао]г[аоу]+н).*")) word = "Hormagaunts";
                    else if (s.matches("(t[ei]?rm[ao]g[ao]u?n|т[еэ]рм[ао]г[аоу]+н).*")) word = "Termagants";
                    else if (s.matches("(t[yi]ran+[oa][ck]|тиран+[ао][ск]).*")) word = "Tyrannocyte";
                    else if (s.matches("(dea?(th?|[fv])l[iea]+p|д[еэ][фв]л[иаоеэ]+п).*")) word = "Deathleaper";
                    else if (s.matches("([hg][ao]rusp|[хг][фо]р[уо]сп).*")) word = "Haruspex";
                    else if (s.matches("(li[ck]t[oa]r|ли[кс]т[оа]р).*")) word = "Lictor";
                    else if (s.matches("(ma[ie]?le?[cs][eia]pt|м[аэеий]+лс[еиэ]пт).*")) word = "Maleceptor";
                    else if (s.matches("(p[yi]r[ao]v[ao]r|пир[оа]в[ао]р).*")) word = "Pyrovores";
                    else if (s.matches("(b[iayo]+v[ao]r|би[оа]в[ао]р).*")) word = "Biovores";
                    else if (s.matches("(ужас|ter+[oa]r|т[еэ]р+[оа]р).{0,2}")) word = "The Red Terror";
                    else if (s.matches("(v[ie]+n[oa]mth?rop|в[еи]н[ао]м[тфв]роп).*")) word = "Venomthropes";
                    else if (s.matches("(z[oa]+nth?rop|[зс][ао]+н[тфв]роп).*")) word = "Zoanthropes";
                    else if (s.matches("(g[oa]rg[yuo]l|[гх][ао]ргул).{0,3}")) word = "Gargoyles";
                    else if (s.matches("(spor[oa][ck][ay]st|спор[ао]-?[кс][аоиу]ст).*")) word = "Sporocyst";
                    else if (s.matches("(mu[ck][oa]l+[ie]+d|м[уа]к[оа]лид).*")) {
                        word = "Mucolid Spores";
                        lastName.replace(0, lastName.length(), "ne_spor");
                    }
                    else if (s.matches("(m[eioa]+t[ie][ck]|м[еэийао]+ти[кч]).*")) {
                        word = "Meiotic Spores";
                        lastName.replace(0, lastName.length(), "ne_spor");
                    }
                    else if (s.matches("(spor|спор).{0,2}") && !lastName.toString().equals("ne_spor")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "spor");
                        flag.set(true);
                    }
                    else if (s.matches("(m[ia]+ne?s?|м[иай]+н+ы?[ийе]?)")) word = "Spore Mines";
                    else if (s.matches("(r[aei]+v[ie]n[ei]r|р[еэий]+в[еэи]н[иеэ]р).*")) word = "Raveners";
                    else if (s.matches("(dimac?h[aei]+ron|дим[ао]х[аоеэ]+р[оа]н).*")) word = "Dimachaeron";
                    else if (s.matches("([hg]arpy?i?e?s?|[гх]арп[иейы]+)")) word = "Harpy";
                    else if (s.matches("([ck][ao]rni[fv]|[кс][ао]рн[ие][фв]).*")) word = "Carnifexes";
                    else if (s.matches("(e[xks]+[oa][ck]r[ae]?in|[еэ]к[зс][оа]кр[аеэий]+н).*")) word = "Exocrine";
                    else if (s.matches("(ma[wv]lo[ck]|м[ао][ву]ло[кс]).*")) word = "Mawloc";
                    else if (s.matches("(s[ck]r[eiao]+m[ei]r|скр[иеао]+м[еиэ]р).*")) word = "Screamer-Killers";
                    else if (s.matches("((th?|[fv])ornba[ck]|[тфв]орнб[еэ]к).*")) word = "Thornbacks";
                    else if (s.matches("(to(x|[ksc]+)[ie][ck]r[ie]n|то[кхс]+[ие]кр[иеэ]н).*")) word = "Toxicrene";
                    else if (s.matches("(tr[yi]g[ao]?n|триг[ао]н).{0,3}")) word = "Trygon";
                    else if (s.matches("(t[yi]ran+[oae]+f[ei](x|[ksc]+)|тиран+[ао][фв][еэи]кс?).{0,2}")) word = "Tyrannofex";
                    else if (s.matches("(h[ie]+r[oa]d[uy]l|[хг]?[иеэ]+р[оа]дул).*")) word = "Hierodule";
                    else if (s.matches("(har+[ie]da?|[хг][ао]р+[ие]да?).{0,3}")) word = "Harridan";
                    else if (s.matches("((bio?)?-?titan|(био?)?-?титан).*")) word = "Hierophant Bio-titan";
                        //генокульт
                    else if (s.matches("(ab[oa]m[ie]nan|[ао]б[оа]м[ие]нан).*")) word = "Abominant";
                    else if (s.matches("(al(ph?|[fv])[uao]s|аль?ф[уо]?с?)")) word = "Jackal Alphus";
                    else if (s.matches("((j|sh)a[ck]+al|д?[жш][ао]кал).*") && !lastName.toString().equals("ne_jack")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "jackal");
                        flag.set(true);
                    }
                    else if (s.matches("(atalan|[ао]т[аоеэ]лан).*")) {
                        word = "Atalan Jackals";
                        lastName.replace(0, lastName.length(), "ne_jack");
                    }
                    else if (s.matches("(mag[uo]s|маг[уо]с).*")) word = "Magus";
                    else if (s.matches("(prim[uo]s|прим[уо]с).*")) word = "Primus";
                    else if (s.matches("(patr[ie]ar|патриар).*")) word = "Patriarch";
                    else if (s.matches("(a[ck][oa]l[yi]t|ак[оа]лит).{0,2}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "acol");
                        flag.set(true);
                    }
                    else if (s.matches("(metamor|м[еи]т[ао]мор).*")) word = "Hybrid Metamorphs";
                    else if (s.matches("(h[yi]brid|[гх]ибрид).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "hybrid");
                        flag.set(true);
                    }
                    else if (s.matches("(n[eioa]+(ph|f)[yi]t|н[еэио]+(пх?|ф)ит).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "neoph");
                        flag.set(true);
                    }
                    else if (s.matches("(a?i[ck][oa]n.*|а?[ий]кон.*|знаменос.*)")) word = "Acolyte Iconward";
                    else if (s.matches("(br[oa](th|[fvs])er|бр[ао][зст]ь?[еэя]).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "broth");
                        flag.set(true);
                    }
                    else if (s.matches("(infantry|пехот|взвод).*")) word = "Brood Brothers Infantry Squad";
                    else if (s.matches("(heavy|х[эе]ви|хвт-?(расч[её]т.?)?|расч[её]т.*)")) word = "Brood Brothers Heavy Weapons Squad";
                    else if (s.matches("(chimer|химер).*")) word = "Cult Chimera";
                    else if (s.matches("(g[oa]l[iea]+(th?|[fv])|[гх][оа]л[иеа]+[фв]).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "goliath");
                        flag.set(true);
                    }
                    else if (s.matches("(tr[ua][ck]|трак|груз[оа]вик).{0,2}")) word = "Goliath Truck";
                    else if (s.matches("((ro[ck]+)?-?gr[iae]+nd|(рок)?-?(гр[аийеэ]+нд|измелчител)).{0,4}")) word = "Goliath Rockgrinder";
                    else if (s.matches("(ab+[ei]r+ant|аб+[еиэ]р+ант).*")) word = "Aberrants";
                    else if (s.matches("(bio(ph?|f)ag|биофаг).{0,4}")) word = "Biophagus";
                    else if (s.matches("([ck]l[ao]m[ao]?v|[кс]л[ао]м[ао]в).{0,4}")) word = "Clamavus";
                    else if (s.matches("([kc][ei]l[ei]mor|к[еиэ]л[еиэ]мор).*")) word = "Kelermorph";
                    else if (s.matches("(lo[ck][ua]s|лок[уа]с).*")) word = "Locus";
                    else if (s.matches("(ne(x|[ksc]+)os|н[еиэ]кс?[оа]с).*")) word = "Nexos";
                    else if (s.matches("(san[ck]t|санкт).{0,4}")) word = "Sanctus";
                    else if (s.matches("(sentinel|с[еэ]нтин[эеа]л).*")) word = "Cult Sentinels";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Achilles Ridgerunners";
                    else if (s.matches("(lem+an|лем+ан|panish|demolish|паниш[еэ]р|демолиш[еэ]р).*") || s.matches("бт|bt")) word = "Cult Leman Russ";
                    else if (s.matches("((fr[ae]g)?dr[ie]l+|(фр[аеэ]+г)?-?др[ие]л+ь?).{0,2}")) word = "Tectonic Fragdrill";
                    else if (s.matches("(col+ect|кол+ектинг|старт).*") || s.matches("(sk|ск)")) word = "Tyranid Start Collecting";
                    else word = "";
                    break;
            }
        else switch (alb_id) {
            case "222401877": //necrons
                if (lastName.toString().equals("destroyer")) {
                    if (s.contains("lord") || s.contains("лорд"))word = "Necron Destroyer Lord";
                    else word = "Necron Destroyers";

                    lastName.replace(0, lastName.length(),"");
                    flag.set(false);
                }
                if (lastName.toString().equals("night")) {
                    if (s.contains("shroud") || s.contains("шрауд") || s.contains("шроуд")) word = "Night Shroud";
                    else word = "Night Scythe";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("tesseract")) {
                    if (s.contains("ark") || s.contains("арк")) word = "Tesseract Ark";
                    else word = "Tesseract Vault";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401920":
                if (lastName.toString().equals("herald")) {
                    if (s.contains("of")) {
                        lastName.replace(0, lastName.length(), "herald of");
                        word = "";
                    }
                    else {
                        if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Herald of Slaanesh";
                        else if (s.contains("tzi") || s.contains("tze") || s.contains("тзи")) word = "Herald of Tzeentch";
                        else if (s.contains("nurg") || s.contains("нург")) word = "Herald of Nurgle";
                        else if (s.contains("kho") || s.contains("кхо")) word = "Herald of Khorn";
                        else word = "Herald of Chaos";

                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
            else if (lastName.toString().equals("herald of")) {
                    if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Herald of Slaanesh";
                    else if (s.contains("tzi") || s.contains("tze") || s.contains("тзи")) word = "Herald of Tzeentch";
                    else if (s.contains("nurg") || s.contains("нург")) word = "Herald of Nurgle";
                    else if (s.contains("kho") || s.contains("кхо")) word = "Herald of Khorn";

                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("blood")) {
                    if (s.contains("thron")) word = "Blood Throne";

                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dp")) {
                    if (s.contains("of")) {
                        lastName.replace(0, lastName.length(), "dp of");
                        word = "";
                    }
                    else {
                        if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Daemon Prince of Slaanesh";
                        else if (s.matches("(tz[ie]+nch|тзинч).*")) word = "Daemon Prince of Tzeentch";
                        else if (s.contains("nurg") || s.contains("нург")) word = "Daemon Prince of Nurgle";
                        else if (s.contains("kho") || s.contains("кхо")) word = "Daemon Prince of Khorn";
                        else word = "Daemon Prince of Chaos";

                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("dp of")) {
                    if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Daemon Prince of Slaanesh";
                    else if (s.matches("(tz[ie]+nch|тзинч).*")) word = "Daemon Prince of Tzeentch";
                    else if (s.contains("nurg") || s.contains("нург")) word = "Daemon Prince of Nurgle";
                    else if (s.contains("kho") || s.contains("кхо")) word = "Daemon Prince of Khorn";
                    else word = "Daemon Prince of Chaos";

                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }

                else if (lastName.toString().equals("beast")) {
                    if (s.contains("of")) {
                        lastName.replace(0, lastName.length(), "beast of");
                        word = "";
                    }
                    else {
                        if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Beasts of Slaanesh";
                        else if (s.matches("(tz[ie]+nch|тзинч).*")) word = "Beastsof Tzeentch";
                        else if (s.contains("nurg") || s.contains("нург")) word = "Beasts of Nurgle";
                        else if (s.contains("kho") || s.contains("кхо")) word = "Beastsof Khorn";
                        else word = "Beasts of Chaos";

                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("beast of")) {
                    if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Beasts of Slaanesh";
                    else if (s.contains("tzi") || s.contains("tze") || s.contains("тзи")) word = "Beasts of Tzeentch";
                    else if (s.contains("nurg") || s.contains("нург")) word = "Beastsof Nurgle";
                    else if (s.contains("kho") || s.contains("кхо")) word = "Beasts of Khorn";
                    else word = "Beasts of Chaos";

                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }

                else if (lastName.toString().equals("daemons")) {
                    if (s.contains("of")) {
                        lastName.replace(0, lastName.length(), "daemons of");
                        word = "";
                    }
                    else {
                        if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Daemons of Slaanesh";
                        else if (s.contains("tzi") || s.contains("tze") || s.contains("тзи"))
                            word = "Daemons of Tzeentch";
                        else if (s.contains("nurg") || s.contains("нург"))
                            word = "Daemons of Nurgle";
                        else if (s.contains("kho") || s.contains("кхо")) word = "Daemons of Khorn";
                        else word = "Daemons";

                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("daemons of")) {
                    if (s.contains("slaanesh") || s.contains("slanesh") || s.contains("слаанеш") || s.contains("сланеш")) word = "Daemons of Slaanesh";
                    else if (s.contains("tzi") || s.contains("tze") || s.contains("тзи")) word = "Daemons of Tzeentch";
                    else if (s.contains("nurg") || s.contains("нург")) word = "Daemons of Nurgle";
                    else if (s.contains("kho") || s.contains("кхо")) word = "Daemons of Khorn";
                    else word = "Daemons";

                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401895":
                 if ( lastName.toString().equals("command")) {
                     if (s.matches("farsig?h?t.*") || s.matches("фарс(а[йи]|иг?х?)т.*") || s.contains("дальновид")) word = "Commander Farsight";
                     else if (s.matches("shadowsun.*") || s.matches("ш[еэ]до[ув]с[ау]н.*") || s.contains("дальновид")) word = "Commander Shadowsun";
                     else word = "Tau Commander";
                     lastName.replace(0, lastName.length(), "");
                     flag.set(false);
                 }
                 else if ( lastName.toString().equals("aun")) {
                     if (s.matches("(va | ва)")) word = "Aun Va";
                     else if (s.matches("(shi | ши)")) word = "Aun Shi";
                     lastName.replace(0, lastName.length(), "");
                     flag.set(false);
                 }
                 else if ( lastName.toString().equals("shaso")) {
                     if (s.matches("r'?alai") || s.matches("[еэ]р'?ала.*")) word = "Shas’o R'alai";
                     else if (s.matches("r'?m[yu]r") || s.matches("[эе]?р'?м[иу]р")) word = "Shas'o R'myr";
                     else word = "Shas'o";
                     lastName.replace(0, lastName.length(), "");
                     flag.set(false);
                 }
                 else if ( lastName.toString().equals("fire")) {
                     if (s.matches("кру{1,2}т.*")) word = "Kroot Carnivores|Shaper";
                     else if (s.matches("(тау|огня)")) word = "Fire Warriors";
                     else word = "";
                     lastName.replace(0, lastName.length(), "");
                     flag.set(false);
                 }
                 else if (lastName.toString().equals("kroot")){
                     if (s.matches("(гончи|хо?ундс|hound).*"))word = "Kroot Hounds";
                     else word = "Kroot Carnivores|Shaper";
                     lastName.replace(0, lastName.length(), "");
                     flag.set(false);
                 }
                break;
            case "222401931":
                if (lastName.toString().equals("heavy")){
                    if (s.matches("(weapon|оружи).*")) word = "Heavy Weapons Squad";
                    else if (s.matches("(mortar|морт[иа]р).*")) word = "Heavy Mortar Battery";
                    else if (s.matches("(quad|ку?в?ад|с?четвер[её]?н).*")) word = "Heavy Quad Launcher Battery";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;
            case "222401908": //рыцари
                if (lastName.toString().equals("armig")){
                    if (s.matches("(helv[ei]rin|х[эе]лв[еэ]рин).*")) word = "Armiger Helverin";
                    else if (s.matches("(warglai?v|в[ао]ргл[эе][йи]в).*")) word = "Armiger Warglaive";
                    else word = "Armiger";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("knight")){
                    if (s.matches("(castel+an|каст[еэ]л+ь?[яа]н).*")) word = "Knight Castellan";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Knight Crusader";
                    else if (s.matches("(er+ant|[эе]р+ант|блуждающ).*")) word = "Knight Errant";
                    else if (s.matches("(gal+ant|г[ао]л+ант).*")) word = "Knight Gallant";
                    else if (s.matches("(pal+adin|пал+адин).*")) word = "Knight Paladin";
                    else if (s.matches("(preceptor|пр[еэ]с+[еэ]пт[оа]р|наставник).*")) word = "Knight Preceptor";
                    else if (s.matches("(val+iant|вал+иант|доблест?н).*")) word = "Knight Valiant";
                    else if (s.matches("(warden|[ву][ао]рд[еэ]н|смотрител).*")) word = "Knight Warden";
                    else word = "Imperial Knight";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("cerastus")) {
                    if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?)[^-].*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "ceras knight");
                    }
                    else {
                        if (s.matches(".*-?(acheron|ах[еэ]рон|[эа][йи][чх][еэ]рон).*")) word = "Cerastus Knight-Acheron";
                        else if (s.matches(".*-?(atropos|атроп[оа]с).*")) word = "Cerastus Knight-Atropos";
                        else if (s.matches(".*-?(castigator|к[аэ]стиг(а|[еэ][йи])тор).*")) word = "Cerastus Knight-Castigator";
                        else if (s.matches(".*-?(lancer|л[эе]нс[еэ]р).*")) word = "Cerastus Knight-Lancer";
                        else word = "Cerastus Knight";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("ceras knight")) {
                    if (s.matches("(acheron|ах[еэ]рон|[эа][йи][чх][еэ]рон).*")) word = "Cerastus Knight-Acheron";
                    else if (s.matches("(atropos|атроп[оа]с).*")) word = "Cerastus Knight-Atropos";
                    else if (s.matches("(castigator|к[аэ]стиг(а|[еэ][йи])тор).*")) word = "Cerastus Knight-Castigator";
                    else if (s.matches("(lancer|л[эе]нс[еэ]р).*")) word = "Cerastus Knight-Lancer";
                    else word = "Cerastus Knight";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("qvestor")) {
                    if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?)[^-].*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "qvestor knight");
                    }
                    else {
                        if (s.matches("(magaer|м[аеэ]га?[еэ]р).*")) word = "Questoris Knight Magaera";
                        else if (s.matches("(st[yi]rix|ст[иу]рикс).*")) word = "Questoris Knight Styrix";
                        else word = "Questoris Knight";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("qvestor knight")) {
                    if (s.matches("(magaer|м[аеэ]га?[еэ]р).*")) word = "Questoris Knight Magaera";
                    else if (s.matches("(st[yi]rix|ст[иу]рикс).*")) word = "Questoris Knight Styrix";
                    else word = "Questoris Knight";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "240229103":
                if (lastName.toString().equals("raider")) {
                    if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else word = "Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("wolf")){
                    if (s.matches("(scout|скаут).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "wolf scout");
                    }
                    else {
                        if (s.matches("(lord|лорд).*")) word = "Wolf Lord";
                        else if (s.matches("(priest|прист|священ|жрец).*")) word = "Wolf Priest";
                        else if (s.matches("(guard|г(у|ь)?[ая]рд|стражник).*")) word = "Wolf Guard";
                        else word = "";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("wolf")){
                    if (s.matches("(ба[йи]к.*|biker.*|на|мотоцикл.*)")) word = "Wolf Scout Bikers";
                    else word = "Wolf Scouts";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("swift")){
                    if (s.matches("(at+ac?k?|[аэ]т+ак.*)")) lastName.replace(0, lastName.length(), "swift at");
                    else {
                        word = "Swiftclaws";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("swift at")){
                    if (s.matches("(bike|ба[йи]к).*")) word = "Swiftclaw Attack Bikes";
                    else word = "Swiftclaws";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222402011": //спэйс марины
                if (lastName.toString().equals("chaplan")) {
                    if (s.matches("(drea?dnoug?h?t|др[еэ]дноут).*")) lastName.replace(0, lastName.length(), "chapDred");
                    else {
                        if (s.matches("(venerable|поч[её]?те?н|в[еэ]н[еэ]р[еэа][йи]?бл).*")) word = "Chaplain Venerable Dreadnought";
                        else if (s.matches("(titus|титу?с?).?")) word = "Chaplain Dreadnought Titus";
                        else if (s.matches("(cas+ius|кас+и).*")) word = "Chaplain Cassius";
                        else if (s.matches("(grima?la?dus|грима?ла?д).*")) word = "Chaplain Grimaldus";
                        else if (s.matches("(ivanus|иван).*")) word = "Chaplain Ivanus Enkomi";
                        else if (s.matches("(thulsa|[тфв]улс).{0,2}")) word = "High Chaplain Thulsa Kane";
                        else word = "Chaplain";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("chapDred")) {
                    if (s.matches("(titus|титу?с?).?")) word = "Chaplain Dreadnought Titus";
                    else word = "Chaplain Dreadnought";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("raider")) {
                    if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else word = "Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("libra")) {
                    if (s.matches("(tiguri|тигури).*")) word = "Chief Librarian Tigurius";
                    else word = "Librarian";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("cap")) {
                    if (s.matches("(sicari?us|сикари).*")) word = "Captain Sicarius";
                    else if (s.matches("(l[yi]sander|ли[сз]андер).*")) word = "Captain Lysander";
                    else if (s.matches("(z?h?g?rukh?al|androcle|д?ж?рукх?ал|андрокл?ес).*")) word = "Captain Zhrukhal Androcles";
                    else if (s.matches("(sumatris|с[уиа]матрис).*")) word = "Captain Corien Sumatris";
                    else if (s.matches("(mordaci|морда[кс]).*")) word = "Captain Mordaci Blaylock";
                    else if (s.matches("(pel+as|пел+ас).*")) word = "Captain Pellas Mir’san";
                    else if (s.matches("(sil+as|сил+ас).*")) word = "Captain Silas Alberec";
                    else if (s.matches("(tarnus|тарн|vale|в[эае][ий]л).{0,4}")) word = "Captain Tarnus Vale";
                    else if (s.matches("(elam|[еэ]лам).*")) word = "Knight-Captain Elam Courbray";
                    else word = "Captain";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("scout")) {
                    if (s.matches("(ба[йи]к.*|biker.*|на|мотоцикл.*)")) word = "Scout Bike Squad";
                    else word = "Scout Squad";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dred")) {
                    if (s.matches("(contem?ptor|контем?птор).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "cont_dred");
                    }
                    else {
                        if (s.matches("(mortis|мортис).*")) word = "Mortis Dreadnought";
                        else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) word = "Ironclad Dreadnought";
                        else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) word = "Redemptor Dreadnought";
                        else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) word = "Venerable Dreadnought";
                        else if (s.matches("(levia(th|f)an|левиафан).*")) word = "Leviathan Dreadnought";
                        else if (s.matches("(sieg|сидж|осадн).{0,4}")) word = "Siege Dreadnought";
                        else word = "Dreadnought";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("cont_dred")) {
                    if (s.matches("(mortis|мортис).*")) word = "Contemptor Mortis Dreadnought";
                    else word = "Contemptor Dreadnought";
                    lastName.replace(0, lastName.length(), "ne_dred");
                    flag.set(false);
                }
                else if (lastName.toString().equals("wirl")) {
                    if (s.matches("(h[yi]periou?s|гипери).*")) word = "Whirlwind Hyperios";
                    else if (s.matches("(scorpius|скорпи).*")) word = "Relic Whirlwind Scorpius";
                    else word = "Whirlwind";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("asalt")) {
                    if (s.matches("(ram|р[эе]м|баран).*")) word = "Caestus Assault Ram";
                    else if (s.matches("(gunship|г[ау]ншип|корабл).*")) word = "Assault Gunship";
                    else word = "Assault Squad";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401962": //хаоситы
                if (lastName.toString().equals("lord")) {
                    if (s.matches("of")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "lord of");
                    }
                    else {
                        if (s.matches("(discordant|диск[ао]рдант|несогласн).*")) word = "Lord Discordant on Helstalker";
                        else if (s.matches("(arkos|аркос).*")) word = "Lord Arkos";
                        else if (s.matches("(гварди)")) word = "Lord of Contagion";
                        else word = "Chaos Lord";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("lord of")) {
                    if (s.matches("(contagion.*|разложе.*)")) word = "Lord of Contagion";
                    else if (s.matches("(skul|ск[уа]л).*")) word = "Khorne Lord of Skulls";
                    else word = "Chaos Lord";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dp")) {
                    if (s.contains("of")) {
                        lastName.replace(0, lastName.length(), "dp of");
                        word = "";
                    }
                    else {
                        if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Daemon Prince of Slaanesh";
                        else if (s.matches("(tz[ie]+nch|тзинч).*")) word = "Daemon Prince of Tzeentch";
                        else if (s.contains("nurg") || s.contains("нург")) word = "Daemon Prince of Nurgle";
                        else if (s.contains("kho") || s.contains("кхо")) word = "Daemon Prince of Khorn";
                        else word = "Daemon Prince of Chaos";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("dp of")) {
                    if (s.matches("(sla+n[ei]sh|сла+н[еи]ш).*")) word = "Daemon Prince of Slaanesh";
                    else if (s.matches("(tz[ie]+nch|тзинч).*")) word = "Daemon Prince of Tzeentch";
                    else if (s.contains("nurg") || s.contains("нург")) word = "Daemon Prince of Nurgle";
                    else if (s.contains("kho") || s.contains("кхо")) word = "Daemon Prince of Khorn";
                    else word = "Daemon Prince of Chaos";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dred")) {
                    if (s.matches("(contem?ptor|контем?птор).*")) word = "Hellforged Contemptor Dreadnought";
                    else if (s.matches("(deredeo|д[еэ]р[еэ]д[еэ]о).*")) word = "Hellforged Deredeo Dreadnought";
                    else if (s.matches("(infernus|инферн).*")) word = "Ferrum Infernus Chaos Dreadnought";
                    else if (s.matches("(levia(th|f)an|левиафан).*")) word = "Hellforged Leviathan Dreadnought";
                    else word = "Hellforged Dreadnought";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("hel")) {
                    if (s.matches("(blad|бл[аэ][йи]?д).*")) word = "Chaos Hell Blade";
                    else if (s.matches("(talon|талон|когт).*")) word = "Chaos Hell Talon";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("asalt")) {
                    if (s.matches("(cl[ao][wv]|кло|кого?т).{0,3}")) word = "Hellforged Kharybdis Assault Claw";
                    else if (s.matches("(gunship|г[ау]ншип|корабл).*")) word = "Hellforged Assault Gunship";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("raider")) {
                    if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Hellforged Land Raider Achilles";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Hellforged Land Raider Proteus";
                    else word = "Hellforged Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("plag")) {
                    if (s.matches("([ck]r[ao][wu]ler|кр[ао]у?л+ер).*")) {
                        word = "Plague Crawler";
                        lastName.replace(0, lastName.length(), "ne_crawl");
                    }
                    else {
                        if (s.matches("(marin.*|м[аэ]рин.*|sm|мар.{0,3}|парн(и|ей|я).*)")) word = "Plague Marines";
                        else if (s.matches("([kc]aster|каст[еэ]р).*")) word = "Malignant Plaguecaster";
                        else if (s.matches("(drone|дрон).{0,3}")) word = "Plague Drone";
                        else if (s.matches("(surgeon|сург[еи]он|хирург).*")) word = "Plague Surgeon";
                        else word = "";
                        lastName.replace(0, lastName.length(), "");
                    }
                    flag.set(false);
                }
                break;

            case "222401939": //блады
                if (lastName.toString().equals("cap")) {
                    if (s.matches("(t[yi]c?h|ти[хш]).{0,4}")) word = "Captain Tycho";
                    else word = "Captain";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("libra")) {
                    if (s.matches("(me(ph|[fv])[ie]sto|меп?х?ф?исто).*")) word = "Chief Librarian Mephiston";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут).*")) word = "Librarian Dreadnought";
                    else word = "Librarian";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("scout")) {
                    if (s.matches("(ба[йи]к.*|biker.*|на|мотоцикл.*)")) word = "Scout Bike Squad";
                    else word = "Scout Squad";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dred")) {
                    if (s.matches("(dea?(th|[fv])|д[эе][фсзв]).{0,2}")){
                        word = "";
                        lastName.replace(0, lastName.length(), "death_company");
                    }
                    else {
                        if (s.matches("(contem?ptor|контем?птор).*")) word = "Contemptor Dreadnought";
                        else if (s.matches("(furiou?[sc]|фь?[ую]рио[сз]|яростн|бешен).{0,5}")) word = "Furioso Dreadnought";
                        else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) word = "Redemptor Dreadnought";
                        else if (s.matches("(mortis|мортис).*")) word = "Mortis Dreadnought";
                        else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) word = "Ironclad Dreadnought";
                        else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) word = "Redemptor Dreadnought";
                        else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) word = "Venerable Dreadnought";
                        else if (s.matches("(levia(th|f)an|левиафан).*")) word = "Leviathan Dreadnought";
                        else if (s.matches("(sieg|сидж|осадн).{0,4}")) word = "Siege Dreadnought";
                        else word = "Dreadnought";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("death_company")) {
                    if (s.matches("(compan|к[оа]мп[оа]н).*")) word = "Death Company Dreadnought";
                    else word = "Dreadnought";
                    lastName.replace(0, lastName.length(), "ne_dred");
                    flag.set(false);
                }
                else if (lastName.toString().equals("company")) {
                    if (s.matches("(champion|ч[еэа]мпион).*")) word = "Company Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Company Ancient";
                    else if (s.matches("(veteran|ветер[ае]н).*")) word = "Company Veteran";
                    else if (s.matches("(dreadnoug?h?t|др[еэ]дноут|др[еэ]д).{0,3}")) word = "Death Company Dreadnought";
                    else word = "Death Company";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("sang")) {
                    if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) {
                        word = "Sanguinary Ancient";
                        lastName.replace(0, lastName.length(), "comp");
                        flag.set(false);
                    }
                    else {
                        if (s.matches("(priest|прист|священ|жрец).*")) word = "Sanguinary Priest";
                        else if (s.matches("(guard|гв?(у|ь)?[ая]рд|стражник).{0,3}")) word = "Sanguinary Guard";
                        else if (s.matches("(noviti?at|новити?а[йи]?т|послушник).{0,3}")) word = "Sanguinary Novitiate";
                        else word = "";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("baal")) {
                    if (s.matches("(predator|предатор|хи[щш]ник).*")) word = "Baal Predator";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("raider")) {
                    if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else word = "Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401988": //темные ангелы
                if (lastName.toString().equals("scout")) {
                    if (s.matches("(ба[йи]к.*|biker.*|на|мотоцикл.*)")) word = "Scout Bike Squad";
                    else word = "Scout Squad";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("death")) {
                    if (s.matches("(terminator.*|терм(инатор|ос).*|терм.?.?)")) word = "Deathwing Terminator Squad";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Deathwing Ancient";
                    else if (s.matches("(command|к[оа]м+андн.*)")) word = "Deathwing Command Squad";
                    else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Deathwing Champion";
                    else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Deathwing Apothecary";
                    else if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?).*")) word = "Deathwing Knights";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("raven")) {
                    if (s.matches("(dark|дарк|т[её]мн).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "dark");
                    }
                    else {
                        if (s.matches("(talonmast|талонмаст|маст[еэ]р).*")) word = "Ravenwing Talonmaster";
                        else if (s.matches("(d[ao]rksh?r[ao]u?d|д[ао]рк[шс]р[ао]у?о?д).*")) word = "Ravenwing Darkshroud";
                        else if (s.matches("(d[ao]rkt[ao]l[oa]n|д[ао]ркт[еэ]л[оа]н).*")) word = "Ravenwing Dark Talon";
                        else if (s.matches("(command|к[оа]м+андн.*)")) word = "Ravenwing Command Squad";
                        else if (s.matches("((l[ea]n?d?)?-?spe+der|(л[еэ]н?д?)?-?спид[еэ]р).*")) word = "Ravenwing Land Speeder";
                        else if (s.matches("(ба[йи]к.*|biker?.*|на|мотоцикл.*)")) word = "Ravenwing Bike Squad";
                        else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Ravenwing Ancient";
                        else if (s.matches("(champion|ч[еэа]мпион).*")) word = "Ravenwing Champion";
                        else if (s.matches("(apothecary|ап[оа]т[еи]кари).*")) word = "Ravenwing Apothecary";
                        else if (s.matches("(blac?k|бл[еэ]к|ч[её]рн|k?nigh?t|к?на[йи]т|рыцарь?).*")) word = "Ravenwing Black Knights";
                        else word = "";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("dark")) {
                    if (s.matches("(sh?r[ao]u?d|[шс]р[ао]у?о?д|саван).*")) word = "Ravenwing Darkshroud";
                    else if (s.matches("(t[ao]l[oa]n|т[еэ]л[оа]н|кого?т).*")) word = "Ravenwing Dark Talon";
                    else if (s.matches("(k?nigh?t|к?на[йи]т|рыцарь?).*")) word = "Ravenwing Black Knights";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("company")) {
                    if (s.matches("(champion|ч[еэа]мпион).*")) word = "Company Champion";
                    else if (s.matches("(ansient|[эае][йи]?н[шс]и?[еэ]?нт|древн).*")) word = "Company Ancient";
                    else if (s.matches("(veteran|ветер[ае]н).*")) word = "Company Veteran";
                    else if (s.matches("(master|маст[еэ]р).*")) word = "Company Master";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("raider")) {
                    if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else word = "Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dred")) {
                    if (s.matches("(contem?ptor|контем?птор).*")) word = "Contemptor Dreadnought";
                    else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) word = "Redemptor Dreadnought";
                    else if (s.matches("(mortis|мортис).*")) word = "Mortis Dreadnought";
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) word = "Ironclad Dreadnought";
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) word = "Redemptor Dreadnought";
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) word = "Venerable Dreadnought";
                    else if (s.matches("(levia(th|f)an|левиафан).*")) word = "Leviathan Dreadnought";
                    else if (s.matches("(sieg|сидж|осадн).{0,4}")) word = "Siege Dreadnought";
                    else word = "Dreadnought";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("wing")) {
                    if (s.matches("(dea?(th|[fv])[wv]ing|ду?[еэ]у?[фсз]в?ин).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "death");
                    }
                    else if (s.matches("(rave?n[wv]ing|р[еэ][ий]в[еэ]н?в?ин).{0,3}")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "raven");
                    }
                    else {
                        word = "";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                break;

            case "222401924":
                if (lastName.toString().equals("mek")) {
                    if (s.matches("(drea?d|др[еэ]д).{0,4}]")) word = "Meka-Dread";
                    else if (s.matches("(gun|г[ау]|пушк).{0,2}")) word = "Mek Gunz";
                    else word = "Mek";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("big")) {
                    if (s.matches("(m[ea][kc]|м[еэ]к).{0,2}")) word = "Big Mek";
                    else if (s.matches("(gun|г[ау]|пушк).{0,2}")) word = "Big Gunz";
                    else if (s.matches("(tr[ae][kc]|тр[ауеэ]к).*")) word = "Big Trakk";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("death")) {
                    if (s.matches("(drea?d|др[еэ]д).*")) word = "Deff Dreads";
                    else if (s.matches("([kc]il+|кил+|уби[ий]).*")) word = "Deffkilla Wartrike";
                    else if (s.matches("([kc]opt|копт|вертол).*")) word = "Deffkoptas";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("burn")) {
                    if (s.matches("(bo(y|ie?)[sz]?|б[оа][ий][зс]).{0,2}")) word = "Burna Boyz";
                    else if (s.matches("(bom+b?er|б[оа]м+б?[еэ]р).*")) word = "Burna-bommer";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("flight")) {
                    if (s.matches("(bom+b?er|б[оа]м+б?[еэ]р).*")) word = "Fighta-Bommer";
                    else word = "Attack Fighta";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("mega")) {
                    if (s.matches("(nob|но[бп]).{0,3}")) word = "Meganobz";
                    else if (s.matches("(tr[ae][kc]|тр[ауеэ]к|sca?rap[jdg]+[ea]t|скр[аэе]пд?ж[еэа]т).*")) word = "Megatrakk Scrapjets";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                if (lastName.toString().equals("war")) {
                    if (s.matches("(b[ua]g+[iy].{0,5}|баг.{0,3})")) word = "Warbuggies";
                    else if (s.matches("(tr[ae][kc]|тр[ауеэ]к).*"))  word = "Wartrakks";
                    else if (s.matches("([wv][aeo]gon|в[еэа]г).{0,6}")) word = "Battlewagon";
                    else if (s.matches("(bi[kc]e?r|ба[йи]к|мотоцикл).*")) word = "Warbikers";
                    else if (s.matches("(fortr[ea]s|фортр[еэ]с|крепост).*")) word = "Battle Fortress";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401887":
                if (lastName.toString().equals("knight")){
                    if (s.matches("(castel+an|каст[еэ]л+ь?[яа]н).*")) word = "Knight Castellan";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Knight Crusader";
                    else if (s.matches("(er+ant|[эе]р+ант|блуждающ).*")) word = "Knight Errant";
                    else if (s.matches("(gal+ant|г[ао]л+ант).*")) word = "Knight Gallant";
                    else if (s.matches("(pal+adin|пал+адин).*")) word = "Knight Paladin";
                    else if (s.matches("(preceptor|пр[еэ]с+[еэ]пт[оа]р|наставник).*")) word = "Knight Preceptor";
                    else if (s.matches("(val+iant|вал+иант|доблест?н).*")) word = "Knight Valiant";
                    else if (s.matches("(warden|[ву][ао]рд[еэ]н|смотрител).*")) word = "Knight Warden";
                    else word = "Imperial Knight";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("scit")){
                    if (s.matches("(rand?g[ea]r|р[еэ][ий]?нд?ж).*")) word = "Skitarii Rangers";
                    else if (s.matches("(v[ae]ngu?ard|в[аеэ]нгу?ард).*")) word = "Skitarii Vanguard";
                    else word = "Skitarii";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("sicar")){
                    if (s.matches("(ruststalk|р[ау]стсталк).*")) word = "Sicarian Ruststalkers";
                    else if (s.matches("(inflitrator|инфлин?трат[оа]р).*")) word = "Sicarian Infiltrators";
                    else word = "Sicarian";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("armig")){
                    if (s.matches("(helv[ei]rin|х[эе]лв[еэ]рин).*")) word = "Armiger Helverin";
                    else if (s.matches("(warglai?v|в[ао]ргл[эе][йи]в).*")) word = "Armiger Warglaive";
                    else word = "Armiger";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401873": //grey
                if (lastName.toString().equals("raider")) {
                    if (s.matches("(excelsior|[эе]ксц?ел[зс]и[оа]р).*")) word = "Land Raider Excelsior";
                    else if (s.matches("(crusader|кру[сз][аэ][ий]?д[еэо]?р|крестоносе?ц).*")) word = "Land Raider Crusader";
                    else if (s.matches("(r[ei]+d[ei]+m[ei]r|р[иеэ]д[ие]+м[еэ]р).*")) word = "Land Raider Redeemer";
                    else if (s.matches("(ac?hil+es|ахил+ес).*")) word = "Land Raider Achilles";
                    else if (s.matches("(helios|[гх]елиос).*")) word = "Land Raider Helios";
                    else if (s.matches("(prometheus|промет[еи]).*")) word = "Land Raider Prometheus";
                    else if (s.matches("(proteus|проте([ий]|ус)?).*")) word = "Relic Land Raider Proteus";
                    else word = "Land Raider";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("dred")) {
                    if (s.matches("(contem?ptor|контем?птор).*")) word = "Contemptor Dreadnought";
                    else if (s.matches("(do+mgl[ae]i?v|д[уо]+мгл[еэ][ий]в|меч).*")) word = "Doomglaive Pattern Dreadnought";
                    else if (s.matches("(venerable|в[еэ]н[еэ]р[еэа][йи]?бл|поч[её]?те?н).*")) word = "Venerable Dreadnought";
                    else if (s.matches("(redemptor|р[еэ]д[еэ]мпт[оа]р|искупит).*")) word = "Redemptor Dreadnought";
                    else if (s.matches("(mortis|мортис).*")) word = "Mortis Dreadnought";
                    else if (s.matches("(ironcl[ao]d|а[йи]р[оа]нклоа?д|броненос).*")) word = "Ironclad Dreadnought";
                    else if (s.matches("(redemto[rp]|ред[эе]мтор|искупител).*")) word = "Redemptor Dreadnought";
                    else if (s.matches("(levia(th|f)an|левиафан).*")) word = "Leviathan Dreadnought";
                    else if (s.matches("(sieg|сидж|осадн).{0,4}")) word = "Siege Dreadnought";
                    else word = "Dreadnought";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("sorit")) {
                    if (s.matches("(rh?ino|ринк?[оаы]).*")) word = "Sororitas Rhino";
                    else if (s.matches("(r[ea]p+r+[ea]s+[oa]r|р[еэ]п+р+[еэ]с+[ао]р).*")) word = "Sororitas Repressor";
                    else if (s.matches("(sist|с[иеё]ст).*")) word = "Battle Sister Squad";
                    else if (s.matches("([ck]an[oa]nes|[кс][ао]н[оа]н[еэ]с).*")) word = "Canoness";
                    else if (s.matches("(c[ea]le[sc]tine?|[цс][еэ]л[еэ]стин)[eaуы]?[\\.,]?")) word = "Celestine";
                    else if (s.matches("(c[ea]le[sc]tian|[цс][еэ]л[еэ]стин).*")) word = "Celestian Squad";
                    else if (s.matches("(mis+[ie][oa]n|м[ие]с+[ие][оа]н).*")) word = "Missionary";
                    else if (s.matches("([jy]a[ck][ao]b|[ий]?[ая]к[оа][вб]).*")) word = "Uriah Jacobus";
                    else if (s.matches("(im+[ao]l[ya]+t).*")) word = "Immolator";
                    else if (s.matches("(im[ao]d?g[ie][fv]|[ие]м[эеао]д?ж[еиэ][фв]).*")) word = "Imagifier";
                    else if (s.matches("(priest|прист|священ+ик|министорум).*")) word = "Ministorum Priest";
                    else if (s.matches("((ar[ck][aoe]?)?-?[fv]l[aeo]d?g[eao]l+[aoe]n|арм[еэ][ий]?с|маз[ао]хи|([ао]рк[ао]еэ?)?-?[фв]л[ао]д?ж[еэ]л[ао]н).*")) word = "Arco-flagellants";
                    else if (s.matches("(crusader|крусад[еэ]р|крестоносе?ц).*")) word = "Crusaders";
                    else if (s.matches("(d[ie][aoe]l+og|д[ие][ао]л+[оа]г).*")) word = "Dialogus";
                    else if (s.matches("(d?g[ea]min|д?ж[еэ]мин|близн[еяэ]).*")) word = "Geminae Superia";
                    else if (s.matches("(h[ao][scz]p[ie]t[ao]l|[хг][ао]сп[ие]т[ао]л).*")) word = "Hospitaller";
                    else if (s.matches("(mist?r?[eao]|мист?р?[еаэо]|г[оа]сп[оа]ж|раска[ий][ая]|r[ea]p[ea]nt|р[еэа]п[еэ]нт).*")) word = "Mistress of Repentance";
                    else if (s.matches("(pre[ao]?ch|пр[иеэао]+с?х?ч?[еэ]р|пр[оа]п[оа]ведн).*")) word = "Preacher";
                    else if (s.matches("(r[ea]p[ea]nt|р[еэ]п[еэ]нт).*")) word = "Repentia Squad";
                    else if (s.matches("(d[oa]m[ie]n|д[оа]мин).*")) word = "Dominion Squad";
                    else if (s.matches("(s[ei]r[ao]phim|с[еэ]р[ао]фим).*")) word = "Seraphim Squad";
                    else if (s.matches("(exorcis|[еэ]к[сз][ао]рци).*")) word = "Exorcist";
                    else if (s.matches("(penit|п[еэ]н[ие]т|каь?[уяю][шщт]).*")) word = "Penitent Engines";
                    else if (s.matches("([rp]etr[ie]b|[пр][еэ]тр[еи]б|в[оа]зд[ао]).*")) word = "Retributor Squad";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("asasin")){
                    if (s.matches("(dea?(th|f)|д[эе][фсз]|(dea?(th|f))?-?[ck]ult|(д[эе][фсз])?-?куль?т).*")) word = "Death Cult Assassins";
                    else if (s.matches("(cal+[ie]d|[кс][ао]л+[ие]д).*")) word = "Callidus Assassin";
                    else if (s.matches("(c[uy]l+[ie](x|[kc])|[кс]ул+[ие]кс).*")) word = "Culexus Assassin";
                    else if (s.matches("(eversor|[еэ]в[еэ]р[сз]ор).*")) word = "Eversor Assassin";
                    else if (s.matches("([vw][ie]nd[ie][kc]a|в[ие]нд[ие][кс][ау]).*")) word = "Vindicare Assassin";
                    else word = "Assassin";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401845": //эльдары
                if (lastName.toString().equals("aut")){
                    if (s.matches("(sk(y|ai)r[uy]n|ск[уа][ий]?р[ау]н).*")) word = "Autarch Skyrunner";
                    else word = "Autarch";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("far")){
                    if (s.matches("().*")) word = "Farseer Skyrunner";
                    else word = "Farseer";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("night")){
                    if (s.matches("((n[ia]+g?h?t)?-?sp[ei][ao]?r|(н[аиой]+т)?-?сп[ие][ао]?р|к[ао]пь?[её]|il+i[ck]|ил+ик).*")) word = "Illic Nightspear";
                    else if (s.matches("(spin+[eao]?r|спин+[еэао]?р|в[оа]лчо?к).*")) word = "Night Spinner";
                    else if (s.matches("([wv]ing?|[ув]инг?|крыль?[ея]?).{0,2}")) word = "Nightwing";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("warlock")){
                    if (s.matches("(sk(y|ai)r[uy]n|ск[уа][ий]?р[ау]н).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "war_sky");
                    }
                    else {
                        if (s.matches("([ck][oa]n[ck]l[ae]v|к[оа]нклав).*")) word = "Warlock Conclave";
                        else word = "Warlock";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("war_sky")) {
                    if (s.matches("([ck][oa]n[ck]l[ae]v|к[оа]нклав).*")) word = "Warlock Skyrunner Conclave";
                    else word = "Warlock Skyrunner";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("guard")) {
                    if (s.matches("(d[ei]fend|д[еэ]ф[еэ]нд|за[шщ]итн).*")) word = "Guardian Defenders";
                    else word = "Storm Guardians";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("strike")) {
                    if (s.matches("(scorpion|скорпион).*")) word = "Striking Scorpions";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("titan")) {
                    if (s.matches("((f|ph)[aeo]+ntom|(ф|пх?)[аеоэ]+нтом).*")) word = "Phantom Titan";
                    else if (s.matches("(r[eia]+v[eaoi]+n[ei]nt?|р[еэий]+в[аоиеэ]+н[иеэ]нт?н?).{0,3}")) word = "Revenant Titan";
                    else word = "Titan";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("crimson")) {
                    if (s.matches("(hunter|хант[еэ]р|охотник).*")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "crimson_hunt");
                    }
                    else {
                        if (s.matches("([eia]([ksc]+|x)[aoe]rc?h?|[еэи]к[зс][аоеэ]р?х?).{0,3}")) word = "Crimson Hunter Exarch";
                        else word = "";
                        lastName.replace(0, lastName.length(), "");
                        flag.set(false);
                    }
                }
                else if (lastName.toString().equals("crimson_hunt")) {
                    if (s.matches("([eia]([ksc]+|x)[aoe]rc?h?|[еэи]к[зс][аоеэ]р?х?).{0,3}")) word = "Crimson Hunter Exarch";
                    else word = "Crimson Hunter";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("warp")) {
                    if (s.matches("(hunter|хант[еэ]р|охотник).*")) word = "Warp Hunter";
                    else if (s.matches("(sp[aie]+d[ea]?r?|сп[иаий]+д[еэ]?р?|паук).{0,2}")) word = "Warp Spiders";
                    else word = "Crimson Hunter";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("vamp")) {
                    if (s.matches("(hunter|хант[еэ]р|охотник).*")) word = "Vampire Hunter";
                    else if (s.matches("(rai?d|р[эе][ий]д).{0,4}")) word = "Vampire Raider";
                    else word = "Vampire";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("seer")) {
                    if (s.matches("(гнев|[wv]r[ae]i?([fv]|t?h?)|[ву]р[еэ][ий]?[фв]+).{0,3}")) word = "Wraithseer";
                    else if (s.matches("(ду[хш][ао]?|sp[ie]r[ie]?t?|[сз]п[ие]+р[ие]?т?).{0,3}")) word = "Spiritseer";
                    else if (s.matches("(тень?|sh?ad[oa][uw]?|[шс]х?[еэао]+д[ао]?[увф]?).{0,2}")) word = "Shadowseer";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("trup")) {
                    if (s.matches("(master|маст[еэ]р).*")) word = "Troupe Master";
                    else word = "Troupe";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("weav")) {
                    if (s.matches("(зв[её]зд?н?|star|ст[ауеоэ]р).{0,2}")) word = "Starweaver";
                    else if (s.matches("(неб[еа]с?н?|s[kc][yai]+|ск[ау][ий]?).{0,2}")) word = "Skyweavers";
                    else if (s.matches("(пустотн?|vo[iy]d|во[ийа]д).{0,2}")) word = "Voidweaver";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("star_weav")) {
                    if (s.matches("(ткач|[wv][ieao]+v[ei]?r?|в[иеэао]+в[еэи]?р?).{0,2}")) word = "Starweaver";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("sky_weav")) {
                    if (s.matches("(ткач|[wv][ieao]+v[ei]?r?|в[иеэао]+в[еэи]?р?).{0,2}")) word = "Skyweavers";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("void_weav")) {
                    if (s.matches("(ткач|[wv][ieao]+v[ei]?r?|в[иеэао]+в[еэи]?р?).{0,2}")) word = "Voidweaver";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;

            case "222401851": //тиранид
                if (lastName.toString().equals("tiran")) {
                    if (s.matches("(gu?a?rd|гу?ь?[ао]?рд|стражн?и?к?).{0,2}")) word = "Tyrant Guard";
                    else word = "Hive Tyrant";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("hive")) {
                    if (s.matches("(t[yi]rant.*|тиран[аов].{0,2})")) {
                        word = "";
                        lastName.replace(0, lastName.length(), "tiran");
                    }
                    else if (s.matches("(cr[oau]+n|[кс]р[аоу]+н).{0,2}")) word = "Hive Crone";
                    else if (s.matches("(gu?a?rd|гу?ь?[ао]?рд|стражн?и?к?).{0,2}")) word = "Hive Guard";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("lord")) {
                    if (s.matches("(выв[оа]д|b[rl][oua]+d|бл[ауо]+д).{0,3}")) word = "Broodlord";
                    else if (s.matches("(стаь?[яий]|s[wv][oa]+rm|с[ву][ауо]+рм).{0,3}")) word = "The Swarmlord";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("swarm")) {
                    if (s.matches("(lord|лорд).*")) word = "The Swarmlord";
                    else if (s.matches("(r[eia]+per|р[иеа]+п[еэ]р|жнец).*")) word = "Ripper Swarm";
                    else if (s.matches("(sk[aiy]+-?slash?|ск[аийуо]+-?сл[еэа][шсх]+).*")) word = "Sky-Slasher Swarm";
                    else word = "";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("spor")) {
                    if (s.matches("(m[ia]+ne?s?|м[иай]+н+ы?[ийе]?)")) word = "Spore Mines";
                    else word = "Spore";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("jackal")) {
                    if (s.matches("(al(ph?|[fv])[uao]s|аль?ф[уо]?с?)")) word = "Jackal Alphus";
                    else word = "Jackal";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("acol")) {
                    if (s.matches("(a?i[ck][oa]n.*|а?[ий]кон.*|знаменос.*)")) word = "Acolyte Iconward";
                    else word = "Acolyte Hybrids";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("neoph")) {
                    word = "Neophyte Hybrids";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("broth")) {
                    if (s.matches("(infantry|пехот|взвод).*")) word = "Brood Brothers Infantry Squad";
                    else if (s.matches("(heavy|х[эе]ви|хвт-?(расч[её]т.?)?|расч[её]т.*)")) word = "Brood Brothers Heavy Weapons Squad";
                    else word = "Brood Brothers";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("goliath")) {
                    if (s.matches("(tr[ua][ck]|трак|груз[оа]вик).{0,2}")) word = "Goliath Truck";
                    else if (s.matches("((ro[ck]+)?-?gr[iae]+nd|(рок)?-?(гр[аийеэ]+нд|измелчител)).{0,4}")) word = "Goliath Rockgrinder";
                    else word = "Goliath";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                else if (lastName.toString().equals("hybrid")) {
                    if (s.matches("(metamor|м[еи]т[ао]мор).*")) word = "Hybrid Metamorphs";
                    else word = "Hybrids";
                    lastName.replace(0, lastName.length(), "");
                    flag.set(false);
                }
                break;
        }
        return word;
    }

}